package com.blaybus.domain.reservation.repository;

import com.blaybus.domain.reservation.entity.Reservation;
import com.blaybus.domain.reservation.repository.custom.reservation.CustomReservationRepository;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;


@Repository
public interface ReservationRepository extends MongoRepository<Reservation,String>, CustomReservationRepository
{
    void deleteByCreatedAtBeforeAndStatusNot(Date createdAt, String status);
}
