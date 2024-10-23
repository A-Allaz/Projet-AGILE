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

async function loadData() {
    try {
        const response = await fetch('/mapData');

        if (!response.ok) {
            throw new Error('Network response was not ok: ' + response.statusText);
        }

        const data = await response.json();

        if (data.error) {
            console.error('Error:', data.error);
            return;
        }

        nodes = data.nodes;
        roadSegments = data.roadSegments;

        plotNodes(nodes);
        plotRoadSegments(roadSegments);

    } catch (error) {
        console.error('Error loading data:', error);
        alert("Error loading data: " + error.message); // Display user-friendly error
    }
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

function displayMap(mapFile, mapContainerId, toHideElementsIds) {
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
    const {center, zoom} = fitMap(nodes)
    console.log(center);
    console.log(zoom); 
    map.setView(center, zoom)
}

document.getElementById('confirmMapButton').addEventListener('click', function () {
    const input = document.getElementById('mapFileInput');
    if (input.files.length > 0) {
        const file = input.files[0];
        console.log('Selected file:', file);
        sendFileToServer(file, '/uploadMap');
    } else {
        alert("No file selected");
    }
});

function sendFileToServer(file, uploadUrl) {
    const formData = new FormData();
    formData.append("file", file);
    console.log('FormData:', Array.from(formData.entries())); // Log form data entries

    fetch(uploadUrl, {
        method: "POST",
        body: formData
    })
        .then(response => {
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            return response.text(); // Get response as text
        })
        .then(data => {
            console.log('File upload successful:', data);
            alert(data);  // Display the response in an alert
        })
        .catch(error => {
            console.error('Error uploading file:', error);
            alert("Error uploading the file: " + error);
        });
}
