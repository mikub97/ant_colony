let map;
let points = [];
let markers=[]
let traces=null;
let edges = []

function loadGeoJsonString(geoString, is_Result=false) {
    const geojson = JSON.parse(geoString);
    features = geojson["features"]
    if (!is_Result )
        processFeatures(features)
    try{
        map.data.addGeoJson(geojson);
    } catch (e) {
        alert("Not a GeoJSON file!");
    }
    console.log("edges : ", edges)
    console.log("points : ", points)
  zoom(map);
}

function processFeatures(features) {
    points = []
    edges = []
    for (let i = 0; i < features.length; i++) {
        if (features[i]["geometry"].type=="Point"){
            points.push(features[i]["properties"]);
        } else {
            edges.push(features[i])
        }
    }
    createTable()
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
        mapTypeId: google.maps.MapTypeId.ROADMAP
    };
    map = new google.maps.Map(document.getElementById('map'), mapOptions);
    google.maps.event.addListener(map, "click", (event) => {
    });
    map.data.setStyle(function (feature) {
        var SD_NAME = feature.getProperty('isResult');
        var color = "black"
        var strokeOpacity = 0.6
        var strokeWeight = 3
        if (SD_NAME == "True") {
            color = "yellow";
            strokeWeight: 8;
            strokeOpacity: 1;
        }
        return {
            strokeColor: color,
            strokeWeight: strokeWeight,
            strokeOpacity: strokeOpacity
        }
    });
}




function addMarker(location,lab,type=0) {
    if (type==0)
        image = "http://maps.google.com/mapfiles/kml/pushpin/ylw-pushpin.png";
    else if (type==1)
        image = "http://maps.google.com/mapfiles/kml/pushpin/grn-pushpin.png";
    else if (type==2)
        image = "http://maps.google.com/mapfiles/kml/pushpin/ltblu-pushpin.png";
    m = new google.maps.Marker({
      position: location,
      label: lab,
      map: map,
      icon: image
    });
    markers.push(m)
}

function clearMarkers(){
    for (let i = 0; i < markers.length; i++) {
        markers[i].setMap(null)
    }
    markers=[]
}

function initialize() {
    initMap();
    clearMarkers()
    document.getElementById("startNode").value="";
    document.getElementById("endNode").value="";
    points=[]
    traces=null
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
            lat = list[i][cols[3]]
            lon = list[i][cols[2]]
            id = list[i][cols[1]]
            cell.innerHTML='<button onclick=\"markPoint('+lat+','+lon+','+id+')\">mark</button>\n'+'' +
                '<button onclick="markStartNode('+lat+','+lon+','+id+')">start</button>'+
                '<button onclick="markEndNode('+lat+','+lon+','+id+')">dest</button>'

            for (var j = 1; j < cols.length; j++) {
                cell = trow.insertCell(-1);
                // Inserting the cell at particular place
                cell.innerHTML = list[i][cols[j]];
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
}


function loadgraph(id) {
    $("body").addClass("loading");
    const Http = new XMLHttpRequest();
    const url='http://localhost:8888/prepare_graph/'+id;
    Http.open("GET", url);
    Http.send();
    Http.onreadystatechange =  function() {
        if (Http.readyState === XMLHttpRequest.DONE) {
            if (this.readyState == 4 && this.status == 200) {
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
function markPoint(lat,lon,id) {
    pos = new google.maps.LatLng({
        lat: lat,
        lng: lon
    });
    addMarker(pos,id.toString())
}
function markStartNode(lat,lon,id){
    document.getElementById("startNode").value=id;
    pos = new google.maps.LatLng({
        lat: lat,
        lng: lon
    });
    addMarker(pos,id.toString(),2)
}
function markEndNode(lat,lon,id){
    document.getElementById("endNode").value=id;
    pos = new google.maps.LatLng({
        lat: lat,
        lng: lon
    });
    addMarker(pos,id.toString(),1)
}

function run() {
    $("body").addClass("loading");
    startId = document.getElementById("startNode").value
    endId = document.getElementById("endNode").value
    console.log("run("+startId+","+endId+")")
    const Http = new XMLHttpRequest();
    const url='http://localhost:8888/run/'+startId+'&'+endId;
    Http.open("GET", url);
    Http.send();
    Http.onreadystatechange =  function() {
        if (Http.readyState=== XMLHttpRequest.DONE) {
            if (this.status == 409 || this.status == 400) {
                window.alert("Something's wrong.\nTry loading the graph from DB and input starting and finishing nodes");
            }
            if (this.readyState == 4 && this.status == 200) {
                loadGeoJsonString(Http.responseText, true)
                traces=Http.responseText;
                console.log(Http.done)
            }
            $("body").removeClass("loading");
        }
    }
}

function mark() {
  id = document.getElementById("nodeId").textContent
  pos = new google.maps.LatLng({
    lat: parseFloat(document.getElementById("lat").value),
    lng: parseFloat(document.getElementById("lon").value)
  });

  addMarker(pos,id)
}

for(var i = 1; i < 10; i++) {
    document.getElementById("graph_loaders").innerHTML += '<input type=\'button\' id=\'load'+i+'\' value=\'Load Graph nr.'+i+'\' onclick=\'loadgraph('+i+');\'>'
}
