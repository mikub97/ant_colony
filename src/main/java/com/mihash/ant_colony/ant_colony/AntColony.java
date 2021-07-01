package com.mihash.ant_colony.ant_colony;

import com.mihash.ant_colony.dao.AntColonyDao;
import com.mihash.ant_colony.graph.Edge;
import com.mihash.ant_colony.graph.Graph;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class AntColony {
    private long destinationNode;
    public  double alfa;    // priorytet feromonu
    public double beta;    // priorytet heurystyki
    public double ro; //wsp. parowania
    public int antN; // ilość mrówek
    public  int iterN; // ilość iteracji
    public double q1;
    public double q2;
    private double bestResult;
    private List<Long> bestEdgeHistory;
    private List<Long> bestNodesHistory;
    private List<Map<Integer,List<Long>>> traces;
    private Graph graph;
    private List<Ant> ants;

    public AntColony(Graph graph, AntColonyDao antColonyDao) {
        this.graph = graph;
        this.antN = antColonyDao.getAntN();
        this.ants = new ArrayList<>();
        for (int i = 0; i < antN; i++) {
            ants.add(new Ant(graph,antColonyDao,i));
        }
        this.alfa=antColonyDao.getAlfa();
        this.beta=antColonyDao.getBeta();
        this.ro = antColonyDao.getRo();
        this.q1=antColonyDao.getQ1();
        this.q2=antColonyDao.getQ2();
        this.iterN = antColonyDao.getIterN();
        this.destinationNode = antColonyDao.getNode_to();
        this.bestResult=Double.MAX_VALUE;
        this.bestEdgeHistory = new ArrayList<>();
        this.bestNodesHistory = new ArrayList<>();
        this.traces = new ArrayList<>();
    }


    public int run() {
        bestResult=Double.MAX_VALUE;
        bestEdgeHistory=new ArrayList<>();
        bestNodesHistory = new ArrayList<>();
        clearFeromone();
        for (int i = 0; i < iterN; i++) {
            try {
                iteration(i);
                for (Ant ant : ants) {
                    System.out.println(ant.describeIter(i));

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            FileWriter writer = new FileWriter(new File("res.txt"));
            for (int i = 0; i < iterN; i++) {
                for (int j = 0; j < ants.size(); j++) {
                    writer.write(ants.get(j).describeIter(i));
                }
            }
            writer.write("\n\t Best result : " + bestNodesHistory.toString() + " len = " + bestResult);
            writer.close();
        }catch (Exception e){
            e.printStackTrace();
        }
            return 0;
    }



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
    private void clearFeromone() {
        graph.getEdges().forEach(new Consumer<Edge>() {
            @Override
            public void accept(Edge o) {
                o.setFeromone(q1);
            }
        });
    }

    public void vaporizeFeromone(){
        graph.getEdges().forEach(new Consumer<Edge>() {
            @Override
            public void accept(Edge edge) {
//                if (edge.getFeromone()>q1)
                edge.setFeromone((1-ro)*edge.getFeromone());
            }
        });
    }

    private void resetAnts(int iter) {
        for (int i = 0; i < ants.size(); i++) {
            ants.get(i).reset(iter);
        }
    }

    private void updateFeromone(){
        for (int i = 0; i < ants.size(); i++) {
            List<Long> nodeHistory = ants.get(i).getNodeHistory();
            if (nodeHistory.get(nodeHistory.size()-1)==destinationNode){ // trasa poprawna
                List<Long> edgeHistory = ants.get(i).getEdgeHistory();
                for (int j = 0; j < edgeHistory.size(); j++) {
                    this.graph.addFeromone(edgeHistory.get(j),q2/ants.get(i).getRouteLength());
//                    this.graph.addFeromone(edgeHistory.get(j),q2);

                }
            }
        }
    }


    private void updateBestResult() {
        int k = -1;
        for (int i = 0; i < ants.size(); i++) {
            double length = ants.get(i).getRouteLength();
            if ((length < this.bestResult)) {
                this.bestResult = length;
                this.bestEdgeHistory = new ArrayList<>(ants.get(i).getEdgeHistory());
                this.bestNodesHistory = new ArrayList<>(ants.get(i).getNodeHistory());
                k = i;
            }
        }

        if (k >= 0) {
            for (Long edgeID : ants.get(k).getEdgeHistory()) {
                this.graph.getEdge(edgeID).setFeromone(this.graph.getEdge(edgeID).getFeromone() + q2);
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

    private void saveBestToFile() throws IOException {
        FileWriter writer = new FileWriter(new File("res.txt"));
        writer.write("Best edge result\n");
        for (int i = 0; i < bestEdgeHistory.size(); i++) {
            writer.write(String.valueOf(bestEdgeHistory.get(i))+";");
        }
        writer.write("\n");
        writer.write("Best node result\n");
        for (int i = 0; i < bestEdgeHistory.size(); i++) {
            writer.write(String.valueOf(bestNodesHistory.get(i))+";");
        }
        writer.write("\n");
        writer.write("length\n"+bestResult);
        writer.close();

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

}
