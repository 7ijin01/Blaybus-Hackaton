package com.blaybus.reservation.repository;

import com.blaybus.reservation.entity.Designer;
import com.blaybus.reservation.repository.custom.design.CustomDesignerRepository;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DesignerRepository extends MongoRepository<Designer,String>, CustomDesignerRepository
{

}
