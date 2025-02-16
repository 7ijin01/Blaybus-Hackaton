package com.blaybus.domain.reservation.repository.custom.design;

import com.blaybus.domain.reservation.entity.Designer;

import com.blaybus.domain.reservation.repository.MongoRepositoryUtil;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.ArrayList;
import java.util.List;

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

            query.addCriteria(Criteria.where("type").is(List.of("대면")));
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

            query.addCriteria(Criteria.where("type").is(List.of("비대면")));

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
        }
        else if (meet == 2) {
            query.addCriteria(Criteria.where("type").all(List.of("대면", "비대면")));

            List<Criteria> priceCriteriaList = new ArrayList<>();

            if (minPrice != null) {
                priceCriteriaList.add(Criteria.where("price.online").gte(minPrice));
            }
            if (maxPrice != null) {
                priceCriteriaList.add(Criteria.where("price.offline").lte(maxPrice));
            }

            if (!priceCriteriaList.isEmpty()) {
                query.addCriteria(new Criteria().andOperator(priceCriteriaList.toArray(new Criteria[0])));
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
