package com.mihash.ant_colony.graph;

import com.mihash.ant_colony.dao.EdgeDao;
import com.mihash.ant_colony.dao.NodeDao;
import com.mihash.ant_colony.graph.reader.Reader;


import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;


public class Graph {
    Map<Long, Edge> edgeMap;
    Map<Long, Node> nodeMap;

    public Graph(){
        edgeMap  = Reader.readEdges();
        nodeMap = Reader.readNodes();
        HashMap<String, List<Long>> errors = connect();
        this.clear(errors.get("nodesErrors"),errors.get("edgesErrors"));
        this.calcDistances();
    }


    public Graph(List<EdgeDao> edgeDaos, List<NodeDao> nodeDaos){
        nodeMap = new HashMap<>();
        edgeMap = new HashMap<>();

        for (int i = 0; i < edgeDaos.size(); i++) {
            Edge e = new Edge(edgeDaos.get(i));
            edgeMap.put(e.getId(),e);
        }


        for (int i = 0; i < nodeDaos.size(); i++) {
            Node n = new Node(nodeDaos.get(i));
                nodeMap.put(n.getId(),n);
        }
        HashMap<String, List<Long>> err = this.connect();
        this.calcDistances();

    }

    public Graph(HashMap<Long, Node> nodeMap, HashMap<Long, Edge> edgeMap) {
        this.edgeMap=edgeMap;
        this.nodeMap=nodeMap;
    }

    public Graph(List<Node> nodes, List<Edge> edges,boolean b) {
        this.edgeMap= new HashMap<>();
        this.nodeMap= new HashMap<>();
        for (int i = 0; i < nodes.size(); i++) {
            nodeMap.put(nodes.get(i).getId(),nodes.get(i));
        }
        for (int i = 0; i < edges.size(); i++) {
            edgeMap.put(edges.get(i).getId(),edges.get(i));
        }
        connect();
    }


    public List<Long> getNodesIds() {
        return nodeMap.keySet().stream().collect(Collectors.toList());
    }
    public List<Long> getEdgesIds() {
        return edgeMap.keySet().stream().collect(Collectors.toList());
    }
    public Node getNode(long id){
        return nodeMap.get(id);
    }

    public Edge getEdge(long id) {
        return edgeMap.get(id);
    }


    //-----------------------------------------------------------------------------------------------------------------
    //                                       KONSTRUKCJA GRAFU
    //-----------------------------------------------------------------------------------------------------------------

