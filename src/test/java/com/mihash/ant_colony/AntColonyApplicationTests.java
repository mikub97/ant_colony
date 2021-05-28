package com.mihash.ant_colony;

import com.mihash.ant_colony.graph.Graph;
import org.junit.jupiter.api.Test;


class AntColonyApplicationTests {


	@Test
	void test() {
		Graph g = new Graph();
		Graph g5 = g.createGraphFromComponent(5);
		System.out.println(g5.toGeoString());
	}

}
