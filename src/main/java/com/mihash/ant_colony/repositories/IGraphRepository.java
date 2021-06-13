package com.mihash.ant_colony.repositories;

import com.mihash.ant_colony.dao.GraphDao;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IGraphRepository extends MongoRepository<GraphDao,String> {
     @Override
     Iterable<GraphDao> findAllById(Iterable<String> iterable);

     GraphDao getGraphDaoById(int id);
     @Override
     <S extends GraphDao> S insert(S s);
}
