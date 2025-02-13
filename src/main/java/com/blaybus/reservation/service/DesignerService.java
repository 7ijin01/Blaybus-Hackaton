package com.blaybus.reservation.service;

import com.blaybus.reservation.entity.Designer;
import com.blaybus.reservation.repository.DesignerRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class DesignerService
{
    private final DesignerRepository designerRepository;

    public DesignerService(DesignerRepository designerRepository) {
        this.designerRepository = designerRepository;
    }

    public List<Designer> getAllDesigners()
    {
        return designerRepository.findAll();
    }

    public List<Designer> getDesignersByRegionAndPrice(Integer maxPrice,List<String> region)
    {
        if (maxPrice == null || region == null || region.isEmpty()) {
            throw new IllegalArgumentException("가격 또는 지역 정보가 올바르지 않습니다.");
        }

        List<Designer> filteredList=designerRepository.findAllByPriceAndRegion(maxPrice,region);

        return  filteredList;

    }
    public Map<String, List<Designer>> filterdDesignersWithMeet(List<Designer> filteredList)
    {
        if (filteredList == null) {
            throw new IllegalArgumentException("디자이너 목록이 null입니다.");
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
    public Designer getOneDesigner(String designerId)
    {
        return Optional.ofNullable(designerRepository.findOneById(designerId))
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 디자이너를 찾을 수 없습니다: " + designerId));
    }

}
