package com.blaybus.reservation.repository.custom.reservation;

import com.blaybus.reservation.entity.Designer;
import com.blaybus.reservation.entity.Reservation;
import com.blaybus.reservation.service.ReservationService;

import java.util.List;

public interface CustomReservationRepository
{
    Reservation findOneById(String reservationId);
}
