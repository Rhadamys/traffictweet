package cl.usach.traffictweet.services;

import cl.usach.traffictweet.models.Category;
import cl.usach.traffictweet.repositories.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("/categories")
public class CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;

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

    @RequestMapping(method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public Category create(@RequestBody Category resource) {
        return categoryRepository.save(resource);
    }

}
