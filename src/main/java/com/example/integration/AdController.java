package com.example.integration;

import com.example.domain.Ad;
import com.example.domain.AdRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.PersistentEntityResourceAssembler;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.hateoas.Resource;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author volodymyr.tsukur
 */
@RepositoryRestController
public class AdController {

    @Autowired
    private AdRepository adRepository;

    @RequestMapping(value = "/ads/{id}/publishing",
            method = RequestMethod.POST)
    @ResponseBody
    public Resource publish(@PathVariable("id") Long id,
                            PersistentEntityResourceAssembler asm) {
        final Ad ad = adRepository.findOne(id);
        ad.publish();
        return asm.toFullResource(adRepository.save(ad));
    }

    @RequestMapping(value = "/ads/{id}/expiration",
            method = RequestMethod.POST)
    @ResponseBody
    public Resource expire(@PathVariable("id") Long id,
                            PersistentEntityResourceAssembler asm) {
        final Ad ad = adRepository.findOne(id);
        ad.expire();
        return asm.toFullResource(adRepository.save(ad));
    }

}
