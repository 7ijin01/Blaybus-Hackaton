package com.blaybus.domain.reservation.controller;

import com.blaybus.domain.reservation.entity.Designer;
import com.blaybus.domain.reservation.service.DesignerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/designers")
public class DesignerController
{
    private final DesignerService designerService;


    public DesignerController(DesignerService designerService) {
        this.designerService = designerService;

    }

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
