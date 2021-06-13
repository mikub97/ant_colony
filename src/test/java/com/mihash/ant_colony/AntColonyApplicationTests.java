package com.mihash.ant_colony;

import com.mihash.ant_colony.dao.GraphDao;
import com.mihash.ant_colony.graph.Graph;
import com.mihash.ant_colony.services.GraphService;
import com.mihash.ant_colony.services.IGraphService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = GraphService.class)
class AntColonyApplicationTests {



	@Test
	void test() {
        Graph graphInArea = new Graph();
        graphInArea.createGraphInArea(34.1359485,-118.22527989,0.5);
        GraphDao graphDao = new GraphDao();
        graphDao.setEdges_size(graphInArea.getEdgesIds().size());
        graphDao.setNodes_size(graphInArea.getNodesIds().size());
        graphDao.setEdgeIds(graphInArea.getEdgesIds());
        graphDao.setNodeIds(graphInArea.getNodesIds());
        graphDao.setId(1);
        new GraphService().insert(graphDao);
	}

}
