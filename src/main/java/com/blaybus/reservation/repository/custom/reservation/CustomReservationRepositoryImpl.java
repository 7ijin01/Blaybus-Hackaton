package com.blaybus.reservation.repository.custom.reservation;

import com.blaybus.reservation.entity.Reservation;
import com.blaybus.reservation.repository.MongoRepositoryUtil;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;


public class CustomReservationRepositoryImpl implements CustomReservationRepository
{
    private final MongoTemplate mongoTemplate;

    public CustomReservationRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public Reservation findOneById(String reservationId)
    {
        Query query=new Query();
        query.addCriteria(Criteria.where("id").is(reservationId));
        return MongoRepositoryUtil.findOneById(mongoTemplate, reservationId, Reservation.class);
    }
}