    public void calcDistances() {
        edgeMap.values().forEach(new Consumer<Edge>() {
            @Override
            public void accept(Edge edge) {
                edge.calcDistance();
            }
        });
    }
    public HashMap<String, List<Long>> connect() {
        Iterator<Edge> edgeIterator = edgeMap.values().stream().iterator();
        List<Long> errorsEdges = new ArrayList<>();
        List<Long> errorsNodes = new ArrayList<>();
        HashMap<String, List<Long>> errors = new HashMap<>();
        while (edgeIterator.hasNext()) {
            Edge edge =edgeIterator.next();
            try {

                Node n1 = nodeMap.get(edge.getFrom_node());
                Node n2 = nodeMap.get(edge.getTo_node());
                boolean null_node_flag=false;
                if (n1==null) {
                    if (!errorsNodes.contains(edge.getFrom_node()))
                        errorsNodes.add(edge.getFrom_node());
                    if (!errorsEdges.contains(edge.getId()))
                        errorsEdges.add(edge.getId());
                    null_node_flag=true;
                }
                if (n2==null){
                    if (!errorsNodes.contains(edge.getTo_node()))
                        errorsNodes.add(edge.getTo_node());
                    if (!errorsEdges.contains(edge.getId()))
                        errorsEdges.add(edge.getId());
                    null_node_flag=true;

                }
                if (null_node_flag)
                        continue;
                n1.addEdge(edge);
                n2.addEdge(edge);
                edge.addNode(n1);
                edge.addNode(n2);
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
        errors.put("nodesErrors",errorsNodes);
        errors.put("edgesErrors",errorsNodes);
        return errors;
    }


    // Usuwanie wybranych krawędzi i węzłów z grafu
    public void clear(List<Long> nodesIds, List<Long> edgesIds){
        if (nodesIds !=null)
            for (int i = 0; i < nodesIds.size(); i++) {
                nodeMap.remove(nodesIds.get(i));
            }
        if (edgesIds !=null)
            for (int i = 0; i < edgesIds.size(); i++) {
                nodeMap.remove(edgesIds.get(i));
            }
        return;
    }


    //-----------------------------------------------------------------------------------------------------------------
    //                                           SPÓJNY KOMPONENT
    //-----------------------------------------------------------------------------------------------------------------

    public HashMap<Integer, List<Long>> consistentComponents(){
        HashMap<Long, Integer> id_to_counter = new HashMap<>();
        nodeMap.values().forEach(new Consumer<Node>() {
            @Override
            public void accept(Node node) {
                id_to_counter.put(node.getId(),0);
            }
        });

        List<Node> nodes = new ArrayList<>(nodeMap.values());
        Stack<Node> stack = new Stack<>();
        int cn=0;
        for (int i = 0; i < nodeMap.size(); i++) {
            if(id_to_counter.get(nodes.get(i).getId())>0)
                continue;
            cn++;
            stack.push(nodes.get(i));
            id_to_counter.replace(nodes.get(i).getId(),cn);
            while(!stack.empty()){
                Node v = stack.pop();
                for (int j = 0; j < v.getNeighbours().size(); j++) {
                    if (id_to_counter.get(v.getNeighbours().get(j).getId())>0){
                        continue;
                    }
                    stack.push(v.getNeighbours().get(j));
                    id_to_counter.replace(v.getNeighbours().get(j).getId(),cn);
                }
            }
        }
        HashMap<Integer, List<Long>> components = new HashMap<>();
        for (int i = 0; i < cn; i++) {
            List<Long> ids=new ArrayList<>();
            components.put(i,ids);
            for (int j = 0; j < nodes.size(); j++) {
                if (id_to_counter.get(nodes.get(j).getId())==i) {
                    ids.add(nodes.get(j).getId());
                }

            }
        }
    return components;
    }

    public Graph createGraphInArea(double lat, double lon, double r) {
        List<Node> nodes = nodeMap.values().stream().filter((node) -> {
            return (Haversine.distance(lat, lon, node.getLat(), node.getLon()) <= r);
        }).collect(Collectors.toList());
        List<Edge> edges = new ArrayList<>();
        for (int i = 0; i < nodes.size(); i++) {
            for (int j = 0; j < nodes.get(i).getEdges().size(); j++) {
                if (! edges.contains(nodes.get(i).getEdges().get(j)) && (nodes.contains(nodes.get(i).getEdges().get(j).getNodes().get(0))&& (nodes.contains(nodes.get(i).getEdges().get(j).getNodes().get(1)))))
                    edges.add(nodes.get(i).getEdges().get(j));
            }
        }

        return new Graph(nodes,edges,true);
    }

    // n - który z kolei komponent 1 - największy 2- drugi największy itp.
    public Graph createGraphFromComponent(int n) {

        HashMap<Integer, List<Long>> consistentComponents = consistentComponents();
        Iterator<Map.Entry<Integer, List<Long>>> components = consistentComponents.entrySet().stream().sorted(new Comparator<Map.Entry<Integer, List<Long>>>() {
            @Override
            public int compare(Map.Entry<Integer, List<Long>> t1, Map.Entry<Integer, List<Long>> t2) {
                return t2.getValue().size() - t1.getValue().size();
            }
        }).iterator();
        List<Long> nodesIds = null;
        for (int i = 1; i <=n; i++) {
            if (components.hasNext())
                nodesIds= components.next().getValue();
        }
        if (nodesIds==null)
            return null;
        List<Long> finalNodesIds = nodesIds;

        HashMap<Long, Node> nodeMapNew = new HashMap<>();
        HashMap<Long, Edge> edgeMapNew = new HashMap<>();

        nodeMap.entrySet().stream().filter(new Predicate<Map.Entry<Long, Node>>() {
            @Override
            public boolean test(Map.Entry<Long, Node> longNodeEntry) {
                return finalNodesIds.contains(longNodeEntry.getKey());
            }
        }).forEach(new Consumer<Map.Entry<Long, Node>>() {
            @Override
            public void accept(Map.Entry<Long, Node> longNodeEntry) {
                nodeMapNew.put(longNodeEntry.getKey(),longNodeEntry.getValue());
                for (int i = 0; i < longNodeEntry.getValue().getEdges().size(); i++) {
                    if (!edgeMapNew.containsKey(longNodeEntry.getValue().getEdges().get(i)))
                        edgeMapNew.put(longNodeEntry.getValue().getEdges().get(i).getId(),longNodeEntry.getValue().getEdges().get(i));
                }
            }
        });
        return new Graph(nodeMapNew,edgeMapNew);

    }


    public <Edges> Collection getEdges() {
        return edgeMap.values();
    }

    public <Nodes> Collection getNodes(){
        return nodeMap.values();
    }

    public double calcHeuristic(Edge edge, long currentNodeid, Long destinationNodeId) throws Exception {
        double dist = edge.getDistance();
        Node current = nodeMap.get(currentNodeid);
        Node next = edge.getSecondNode(current);
        Node dest = nodeMap.get(destinationNodeId);
        if ((next ==null )||(current == null))
            throw new Exception("cannot find a node");

        return 1+1/dist+1/Haversine.distance(next.getLat(),next.getLon(),
                dest.getLat(),dest.getLon());
    }



    public void addFeromone(Long edgeId, double value){
        edgeMap.get(edgeId).setFeromone(edgeMap.get(edgeId).getFeromone()+value);
    }

    public String toGeoString(){
        StringBuilder builder= new StringBuilder();
        builder.append("{\"type\":\"FeatureCollection\", \"features\": [");
        nodeMap.values().forEach(new Consumer<Node>() {
            @Override
            public void accept(Node node) {
                builder.append(node.toGeoString());
                builder.append(",\n");
            }
        });
        edgeMap.values().forEach(new Consumer<Edge>() {
            @Override
            public void accept(Edge edge) {
                builder.append(edge.toGeoString(false));
                builder.append(",\n");
            }
        });
        builder.deleteCharAt(builder.length()-1);
        builder.deleteCharAt(builder.length()-1);

        builder.append("]}");
        return builder.toString();

    }
}
