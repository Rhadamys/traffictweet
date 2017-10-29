package cl.usach.traffictweet.services;

import cl.usach.traffictweet.models.Category;
import cl.usach.traffictweet.models.Occurrence;
import cl.usach.traffictweet.repositories.CategoryRepository;
import cl.usach.traffictweet.repositories.OccurrenceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.Calendar;
import java.util.Date;
import java.util.Set;

@RestController
@CrossOrigin
@RequestMapping("/occurrences")
public class OccurrenceService {
    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private OccurrenceRepository occurrenceRepository;

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public Iterable<Occurrence> getAll() {
        return occurrenceRepository.findAll();
    }

    @RequestMapping(
            value = "/today",
            method = RequestMethod.GET)
    @ResponseBody
    public Iterable<Occurrence> getAllOfToday() {
        Date now = new Date();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        return occurrenceRepository.findByDateBetween(calendar.getTime(), now);
    }

    @RequestMapping(
            value = "/between",
            method = RequestMethod.GET)
    @ResponseBody
    public Iterable<Occurrence> getAllBetween(
            @RequestParam("from") @DateTimeFormat(pattern="yyyy-MM-dd") Date from,
            @RequestParam("to") @DateTimeFormat(pattern="yyyy-MM-dd") Date to) {
        return occurrenceRepository.findByDateBetween(from, to);
    }


    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseBody
    public Occurrence findOne(@PathVariable("id") Integer id) {
        return occurrenceRepository.findOne(id);
    }

    @RequestMapping(value = "/{id}/categories", method = RequestMethod.GET)
    @ResponseBody
    public Set<Category> findCategories(@PathVariable("id") Integer id) {
        return occurrenceRepository.findOne(id).getCategories();
    }

    @RequestMapping(
            value = "/{occurrenceID}/categories/{categoryID}",
            method = RequestMethod.POST)
    @ResponseBody
    public Set<Category> addCategory(
            @PathVariable("occurrenceID") Integer occurrenceID,
            @PathVariable("categoryID") Integer categoryID,
            HttpServletResponse httpResponse) {
        if(!occurrenceRepository.exists(occurrenceID)) {
            httpResponse.setStatus(HttpStatus.NOT_FOUND.value());
            return null;
        }
        Occurrence occurrence = occurrenceRepository.findOne(occurrenceID);

        for(Category occurrencesCategory: occurrence.getCategories()) {
            if(occurrencesCategory.getId() == categoryID) {
                httpResponse.setStatus(HttpStatus.NOT_MODIFIED.value());
                return null;
            }
        }

        if(!categoryRepository.exists(categoryID)) {
            httpResponse.setStatus(HttpStatus.NOT_FOUND.value());
            return null;
        }
        Category category = categoryRepository.findOne(categoryID);

        occurrence.addCategory(category);
        category.addOccurrence(occurrence);
        categoryRepository.save(category);
        httpResponse.setStatus(HttpStatus.CREATED.value());
        return occurrenceRepository.save(occurrence).getCategories();
    }

    @RequestMapping(method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public Occurrence create(@RequestBody Occurrence resource) {
        return occurrenceRepository.save(resource);
    }

}
