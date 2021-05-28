package com.mihash.ant_colony.ant_colony;


import com.mihash.ant_colony.graph.Edge;
import com.mihash.ant_colony.graph.Graph;
import com.mihash.ant_colony.graph.Node;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Ant {
    private Node current;
    private List<Long> nodesHistory;
    private List<Long> edgesHistory;
    private Long destinationNodeId;
    private double distanceSum;
    private Long startNodeId;
    private boolean atWork;
    private Graph graph;
    private int id;

    public Ant(Graph graph, Long startNodeId,Long destinationNodeId, int id) {
        this.graph=graph;
        this.current = graph.getNode(startNodeId);
        this.nodesHistory = new ArrayList<>();
        this.edgesHistory = new ArrayList<>();
        this.destinationNodeId = destinationNodeId;
        this.startNodeId = startNodeId;
        this.nodesHistory.add(startNodeId);
        this.distanceSum=0.0;
        this.id=id;
        atWork=true;
    }

    public boolean isAtWork() {
        return atWork;
    }

    public void setAtWork(boolean atWork) {
        this.atWork = atWork;
    }

    public double getRouteLength(){
        double res = 0.0;
        for (int i = 0; i < edgesHistory.size(); i++) {
            res+=graph.getEdge(edgesHistory.get(i)).getLenght();

        }
        return res;
    }

    public Node getCurrent(){
        return current;
    }

    public void reset(){
        this.current = graph.getNode(startNodeId);
        this.nodesHistory = new ArrayList<>();
        this.edgesHistory = new ArrayList<>();
        this.nodesHistory.add(startNodeId);
        this.distanceSum=0.0;
        this.atWork=true;
    }

    public int move() throws Exception {
        if (current.getId()==destinationNodeId)
            return 1;
        Edge selectedEdge = selectNextEdge();
        if (selectedEdge==null) {
            //"Error ant at dead end"
            return -1;
        }
        distanceSum+=selectedEdge.getDistance();
        edgesHistory.add(selectedEdge.getId());
        current=selectedEdge.getSecondNode(current);
        nodesHistory.add(current.getId());
        return 0; // nie znalazłem
    }
    public String getNodeHistoryString(int iter){
        StringBuilder builder = new StringBuilder();
        builder.append("\nNODE HISTORY FOR ANT "+id+" (iteration "+iter+")"+"\n");
        for (int i = 0; i < nodesHistory.size(); i++) {
            builder.append("Node "+nodesHistory.get(i));
            if (i<nodesHistory.size()-1)
                builder.append("->");
        }
        return builder.toString();
    }

    private Edge selectNextEdge() throws Exception {
        List<Edge> edges = current.getEdges();
        edges = edges.stream().filter(new Predicate<Edge>() {
            @Override
            public boolean test(Edge edge) {
                Node sec = edge.getSecondNode(current);
                return !nodesHistory.contains(sec.getId());
            }
        }).collect(Collectors.toList());
        if (edges.size()==0)
            return null;
        else if (edges.size()==1)
            return edges.get(0);
        List<Double> odds = new ArrayList<>();
        double sum=0.0;
        for (int i = 0; i < edges.size(); i++) {
            double v = Math.pow(edges.get(i).getFeromone(),AntColony.alfa) * Math.pow(graph.calcHeuristic(edges.get(i),current.getId(),destinationNodeId),AntColony.beta);
            odds.add(v);
            sum+=v;
        }
        for (int i = 0; i < odds.size(); i++) {
            odds.set(i,odds.get(i)/sum);
        }
        return roulette(zipToMap(edges,odds));

    }

    private Edge roulette(Map<Edge,Double> map) {
        Iterator<Map.Entry<Edge, Double>> iterator = map.entrySet().stream().sorted(Map.Entry.<Edge, Double>comparingByValue()).iterator();

//        List<Map.Entry<Edge, Double>> entryList = map.entrySet().stream().sorted(Map.Entry.<Edge, Double>comparingByValue()).collect(Collectors.toList());

        double r = new Random().nextDouble();
        Map.Entry<Edge, Double> last=null;
        Map.Entry<Edge, Double> current=null;

        if (iterator.hasNext())
            last=iterator.next();
        if (iterator.hasNext())
            current=iterator.next();

        if (r<=last.getValue())
            return last.getKey();
        while (iterator.hasNext()) {
            if ((last.getValue()<=r)&&(current.getValue()>=r)){
                return current.getKey();
            }
            last=current;
            current=iterator.next();
        }
        return current.getKey();

    }

    public static <K, V> Map<K, V> zipToMap(List<K> keys, List<V> values) {
        Iterator<K> keyIter = keys.iterator();
        Iterator<V> valIter = values.iterator();
        return IntStream.range(0, keys.size()).boxed()
                .collect(Collectors.toMap(_i -> keyIter.next(), _i -> valIter.next()));
    }
    public int getId(){
        return id;
    }

    public List<Long> getEdgeHistory() {
        return this.edgesHistory;
    }

    public List<Long> getNodeHistory() {
        return this.nodesHistory;
    }
}
