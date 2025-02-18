package com.blaybus.domain.reservation.service;

import com.blaybus.domain.reservation.entity.Designer;
import com.blaybus.domain.reservation.repository.DesignerRepository;
import com.mongodb.client.gridfs.model.GridFSFile;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.InputStream;
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

        String imageUrl = "https://blaybus-glowup.com/designers/portfolio/load?objectId=" + objectId.toString();

        Designer designer = designerRepository.findOneById(designerId);

        if(designer.getPortfolios() == null){
            designer.setPortfolios(new ArrayList<>());
        }

        List<String> arrayList = designer.getPortfolios();
        Objects.requireNonNull(arrayList).add(imageUrl);
        designerRepository.save(designer);

        return objectId.toHexString();
    }

    public List<String> findPortfolioIdsByDesignerId(String designerId){
        return designerRepository.findOneById(designerId).getPortfolios();
    }

    public ResponseEntity<Resource> streamPortfolios(String objectId){
        GridFSFile file = gridFsTemplate.findOne(new Query(Criteria.where("_id").is(new ObjectId(objectId))));
        String rangeHeader = "bytes=0-1048576";
        if (file == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "파일을 찾을 수 없습니다.");
        }

        GridFsResource resource = gridFsTemplate.getResource(file);
        String contentType = Optional.ofNullable(resource.getContentType()).orElse("application/octet-stream");

        try {
            long fileSize = file.getLength();
            InputStream inputStream = resource.getInputStream();

            // ✅ 1. 이미지 파일이면 그대로 반환 (스트리밍 불필요)
            if (contentType.startsWith("image/")) {
                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .body(resource);
            }

            // ✅ 2. 비디오 파일이면 스트리밍 처리
            if (contentType.startsWith("video/") && rangeHeader != null) {
                return handleVideoStreaming(rangeHeader, inputStream, fileSize, contentType);
            }

            // ✅ 3. 비디오지만 Range 요청이 없는 경우 (iPhone 등에서 발생 가능)
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(resource);

        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "파일을 읽는 중 오류 발생", e);
        }
    }
    private ResponseEntity<Resource> handleVideoStreaming(String rangeHeader, InputStream inputStream, long fileSize, String contentType) throws IOException {
        String[] ranges = rangeHeader.replace("bytes=", "").split("-");
        long start = Long.parseLong(ranges[0]);
        long end = (ranges.length > 1 && !ranges[1].isEmpty()) ? Long.parseLong(ranges[1]) : fileSize - 1;
        long chunkSize = end - start + 1;

        byte[] buffer = new byte[(int) chunkSize];
        inputStream.skip(start);
        inputStream.read(buffer, 0, (int) chunkSize);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", contentType);
        headers.set("Accept-Ranges", "bytes");
        headers.set("Content-Range", "bytes " + start + "-" + end + "/" + fileSize);
        headers.setContentLength(chunkSize);

        return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                .headers(headers)
                .body(new ByteArrayResource(buffer));
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
