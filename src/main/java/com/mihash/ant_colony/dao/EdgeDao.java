package com.mihash.ant_colony.dao;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "edges")
public class EdgeDao {


    @Field("id")
    private long id;

    @Field("the_geom")
    private String coordinates;

    @Field("int_id_fro")
    private long cl_node_id_from;

    @Field("int_id_to")
    private long cl_node_id_to;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }



    public String getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(String coordinates) {
        this.coordinates = coordinates;
    }

    public long getCl_node_id_from() {
        return cl_node_id_from;
    }

    public void setCl_node_id_from(long cl_node_id_from) {
        this.cl_node_id_from = cl_node_id_from;
    }

    public long getCl_node_id_to() {
        return cl_node_id_to;
    }

    public void setCl_node_id_to(long cl_node_id_to) {
        this.cl_node_id_to = cl_node_id_to;
    }
}
