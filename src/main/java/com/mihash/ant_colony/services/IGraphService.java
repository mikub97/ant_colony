package com.mihash.ant_colony.services;

import com.mihash.ant_colony.dao.AntColonyDao;
import com.mihash.ant_colony.dao.EdgeDao;
import com.mihash.ant_colony.dao.GraphDao;
import com.mihash.ant_colony.dao.NodeDao;
import com.mihash.ant_colony.graph.Graph;

import java.util.Collection;
import java.util.List;

public interface IGraphService {

    List<GraphDao> listAllGraphs();

    GraphDao getGraphByID(int id);

    int insert(GraphDao graphDao);

    List<GraphDao> findAllGraphsById(Collection<Integer> ids);

    Graph createGraphFromId(int n);

    List<EdgeDao> listAllEdges();

    EdgeDao getEdgeByID(long id);

    List<EdgeDao> findAllEdgesById(Collection<Long> ids);


    List<NodeDao> listAllNodes();

    NodeDao getNodeByID(long id);

    List<NodeDao> findAllNodesById(Collection<Long> ids);

    AntColonyDao run(AntColonyDao antColonyDao);

    String clear();
}
