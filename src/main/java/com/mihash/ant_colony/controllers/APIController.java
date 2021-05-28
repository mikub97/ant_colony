package com.mihash.ant_colony.controllers;

import com.mihash.ant_colony.ant_colony.AntColony;
import com.mihash.ant_colony.graph.Graph;
import com.mihash.ant_colony.services.IGraphService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(maxAge = 3600)
@ComponentScan("com.mihash.ant_colony.services")
public class APIController {

    private IGraphService graphService;

    @Autowired
    public void setEdgesService( IGraphService graphService){
        this.graphService = graphService;
    }


    @RequestMapping(path = "/run/{from}&{to}")
    public ResponseEntity run(@PathVariable int from, @PathVariable int to) {
        System.out.println("Running algorithm from node "+from +" to node " + to);
        String res = graphService.run(from,to);
        if (res==null)
            return new ResponseEntity("No graph",HttpStatus.CONFLICT);

        return new ResponseEntity(res, HttpStatus.OK);

    }

    @RequestMapping(path = "/prepare_graph/{id}")
    public String preprareGraph(@PathVariable int id){
        return graphService.createGraphFromId(id).toGeoString();

    }

    @RequestMapping(path = "/clear")
    public String clear(){
        graphService.clear();
        return "done";

    }
}

