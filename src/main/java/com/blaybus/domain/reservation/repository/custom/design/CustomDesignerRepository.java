package com.blaybus.domain.reservation.repository.custom.design;

import com.blaybus.domain.reservation.entity.Designer;

import java.util.List;

public interface CustomDesignerRepository
{
    List<Designer> findAllByPriceAndRegion(Integer maxPrice,List<String> region);
    Designer findOneById(String designerId);

}
