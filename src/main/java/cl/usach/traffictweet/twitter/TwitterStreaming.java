package cl.usach.traffictweet.twitter;

import cl.usach.traffictweet.mongo.models.Occurrence;
import cl.usach.traffictweet.mongo.repositories.OccurrenceRepository;
import cl.usach.traffictweet.neo4j.Neo4j;
import cl.usach.traffictweet.sql.models.Category;
import cl.usach.traffictweet.sql.models.*;
import cl.usach.traffictweet.sql.repositories.*;
import cl.usach.traffictweet.utils.MatchCase;
import cl.usach.traffictweet.utils.Util;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import twitter4j.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

//import javax.websocket.Session;

@Component
public class TwitterStreaming implements ApplicationRunner {
	private final static java.util.logging.Logger LOGGER = Logger.getLogger(TwitterStreaming.class.getName());

	private static final int MAX_LAST_TWEETS = 5;

	private OccurrenceRepository occurrenceRepository;
	private CategoryRepository categoryRepository;
	private CommuneRepository communeRepository;
	private MetricRepository metricRepository;
	private CategoryMetricRepository categoryMetricRepository;
	private CommuneMetricRepository communeMetricRepository;

	private final TwitterStream twitterStream;
	private final List<String> keywords;
	private final List<String> lastTweets;

	@Autowired
	public TwitterStreaming(
			OccurrenceRepository occurrenceRepository,
			CategoryRepository categoryRepository,
			CommuneRepository communeRepository,
			MetricRepository metricRepository,
			CategoryMetricRepository categoryMetricRepository,
			CommuneMetricRepository communeMetricRepository) {
		this.occurrenceRepository = occurrenceRepository;
		this.categoryRepository = categoryRepository;
		this.communeRepository = communeRepository;
		this.metricRepository = metricRepository;
		this.categoryMetricRepository = categoryMetricRepository;
		this.communeMetricRepository = communeMetricRepository;
		this.twitterStream = new TwitterStreamFactory().getInstance();

		this.keywords = new ArrayList<>();
		this.lastTweets = new ArrayList<>();

		loadKeywords();
	}

	private void loadKeywords() {
		try {
			ClassLoader classLoader = getClass().getClassLoader();
			keywords.addAll(
					IOUtils.readLines(
							classLoader.getResourceAsStream("words.dat"),"UTF-8"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void run(ApplicationArguments args) {
		StatusListener listener = new StatusListener() {

			public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
				System.out.println("Got a status deletion notice id:" + statusDeletionNotice.getStatusId());
			}

			public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
				System.out.println("Got track limitation notice:" + numberOfLimitedStatuses);
			}

			public void onScrubGeo(long userId, long upToStatusId) {
				System.out.println("Got scrub_geo event userId:" + userId + " upToStatusId:" + upToStatusId);
			}

			public void onException(Exception ex) {
				ex.printStackTrace();
			}

			@Override
			public void onStallWarning(StallWarning arg0) {	}

			@Override
			public void onStatus(Status status) {
				// Creating document
				LOGGER.log(Level.INFO, "Generating document...");
				boolean isTheSame = false;
				synchronized (lastTweets) {
					int i = 0;
					while(!isTheSame && i < lastTweets.size()) {
						String tweet = lastTweets.get(i);
						if(Util.isSameText(status.getText(), tweet)) {
							LOGGER.log(Level.INFO, "Same detected:\nTweet:\t" + status.getText() + "\nTemp:\t" + tweet);
							isTheSame = true;
						}
						i++;
					}

					if(!isTheSame) {
						if(lastTweets.size() == MAX_LAST_TWEETS)
							lastTweets.remove(0);
						lastTweets.add(status.getText());
					}
				}

				MatchCase matchCase = isTheSame ?
						MatchCase.ALREADY_EXISTS :
						Util.match(status.getText(), keywords);
				LOGGER.log(Level.INFO, "Tweet, probability of be a valid event: " + matchCase);
				if(matchCase == MatchCase.MATCH) {
					System.out.println("Generating occurrence...");
					Occurrence occurrence = storeOccurrence(status);
					Neo4j.insertNode(occurrence);

					LOGGER.log(Level.INFO, "Document inserted successfully!");
				} else {
					LOGGER.log(Level.INFO, "Document was ignored...");
				}
			}
		};

		FilterQuery fq = new FilterQuery()
				.language("es")
				.locations(
						new double[] { -71.7088, -34.2878 },
						new double[] { -69.769, -32.9195 });

		this.twitterStream.addListener(listener);
		this.twitterStream.filter(fq);
	}

	private Occurrence storeOccurrence(Status status) {
		String tweetId = String.valueOf(status.getId());
		String username = status.getUser().getScreenName();
		String image = status.getUser().getOriginalProfileImageURL();
		String text = status.getText();

		List<Category> categories = new ArrayList<>();
		categoryRepository.findAll().forEach(categories::add);

		List<Commune> communes = new ArrayList<>();
		communeRepository.findAll().forEach(communes::add);
		Commune commune, occurrenceCommune = communes.get(0);
		boolean found = false;
		int i = 1;
		while (!found && i < communes.size()) {
			commune = communes.get(i);
			List<String> keywords = new ArrayList<>();
			keywords.add(Util.clean(commune.getName()));
			for(Street street: commune.getStreets())
				keywords.add(Util.clean(street.getName()));

			if(Util.match(text, keywords) == MatchCase.MATCH) {
				occurrenceCommune = commune;
				found = true;
			}
		}

		List<String> occurrenceCategories = new ArrayList<String>();
		for (Category category : categories) {
			List<String> keywords = new ArrayList<>();
			for (Keyword keyword : category.getKeywords())
				keywords.add(keyword.getName());

			if (Util.match(text, keywords) == MatchCase.MATCH) {
				occurrenceCategories.add(category.getName());
				Metric.update(metricRepository, category, occurrenceCommune);
				CategoryMetric.update(categoryMetricRepository,category);
			}
		}

		CommuneMetric.update(communeMetricRepository, occurrenceCommune);

		Occurrence occurrence = new Occurrence(tweetId, username, image, text, new Date(),
				occurrenceCommune.getName(), occurrenceCategories);
		return occurrenceRepository.save(occurrence);
	}
}
