package com.blaybus.reservation.service;

import com.blaybus.reservation.entity.Designer;
import com.blaybus.reservation.entity.Reservation;
import com.blaybus.reservation.repository.ReservationRepository;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ReservationService
{
    private final ReservationRepository reservationRepository;
    private final DesignerService designerService;

    public ReservationService(ReservationRepository reservationRepository, DesignerService designerService) {
        this.reservationRepository = reservationRepository;
        this.designerService = designerService;
    }

    //유저 코드 생성이후 연동할 예정
    public String createReservation() {
        Reservation reservation = new Reservation();
        reservation.setUserId("1");
        reservation.setId(UUID.randomUUID().toString());
        reservation.setStatus("PENDING");
        reservationRepository.save(reservation);
        return reservation.getId();

    }
    public void updateReservationMeet(String reservationId,Boolean meet)
    {
        Reservation reservation = reservationRepository.findOneById(reservationId);
        reservation.setMeet(meet);
        reservationRepository.save(reservation);
    }
    public void updateReservationDesigner(String reservationId,String designerId)
    {
        Reservation reservation = reservationRepository.findOneById(reservationId);
        if (reservation == null) {
            throw new IllegalArgumentException("no reservation " + reservationId);
        }

        Designer designer= designerService.getOneDesigner(designerId);
        if (designer == null) {
            throw new IllegalArgumentException("no designerss" + reservationId);
        }

        reservation.setDesignerId(designerId);
        reservation.setShop(designer.getAddress());
        reservation.setPrice(reservation.getMeet() ?
                designer.getPrice_meet().toString() :
                designer.getPrice_not_meet().toString());


        reservationRepository.save(reservation);
    }
}
