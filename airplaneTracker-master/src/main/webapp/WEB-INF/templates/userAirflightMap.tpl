<!DOCTYPE html>
<html>
<head>
    <meta charset='utf-8' />
    <title></title>
    <meta name='viewport' content='initial-scale=1,maximum-scale=1,user-scalable=no' />
    <link href="https://fonts.googleapis.com/css?family=Open+Sans" rel="stylesheet">
    <link rel="stylesheet" href="util/CSS/siteTheme.css" type="text/css">    
    <link rel="stylesheet" href="util/CSS/Style.css" type="text/css">
    
    <script src='https://api.tiles.mapbox.com/mapbox-gl-js/v0.53.0/mapbox-gl.js'></script>
    <link href='https://api.tiles.mapbox.com/mapbox-gl-js/v0.53.0/mapbox-gl.css' rel='stylesheet' />
    <style>
    </style>
</head>
<body>
<header class="headstyle">
    <section>
            
            <div class="btn-group" style="width:100%">
                

                <button onclick="location.href='/airplane/tracker?cmd=savedPlanes'" type="button">Save More Planes!</button>

                <button onclick="location.href='/airplane/tracker?cmd=detail'" type="button">My List of Saved Planes</button>

                <button onclick="location.href='/airplane/tracker?cmd=home'" type="button">Log out</button>
                <p name="username" value="${username}">${username}</p>
              </div>
    </section>
  
</header>


<div class='filter-ctrl'>
<input id='filter-input' type='text' name='filter' placeholder='Filter by name' />
</div>
<nav id='filter-group' class='filter-group'></nav>
</br>

</br>
<div id='map'>
    <!--Added-->

</div>
 

    



<script>

mapboxgl.accessToken = 'pk.eyJ1IjoianNhcm1pZXMiLCJhIjoiY2pzeTdxM3NoMGEzbTN5cDU5enpyOTc1ZCJ9.SB2HX-HgqpZ6hPzxZ8FR9g';
//Added
//*****These are for search field*****
var layerIDs = []; // Will contain a list used to filter against.
var filterInput = document.getElementById('filter-input');
//***********************************
var filterGroup = document.getElementById('filter-group');
var map = new mapboxgl.Map({
  container: 'map',
  style: 'mapbox://styles/mapbox/light-v9',
  center: [-96, 37.8],
  zoom: 3
});

    
var geojson = ${geoJsonFile};

    
map.on('load', function() {
// Add a GeoJSON source containing place coordinates and information.
map.addSource("geojson", {
    "type": "geojson",
    "data": geojson
});
 
geojson.features.forEach(function(feature) {
    var symbol = feature.properties['title'];
    var layerID = 'poi-' + symbol.toLowerCase();
 
// Add a layer for this symbol type if it hasn't been added already.
if (!map.getLayer(layerID)) {

    map.addLayer({
        "id": layerID,
        "type": "symbol",
        "source": "geojson",
        "layout": {
            "icon-image": "airport" + "-15",    
            "icon-allow-overlap": true
        },

        "filter": ["==", "title", symbol]
    });
    
    layerIDs.push(layerID);


    // Add checkbox and label elements for the layer.
    var input = document.createElement('input');
    input.type = 'checkbox';
    input.id = layerID;
    input.checked = true;
    filterGroup.appendChild(input);

    var label = document.createElement('label');
    label.setAttribute('for', layerID);
    label.textContent = symbol;
    filterGroup.appendChild(label);
 
    // When the checkbox changes, update the visibility of the layer.
    input.addEventListener('change', function(e) {
    map.setLayoutProperty(layerID, 'visibility',
    e.target.checked ? 'visible' : 'none');
    });
    }
});


//*****************************
filterInput.addEventListener('keyup', function(e) {
// If the input value matches a layerID set
// it's visibility to 'visible' or else hide it.
var value = e.target.value.trim().toLowerCase();
layerIDs.forEach(function(layerID) {
map.setLayoutProperty(layerID, 'visibility',
layerID.indexOf(value) > -1 ? 'visible' : 'none');
});
});
//*****************************


});

geojson.features.forEach(function(marker) {

  // create a HTML element for each feature
  var el = document.createElement('div');
  el.className = 'marker';

  // make a marker for each feature and add to the map
  new mapboxgl.Marker(el)
    .setLngLat(marker.geometry.coordinates)
    .setPopup(new mapboxgl.Popup({ offset: 25 }) // add popups
    .setHTML('<h3>' + marker.properties.title + '</h3><p>'))
    .addTo(map);
});

</script>

</body>
</html>