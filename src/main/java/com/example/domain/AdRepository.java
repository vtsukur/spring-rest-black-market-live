package com.example.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RestResource;

import java.util.List;

//@RepositoryRestResource(excerptProjection = DistractionFreeAd.class)
public interface AdRepository extends PagingAndSortingRepository<Ad, Long> {

    @Query("select ad from Ad ad where ad.status = 'PUBLISHED'")
//    @RestResource(path = "published")
    Page<Ad> findPublished(Pageable pageable);

    @Query("select ad from Ad ad where ad.user.phoneNumber = ?#{ principal?.username }")
    @RestResource(path = "my")
    List<Ad> findMyAds();

}
