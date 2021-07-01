let map;
let points = [];
let edges = []
let features = []
let result_edge = []
let startnode = null;
let endnode = null
let s_c = '#FF2BE9'


function loadGeoJsonString(geoString) {
    const geojson = JSON.parse(geoString);
    features = geojson["features"]
    processFeatures(features)
    console.log("edges : ", edges)
    console.log("points : ", points)

}

function loadResults(results) {
    if (results["done"]==true) {
        result_edge = results["bestEdgeHistory"]
        console.log("Results:")
        console.log(result_edge)

        repaintEdges()
    }
}

function repaintEdges(){
    for (let j = 0; j < edges.length; j++) {
        if (result_edge.includes(parseInt(edges[j]["id"]))) {
            edges[j]["polyline"].setOptions({strokeColor: s_c, strokeWeight: 8})
        }
        else{
            edges[j]["polyline"].setOptions({strokeColor: 'black',strokeWeight: 2})
        }
    }
}
function processFeatures(features) {
    points = []
    edges = []
    for (let i = 0; i < features.length; i++) {
        if (features[i]["geometry"].type=="Point"){
            p = {
                "id":features[i]['properties']['cl_node_id'],
                "node": features[i]["properties"],
                "marker": createMarker(features[i])
            }
            points.push(p)
        } else {
            e = {
                "id": features[i]['properties']['id'],
                "edge": features[i]['properties'],
                "polyline": createPoly(features[i])
            }
            edges.push(e)

        }

    }
        zoomToMarkers()
        createTable()
}


function createMarker(feature){
    marker = new google.maps.Marker({
        position: {
            lat: parseFloat(feature['properties']['lat']),
            lng: parseFloat(feature['properties']['lon'])
        },
        icon: {
            url:"http://maps.google.com/mapfiles/ms/icons/red-dot.png",
        },
        label:feature['properties']['cl_node_id'],
        size: (200, 200),
        map: map,
    })


    google.maps.event.addListener(marker,  'rightclick',  function(mouseEvent) {markEndNode(feature['properties']['cl_node_id'])});
    google.maps.event.addListener(marker,  'click',  function(mouseEvent) {markStartNode(feature['properties']['cl_node_id'])});

    // marker.addListener("click", (e) => {
    //     if (pressedKeys[KeyboardEvent.ctrlKey])
    //         markEndNode(feature['properties']['cl_node_id'])
    //     else
    //         markStartNode(feature['properties']['cl_node_id'])
    //
    // });
    return marker

}
function createPoly(feature){
    const coords = [];
    for (let i = 0; i < feature['geometry']['coordinates'][0].length; i++) {
        coords.push({ lat: parseFloat(feature['geometry']['coordinates'][0][i][1]), lng:parseFloat(feature['geometry']['coordinates'][0][i][0]) })
    }
    col = "black"
    // Construct the polygon.
    const poly = new google.maps.Polyline({
        path: coords,
        strokeColor: col,
    });
    poly.setMap(map)
    return poly

}





/**
 * Update a map's viewport to fit each geometry in a dataset
 */
function zoom(map) {
  const bounds = new google.maps.LatLngBounds();
  map.data.forEach((feature) => {
    processPoints(feature.getGeometry(), bounds.extend, bounds);
  });
  map.fitBounds(bounds);
}

/**
 * Process each point in a Geometry, regardless of how deep the points may lie.
 */
function processPoints(geometry, callback, thisArg) {
  if (geometry instanceof google.maps.LatLng) {
    callback.call(thisArg, geometry);
  } else if (geometry instanceof google.maps.Data.Point) {
    callback.call(thisArg, geometry.get())
  } else {
    geometry.getArray().forEach((g) => {
      processPoints(g, callback, thisArg);
    });
  }
}

function initMap() {
    var mapOptions = {
        zoom: 10,
        center: new google.maps.LatLng(33.945621, -118.240816),
        mapTypeId: google.maps.MapTypeId.ROADMAP,
        mapId: 'TO_FILL'

    };
    map = new google.maps.Map(document.getElementById('map'), mapOptions);
}


