package com.example;

import com.example.domain.AdValidator;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.example.domain.Ad;
import com.example.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.core.event.ValidatingRepositoryEventListener;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurerAdapter;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.stream.IntStream;

@Component
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class AppConfig {

    private static final Integer[] MOBILE_OPERATOR_CODES = new Integer[]{
            39,
            50,
            63,
            66,
            67,
            68,
            93,
            95,
            96,
            97,
            98,
            99
    };

    private static final String CITY = "Киев";

    private static final String[] DISTRICTS = new String[]{
            "Голосеевский",
            "Дарницкий",
            "Днепровский",
            "Деснянский",
            "Святошинский",
            "Соломенский",
            "Оболонский",
            "Подольский",
            "Печерский",
            "Шевченковский"
    };

    private static final String[] COMMENTS = new String[]{
            "",
            "всю сумму",
            "ну очень нужно",
            "можна частями",
            "малыш, могу подъехать"
    };

    public static final int PUBLISHING_TIME_MAX_DIFF = 4;

    @Autowired
    private CrudRepository<User, Long> userRepository;

    @Autowired
    private CrudRepository<Ad, Long> adRepository;

    private boolean stableUsersOnly;

    public void load() {
        int amount = 99;
        final LocalDateTime now = LocalDateTime.now();

        User admin = new User();
        admin.setPhoneNumber("hontareva");
        userRepository.save(admin);

        if (!stableUsersOnly) {
            IntStream.range(0, amount)
                    .forEach(i -> {
                        User user = nextUser();
                        userRepository.save(user);

                        Ad ad = nextAd(user, now.minusMinutes(amount - i));
                        adRepository.save(ad);
                    });

            setupAdminStake(admin, now);
        }
    }

    private void setupAdminStake(User admin, LocalDateTime publishedAt) {
        if (!stableUsersOnly) {
            Ad ad = new Ad();
            ad.setType(Ad.Type.BUY);
            ad.setAmount(BigInteger.valueOf(100000000));
            ad.setCurrency(Ad.Currency.USD);
            ad.setRate(nextRate(ad.getCurrency(), ad.getType()));
            ad.setUser(admin);
            ad.setStatus(Ad.Status.PUBLISHED);
            ad.setPublishedAt(publishedAt);
            ad.setLocation(new Ad.Location("Киев", "Печерский"));
            ad.setComment("играем по крупному");
            adRepository.save(ad);
        }
    }

    private static User nextUser() {
        User user = new User();
        user.setPhoneNumber(nextPhoneNumber());
        return user;
    }

    private static Ad nextAd(User user, LocalDateTime publishedAt) {
        Ad ad = new Ad();

        Ad.Type type = nextType();
        ad.setType(type);
        Ad.Currency currency = nextCurrency();
        ad.setCurrency(currency);

        ad.setAmount(nextAmount());
        ad.setRate(nextRate(currency, type));
        ad.setUser(user);

        ad.setLocation(new Ad.Location(CITY, nextDistrict()));
        ad.setComment(nextComments());
        ad.setStatus(Ad.Status.PUBLISHED);
        ad.setPublishedAt(nextPublishingTime(publishedAt));

        return ad;
    }

    private static String nextPhoneNumber() {
        return String.format("0%d%07d", nextMobileOperatorCode(), nextInt(10000000));
    }

    private static int nextMobileOperatorCode() {
        return nextRandomFromArray(MOBILE_OPERATOR_CODES);
    }

    private static Ad.Type nextType() {
        return nextRandomFromArray(Ad.Type.values());
    }

    private static BigInteger nextAmount() {
        return BigInteger.valueOf(nextInt(100) * 100 + 100);
    }

    private static Ad.Currency nextCurrency() {
        return nextRandomFromArray(Ad.Currency.values());
    }

    private static BigDecimal nextRate(Ad.Currency currency, Ad.Type type) {
        return avgRate(currency, type);
    }

    private static BigDecimal avgRate(Ad.Currency currency, Ad.Type type) {
        return (currency == Ad.Currency.USD ?
                BigDecimal.valueOf(type == Ad.Type.BUY ? 27.1 : 27.35) :
                BigDecimal.valueOf(type == Ad.Type.BUY ? 29.9 : 30.2)
        );
    }

    private static String nextDistrict() {
        return nextRandomFromArray(DISTRICTS);
    }

    private static String nextComments() {
        return nextRandomFromArray(COMMENTS);
    }

    private static LocalDateTime nextPublishingTime(LocalDateTime previous) {
        return previous.plusMinutes(nextInt(PUBLISHING_TIME_MAX_DIFF - 1) + 1);
    }

    private static <T> T nextRandomFromArray(T[] array) {
        return array[nextInt(array.length)];
    }

    private static int nextInt(int bound) {
        return new Random().nextInt(bound);
    }

    public AppConfig minimalSet(boolean stableUsersOnly) {
        this.stableUsersOnly = stableUsersOnly;
        return this;
    }

    @Bean
    CommandLineRunner commandLineRunner(AppConfig dataLoader) {
        return (o) -> dataLoader.minimalSet(false).load();
    }

    @Bean
    public Module newJavaTimeModule() {
        return new JavaTimeModule();
    }

    @Configuration
    public static class CustomRepositoryRestConfigurer extends RepositoryRestConfigurerAdapter {

        @Override
        public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config) {
            config.exposeIdsFor(Ad.class, User.class);
        }

        @Override
        public void configureValidatingRepositoryEventListener(ValidatingRepositoryEventListener validatingListener) {
            validatingListener.addValidator("beforeCreate", new AdValidator());
        }

    }

}
