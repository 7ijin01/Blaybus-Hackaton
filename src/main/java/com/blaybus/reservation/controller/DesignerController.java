package com.blaybus.reservation.controller;

import com.blaybus.reservation.entity.Designer;
import com.blaybus.reservation.service.DesignerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/designers")
@RequiredArgsConstructor
public class DesignerController
{
    private final DesignerService designerService;


    @GetMapping
    public ResponseEntity<List<Designer>> getAllDesigners()
    {
        return ResponseEntity.ok(designerService.getAllDesigners());
    }

    @GetMapping("/filter")
    public ResponseEntity<Map<String,List<Designer>>> getDesignersByRegionAndPrice(
            @RequestParam(required = false) Integer maxPrice,
            @RequestParam(required = false) List<String> regions)
    {

        List<Designer> filterdList = designerService.getDesignersByRegionAndPrice(maxPrice,regions);

        Map<String,List<Designer>> filterdDesignersWithMeet = designerService.filterdDesignersWithMeet(filterdList);

        return ResponseEntity.ok(filterdDesignersWithMeet);
    }



}
