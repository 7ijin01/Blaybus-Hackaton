package com.blaybus.domain.reservation.repository;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

public class MongoRepositoryUtil {

    public static <T> T findOneById(MongoTemplate mongoTemplate, String id, Class<T> clazz) {
        Query query = new Query();
        query.addCriteria(Criteria.where("id").is(id));
        return mongoTemplate.findOne(query, clazz);
    }
}