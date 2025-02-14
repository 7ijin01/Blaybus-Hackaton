package com.blaybus.repository;

import com.blaybus.entity.DesignerEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DesignerRepository extends MongoRepository<DesignerEntity, String> {
}