function zoomToMarkers(){
    var bounds = new google.maps.LatLngBounds();
    for (var i = 0; i < points.length; i++) {
        bounds.extend(points[i]['marker'].getPosition());
    }

    //center the map to the geometric center of all markers
    map.setCenter(bounds.getCenter());

    map.fitBounds(bounds);

    //remove one zoom level to ensure no marker is on the edge.
    map.setZoom(map.getZoom()-1);

    // set a minimum zoom
    // if you got only 1 marker or all markers are on the same address map will be zoomed too much.
    if(map.getZoom()> 15){
        map.setZoom(15);
    }

    //Alternatively this code can be used to set the zoom for just 1 marker and to skip redrawing.
    //Note that this will not cover the case if you have 2 markers on the same address.
    if(points.length == 1){
        map.setMaxZoom(15);
        map.fitBounds(bounds);
        map.setMaxZoom(Null)
    }

    map.fitBounds(bounds);
}
function initialize() {
    initMap();
    document.getElementById("startNode").value="";
    document.getElementById("endNode").value="";
    points=[]
    edges=[]
    startnode=null
    endnode=null
    result_edge=null
}
function createTable(){
        var cols = ["opcje","cl_node_id","lon","lat"];
        list = points
        // Create a table element
        var table = document.createElement("table");
        // Create table row tr element of a table
        var tr = table.insertRow(-1);
        for (var i = 0; i < cols.length; i++) {
            // Create the table header th element
            var theader = document.createElement("th");
            theader.innerHTML = cols[i];
            // Append columnName to the table row
            tr.appendChild(theader);
        }
        // Adding the data to the table
        for (var i = 0; i < list.length; i++) {
            // Create a new row
            trow = table.insertRow(-1);
            var cell = trow.insertCell(-1); //lat,lon,id)
            lat = list[i]['node'][cols[3]]
            lon = list[i]['node'][cols[2]]
            id = list[i]['node'][cols[1]]
            cell.innerHTML='<button onclick="markStartNode('+id+')">start</button>'+
                '<button onclick="markEndNode('+id+')">dest</button>'

            for (var j = 1; j < cols.length; j++) {
                cell = trow.insertCell(-1);
                // Inserting the cell at particular place
                cell.innerHTML = list[i]['node'][cols[j]];
            }
        }
        // Add the newely created table containing json data
        var el = document.getElementById("table");
        el.innerHTML = "";
        el.appendChild(table);
}

function clearMap() {
    const Http = new XMLHttpRequest();
    const url='http://localhost:8888/clear';
    Http.open("GET", url);
    Http.send();
    Http.onreadystatechange =  function() {
        if (this.readyState==4 && this.status==200) {
            initialize();
            document.getElementById("table").innerHTML=""
        }
    }
    points = [];
    edges = []
    result_edge = []

}


function loadgraph(id) {
    $("body").addClass("loading");
    $("#check").prop('checked', true);
    const Http = new XMLHttpRequest();
    const url='http://localhost:8888/prepare_graph/'+id;
    Http.open("GET", url);
    Http.send();
    Http.onreadystatechange =  function() {
        if (Http.readyState === XMLHttpRequest.DONE) {
            if (this.status == 409 || this.status == 400) {
                window.alert("Something's wrong.\nTry loading the graph from DB and input starting and finishing nodes");
                $("body").removeClass("loading");

            }
            else if (this.readyState == 4 && this.status == 200) {
                initialize();
                loadGeoJsonString(Http.responseText)
                $("body").removeClass("loading");
            }
            else {
                loadGeoJsonString(Http.responseText)
                $("body").removeClass("loading");

            }
        }
    }
}

