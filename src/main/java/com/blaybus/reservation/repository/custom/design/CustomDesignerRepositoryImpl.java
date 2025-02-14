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
    public List<Designer> findAllByPriceAndRegion(Integer meet,Integer maxPrice,Integer minPrice, List<String> region) {
        Query query=new Query();
        if(meet==1)
        {
            if(maxPrice!=null)
            {
                query.addCriteria(Criteria.where("price_meet").lte(maxPrice)
                .and("meet").is(1));
            }
            if(minPrice!=null)
            {
                query.addCriteria(Criteria.where("price_meet").gte(minPrice)
                        .and("meet").is(1));
            }
        }
        else if(meet==0)
        {
            if(maxPrice!=null)
            {
                query.addCriteria(Criteria.where("price_not_meet").lte(maxPrice)
                        .and("meet").is(0));
            }
            if(minPrice!=null)
            {
                query.addCriteria(Criteria.where("price_not_meet").gte(minPrice)
                        .and("meet").is(0));
            }
        }
        else {
            if(maxPrice!=null)
            {
                query.addCriteria(Criteria.where("price_meet").lte(maxPrice)
                        .and("meet").is(2));
            }
            if(minPrice!=null)
            {
                query.addCriteria(Criteria.where("price_not_meet").gte(minPrice)
                        .and("meet").is(2));
            }
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
        return MongoRepositoryUtil.findOneById(mongoTemplate, designerId, Designer.class);
    }



}
