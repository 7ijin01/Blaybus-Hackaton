package com.blaybus.domain.register.repository.custom;

import com.blaybus.domain.register.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CustomUserRepositoryImpl implements CustomUserRepository {
    private final MongoTemplate mongoTemplate;

    @Override
    public User findByGoogleId(String googleId) {
        Query query = new Query(Criteria.where("googleId").is(googleId));
        return mongoTemplate.findOne(query, User.class);
    }
}
