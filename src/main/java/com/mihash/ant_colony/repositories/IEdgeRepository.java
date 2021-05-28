package com.mihash.ant_colony.repositories;

import com.mihash.ant_colony.dao.EdgeDao;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IEdgeRepository extends MongoRepository<EdgeDao,String> {

     EdgeDao getEdgeById(long id);

     List<EdgeDao> findAllById(Iterable<String> id);


}
