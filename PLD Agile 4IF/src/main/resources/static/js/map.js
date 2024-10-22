var nodes, roadSegments, map;

async function loadJSON(url) {
    try {
        const response = await fetch(url);
        return await response.json();
    } catch (error) {
        console.error('Error loading JSON file:', error);
        throw error;
    }
}

function loadData() {
    return Promise.all([
        loadJSON('node.json').then(data => {
            nodes = data.nodes;
            plotNodes(nodes);
            checkAndPlotSegments();
        }),
        loadJSON('segment.json').then(data => {
            roadSegments = data.roadSegments;
            checkAndPlotSegments();
        })
    ]);
}

function checkAndPlotSegments() {
    if (nodes && roadSegments) {
        plotRoadSegments(roadSegments);
    }
}

function plotNodes(nodes) {
    for (var nodeId in nodes) {
        var node = nodes[nodeId];
        L.circleMarker([node.lat, node.lon], {
            radius: 2,
            color: 'black',
            fillColor: 'black',
            fillOpacity: 1
        }).addTo(map)
    }
}

function plotRoadSegments(roadSegments) {
    roadSegments.forEach(function(segment) {
        var originNode = nodes[segment.origine]
        var destinationNode = nodes[segment.destination]

        if (originNode && destinationNode) {
            L.polyline([
                [originNode.lat, originNode.lon],
                [destinationNode.lat, destinationNode.lon]
            ], {
                color: "rgb(70, 70, 70)",
                weight: 3
            }).addTo(map)
        }
    })
}

function fitMap(nodes) {
    var southwest = {lat: 90.0, lon: 180.0};
    var northeast = {lat: -90.0, lon: -180.0};

    for(var nodeId in nodes){
        var node = nodes[nodeId];
        node.lat > northeast.lat ? northeast.lat = node.lat : null;
        node.lon > northeast.lon ? northeast.lon = node.lon : null;
        node.lat < southwest.lat ? southwest.lat = node.lat : null;
        node.lon < southwest.lon ? southwest.lon = node.lon : null;
    } 

    var center = [(northeast.lat + southwest.lat) / 2, (northeast.lon + southwest.lon) / 2]
    var zoom = map.getBoundsZoom(L.latLngBounds(southwest, northeast))

    console.log(southwest, northeast)
    return {center: center, zoom: zoom};
}

function displayMap(mapContainerId, toHideElementsIds) {
    map = L.map(mapContainerId)

    for(const elementId of toHideElementsIds){
        const element = document.getElementById(elementId)
        element.style.visibility = "hidden"
    }

    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
        attribution: '&copy; OpenStreetMap contributors'
    }).addTo(map)
    
    loadData()
        .then(() => {
            const {center, zoom} = fitMap(nodes);
            console.log(center);
            console.log(zoom);
            map.setView(center, zoom);
        })
        .catch(error => {
            console.error('Error loading data:', error);
        });
    console.log(loadedNodes)
    const {center, zoom} = fitMap(nodes)
    console.log(center);
    console.log(zoom); 
    map.setView(center, zoom)
}