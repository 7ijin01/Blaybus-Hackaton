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

        if (meet == 1)
        {
            query.addCriteria(Criteria.where("type").is(List.of("대면")));
            Criteria priceCriteria = new Criteria();
            if (maxPrice != null) {
                priceCriteria = priceCriteria.and("price.offline").lte(maxPrice);
            }
            if (minPrice != null) {
                priceCriteria = priceCriteria.and("price.offline").gte(minPrice);
            }
        }
        else if (meet == 0)
        {
            query.addCriteria(Criteria.where("type").is(List.of("비대면")));
            Criteria priceCriteria = new Criteria();
            if (maxPrice != null) {
                priceCriteria = priceCriteria.and("price.online").lte(maxPrice);
            }
            if (minPrice != null) {
                priceCriteria = priceCriteria.and("price.online").gte(minPrice);
            }
        }
        else if (meet == 2)
        {
            query.addCriteria(Criteria.where("type").is(List.of("대면","비대면")));
            Criteria priceCriteria = new Criteria();
            if (maxPrice != null) {
                priceCriteria = priceCriteria.orOperator(
                        Criteria.where("price.online").lte(maxPrice),
                        Criteria.where("price.offline").lte(maxPrice)
                );
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
