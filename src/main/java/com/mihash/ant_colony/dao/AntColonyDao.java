package com.mihash.ant_colony.dao;

import java.util.List;

public class AntColonyDao {
    double alfa;    // priorytet feromonu
    double beta;    // priorytet heurystyki
    double ro; //wsp. parowania
    int antN; // ilość mrówek
    int iterN; // ilość iteracji
    long node_from;
    long node_to;
    private boolean done;
    private double bestResult;
    private List<Long> bestEdgeHistory;
    private List<Long> bestNodesHistory;


    public AntColonyDao(double alfa, double beta, double ro, int antN, int iterN, long node_from, long node_to, boolean done, double bestResult, List<Long> bestEdgeHistory, List<Long> bestNodesHistory) {
        this.alfa = alfa;
        this.beta = beta;
        this.ro = ro;
        this.antN = antN;
        this.iterN = iterN;
        this.node_from = node_from;
        this.node_to = node_to;
        this.done = done;
        this.bestResult = bestResult;
        this.bestEdgeHistory = bestEdgeHistory;
        this.bestNodesHistory = bestNodesHistory;
    }

    public long getNode_from() {
        return node_from;
    }

    public void setNode_from(long node_from) {
        this.node_from = node_from;
    }

    public long getNode_to() {
        return node_to;
    }

    public void setNode_to(long node_to) {
        this.node_to = node_to;
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

    public  int getAntN() {
        return antN;
    }

    public  void setAntN(int antN) {
        this.antN = antN;
    }

    public  int getIterN() {
        return iterN;
    }

    public void setIterN(int iterN) {
        this
                .iterN = iterN;
    }
    public void setResultEmpty(){
        this.done  = false;
        this.bestEdgeHistory =null ;
        this.bestNodesHistory =null ;
        this.bestResult = Double.MAX_VALUE;
    }

    public void setResults(List<Long> bestEdgeHistory, List<Long> bestNodesHistory, double bestResult){
        this.done = true;
        this.bestEdgeHistory =bestEdgeHistory ;
        this.bestNodesHistory =bestNodesHistory ;
        this.bestResult = bestResult;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public double getBestResult() {
        return bestResult;
    }

    public void setBestResult(double bestResult) {
        this.bestResult = bestResult;
    }

    public List<Long> getBestEdgeHistory() {
        return bestEdgeHistory;
    }

    public void setBestEdgeHistory(List<Long> bestEdgeHistory) {
        this.bestEdgeHistory = bestEdgeHistory;
    }

    public List<Long> getBestNodesHistory() {
        return bestNodesHistory;
    }

    public void setBestNodesHistory(List<Long> bestNodesHistory) {
        this.bestNodesHistory = bestNodesHistory;
    }

    @Override
    public String toString() {
        if (this.isDone())
        return "AntColonyParamsDao{" +
                "node_from=" + node_from +
                ", node_to=" + node_to +
                ", alfa=" + alfa +
                ", beta=" + beta +
                ", ro=" + ro +
                ", antN=" + antN +
                ", iterN=" + iterN +
                ", bestNodeHistory=" + this.bestNodesHistory +
                '}';
        return "AntColonyParamsDao{" +
                "node_from=" + node_from +
                ", node_to=" + node_to +
                ", alfa=" + alfa +
                ", beta=" + beta +
                ", ro=" + ro +
                ", antN=" + antN +
                ", iterN=" + iterN +
                '}';
    }

    public boolean isDone() {
        return done;
    }
}
