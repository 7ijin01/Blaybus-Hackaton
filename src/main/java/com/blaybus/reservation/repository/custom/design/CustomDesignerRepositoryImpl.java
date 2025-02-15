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
    public List<Designer> findAllByPriceAndRegion(Integer meet, Integer maxPrice, Integer minPrice, List<String> region) {
        Query query = new Query();

        if (meet == 1) {
            query.addCriteria(Criteria.where("type").is("대면"));
            if (maxPrice != null || minPrice != null) {
                Criteria priceCriteria = Criteria.where("price.offline");
                if (maxPrice != null) {
                    priceCriteria = priceCriteria.lte(maxPrice);
                }
                if (minPrice != null) {
                    priceCriteria = priceCriteria.gte(minPrice);
                }
                query.addCriteria(priceCriteria);
            }
        } else if (meet == 0) {
            query.addCriteria(Criteria.where("type").is("비대면"));
            if (maxPrice != null || minPrice != null) {
                Criteria priceCriteria = Criteria.where("price.online");
                if (maxPrice != null) {
                    priceCriteria = priceCriteria.lte(maxPrice);
                }
                if (minPrice != null) {
                    priceCriteria = priceCriteria.gte(minPrice);
                }
                query.addCriteria(priceCriteria);
            }
        } else if (meet == 2) {
            query.addCriteria(Criteria.where("type").in("대면", "비대면"));
            if (maxPrice != null || minPrice != null) {
                Criteria priceCriteria = new Criteria().andOperator(
                        Criteria.where("price.offline").lte(maxPrice),
                        Criteria.where("price.online").gte(minPrice)
                );
                query.addCriteria(priceCriteria);
            }
        }

        if (region != null && !region.isEmpty()) {
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
