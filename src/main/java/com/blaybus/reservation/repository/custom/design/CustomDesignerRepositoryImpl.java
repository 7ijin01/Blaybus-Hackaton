package com.blaybus.reservation.repository.custom.design;

import com.blaybus.reservation.entity.Designer;

import com.blaybus.reservation.entity.Reservation;
import com.blaybus.reservation.repository.MongoRepositoryUtil;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;
import java.util.Optional;

public class CustomDesignerRepositoryImpl implements CustomDesignerRepository
{
    private final MongoTemplate mongoTemplate;

    public CustomDesignerRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public List<Designer> findAllByPriceAndRegion(Integer maxPrice, List<String> region) {
        Query query=new Query();

        if(maxPrice!=null)
        {
            query.addCriteria(Criteria.where("price_meet").lte(maxPrice));
        }

        if(region!=null && !region.isEmpty())
        {
            query.addCriteria(Criteria.where("region").in(region));
        }
        return mongoTemplate.find(query, Designer.class);
    }

    @Override
    public Designer findOneById(String designerId) {
        Query query=new Query();
        query.addCriteria(Criteria.where("id").is(designerId));
        return mongoTemplate.findOne(query, Designer.class);
    }



}
