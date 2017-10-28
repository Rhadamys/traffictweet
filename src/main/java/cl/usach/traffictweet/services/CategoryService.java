package cl.usach.traffictweet.services;

import cl.usach.traffictweet.models.Category;
import cl.usach.traffictweet.models.Keyword;
import cl.usach.traffictweet.models.Occurrence;
import cl.usach.traffictweet.repositories.CategoryRepository;
import cl.usach.traffictweet.repositories.OccurrenceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.Set;

@RestController
@CrossOrigin
@RequestMapping("/categories")
public class CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private OccurrenceRepository occurrenceRepository;

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public Iterable<Category> getAll() {
        return categoryRepository.findAll();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseBody
    public Category findOne(@PathVariable("id") Integer id) {
        return categoryRepository.findOne(id);
    }

    @RequestMapping(value = "/{id}/occurrences", method = RequestMethod.GET)
    @ResponseBody
    public Set<Occurrence> findOccurrences(@PathVariable("id") Integer id) {
        return categoryRepository.findOne(id).getOccurrences();
    }

    @RequestMapping(value = "/{id}/keywords", method = RequestMethod.GET)
    @ResponseBody
    public Set<Keyword> findKeywords(@PathVariable("id") Integer id) {
        return categoryRepository.findOne(id).getKeywords();
    }

    @RequestMapping(
            value = "/{categoryID}/occurrences/{occurrenceID}",
            method = RequestMethod.POST)
    @ResponseBody
    public Set<Occurrence> addOccurrence(
            @PathVariable("categoryID") Integer categoryID,
            @PathVariable("occurrenceID") Integer occurrenceID,
            HttpServletResponse httpResponse) {
        if(!categoryRepository.exists(categoryID)) {
            httpResponse.setStatus(HttpStatus.NOT_FOUND.value());
            return null;
        }
        Category category = categoryRepository.findOne(categoryID);

        for(Occurrence categoryOccurrences: category.getOccurrences()) {
            if(categoryOccurrences.getId() == occurrenceID) {
                httpResponse.setStatus(HttpStatus.NOT_MODIFIED.value());
                return null;
            }
        }

        if(!occurrenceRepository.exists(occurrenceID)) {
            httpResponse.setStatus(HttpStatus.NOT_FOUND.value());
            return null;
        }
        Occurrence occurrence = occurrenceRepository.findOne(occurrenceID);

        category.addOccurrence(occurrence);
        occurrence.addCategory(category);
        occurrenceRepository.save(occurrence);
        httpResponse.setStatus(HttpStatus.CREATED.value());
        return categoryRepository.save(category).getOccurrences();
    }

    @RequestMapping(method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public Category create(@RequestBody Category resource) {
        return categoryRepository.save(resource);
    }

}
