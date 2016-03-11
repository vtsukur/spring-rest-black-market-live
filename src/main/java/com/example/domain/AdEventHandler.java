package com.example.domain;

import com.example.SecurityConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.core.annotation.HandleBeforeCreate;
import org.springframework.data.rest.core.annotation.HandleBeforeSave;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * @author volodymyr.tsukur
 */
@Component
@RepositoryEventHandler
public class AdEventHandler {

    @Autowired
    private UserRepository userRepository;

    @HandleBeforeCreate
    @HandleBeforeSave
    public void setLastModifiedTime(Ad ad) {
        ad.setLastModified(LocalDateTime.now());
    }

    @HandleBeforeCreate
    public void setOwner(Ad ad) {
        final String phoneNumber = SecurityConfig.currentPrincipal().getUsername();
        ad.setUser(userRepository.findByPhoneNumber(phoneNumber));
    }

}
