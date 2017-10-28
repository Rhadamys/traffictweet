package cl.usach.traffictweet.Rest;

import cl.usach.traffictweet.Models.Keyword;
import cl.usach.traffictweet.Repositories.KeywordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/keywords")
public class KeywordService {
    @Autowired
    private KeywordRepository keywordRepository;

    @CrossOrigin
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

    @CrossOrigin
    @RequestMapping(method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public Keyword create(@RequestBody Keyword resource) {
        return keywordRepository.save(resource);
    }

}
