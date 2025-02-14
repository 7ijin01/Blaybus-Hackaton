package com.blaybus.reservation.controller;

import com.blaybus.reservation.entity.Designer;
import com.blaybus.reservation.service.DesignerService;
import com.mongodb.client.gridfs.model.GridFSFile;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/designers")
@RequiredArgsConstructor
public class DesignerController
{
    private final DesignerService designerService;
    private final GridFsTemplate gridFsTemplate;


    @GetMapping
    public ResponseEntity<List<Designer>> getAllDesigners() {
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


    @PostMapping("/portfolio/register")
    public void registerPortfolio(@RequestParam("file") MultipartFile file, @RequestParam("designerId") String designerId) throws Exception{
        designerService.upload(file, designerId);
    }

    @GetMapping("/portfolio/list")
    public List<String> listPortfolioIds(@RequestParam("designerId") String designerId){
        return designerService.findPortfolioIdsByDesignerId(designerId);
    }

    @GetMapping("/portfolio/load")
    public ResponseEntity<InputStreamResource> loadPortfolios(@RequestParam("objectId") String objectId) {
        return designerService.streamPortfolios(objectId);
    }

}
