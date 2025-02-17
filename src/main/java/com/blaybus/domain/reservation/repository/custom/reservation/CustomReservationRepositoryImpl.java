package com.blaybus.domain.reservation.repository.custom.reservation;

import com.blaybus.domain.reservation.entity.Reservation;
import com.blaybus.domain.reservation.repository.MongoRepositoryUtil;
import org.checkerframework.checker.units.qual.C;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.time.LocalDate;
import java.util.List;
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
        return mongoTemplate.findOne(query, Reservation.class);
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
    @Override
    public List<Reservation> findAllByGoogleId(String googleId) {
        Query query = new Query(Criteria.where("userId").is(googleId));
        return mongoTemplate.find(query, Reservation.class);
    }

    @Override
    public String deleteByRid(String reservationId){
        Query query = new Query(Criteria.where("_id").is(reservationId));
        Reservation deletedReservation =  mongoTemplate.findOne(query, Reservation.class);

        if (deletedReservation != null) {
            mongoTemplate.remove(deletedReservation);
            return "예약 정보 삭제 완료";
        }
        return "예약 정보 삭제 실패";
    }

}


