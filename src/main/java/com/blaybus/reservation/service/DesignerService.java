package com.blaybus.reservation.service;

import com.blaybus.reservation.entity.Designer;
import com.blaybus.reservation.exception.CustomException;
import com.blaybus.reservation.exception.ExceptionCode;
import com.blaybus.reservation.repository.DesignerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DesignerService
{
    private final DesignerRepository designerRepository;


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
