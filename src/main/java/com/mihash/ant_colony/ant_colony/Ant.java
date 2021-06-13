package com.mihash.ant_colony.ant_colony;


import com.mihash.ant_colony.dao.AntColonyDao;
import com.mihash.ant_colony.graph.Edge;
import com.mihash.ant_colony.graph.Graph;
import com.mihash.ant_colony.graph.Node;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Ant {
    private AntColonyDao antColonyDao;
    private Node current;
    private List<List<List<Long>>> traces;
    private List<List<Long>> trace_from_iter;
    private List<Long> nodesHistory;
    private List<Long> edgesHistory;
    private Long destinationNodeId;
    private Long startNodeId;
    private boolean atWork;
    private Graph graph;
    private int id;
    private int iter;
    private double distanceSum;

    public Ant(Graph graph, AntColonyDao antColonyDao, int id) {
        this.graph=graph;
        this.antColonyDao = antColonyDao;
        this.current = graph.getNode(antColonyDao.getNode_from());
        this.nodesHistory = new ArrayList<>();
        this.edgesHistory = new ArrayList<>();
        this.destinationNodeId = antColonyDao.getNode_to();
        this.startNodeId = antColonyDao.getNode_from();
        this.nodesHistory.add(startNodeId);
        this.distanceSum=0.0;
        this.id=id;
        this.traces=new ArrayList<>();
        this.trace_from_iter=new ArrayList<>();
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

    public void reset(int iter){
        if (iter>0) {
            this.traces.add(new ArrayList<>(trace_from_iter));
        }
        this.trace_from_iter=new ArrayList<>();
        this.current = graph.getNode(startNodeId);
        this.nodesHistory = new ArrayList<>();
        this.edgesHistory = new ArrayList<>();
        this.nodesHistory.add(startNodeId);
        this.distanceSum=0.0;
        this.atWork=true;
    }
    //Uruchomione gdy mrówka jest resetowana, bo weszła w ślepą uliczkę,
    public void deadEndReset(){
        this.edgesHistory.add(-0L);
        this.trace_from_iter.add(new ArrayList(nodesHistory));
        this.current = graph.getNode(startNodeId);
        this.nodesHistory = new ArrayList<>();
        this.edgesHistory = new ArrayList<>();
        this.nodesHistory.add(startNodeId);
        this.distanceSum=0.0;
        this.atWork=true;
    }

//    return 1, when arrived
//    return 0 if not yet
//   return -1 if in dead end

    public int move() throws Exception {
        if (current.getId()==destinationNodeId) {
            this.trace_from_iter.add(this.nodesHistory);
            return 1;
        }
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
            double v = Math.pow(edges.get(i).getFeromone(),antColonyDao.getAlfa()) + Math.pow(graph.calcHeuristic(edges.get(i),current.getId(),destinationNodeId),antColonyDao.getBeta());
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

    public String describe() {
        StringBuilder builder = new StringBuilder();
        builder.append("Ant nr."+this.id+"\n");
        for (int i = 0; i < traces.size(); i++) {
            builder.append("\tIteration nr."+i+"\n");
            for (int j = 0; j < traces.get(i).size(); j++) {
                builder.append("\t\t"+traces.get(i).get(j).toString()+"\n");
            }
        }
        return builder.toString();

    }
}
