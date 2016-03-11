package com.example.domain;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.security.access.prepost.PreAuthorize;

public interface UserRepository extends CrudRepository<User, Long> {

    User findByPhoneNumber(@Param("number") String phoneNumber);

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Override
    Iterable<User> findAll();

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Override
    Iterable<User> findAll(Iterable<Long> longs);

    @RestResource(exported = false)
    @Override
    void delete(Long aLong);

    @RestResource(exported = false)
    @Override
    void delete(User entity);

}
