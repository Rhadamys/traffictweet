package cl.usach.traffictweet.services;

import cl.usach.traffictweet.models.Keyword;
import cl.usach.traffictweet.repositories.KeywordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("/keywords")
public class KeywordService {
    @Autowired
    private KeywordRepository keywordRepository;

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public Iterable<Keyword> getAll() {
        return keywordRepository.findAll();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseBody
    public Keyword findOne(@PathVariable("id") Integer id) {
        return keywordRepository.findOne(id);
    }

    @RequestMapping(method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public Keyword create(@RequestBody Keyword resource) {
        return keywordRepository.save(resource);
    }

}
