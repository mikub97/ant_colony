package com.mihash.ant_colony.graph;


import com.mihash.ant_colony.dao.EdgeDao;

import java.util.ArrayList;
import java.util.List;

public class Edge {
    private long id;
    private long from_node;
    private long to_node;
    private String street;
    private List<Node> nodes;
    private double lenght;
    private double feromone;
    List<double[]> the_geom;

    public Edge(EdgeDao edgeDao) {
        this.id = edgeDao.getId();
        this.from_node =  edgeDao.getCl_node_id_from();
        this.to_node =  edgeDao.getCl_node_id_to();
        this.nodes=new ArrayList<>();
        this.the_geom=new ArrayList<double[]>();
        String [] corrs = edgeDao.getCoordinates().replaceAll("\\[","").replaceAll("\\]","").replaceAll(" ","").split(",");
        for (int i = 0; i < corrs.length; i=i+2) {
            this.the_geom.add(new double[]{Double.valueOf(corrs[i]),Double.valueOf(corrs[i+1])});

        }
        this.lenght=0;
        this.feromone=0;
    }
    public Edge(long id, long from_node, long to_node, String street,String the_geom) {
        this.id = id;
        this.from_node = from_node;
        this.to_node = to_node;
        this.street = street;
        this.nodes=new ArrayList<>();
        this.the_geom=new ArrayList<>();
        String [] corrs = the_geom.replaceAll("\\[","").replaceAll("\\]","").replaceAll(" ","").split(",");
        for (int i = 0; i < corrs.length; i=i+2) {
            this.the_geom.add(new double[]{Double.valueOf(corrs[i]),Double.valueOf(corrs[i+1])});

        }
        this.lenght=0;
        this.feromone=1.0;
    }

    public void addNode(Node node) {
        nodes.add(node);
    }

    public long getId() {
        return id;
    }

    public long getFrom_node() {
        return from_node;
    }

    public long getTo_node() {
        return to_node;
    }

    public List<Node> getNodes(){
        return nodes;
    }
    public String getStreet() {
        return street;
    }

    @Override
    public String toString() {
        return "Edge{" +
                "id=" + id +
                ", street='" + street + '\'' +
                '}';
    }

    public Node getSecondNode(Node node) {
        Node n1,n2;
        if (nodes.size()==2) {
            n1 = nodes.get(0);
            n2 = nodes.get(1);
        }
        else return null;

        if (n1.getId()==node.getId())
            return n2;
        else if (n2.getId()==node.getId())
            return n1;
        else
            return null;

    }

    public void calcDistance(){
        this.lenght=distanceInKmBetweenEarthCoordinates();
    }

    public double distanceInKmBetweenEarthCoordinates() {
        double distance=0.0;
        for (int i = 0; i < the_geom.size()-1; i++) {
            distance+=Haversine.distance(the_geom.get(i)[0],the_geom.get(i)[1],the_geom.get(i+1)[0],the_geom.get(i+1)[1]);
        }
        return distance;
    }

    public double distance1andLast() {
        double distance=Haversine.distance(the_geom.get(0)[0],the_geom.get(0)[1],the_geom.get(the_geom.size()-1)[0],the_geom.get(the_geom.size()-1)[1]);


        return distance;
    }
    public double getHeuristic(){
        return 1/lenght;
    }

    public void setFeromone(double v) {
        this.feromone=v;
    }

    public double getLenght() {
        return lenght;
    }

    public double getFeromone() {
        return feromone;
    }

    public double getDistance() {
        return this.lenght;
    }


    //              {"geometry":{"coordinates":[[[-118.22232560182296,33.783683556011646],[-118.22221076551075,33.78366482375996],[-118.22214265840326,33.783652785415995],[-118.22207485748203,33.78363976112049],[-118.22200734006981,33.78362574821128],[-118.22194012455606,33.783610759276016],[-118.2218732346708,33.78359478525643],[-118.2218066877451,33.78357784685582],[-118.22174049886968,33.783559934130395],[-118.2216747048134,33.783541069550715],[-118.221608365503,33.783520951941355],[-118.22158627010566,33.78351249860804],[-118.22158035935442,33.783509724730145]]],"type":"MultiLineString"},"type":"Feature","properties":{"mapsheet":"033B217","stsfx_a":null,"zip_l":"90744","adlt":"3499","adrt":"0","tooltip":"I ST\\nStreet Designation: Local Street - Standard","type":"70","tdir":"E","stnum":"5726","stsfx":"ST","adlf":"3401","street_des":"Local Street - Standard","int_id_fro":"3781","assetid":"163219","stname_a":null,"int_id_to":"63986","sect_id":"2725000","modified":"0","sfxdir":null,"adrf":"0","id":"98902","zip_r":"90744","street_d_1":"Local Street - Standard","stname":"I"}}
    public String toGeoString(boolean isResult) {
        StringBuilder builder = new StringBuilder ();
        builder.append("{\"geometry\":{\"coordinates\":[[");
        for (int i = 0; i < the_geom.size(); i++) {
            builder.append("["+the_geom.get(i)[0]+","+the_geom.get(i)[1]+"]");
            if (i!=the_geom.size()-1)
                builder.append(",");
        }
        if (isResult) {
            builder.append("]],\"type\":\"MultiLineString\"},\"type\":\"Feature\",\"properties\":{\"id\":\"" + id + "\"" +
                    ", \"isResult\": \"True\"}}");
        }
        else{
            builder.append("]],\"type\":\"MultiLineString\"},\"type\":\"Feature\",\"properties\":{\"id\":\"" + id + "\"}}");
        }
        return builder.toString();

    }


    public double calcHeuristic() {
        return 1/lenght;
    }
    public double calcAim(double alfa, double beta){
        return Math.pow(this.getFeromone(),alfa) * Math.pow(this.calcHeuristic(),beta);

    }
}
