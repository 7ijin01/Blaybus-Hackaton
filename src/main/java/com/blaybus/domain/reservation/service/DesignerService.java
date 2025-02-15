package com.blaybus.domain.reservation.service;

import com.blaybus.domain.reservation.entity.Designer;
import com.blaybus.domain.reservation.repository.DesignerRepository;
import com.mongodb.client.gridfs.model.GridFSFile;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.core.io.InputStreamResource;
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

    public String upload(MultipartFile file, String designerId) throws IOException {
        ObjectId objectId = gridFsTemplate.store(
                file.getInputStream(),
                file.getOriginalFilename(),
                file.getContentType()
        );


        Designer designer = designerRepository.findOneById(designerId);

        if(designer.getPortfolios() == null){
            designer.setPortfolios(new ArrayList<>());
        }

        List<String> arrayList = designer.getPortfolios();
        Objects.requireNonNull(arrayList).add(objectId.toHexString());
        designerRepository.save(designer);

        return objectId.toHexString();
    }

    public List<String> findPortfolioIdsByDesignerId(String designerId){
        return designerRepository.findOneById(designerId).getPortfolios();
    }

    public ResponseEntity<InputStreamResource> streamPortfolios(String objectId){
        GridFSFile file = gridFsTemplate.findOne(new Query(Criteria.where("_id").is(objectId)));

        GridFsResource resource = gridFsTemplate.getResource(file);

        String contentType = Optional.ofNullable(file.getMetadata())
                .map(metadata -> metadata.getString("contentType"))
                .orElse("application/octet-stream");

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(contentType));
            headers.setContentDisposition(ContentDisposition.inline().filename(file.getFilename()).build());

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(new InputStreamResource(resource.getInputStream()));

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
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
