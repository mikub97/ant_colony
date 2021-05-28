package com.mihash.ant_colony.graph;

import com.mihash.ant_colony.graph.reader.Reader;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertTrue;


public class GraphTest {
    Reader reader;
    Graph graph;
    private HashMap<String, List<Long>> errors;

    public GraphTest(){
        this.reader = new Reader();
        this.graph = new Graph();

    }
    @Test
    public void nodes(){
        Map<Long, Node> nodes = reader.readNodes();
        Iterator<Node> iterator = nodes.values().stream().iterator();

        for (int i = 0; i < 5; i++) {
            System.out.println(iterator.next().toString());
        }
    }
    @Test
    public void edges() {
        Map<Long, Edge> edges = reader.readEdges();
        Iterator<Edge> iterator = edges.values().stream().iterator();
        for (int i = 0; i < 5; i++) {
            System.out.println(iterator.next().toString());
        }
    }
    @Test
    public void createGraph() {
        Node node = graph.getNode(62762);
        Edge edge = graph.getEdge(103199);
        System.out.println("finito");
    }

    @Test
    public void getNeighbours(){
        System.out.print("Test for getNeighbours()");
        Node node = graph.getNode(8236);
        List<Node> neighbours = node.getNeighbours();
        List<Long> true_ids = new ArrayList<>();
        true_ids.add(8505L);
        true_ids.add(8237L);
        true_ids.add(8539L);
        true_ids.add(8526L);
        assertTrue((neighbours.size()==4));
        for (int i = 0; i < neighbours.size(); i++) {
            assertTrue(true_ids.contains(neighbours.get(i).getId()));
        }
        System.out.println(" passed");
    }

    @Test
    public void getSecondNode(){
        System.out.print("Test for getSecondNode()");
        Edge edge = graph.getEdge(15708);
        Node node1 = graph.getNode(8236);
        Node node2 = edge.getSecondNode(node1);
        assertTrue(node2.getId()==8526);
        System.out.println(" passed");
    }


    @Test
    public void consistance(){
        HashMap<Integer, List<Long>> consistentComponents = graph.consistentComponents();
        Iterator<Map.Entry<Integer, List<Long>>> components = consistentComponents.entrySet().stream().sorted(new Comparator<Map.Entry<Integer, List<Long>>>() {
            @Override
            public int compare(Map.Entry<Integer, List<Long>> t1, Map.Entry<Integer, List<Long>> t2) {
                return t2.getValue().size() - t1.getValue().size();
            }
        }).iterator();

        List<Long> c1 = null;
        if (components.hasNext())
            c1=components.next().getValue();
        List<Long> c2 = null;
        if (components.hasNext())
            c2=components.next().getValue();
        List<Long> c3=null;
        if (components.hasNext())
            c3=components.next().getValue();
        System.out.println("finito");
    }

    @Test
    public void createGraphFromComponent(){
        Graph g = graph.createGraphFromComponent(6);
        Edge edge = g.getEdge(86374);
    }

}
