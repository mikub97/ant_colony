package com.mihash.ant_colony.repositories;

import com.mihash.ant_colony.dao.NodeDao;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface INodeRepository extends MongoRepository<NodeDao,String> {
     NodeDao getNodeById(long id);

     List<NodeDao> findAllById(Collection<Long> id);


}
