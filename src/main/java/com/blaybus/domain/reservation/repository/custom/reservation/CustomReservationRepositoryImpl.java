package com.blaybus.domain.reservation.repository.custom.reservation;

import com.blaybus.domain.reservation.entity.Reservation;
import com.blaybus.domain.reservation.repository.MongoRepositoryUtil;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
public class CustomReservationRepositoryImpl implements CustomReservationRepository {
    private final MongoTemplate mongoTemplate;

    public CustomReservationRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public Reservation findOneById(String reservationId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("id").is(reservationId));
        return MongoRepositoryUtil.findOneById(mongoTemplate, reservationId, Reservation.class);
    }

    @Override
    public Set<String> findByDesignerIdAndDate(String designerId, LocalDate date) {
        Query query = new Query();
        query.addCriteria(Criteria.where("designerId").is(designerId)
                .and("date").gte(date.atStartOfDay()).lt(date.plusDays(1).atStartOfDay()));

        return mongoTemplate.find(query, Reservation.class)
                .stream()
                .map(reservation -> reservation.getStart().toString())
                .collect(Collectors.toSet());
    }

    @Override
    public List<Reservation> findAllByGoogleId(String googleId) {
        Query query = new Query(Criteria.where("googleId").is(googleId));

        return mongoTemplate.find(query, Reservation.class);
    }
}


