package com.example.domain;

import org.springframework.hateoas.Identifiable;

public interface LinkableAd extends Identifiable<Long> {

    Ad.Status getStatus();

}
