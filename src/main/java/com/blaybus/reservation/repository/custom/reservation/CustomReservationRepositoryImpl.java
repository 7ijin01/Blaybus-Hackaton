package com.blaybus.reservation.repository.custom.reservation;

import com.blaybus.reservation.entity.Reservation;
import com.blaybus.reservation.repository.MongoRepositoryUtil;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Set;
import java.util.stream.Collectors;


public class CustomReservationRepositoryImpl implements CustomReservationRepository {
    private final MongoTemplate mongoTemplate;

    public CustomReservationRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public Reservation findOneById(String reservationId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(reservationId));
        return MongoRepositoryUtil.findOneById(mongoTemplate, reservationId, Reservation.class);
    }

    @Override
    public Set<String> findByDesignerIdAndDate(String designerId, LocalDate date) {
        Query query = new Query();
        query.addCriteria(Criteria.where("designerId").is(designerId)
                .and("date").gte(date.atStartOfDay()).lt(date.plusDays(1).atStartOfDay())
                .and("status").is("CONFIRMED"));

        return mongoTemplate.find(query, Reservation.class)
                .stream()
                .map(reservation -> reservation.getStart().toString())
                .collect(Collectors.toSet());
    }
}


