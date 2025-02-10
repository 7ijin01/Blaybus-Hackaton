package com.blaybus.reservation.repository.custom.design;

import com.blaybus.reservation.entity.Designer;

import java.util.List;
import java.util.Optional;

public interface CustomDesignerRepository
{
    List<Designer> findAllByPriceAndRegion(Integer maxPrice,List<String> region);
    Designer findOneById(String designerId);

}
