package com.blaybus.reservation.repository;

import com.blaybus.reservation.entity.Reservation;
import com.blaybus.reservation.repository.custom.reservation.CustomReservationRepository;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ReservationRepository extends MongoRepository<Reservation,String>, CustomReservationRepository
{

}
