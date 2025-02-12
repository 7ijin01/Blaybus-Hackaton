package com.blaybus.glowup_backend.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class ReservationService {

    public List<String> read(Map<String, Object> map){
        log.info("read map={}", map);

        if (map == null || !map.containsKey("userId")) {
            log.warn("read; userId is invalid");
            return new ArrayList<>();
        }

        return null;
    }
}
