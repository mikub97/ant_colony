package com.mihash.ant_colony.graph;

import com.mihash.ant_colony.dao.NodeDao;

import java.util.ArrayList;
import java.util.List;

public class Node {
    private long id;
    private double lat;
    private double lon;
    private String ZIP_CODE;
    private List<Edge> edges;

    public Node(long id, double lat, double lon, String ZIP_CODE) {
        this.id = id;
        this.lat = lat;
        this.lon = lon;
        this.ZIP_CODE = ZIP_CODE;
        edges = new ArrayList<>();
    }

    public Node(NodeDao nodeDao) {

        this.id = nodeDao.getId();
        this.lat =  Double.valueOf(nodeDao.getLat());;
        this.lon =  Double.valueOf(nodeDao.getLon());;
        this.ZIP_CODE = nodeDao.getZip_code();
        edges = new ArrayList<>();
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    public String getZIP_CODE() {
        return ZIP_CODE;
    }

    public long getId() {
        return id;
    }

    public void addEdge(Edge edge) {
        edges.add(edge);
    }

    public List<Edge> getEdges() {
        return edges;
    }

    public List<Node> getNeighbours(){
        ArrayList<Node> neigh = new ArrayList<>();
        for (int i = 0; i < edges.size(); i++) {
            neigh.add(edges.get(i).getSecondNode(this));
        }
        return neigh;
    }

    @Override
    public String toString() {
        return "Node = {" +
                "id=" + id +
                ", lat=" + lat +
                ", lon=" + lon +
                ", ZIP_CODE='" + ZIP_CODE + '\'' +
                '}';
    }

    public String toGeoString() {
        return ("{\"geometry\":{\"coordinates\":["+lon+","+lat+"],\"type\":\"Point\"},\"type\":\"Feature\",\"properties\":{"+"\"lon\": \""+lon+"\","+"\"lat\":\""+lat+"\","+"\"cl_node_id\":\""+id+"\"}}");
    }
}
