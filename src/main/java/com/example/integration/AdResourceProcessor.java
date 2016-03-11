package com.example.integration;

import com.example.domain.Ad;
import com.example.domain.LinkableAd;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.support.RepositoryEntityLinks;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceProcessor;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

/**
 * @author volodymyr.tsukur
 */
@Component
public class AdResourceProcessor implements ResourceProcessor<Resource<LinkableAd>> {

    @Autowired
    private RepositoryEntityLinks entityLinks;

    @Override
    public Resource<LinkableAd> process(Resource<LinkableAd> resource) {
        final LinkableAd ad = resource.getContent();
        if (ad.getStatus() == Ad.Status.NEW) {
            resource.add(linkTo(methodOn(AdController.class).publish(ad.getId(), null))
                    .withRel("publishing"));
            resource.add(entityLinks.linkToSingleResource(Ad.class, ad.getId())
                    .withRel("update"));
            resource.add(entityLinks.linkToSingleResource(Ad.class, ad.getId())
                    .withRel("deletion"));
        }
        if (ad.getStatus() == Ad.Status.PUBLISHED) {
            resource.add(linkTo(methodOn(AdController.class).expire(ad.getId(), null))
                    .withRel("expiration"));
        }
        return resource;
    }

}
