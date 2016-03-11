package com.example.domain;

import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;

@Entity
public class Ad implements LinkableAd {

//    public Integer getVersion() {
//        return version;
//    }
//
//    public void setVersion(Integer version) {
//        this.version = version;
//    }
//
//    @Version
//    private Integer version;

    public LocalDateTime getLastModified() {
        return lastModified;
    }

    public void setLastModified(LocalDateTime lastModified) {
        this.lastModified = lastModified;
    }

    @LastModifiedDate
    @Column
    private LocalDateTime lastModified;

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Type type;

    public enum Type {

        BUY,

        SELL

    }

    @Column(nullable = false)
    private BigInteger amount;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Currency currency;

    public enum Currency {

        USD,

        EUR

    }

    @Column(nullable = false)
    private BigDecimal rate;

    @ManyToOne
    @JoinColumn(nullable = false)
    private User user;

    private Location location;

    @Embeddable
    public static class Location {

        @Column(nullable = false)
        private String city;

        private String area;

        public Location() {
        }

        public Location(String city, String area) {
            this.city = city;
            this.area = area;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getArea() {
            return area;
        }

        public void setArea(String area) {
            this.area = area;
        }

    }

    private String comment;

    @Lob
    private LocalDateTime publishedAt;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status = Status.NEW;

    public enum Status {

        NEW,

        PUBLISHED,

        EXPIRED

    }

    public void publish() {
        if (status == Status.NEW) {
            publishedAt = LocalDateTime.now();
            status = Status.PUBLISHED;
        } else {
            throw new InvalidAdStateTransitionException("Ad is already published");
        }
    }

    public void expire() {
        if (status == Status.PUBLISHED) {
            status = Status.EXPIRED;
        } else {
            throw new InvalidAdStateTransitionException(
                    "Ad can be finished only when it is in the " + Status.PUBLISHED + " state");
        }
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    public static class InvalidAdStateTransitionException extends RuntimeException {

        public InvalidAdStateTransitionException(String message) {
            super(message);
        }

    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public BigInteger getAmount() {
        return amount;
    }

    public void setAmount(BigInteger amount) {
        this.amount = amount;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public LocalDateTime getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(LocalDateTime publishedAt) {
        this.publishedAt = publishedAt;
    }

    @Override
    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

}
