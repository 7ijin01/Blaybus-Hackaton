package com.blaybus.glowup_backend.service.reservation;

import com.blaybus.glowup_backend.model.Designers;
import com.blaybus.glowup_backend.model.Payment;
import com.blaybus.glowup_backend.model.Reservations;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReservationService {
    @Autowired
    private MongoTemplate mongoTemplate;

    public List<Reservations> findByUserId(String userId) {
        log.info("findByUserId userId={}", userId);

        if (userId == null) {
            log.warn("read; userId is invalid");
            return new ArrayList<>();
        }
        List<Reservations> list = mongoTemplate.find(
                new Query(Criteria.where("userId").is(userId)), Reservations.class); // 기준 잡아서 시간 순으로 조회 되는 부분 추가하기
        log.info("list={}", list);
        return list;
    }

    public boolean delete(String reservationsId, String designerId) {
        log.info("delete reservationsId={}, designerId={}", reservationsId, designerId);

        if (reservationsId == null || designerId == null) {
            log.warn("delete; reservationsId or designerId is invalid");
            return false;
        }
        try {
            // 1. 예약 상태 '취소'로 변경
            cancelReservation(reservationsId);
            // 2. 디자이너 타임테이블에 반영
            removeFromDesignerTimeTable(reservationsId, designerId);
            // 3. 결제 취소
//            cancelPayment(reservationsId);

            // 4. 디자이너에게 알림 전송 (구현되면 좋을것 같음)

        } catch (Exception e) {
            log.error("예약 취소 시 에러 발생={}", e.getMessage());
            return false;
        }
        return true;
    }

    public int update(String reservationsId, String designerId) {
        log.info("update reservationsId={}, designerId={}", reservationsId, designerId);

        if (reservationsId == null || designerId == null) {
            log.warn("update; reservationsId or designerId is invalid");
            return 0;
        }
        // 1. 시간 변경 -> 디자이너 타임테이블에 반영

        return 1;
    }

    // 예약 상태를 업데이트하는 메서드
    private void updateStatus(String collectionName, String reservationsId, String status) {
        Query query = new Query(Criteria.where("reservationsId").is(reservationsId));
        Update update = new Update().set("status", status);
        mongoTemplate.updateFirst(query, update, collectionName);
    }

    // 예약 취소
    private void cancelReservation(String reservationsId) {
        updateStatus("reservations", reservationsId, "CANCEL");
    }

    // 결제 정보 조회
    private Payment findPaymentByReservationsId(String reservationsId) {
        // payments 컬렉션에서 reservationsId로 결제 정보 조회
        Query query = new Query(Criteria.where("reservationsId").is(reservationsId));
        return mongoTemplate.findOne(query, Payment.class, "payments");
    }

    // 결제 취소
    private void cancelPayment(String reservationsId) {
        Payment payment = findPaymentByReservationsId(reservationsId);

        // 카카오 페이 결제 취소 시
        if ("KAKAOPAY".equals(payment.getPaymentMethod())) {

        }

        // 결제 상태 변경
        updateStatus("payments", reservationsId, "CANCEL");

    }

    // 1. 예약 생성 시 디자이너 id 값 불러와서 setTimeTable로 추가할 경우
    private void removeFromDesignerTimeTable(String reservationsId, String designerId) {
        // 1. 예약 정보 조회 (start, end 시간 가져오기)
        Reservations reservation = mongoTemplate.findOne(
                new Query(Criteria.where("reservationsId").is(reservationsId)
                        .and("designerId").is(designerId)), Reservations.class, "reservations");

        if (reservation == null) {
            throw new RuntimeException("No reservation found for reservationsId=" + reservationsId);
        }

        Date start = reservation.getStart();
        Date end = reservation.getEnd();
        // 2. 디자이너 컬렉션 조회
        Designers designers = mongoTemplate.findOne(
                new Query(Criteria.where("designerId").is(designerId)), Designers.class, "designers");

        if (designers == null) {
            throw new RuntimeException("No designer found with designerId=" + designerId);
        }

        // 3. timeTable에서 start와 end 사이에 포함되는 시간을 제외한 나머지 시간만 남기기
        List<String> updatedTimeTable = designers.getTimeTable().stream()
                .map(time -> parseDate(time))
                .filter(timeDate -> timeDate.before(start) || timeDate.after(end)) // start ~ end 범위 제거
                .map(this::formatDate) // 다시 문자열 변환
                .collect(Collectors.toList());

        // 4. 디자이너 timeTable 업데이트
        mongoTemplate.updateFirst(
                new Query(Criteria.where("designerId").is(designerId)),
                new Update().set("timeTable", updatedTimeTable),
                Designers.class
        );

        log.info("Updated designer timeTable for designerId={}, removed times between {} and {}", designerId, start, end);
    }

    // 2. 디자이너에 타임 테이블 속성 추가 안하고, 예약 테이블에서 디자이너 Id 기준으로 해당하는 예약 정보들 불러 온 후, start와 end 로 값 찾아서 삭제하기
    private void removeDesignerTimeTable(String reservationsId, String designerId) {
        // 예약 테이블에서 start와 end 값 null로 업데이트
        Query query = new Query(Criteria.where("reservationsId").is(reservationsId)
        .and("designerId").is(designerId));

        Update update = new Update().set("start", null).set("end", null);

        mongoTemplate.updateFirst(query, update, "reservations");

        log.info("Updated designer timeTable for designerId={}", designerId);
    }

    // 문자열을 Date로 변환
    private Date parseDate(String time) {
        try {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(time);
        } catch (ParseException e) {
            throw new RuntimeException("Time format error: " + time);
        }
    }

    // Date를 문자열로 변환
    private String formatDate(Date date) {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm").format(date);
    }
}
