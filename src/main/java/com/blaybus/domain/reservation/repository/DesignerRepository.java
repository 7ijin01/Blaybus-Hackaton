package com.blaybus.domain.reservation.repository;

import com.blaybus.domain.reservation.entity.Designer;
import com.blaybus.domain.reservation.repository.custom.design.CustomDesignerRepository;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DesignerRepository extends MongoRepository<Designer,String>, CustomDesignerRepository
{

}