function markStartNode(id) {
    if (startnode !=null){
        startnode["marker"].setOptions({
            icon: {
                url: "http://maps.google.com/mapfiles/ms/icons/red-dot.png",
            },
        })
        startnode["marker"].setMap(null);
        startnode["marker"].setMap(map);
    }


    document.getElementById("startNode").value = id;
    startnode = points.find((value, index) => {
        return (value["id"] == id)
    })
    startnode["marker"].setOptions({icon: {
            url:"http://maps.google.com/mapfiles/ms/icons/green-dot.png",
        },})
    startnode["marker"].setMap(null);
    startnode["marker"].setMap(map);


}
function markEndNode(id) {
    if (endnode !=null){
        endnode["marker"].setOptions({
            icon: {
                url: "http://maps.google.com/mapfiles/ms/icons/red-dot.png",
            },
        })
        endnode["marker"].setMap(null);
        endnode["marker"].setMap(map);
    }

    document.getElementById("endNode").value = id;
    endnode = points.find((value, index) => {
        return (value["id"] == id)
    })
    endnode["marker"].setOptions({icon: {
            url:"http://maps.google.com/mapfiles/ms/icons/blue-dot.png",
        },})
    endnode["marker"].setMap(null);
    endnode["marker"].setMap(map);


}

function test() {
    $("body").addClass("loading");
    startId = document.getElementById("startNode").value
    endId = document.getElementById("endNode").value
    ant_size = document.getElementById("ant_size").value
    iterN = document.getElementById("iterN").value
    beta = document.getElementById("beta").value
    alfa = document.getElementById("alfa").value
    ro = document.getElementById("ro").value

    console.log("run("+startId+","+endId+")")
    const Http = new XMLHttpRequest();
    const url='http://localhost:8888/run/'+startId+'&'+endId;
    Http.open("GET", url);
    Http.send();
    Http.onreadystatechange =  function() {
        if (Http.readyState=== XMLHttpRequest.DONE) {
            if (this.status == 409 || this.status == 400) {
                window.alert("Something's wrong.\nCheck algorithm's parameteres");
            }
            if (this.readyState == 4 && this.status == 200) {
                loadResults(Http.responseText)
            }
            $("body").removeClass("loading");
        }
    }
}
var pressedKeys = {};
window.onkeyup = function(e) { pressedKeys[e.keyCode] = false; }
window.onkeydown = function(e) { pressedKeys[e.keyCode] = true; }

var checkbox = document.querySelector("input[id=check]");

checkbox.addEventListener('change', function() {
    if (this.checked) {
        for (let i = 0; i < points.length; i++) {
            points[i].marker.setMap(map)
        }
    } else {
        for (let i = 0; i < points.length; i++) {
            points[i].marker.setMap(null)
        }
    }
});

function run(){
    var xhr = new XMLHttpRequest();
    var url = "http://localhost:8888/test";
    xhr.open("POST", url, true);
    xhr.setRequestHeader("Content-Type", "application/json");
    xhr.onreadystatechange =  function() {
        if (xhr.readyState=== XMLHttpRequest.DONE) {
            if (this.status == 409 || this.status == 400) {
                window.alert("Something's wrong.\nTry loading the graph from DB and input starting and finishing nodes");
            }
            if (this.readyState == 4 && this.status == 200) {
                loadResults(JSON.parse(xhr.responseText))
            }
            $("body").removeClass("loading");
        }
    }
    node_from = document.getElementById("startNode").value
    node_to = document.getElementById("endNode").value
    ant_size = document.getElementById("ant_size").value
    iterN = document.getElementById("iterN").value

    beta = document.getElementById("beta").value
    alfa = document.getElementById("alfa").value
    ro = document.getElementById("ro").value
    q1 = document.getElementById("q1").value

    q2 = document.getElementById("q2").value
    var data = JSON.stringify({
        "alfa":  alfa,  // priorytet feromonu
        "beta": beta,   // priorytet heurystyki
        "ro": ro, //wsp. parowania
        "q1": q1, // default feromone
        "q2": q2, //feromone zostawiany
        "antN": ant_size, // ilość mrówek
        "iterN": iterN,
        "node_from":node_from,
        "node_to":node_to
    });
    xhr.send(data);
}
//
// for(var i = 1; i < 10; i++) {
//     document.getElementById("graph_loaders").innerHTML += '<input type=\'button\' id=\'load'+i+'\' value=\'Load Graph nr.'+i+'\' onclick=\'loadgraph('+i+');\'>'
// }
