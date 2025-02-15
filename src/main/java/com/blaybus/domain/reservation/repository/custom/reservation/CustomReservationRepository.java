package com.blaybus.domain.reservation.repository.custom.reservation;

import com.blaybus.domain.reservation.entity.Reservation;

import java.time.LocalDate;
import java.util.Set;

public interface CustomReservationRepository
{
    Reservation findOneById(String reservationId);
    Set<String> findByDesignerIdAndDate(String designerId, LocalDate date);

}
