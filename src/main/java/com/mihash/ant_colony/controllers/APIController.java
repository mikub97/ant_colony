package com.mihash.ant_colony.controllers;

import com.mihash.ant_colony.dao.AntColonyDao;
import com.mihash.ant_colony.graph.Graph;
import com.mihash.ant_colony.services.IGraphService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(maxAge = 3600)
@ComponentScan("com.mihash.ant_colony.services")
public class APIController {

    private IGraphService graphService;

    @Autowired
    public void setEdgesService( IGraphService graphService){
        this.graphService = graphService;
    }


//    @RequestMapping(path = "/run/{from}&{to}")
//    public ResponseEntity run(@PathVariable int from, @PathVariable int to) {
//        System.out.println("Running algorithm from node "+from +" to node " + to);
//        String res = graphService.run(from,to);
//        if (res==null)
//            return new ResponseEntity("No graph",HttpStatus.CONFLICT);
//
//        return new ResponseEntity(res, HttpStatus.OK);
//
//    }
    @PostMapping("/test")
    @ResponseBody
    public ResponseEntity run(@RequestBody AntColonyDao antColonyDao) {
        antColonyDao = graphService.run(antColonyDao);
//        check params
        if (antColonyDao.isDone())
            return new ResponseEntity(antColonyDao,HttpStatus.OK);
        else
            return new ResponseEntity(antColonyDao,HttpStatus.CONFLICT);
    }

    @RequestMapping(path = "/prepare_graph/{id}")
    public ResponseEntity preprareGraph(@PathVariable int id){
//        if (id==77){
//            Graph graphInArea = new Graph();
//            double lon = -118.4222236;
//            double lat = 33.91890598;
//            graphInArea = graphInArea.createGraphInArea(lat,lon,0.5);
//            GraphDao graphDao = new GraphDao();
//            graphDao.setEdges_size(graphInArea.getEdgesIds().size());
//            graphDao.setNodes_size(graphInArea.getNodesIds().size());
//            graphDao.setEdgeIds(graphInArea.getEdgesIds());
//            graphDao.setNodeIds(graphInArea.getNodesIds());
//            graphDao.setId(12);
//            graphService.insert(graphDao);
//            return new ResponseEntity("No graph",HttpStatus.CONFLICT);
//        }
        Graph graphFromId = graphService.createGraphFromId(id);
        if (graphFromId==null){
            return new ResponseEntity("No graph",HttpStatus.CONFLICT);
        }
        return new ResponseEntity(graphFromId.toGeoString(),HttpStatus.OK);
    }



    @RequestMapping(path = "/clear")
    public String clear(){
        graphService.clear();
        return "done";

    }
}

