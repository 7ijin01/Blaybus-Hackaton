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

    public List<Designer> getDesignersByRegionAndPrice(Integer maxPrice,Integer minPrice,List<String> region)
    {
        List<Designer> filteredList=designerRepository.findAllByPriceAndRegion(maxPrice,region);
        return  filteredList;
    }
    public Map<String, List<Designer>> filterdDesignersWithMeet(List<Designer> filteredList)
    {
        if (filteredList == null) {
            throw new CustomException(ExceptionCode.INVALID_REQUEST);
        }

        Map<String ,List<Designer>> sortMap =new HashMap<>();

        List<Designer> online =new ArrayList<>();
        List<Designer> offline=new ArrayList<>();
        List<Designer> onoffline=new ArrayList<>();

        for(Designer designer: filteredList)
        {
            if(designer.getMeet()==0)
            {
                online.add(designer);
            }
            else if(designer.getMeet()==1)
            {
                offline.add(designer);
            }
            else if(designer.getMeet()==2)
            {
                onoffline.add(designer);
            }
        }
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
