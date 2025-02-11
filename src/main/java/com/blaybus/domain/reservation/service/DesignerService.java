package com.blaybus.domain.reservation.service;

import com.blaybus.domain.reservation.entity.Designer;
import com.blaybus.domain.reservation.repository.DesignerRepository;
import org.springframework.stereotype.Service;

import java.util.*;

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
        List<Designer> filteredList=designerRepository.findAllByPriceAndRegion(maxPrice,region);

        return  filteredList;

    }
    public Map<String, List<Designer>> filterdDesignersWithMeet(List<Designer> filteredList)
    {

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
        return designerRepository.findOneById(designerId);
    }



//    public List<Designer> getDesignersOnline(List<Designer> designers)
//    {
//        return  designerRepository.findAllByOnline(designers);
//    }
//    public List<Designer> getDesignersOffline(List<Designer> designers)
//    {
//        return  designerRepository.findAllByOffline(designers);
//    }
//    public List<Designer> getDesignersOnlineAndOffline(List<Designer> designers)
//    {
//        return  designerRepository.findAllByOnlineAndOffline(designers);
//    }
}
