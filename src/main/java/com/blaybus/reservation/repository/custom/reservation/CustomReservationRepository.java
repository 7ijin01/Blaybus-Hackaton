package com.blaybus.reservation.repository.custom.reservation;

import com.blaybus.reservation.entity.Designer;
import com.blaybus.reservation.entity.Reservation;
import com.blaybus.reservation.service.ReservationService;
import org.springframework.cglib.core.Local;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public interface CustomReservationRepository
{
    Reservation findOneById(String reservationId);
    Set<String> findByDesignerIdAndDate(String designerId, LocalDate date);

}
