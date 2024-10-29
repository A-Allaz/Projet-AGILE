var nodes, roadSegments, map;

function sleep(ms) {
    return new Promise(resolve => setTimeout(resolve, ms));
}

async function loadData() {
    try {
        await sleep(3000); // Simulate a delay

        const response = await fetch('/mapData');
        if (!response.ok) {
            throw new Error('Network response was not ok: ' + response.statusText);
        }

        const data = await response.json(); // Data contains both nodes and roadSegments
        console.log('Data loaded:', data);

        // Extract nodes and road segments from the JSON object
        nodes = data.node; // Assuming the 'node' key contains an array of node objects
        roadSegments = data.roadSegment; // Assuming 'roadSegment' contains an array of road segments

        console.log('Nodes:', nodes);
        console.log('Road Segments:', roadSegments);

        // Plot the data on the map
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

// Plot the nodes on the map
function plotNodes(nodes) {
    nodes.forEach(node => {
        L.circleMarker([node.latitude, node.longitude], {
            radius: 2,
            color: 'black',
            fillColor: 'black',
            fillOpacity: 1
        }).addTo(map);
    });
}

// Plot the road segments on the map
function plotRoadSegments(roadSegments) {
    roadSegments.forEach(function(segment) {
        const originNode = nodes.find(node => node.id === segment.origin);
        const destinationNode = nodes.find(node => node.id === segment.destination);

        if (originNode && destinationNode) {
            L.polyline([
                [originNode.latitude, originNode.longitude],
                [destinationNode.latitude, destinationNode.longitude]
            ], {
                color: "rgb(70, 70, 70)",
                weight: 3
            }).addTo(map);
        }
    });
}

function fitMap(nodes) {
    var southwest = {lat: 90.0, lon: 180.0};
    var northeast = {lat: -90.0, lon: -180.0};

    for(var nodeId in nodes){
        var node = nodes[nodeId];
        node.latitude > northeast.lat ? northeast.lat = node.latitude : null;
        node.longitude > northeast.lon ? northeast.lon = node.longitude : null;
        node.latitude < southwest.lat ? southwest.lat = node.latitude : null;
        node.longitude < southwest.lon ? southwest.lon = node.longitude : null;
    } 

    var center = [(northeast.lat + southwest.lat) / 2, (northeast.lon + southwest.lon) / 2]
    var zoom = map.getBoundsZoom(L.latLngBounds(southwest, northeast))

    console.log(southwest, northeast)
    return {center: center, zoom: zoom};
}

function displayOptimalTour(tourSegments) {
    tourSegments.forEach(segment => {
        const originNode = nodes.find(node => node.id === segment.origin);
        const destinationNode = nodes.find(node => node.id === segment.destination);

        if (originNode && destinationNode) {
            L.polyline([
                [originNode.latitude, originNode.longitude],
                [destinationNode.latitude, destinationNode.longitude]
            ], {
                color: "red",
                weight: 4
            }).addTo(map);
        }
    });
}

function displayMap(fileInputId, mapContainerId, toHideElementsIds) {
    const fileInput = document.getElementById(fileInputId);
    const mapFile = fileInput.files[0];

    if (!mapFile) {
        alert("Please select a map file to upload.");
        return;
    }

    sendFileToServer(mapFile, '/uploadMap');

    map = L.map(mapContainerId);

    // Masquer les éléments après confirmation de la carte
    toHideElementsIds.forEach(function (elementId) {
        const element = document.getElementById(elementId);
        element.style.visibility = "hidden";
    });

    // Afficher la carte avec les données téléchargées
    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
        attribution: '&copy; OpenStreetMap contributors'
    }).addTo(map);

    // Charger les données de la carte
    loadData()
        .then(() => {
            const { center, zoom } = fitMap(nodes);
            map.setView(center, zoom);
        })
        .catch(error => {
            console.error('Error loading map data:', error);
        });
}

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

// Fonction pour charger les points de livraison et d'entrepôt, puis les afficher sur la carte
function loadMapPoints() {
    if (!map) {
        console.error("Map is not initialized. Please initialize the map before adding points.");
        return;
    }

    fetch('/mapPoints')
        .then(response => response.json())
        .then(data => {
            const deliveries = data.deliveries;
            const warehouse = data.warehouse;

            // Ajouter un marqueur pour l'entrepôt
            if (warehouse) {
                L.marker([warehouse.latitude, warehouse.longitude], {
                    icon: L.icon({
                        iconUrl: '../images/warehouse.png',  // URL de l'icône de l'entrepôt
                        iconSize: [25, 25]
                    })
                }).addTo(map).bindPopup("Warehouse");
            }

            // Ajouter des marqueurs pour chaque livraison
            deliveries.forEach((delivery, index) => {
                L.marker([delivery.pickupLocation.latitude, delivery.pickupLocation.longitude], {
                    icon: L.icon({
                        iconUrl: '../images/pickup.png',  // URL de l'icône de pickup
                        iconSize: [20, 20]
                    })
                }).addTo(map).bindPopup(`Pickup ${index + 1}`);

                L.marker([delivery.deliveryLocation.latitude, delivery.deliveryLocation.longitude], {
                    icon: L.icon({
                        iconUrl: '../images/delivery.png',  // URL de l'icône de livraison
                        iconSize: [20, 20]
                    })
                }).addTo(map).bindPopup(`Delivery ${index + 1}`);
            });
        })
        .catch(error => {
            console.error('Error loading map points:', error);
            alert("Could not load map points: " + error.message);
        });
}
