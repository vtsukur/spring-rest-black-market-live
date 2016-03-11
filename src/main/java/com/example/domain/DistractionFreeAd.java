package com.example.domain;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.rest.core.config.Projection;

import java.math.BigDecimal;

@Projection(types = Ad.class, name = "minimal")
public interface DistractionFreeAd extends LinkableAd {

    Ad.Type getType();

    BigDecimal getAmount();

    String getCurrency(); // Ad.Currency

    String getRate();

    @Value("#{target.user?.phoneNumber}")
    String getPhoneNumber();

}
