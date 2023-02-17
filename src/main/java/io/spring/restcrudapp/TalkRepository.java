package io.spring.restcrudapp;

import org.springframework.data.repository.CrudRepository;

public interface TalkRepository extends CrudRepository<Talk, Long> {
}