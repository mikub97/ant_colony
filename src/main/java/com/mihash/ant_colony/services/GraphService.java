package com.mihash.ant_colony.services;

import com.mihash.ant_colony.ant_colony.AntColony;
import com.mihash.ant_colony.dao.AntColonyDao;
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
        return graphRepository.getGraphDaoById(id);
    }

    @Override
    public int insert(GraphDao graphDao) {
        graphRepository.insert(graphDao);
        return 0;
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
        if (graphDao==null)
            return null;
        List<Long> edgesIDs = graphDao.getEdgeIds();
        List<Long> nodesIDs = graphDao.getNodeIds();
        if (n==1){
            nodesIDs.remove(19354L);
            nodesIDs.remove(57836L);
            nodesIDs.remove(57835L);
            nodesIDs.remove(57834L);
            nodesIDs.remove(63750L);

            nodesIDs.remove(58196L);
            nodesIDs.remove(58197L);
            nodesIDs.remove(58197L);
// 58196  58197  58197
        }
        List<EdgeDao> edgeDaos = new ArrayList<>();
        List<NodeDao> nodeDaos = new ArrayList<>();

        edgesIDs.forEach((Long eID) -> edgeDaos.add(getEdgeByID(eID)));
        nodesIDs.forEach((Long nID) -> nodeDaos.add(getNodeByID(nID)));
        this.graph = new Graph(edgeDaos, nodeDaos);
        return this.graph;
    }

    public AntColonyDao run(AntColonyDao antColonyDao) {
        antColonyDao.setResultEmpty();
        if (this.checkParams(antColonyDao)) {
            AntColony antColony = new AntColony(graph, antColonyDao);
            if (antColonyDao.getNode_from() == antColonyDao.getNode_to()){
                antColonyDao.setDone(true);
                antColonyDao.setBestResult(0.0);
                return antColonyDao;
            }
            antColony.run();

            antColonyDao.setResults(antColony.getBestEdgeHistory(),antColony.getBestNodesHistory(),antColony.getBestResult());
        }
        System.out.println("finished:");
        System.out.println(antColonyDao);

        return antColonyDao;

    }

    private boolean checkParams(AntColonyDao antColonyDao){
        if (this.graph == null)
            return false;
        if (! this.graph.getNodesIds().contains(antColonyDao.getNode_to()))
            return false;
        if (! this.graph.getNodesIds().contains(antColonyDao.getNode_from()))
            return false;
        return true;
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
