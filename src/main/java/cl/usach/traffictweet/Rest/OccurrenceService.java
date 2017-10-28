package cl.usach.traffictweet.Rest;

import cl.usach.traffictweet.Models.Category;
import cl.usach.traffictweet.Models.Occurrence;
import cl.usach.traffictweet.Repositories.CategoryRepository;
import cl.usach.traffictweet.Repositories.OccurrenceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.Set;

@RestController
@RequestMapping("/occurrences")
public class OccurrenceService {
    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private OccurrenceRepository occurrenceRepository;

    @CrossOrigin
    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public Iterable<Occurrence> getAll() {
        return occurrenceRepository.findAll();
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

    @CrossOrigin
    @RequestMapping(method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public Occurrence create(@RequestBody Occurrence resource) {
        return occurrenceRepository.save(resource);
    }

}
