package com.mihash.ant_colony.dao;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Document(collection = "graphs")
public class GraphDao {


    @Field("number")
    private int id;

    @Field("nodeIds")
    private List<Long> nodeIds;

    @Field("edgeIds")
    private List<Long> edgeIds;

    @Field("edges_size")
    private int edges_size;

    @Field("nodes_size")
    private int nodes_size;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<Long> getNodeIds() {
        return nodeIds;
    }

    public void setNodeIds(List<Long> nodeIds) {
        this.nodeIds = nodeIds;
    }

    public List<Long> getEdgeIds() {
        return edgeIds;
    }

    public void setEdgeIds(List<Long> edgeIds) {
        this.edgeIds = edgeIds;
    }

    public int getEdges_size() {
        return edges_size;
    }

    public void setEdges_size(int edges_size) {
        this.edges_size = edges_size;
    }

    public int getNodes_size() {
        return nodes_size;
    }

    public void setNodes_size(int nodes_size) {
        this.nodes_size = nodes_size;
    }
}
