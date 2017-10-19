package cl.usach.traffictweet;

import cl.usach.traffictweet.models.Commune;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/communes")
public class CommuneService {

    @Autowired
    private CommuneRepository communeRepository;

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

}
