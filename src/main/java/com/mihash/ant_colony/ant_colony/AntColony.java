package com.mihash.ant_colony.ant_colony;

import com.mihash.ant_colony.graph.Edge;
import com.mihash.ant_colony.graph.Graph;
import com.mihash.ant_colony.graph.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class AntColony {
    public static double alfa=1;    // priorytet feromonu
    public static double beta=1;    // priorytet heurystyki
    public static double ro=0.02; //wsp. parowania
    public static int antN=10; // ilość mrówek
    public static int iterN=10; // ilość iteracji
    private double bestResult;
    private List<Long> bestEdgeHistory;
    private List<Long> bestNodesHistory;
    private List<Map<Integer,List<Long>>> traces;
    private Graph graph;
    private List<Ant> ants;


    public AntColony(Graph graph, Node start, Node end) {
        this.graph = graph;
        this.ants = new ArrayList<>();
        for (int i = 0; i < antN; i++) {
            ants.add(new Ant(graph,start.getId(), end.getId(),i));
        }
        this.bestResult=Double.MAX_VALUE;
        this.bestEdgeHistory = new ArrayList<>();
        this.bestNodesHistory = new ArrayList<>();
        this.traces = new ArrayList<>();

    }

    public AntColony(Graph graph,long startId,long endId) {
        this.graph = graph;
        this.ants = new ArrayList<>();
        for (int i = 0; i < antN; i++) {
            ants.add(new Ant(graph,startId, endId,i));
        }
        this.bestResult=Double.MAX_VALUE;
        this.bestEdgeHistory = new ArrayList<>();
        this.bestNodesHistory = new ArrayList<>();
        this.traces = new ArrayList<>();

    }

    public void vaporizeFeromone(){
        graph.getEdges().forEach(new Consumer<Edge>() {
            @Override
            public void accept(Edge edge) {
                edge.setFeromone((1-ro)*edge.getFeromone());
            }
        });
    }

    public String run() {
        for (int i = 0; i < iterN; i++) {
            try {
                iteration(i);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return bestNodesHistory.toString();
    }

    public String resultsToGeoString(){

        StringBuilder builder= new StringBuilder();
        builder.append("{\"type\":\"FeatureCollection\", \"features\": [");
       bestEdgeHistory.forEach(new Consumer<Long>() {
            @Override
            public void accept(Long edgeId) {
                Edge edge = graph.getEdge(edgeId);
                builder.append(edge.toGeoString(true));
                builder.append(",\n");
            }
        });
        builder.deleteCharAt(builder.length()-1);
        builder.deleteCharAt(builder.length()-1);

        builder.append("]}");
        return builder.toString();

    }

    public boolean areAntsWorking() {
        for (int i = 0; i < ants.size(); i++) {
            if (ants.get(i).isAtWork())
                return true;
        }
        return false;
    }
    public int iteration(int iter) throws Exception {
        while (areAntsWorking()) {
            for (int i = 0; i < ants.size(); i++) {
                Ant currentAnt = ants.get(i);
                    if (currentAnt.isAtWork()) {
                        switch (currentAnt.move()) {
                            case 1:
                                double length = currentAnt.getRouteLength();
                                if (length < this.bestResult) {
                                    this.bestResult = length;
                                    this.bestEdgeHistory = currentAnt.getEdgeHistory();
                                    this.bestNodesHistory = currentAnt.getNodeHistory();
                                }
                                currentAnt.setAtWork(false);
                                continue;
                            case -1:
                                currentAnt.reset();
                                continue;

                    }
                }
            }
        }
        vaporizeFeromone();
        double sum=0.0;
        HashMap<Integer, List<Long>> trace = new HashMap<>();
        for (int i = 0; i < ants.size(); i++) {
            trace.put(ants.get(i).getId(),ants.get(i).getNodeHistory());
            ants.get(i).reset();
            graph.updateFeromone(ants.get(i).getEdgeHistory(),
                    ants.get(i).getRouteLength()/bestResult);
        }
        traces.add(trace);

        return 0;

    }

    public double getBestResult() {
        return bestResult;
    }

    public List<Long> getBestEdgeHistory() {
        return bestEdgeHistory;
    }

    public List<Long> getBestNodesHistory() {
        return bestNodesHistory;
    }

    public String getBestNodesHistoryString() {
        StringBuilder builder=new StringBuilder();
        for (int i = 0; i < bestNodesHistory.size(); i++) {
            builder.append(bestNodesHistory.get(i));
            if (i<bestNodesHistory.size()-1)
                builder.append("->");
        }
        return builder.toString();
    }
}
