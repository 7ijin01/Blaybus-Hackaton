package com.blaybus.domain.reservation.controller;

import com.blaybus.domain.reservation.entity.Designer;
import com.blaybus.domain.reservation.service.DesignerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;


@Tag(name = "디자이너 조회 API", description = "전체 디자이너 조회 및 필터링된 디자이너 조회")
@RestController
@RequestMapping("/designers")
@RequiredArgsConstructor
public class DesignerController
{
    private final DesignerService designerService;
    private final GridFsTemplate gridFsTemplate;


    @Operation(summary = "전체 디자이너 조회", description = "/desingers 경로로 요청 보내면 리스트 형태로 반환")
    @GetMapping
    public ResponseEntity<List<Designer>> getAllDesigners()
    {
        return ResponseEntity.ok(designerService.getAllDesigners());
    }



    @GetMapping("/filter")
    @Operation(summary = "필터링 디자이너 조회", description = "대면, 비대면, 대면&비대면 3가지 기준으로 (지역, 가격)필터링 대면&비대면 같은 경우 maxPrice은 대면 비용에 맞춰서 , minPrice는 비대면 비용에 맞춰서 필터링     -> 결과  online,offline,onoffline 키를 가지는 맵에 List 형태로 디자이너 알맞게 저장   " )
    public ResponseEntity<Map<String,List<Designer>>> getDesignersByRegionAndPrice(
            @RequestParam(required = false) Integer maxPrice,
            @RequestParam(required = false) Integer minPrice,
            @RequestParam(required = false) List<String> regions)
    {

        Map<String,List<Designer>> filterdDesignersWithMeet = designerService.filterdDesignersWithMeet(maxPrice,minPrice,regions);

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
