package com.blaybus.domain.reservation.service;

import com.blaybus.domain.reservation.entity.Designer;
import com.blaybus.domain.reservation.repository.DesignerRepository;
import com.blaybus.global.util.XuggleThumbnailGenerator;
import com.mongodb.client.gridfs.model.GridFSFile;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Service
@RequiredArgsConstructor
public class DesignerService
{
    private final DesignerRepository designerRepository;
    public final GridFsTemplate gridFsTemplate;
    private final XuggleThumbnailGenerator thumbnailGenerator;
    public String upload(MultipartFile file, String designerId) throws IOException {
        // 1. 파일을 GridFS에 저장
        ObjectId objectId = gridFsTemplate.store(
                file.getInputStream(),
                file.getOriginalFilename(),
                file.getContentType()
        );

        // 2. 파일의 URL 생성
        String fileUrl = "https://blaybus-glowup.com/designers/portfolio/load?objectId=" + objectId.toString();

        // 3. 썸네일 생성 (비디오 파일인 경우)
        String thumbnailUrl = null;
        if (file.getContentType() != null && file.getContentType().startsWith("video/")) {
            ObjectId thumbnailId = thumbnailGenerator.saveThumbnail(objectId.toHexString());
            thumbnailUrl = "https://blaybus-glowup.com/thumbnails/" + thumbnailId.toHexString();
        }

        // 4. 디자이너의 포트폴리오 업데이트
        Designer designer = designerRepository.findOneById(designerId);
        if (designer.getPortfolios() == null) {
            designer.setPortfolios(new ArrayList<>());
        }

        List<String> arrayList = designer.getPortfolios();
        Objects.requireNonNull(arrayList).add(fileUrl);

        // 5. 썸네일이 존재하면 포트폴리오 리스트에도 추가
        if (thumbnailUrl != null) {
            Objects.requireNonNull(arrayList).add(thumbnailUrl);
        }

        designerRepository.save(designer);

        return objectId.toHexString();
    }

    public List<String> findPortfolioIdsByDesignerId(String designerId){
        return designerRepository.findOneById(designerId).getPortfolios();
    }

    public ResponseEntity<Resource> streamPortfolios(String objectId){
        GridFSFile file = gridFsTemplate.findOne(new Query(Criteria.where("_id").is(objectId)));

        GridFsResource resource = gridFsTemplate.getResource(file);
        if (resource.getContentType().startsWith("video/")){

        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(resource.getContentType())) // 이미지 타입 설정
                .body(resource);
    }

    public List<Designer> getAllDesigners()
    {
        return designerRepository.findAll();
    }

    public List<Designer> getDesignersByRegionAndPrice(Integer meet,Integer maxPrice,Integer minPrice,List<String> region)
    {
        List<Designer> filteredList=designerRepository.findAllByPriceAndRegion(meet,maxPrice,minPrice,region);
        return  filteredList;
    }
    public Map<String, List<Designer>> filterdDesignersWithMeet(Integer maxPrice,Integer minPrice,List<String> region)
    {
        Map<String ,List<Designer>> sortMap =new HashMap<>();

        List<Designer> online = getDesignersByRegionAndPrice(0,maxPrice,minPrice,region);
        List<Designer> offline= getDesignersByRegionAndPrice(1,maxPrice,minPrice,region);
        List<Designer> onoffline= getDesignersByRegionAndPrice(2,maxPrice,minPrice,region);

        sortMap.put("online",online);
        sortMap.put("offline",offline);
        sortMap.put("onoffline",onoffline);
        return sortMap;
    }

    //    public Designer getOneDesigner(String designerId)
//    {
//        return Optional.ofNullable(designerRepository.findOneById(designerId))
//                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 디자이너를 찾을 수 없습니다: " + designerId));
//    }
}
