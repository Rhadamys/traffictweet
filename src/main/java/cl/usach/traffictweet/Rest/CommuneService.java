package cl.usach.traffictweet.Rest;

import cl.usach.traffictweet.Models.Commune;
import cl.usach.traffictweet.Models.Occurrence;
import cl.usach.traffictweet.Repositories.CommuneRepository;
import cl.usach.traffictweet.Repositories.OccurrenceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.Set;

@RestController
@RequestMapping("/communes")
public class CommuneService {

    @Autowired
    private CommuneRepository communeRepository;

    @Autowired
    private OccurrenceRepository occurrenceRepository;

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public Iterable<Commune> getAll() {
        return communeRepository.findAll();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseBody
    public Commune findOne(@PathVariable("id") Integer id) {
        return communeRepository.findOne(id);
    }

    @RequestMapping(method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public Commune create(@RequestBody Commune resource) {
        return communeRepository.save(resource);
    }

    @RequestMapping(value = "/{id}/occurrences", method = RequestMethod.GET)
    @ResponseBody
    public Set<Occurrence> findActors(@PathVariable("id") Integer id) {
        return communeRepository.findOne(id).getOccurrences();
    }

//    Para asociar una occurrence a una commune
    @RequestMapping(
            value = "/{communeId}/occurrence/{occurrenceId}",
            method = RequestMethod.POST)
    @ResponseBody
    public Set<Occurrence> addOccurrence(
            @PathVariable("communeId") Integer communeId,
            @PathVariable("occurrenceId") Integer occurrenceId,
            HttpServletResponse httpResponse) {
        if(!communeRepository.exists(communeId)) {
            httpResponse.setStatus(HttpStatus.NOT_FOUND.value());
            return null;
        }
        Commune commune = communeRepository.findOne(communeId);

        for(Occurrence communeOccurrence: commune.getOccurrences()) {
            if(communeOccurrence.getId() == occurrenceId) {
                httpResponse.setStatus(HttpStatus.NOT_MODIFIED.value());
                return null;
            }
        }

        if(!occurrenceRepository.exists(occurrenceId)) {
            httpResponse.setStatus(HttpStatus.NOT_FOUND.value());
            return null;
        }
        Occurrence occurrence = occurrenceRepository.findOne(occurrenceId);

        commune.addOccurrence(occurrence);
        communeRepository.save(commune);
        httpResponse.setStatus(HttpStatus.CREATED.value());
        return communeRepository.save(commune).getOccurrences();
    }
}
