package com.mihash.ant_colony.ant_colony;

import com.mihash.ant_colony.dao.AntColonyDao;
import com.mihash.ant_colony.graph.Edge;
import com.mihash.ant_colony.graph.Graph;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class AntColony {
    private long destinationNode;
    public  double alfa=1;    // priorytet feromonu
    public double beta=1;    // priorytet heurystyki
    public double ro=0.02; //wsp. parowania
    public int antN=10; // ilość mrówek
    public  int iterN=10; // ilość iteracji
    private double bestResult;
    private List<Long> bestEdgeHistory;
    private List<Long> bestNodesHistory;
    private List<Map<Integer,List<Long>>> traces;
    private Graph graph;
    private List<Ant> ants;





    public AntColony(Graph graph, AntColonyDao antColonyDao) {
        this.graph = graph;
        this.ants = new ArrayList<>();
        for (int i = 0; i < antColonyDao.getAntN(); i++) {
            ants.add(new Ant(graph,antColonyDao,i));
        }
        this.destinationNode = antColonyDao.getNode_to();
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

    public int run() {
        for (int i = 0; i < iterN; i++) {
            try {
                iteration(i);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        for (int i = 0; i < ants.size(); i++) {
            System.out.println(ants.get(i).describe());
        }
        return 0;
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

    public boolean areAntsFinished() {
        for (int i = 0; i < ants.size(); i++) {
            if (ants.get(i).isAtWork())
                return false;
        }
        return true;
    }
//    public int iteration(int iter) throws Exception {
//        resetAnts(iter);
//        while (!areAntsFinished()) {
//            for (int i = 0; i < ants.size(); i++) {
//                Ant currentAnt = ants.get(i);
//                    if (currentAnt.isAtWork()) {
//                        switch (currentAnt.move()) {
//                            case 1:
//                                currentAnt.setAtWork(false);
//                                continue;
//                            case -1:
//                                currentAnt.reset();
//                                continue;
//                    }
//                }
//            }
//        }
//        updateBestResult();
//        vaporizeFeromone();
//
//        return 0;
//
//    }
    public int iteration(int iter) throws Exception {
        resetAnts(iter);
        for (int i = 0; i < ants.size(); i++) {
            while (ants.get(i).isAtWork()){
                    switch (ants.get(i).move()) {
                        case 1:
                            ants.get(i).setAtWork(false);
                            continue;
                        case -1:
                            ants.get(i).deadEndReset();
                            continue;
                }
            }
        }

        updateBestResult();
        vaporizeFeromone();
        updateFeromone();
        return 0;
    }

    private void resetAnts(int iter) {
        for (int i = 0; i < ants.size(); i++) {
            ants.get(i).reset(iter);
        }
    }

    private void updateFeromone(){
        for (int i = 0; i < ants.size(); i++) {
            List<Long> nodeHistory = ants.get(i).getNodeHistory();
            if (nodeHistory.get(nodeHistory.size()-1)==destinationNode){
                List<Long> edgeHistory = ants.get(i).getEdgeHistory();
                for (int j = 0; j < edgeHistory.size(); j++) {
                    this.graph.addFeromone(edgeHistory.get(j),1*bestResult/ants.get(i).getRouteLength());
                }
            }
        }
    }


    private void updateBestResult() {
        for (int i = 0; i < ants.size(); i++) {
            double length = ants.get(i).getRouteLength();
            if ((length < this.bestResult)) {
                this.bestResult = length;
                this.bestEdgeHistory = new ArrayList<>(ants.get(i).getEdgeHistory());
                this.bestNodesHistory = new ArrayList<>(ants.get(i).getNodeHistory());
            }
        }
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

    public double getAlfa() {
        return alfa;
    }

    public void setAlfa(double alfa) {
        this.alfa = alfa;
    }

    public double getBeta() {
        return beta;
    }

    public void setBeta(double beta) {
        this.beta = beta;
    }

    public double getRo() {
        return ro;
    }

    public void setRo(double ro) {
        this.ro = ro;
    }

    public int getAntN() {
        return antN;
    }

    public void setAntN(int antN) {
        this.antN = antN;
    }

    public int getIterN() {
        return iterN;
    }

    public void setIterN(int iterN) {
        this.iterN = iterN;
    }
}
