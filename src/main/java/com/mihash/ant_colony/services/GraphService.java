package com.mihash.ant_colony.services;

import com.mihash.ant_colony.ant_colony.AntColony;
import com.mihash.ant_colony.dao.EdgeDao;
import com.mihash.ant_colony.dao.GraphDao;
import com.mihash.ant_colony.dao.NodeDao;
import com.mihash.ant_colony.graph.Graph;
import com.mihash.ant_colony.repositories.IEdgeRepository;
import com.mihash.ant_colony.repositories.IGraphRepository;
import com.mihash.ant_colony.repositories.INodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Component
public class GraphService implements IGraphService {

    @Autowired
    private IGraphRepository graphRepository;

    @Autowired
    private INodeRepository nodeRepository;

    @Autowired
    private IEdgeRepository edgeRepository;

    private Graph graph;

    @Override
    public List<GraphDao> listAllGraphs() {
        List<GraphDao> graphs = new ArrayList<>();
        graphRepository.findAll().forEach(graphs::add);
        return graphs;
    }



    @Override
    public GraphDao getGraphByID(int id) {
        return graphRepository.getGraphById(id);
    }


    @Override
    public List<GraphDao> findAllGraphsById(Collection<Integer> ids) {
        List<GraphDao> graphs = listAllGraphs();
        return graphs.stream().filter(new Predicate<GraphDao>() {
            @Override
            public boolean test(GraphDao graph) {
                return ids.contains(graph.getId());
            }
        }).collect(Collectors.toList());
    }

    @Override
    public Graph createGraphFromId(int n) {
        GraphDao graphDao = getGraphByID(n);
        List<Long> edgesIDs = graphDao.getEdgeIds();
        List<Long> nodesIDs = graphDao.getNodeIds();

        List<EdgeDao> edgeDaos = new ArrayList<>();
        List<NodeDao> nodeDaos = new ArrayList<>();

        edgesIDs.forEach((Long eID) -> edgeDaos.add(getEdgeByID(eID)));
        nodesIDs.forEach((Long nID) -> nodeDaos.add(getNodeByID(nID)));
        this.graph = new Graph(edgeDaos, nodeDaos);
        return this.graph;
    }

    public String run(int from, int to) {
        if (this.graph == null) {
            return null;
        }
        AntColony antColony = new AntColony(graph, from, to);
        antColony.run();
        System.out.println(antColony.resultsToGeoString());
        return antColony.resultsToGeoString();
    }

    @Override
    public String clear() {
        this.graph=null;
        return "done";
    }

    @Override
    public List<EdgeDao> listAllEdges() {
        return edgeRepository.findAll();
    }

    @Override
    public EdgeDao getEdgeByID(long id) {
        return edgeRepository.getEdgeById(id);
    }

    @Override
    public List<EdgeDao> findAllEdgesById(Collection<Long> ids) {
        List<EdgeDao> edges = new ArrayList<>();
        ids.forEach((Long e)->edges.add(edgeRepository.getEdgeById(e)));
        return edges;
    }

    @Override
    public List<NodeDao> listAllNodes() {
        List<NodeDao> nodes = new ArrayList<>();
        nodeRepository.findAll().forEach(nodes::add);
        return nodes;
    }

    @Override
    public NodeDao getNodeByID(long id) {
        return nodeRepository.getNodeById(id);
    }

    @Override
    public List<NodeDao> findAllNodesById(Collection<Long> ids) {
        List<NodeDao> nodes = new ArrayList<>();
        ids.forEach((Long n)-> nodes.add(nodeRepository.getNodeById(n)));
        return nodes;
    }


}
