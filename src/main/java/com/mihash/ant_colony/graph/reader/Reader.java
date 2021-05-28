package com.mihash.ant_colony.graph.reader;

import com.mihash.ant_colony.graph.Edge;
import com.mihash.ant_colony.graph.Node;
import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.util.*;

public class Reader {

    private static final String COMMA_DELIMITER = ";";


    public static Map<Long, Edge> readEdges() {
        List<List<String>> edges_str = read("data/edges.csv");
        //edges_str.add((Arrays.asList("59420;[[[33.82823464,-118.22721897],[33.82820608,-118.22709358]]];58262;ALAMEDA;9999".split(COMMA_DELIMITER))));

        Map<Long, Edge> edges = new HashMap<>();
        for (int i = 1; i < edges_str.size(); i++) {
            //    public Edge(long id, long from_node, long to_node, String street) {
            // 50406;[[[-118.2918040563249, 33.95457829022292], [-118.29180669978426, 33.953365154191594]]];50396;VERMONT;75652
            edges.put(Long.valueOf(edges_str.get(i).get(4)), new Edge(Long.valueOf(edges_str.get(i).get(4)), Long.valueOf(edges_str.get(i).get(0)),
                    Long.valueOf(edges_str.get(i).get(2)), edges_str.get(i).get(3), edges_str.get(i).get(1)));
        }
        return edges;
    }
    public static void readGeoNodes(List<Long> ids) {

        InputStream is = null;

        try {
            is = new FileInputStream(new File("Street Intersections.geojson"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        String text = null;
        try {
            text = IOUtils.toString(is, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        JSONArray tosave = new JSONArray();
        JSONArray arr = new JSONObject(text).getJSONArray("features");
        for (int i = 0; i < arr.length(); i++) {
            JSONObject properties = arr.getJSONObject(i).getJSONObject("properties");
            try{
                if (ids.contains(properties.getLong("cl_node_id"))) {
                    System.out.println(arr.getJSONObject(i).toString());
                    tosave.put(arr.get(i));
                }
            }catch (JSONException e){

            }
        }
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(new File("example4_nodes"));
            fileWriter.write("{\n" +
                    "  \"type\": \"FeatureCollection\",\n" +
                    "  \"features\": ");
        } catch (IOException e) {
            e.printStackTrace();
        }
        tosave.write(fileWriter);
        try {
            fileWriter.write("}");
            fileWriter.close();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void readGeoEdges(List<Long> ids) {

        InputStream is = null;

        try {
            is = new FileInputStream(new File("Street Centerline.geojson"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        String text = null;
        try {
            text = IOUtils.toString(is, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        JSONArray tosave = new JSONArray();
        JSONArray arr = new JSONObject(text).getJSONArray("features");
        for (int i = 0; i < arr.length(); i++) {
            JSONObject properties = arr.getJSONObject(i).getJSONObject("properties");
            try{
                if (ids.contains(properties.getLong("id"))) {
                    System.out.println(arr.getJSONObject(i).toString());
                    tosave.put(arr.get(i));
                }
            }catch (JSONException e){

            }
        }
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(new File("example4"));
            fileWriter.write("{\n" +
                    "  \"type\": \"FeatureCollection\",\n" +
                    "  \"features\": ");
        } catch (IOException e) {
            e.printStackTrace();
        }
        tosave.write(fileWriter);
        try {
            fileWriter.write("}");
            fileWriter.close();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Map<Long, Node> readNodes() {
        List<List<String>> edges_str = read("data/nodes.csv");
        Map<Long, Node> nodes = new HashMap<>();
        for (int i = 1; i < edges_str.size(); i++) {
            // public Node(long id, long lat, long lon, String ZIP_CODE) {
            nodes.put(Long.valueOf(edges_str.get(i).get(0)), new Node(Long.valueOf(edges_str.get(i).get(0)), Double.valueOf(edges_str.get(i).get(1)),
                    Double.valueOf(edges_str.get(i).get(2)), edges_str.get(i).get(3)));
        }
        return nodes;
    }

    public static List<List<String>> read(String filename) {
        List<List<String>> records = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                List<String> values = Arrays.asList(line.split(COMMA_DELIMITER));
                records.add(values);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return records;
    }
}
