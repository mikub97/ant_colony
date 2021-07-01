package com.mihash.ant_colony;

import com.mihash.ant_colony.dao.GraphDao;
import com.mihash.ant_colony.graph.Edge;
import com.mihash.ant_colony.graph.Graph;
import com.mihash.ant_colony.services.GraphService;
import com.mihash.ant_colony.services.IGraphService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class AntColonyApplicationTests {



	@Test
	void test() {
        Graph graphInArea = new Graph();
        int [] l = new int[]{19936, 19447, 19431, 19911, 19446, 19720, 19799, 19800, 19798, 19733, 19734, 19934, 19935, 54709, 19503, 19504, 19907, 19761, 19488, 19735, 19494, 19479, 19478};
        ArrayList<Long> nodesIds = new ArrayList<>();
        for (int i = 0; i < l.length; i++) {
            nodesIds.add((long) l[i]);
        }
        try {
            List<Edge> edges = graphInArea.edgeHistoryFromNodeHistory(nodesIds);
            System.out.println(edges);
            System.out.println(graphInArea.calcDistanceFromEdgeHistory(edges));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
