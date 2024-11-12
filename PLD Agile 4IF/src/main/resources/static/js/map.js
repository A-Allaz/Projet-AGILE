var nodes, roadSegments, map;

// Variables pour suivre l'état de la sélection
let isSelectingPickup = false;
let isSelectingDelivery = false;
let selectedDeliveryId = null;  // ID de la livraison en cours de modification


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
        // plotNodes(nodes);
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

// Function to find the nearest node to a given latitude and longitude
function findNearestNode(lat, lng) {
    let nearestNode = null;
    let minDistance = Infinity;

    nodes.forEach(node => {
        const distance = Math.sqrt(Math.pow(node.latitude - lat, 2) + Math.pow(node.longitude - lng, 2));
        if (distance < minDistance) {
            minDistance = distance;
            nearestNode = node;
        }
    });

    return nearestNode;
}

function loadCourierInfo(courierId) {
    fetch(`/courierInfo?courierId=${courierId}`)
        .then(response => response.json())
        .then(data => {
            if (data.status === "success") {
                displayOptimalTour(data.currentRoute); // Affiche la route
            } else {
                console.error(data.message);
                alert("Error loading courier info: " + data.message);
            }
        })
        .catch(error => console.error('Error fetching courier info:', error));
}

// Rafraîchit le trajet pour le livreur actuellement sélectionné
document.getElementById('courierSelect').addEventListener('change', (e) => {
    const selectedCourierId = e.target.value;
    loadCourierInfo(selectedCourierId);
});


function displayOptimalTour(tourSegments) {
    // Nettoyer les anciens segments de tournée
    map.eachLayer(layer => {
        if (layer instanceof L.Polyline && layer.options.color === "red") {
            map.removeLayer(layer);
        }
    });

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

function displayTimeEstimates(timeEstimates) {
    console.log(timeEstimates);
    const timeEstimatesDiv = document.querySelector(".stopListBox");
    timeEstimatesDiv.innerHTML = ""; // Effacer tout contenu existant

    let groupSegments = [];  // Pour stocker les segments de chaque groupe
    let groupStartTime = null;  // Heure de départ du groupe
    let groupEndTime = null;    // Heure d'arrivée au point de Pickup ou Delivery
    let groupIndex = 1;         // Compteur pour l'index des groupes

    timeEstimates.forEach((estimate, index) => {
        // Si c'est le premier segment du groupe, initialiser l'heure de départ
        if (!groupStartTime) {
            groupStartTime = estimate.departureTime;
        }

        // Ajouter le segment actuel aux détails du groupe
        groupSegments.push({
            length: estimate.segment.length,
            name: estimate.segment.name
        });

        // Vérifier si le segment est un point de destination (on retrouve segment.destination dans deliveries)
        if (deliveries.some(delivery => delivery.pickupLocation === estimate.segment.destination || delivery.deliveryLocation === estimate.segment.destination)) {

        // Enregistrer l'heure d'arrivée pour ce groupe
            groupEndTime = estimate.arrivalTime;

            // Créer l'élément de groupe pour affichage
            const groupItem = document.createElement("div");
            groupItem.classList.add("estimate-item");

            // Ajouter en-tête de groupe avec heure de départ
            groupItem.innerHTML = `
                <p><strong>Aller au ${deliveries.some(delivery => delivery.pickupLocation === estimate.segment.destination) ? "Pickup" : "Delivery"} ${deliveries.find(delivery => delivery.pickupLocation === estimate.segment.destination || delivery.deliveryLocation === estimate.segment.destination).id} : départ ${groupStartTime}</strong></p>
                <p><strong>Détails :</strong></p>
            `;

            // Ajouter les détails de chaque segment du groupe
            groupSegments.forEach((segment, index) => {
                if (index > 0 && groupSegments[index - 1].name === segment.name) {
                    // Ajouter la longueur du segment courant au segment précédent
                    groupSegments[index - 1].length += segment.length;
                } else {
                    const detailItem = document.createElement("p");
                    detailItem.innerHTML = `
            ${segment.name} ------------------- ${segment.length.toFixed(1)} m<br>
        `;
                    groupItem.appendChild(detailItem);
                }
            });

            // Ajouter l'heure d'arrivée du groupe
            const arrivalItem = document.createElement("p");
            arrivalItem.innerHTML = `<strong>Arrivée ${groupEndTime}</strong>`;
            groupItem.appendChild(arrivalItem);

            // Séparation visuelle pour le groupe
            groupItem.appendChild(document.createElement("hr"));
            timeEstimatesDiv.appendChild(groupItem);

            // Préparer pour le prochain groupe
            groupSegments = [];        // Réinitialiser les segments pour le nouveau groupe
            groupStartTime = null;     // Réinitialiser l'heure de départ
            groupIndex++;              // Incrémenter l'index du groupe
        }
    });

    // Ajouter le trajet retour vers l'entrepôt
    if (groupSegments.length > 0) {
        const returnItem = document.createElement("div");
        returnItem.classList.add("estimate-item");

        returnItem.innerHTML = `
            <p><strong>Retour à l'entrepôt : départ ${timeEstimates[timeEstimates.findIndex(estimate => estimate.arrivalTime === groupEndTime) + 1].departureTime}</strong></p>
            <p><strong>Détails :</strong></p>
        `;

        groupSegments.forEach((segment, index) => {
            if (index > 0 && groupSegments[index - 1].name === segment.name) {
                groupSegments[index - 1].length += segment.length;
            } else {
                const detailItem = document.createElement("p");
                detailItem.innerHTML = `
                    ${segment.name} ------------------- ${segment.length.toFixed(1)} m<br>
                `;
                returnItem.appendChild(detailItem);
            }
        });

        const arrivalItem = document.createElement("p");
        arrivalItem.innerHTML = `<strong>Arrivée ${timeEstimates[timeEstimates.length - 1].arrivalTime}</strong>`;
        returnItem.appendChild(arrivalItem);

        returnItem.appendChild(document.createElement("hr"));
        timeEstimatesDiv.appendChild(returnItem);
    }
    // Afficher le temps total de la tournée
    const totalTourTime = timeEstimates[timeEstimates.length - 1].arrivalTime;
    let beginTour = new Date();
    beginTour.setHours(timeEstimates[0].departureTime.split(":")[0], timeEstimates[0].departureTime.split(":")[1], timeEstimates[0].departureTime.split(":")[2]);
    let endTour = new Date();
    endTour.setHours(totalTourTime.split(":")[0], totalTourTime.split(":")[1], totalTourTime.split(":")[2]);

    // Affichage selon le format --h--m--s
    document.getElementById("tourTimeValue").innerText = `${String(Math.floor((endTour - beginTour) / 1000 / 60 / 60)).padStart(2, '0')}h${String(Math.floor((endTour - beginTour) / 1000 / 60 % 60)).padStart(2, '0')}m${String(Math.floor((endTour - beginTour) / 1000 % 60)).padStart(2, '0')}s`;
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
            initializeMapClickListener(); // Call after map is initialized
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

    // Enlever les anciens marqueurs de la carte
    map.eachLayer(layer => {
        if (layer instanceof L.Marker) {
            map.removeLayer(layer);
        }
    });


    alert("Loading map points...");

    fetch('/mapPoints')
        .then(response => response.json())
        .then(data => {

            const deliveries = data.deliveries;
            const warehouse = data.warehouse;

            console.log('Deliveries:', deliveries);
            console.log('Warehouse:', warehouse);

            const nodeWarehouse = nodes.find(node => node.id === warehouse.address);

            // Ajouter un marqueur pour l'entrepôt
            if (warehouse) {
                L.marker([nodeWarehouse.latitude, nodeWarehouse.longitude], {
                    icon: L.icon({
                        iconUrl: '../images/warehouse.png',  // URL de l'icône de l'entrepôt
                        iconSize: [25, 25]
                    })
                }).addTo(map).bindPopup("Warehouse");
            }

            // Ajouter des marqueurs pour chaque livraison
            deliveries.forEach((delivery, index) => {
                const nodePickup = nodes.find(node => node.id === delivery.pickupLocation);
                L.marker([nodePickup.latitude, nodePickup.longitude], {
                    icon: L.icon({
                        iconUrl: '../images/pickup.png',  // URL de l'icône de pickup
                        iconSize: [20, 20]
                    })
                }).addTo(map).bindPopup(`Pickup ${delivery.id}`);

                const nodeDelivery = nodes.find(node => node.id === delivery.deliveryLocation);
                L.marker([nodeDelivery.latitude, nodeDelivery.longitude], {
                    icon: L.icon({
                        iconUrl: '../images/delivery.png',  // URL de l'icône de livraison
                        iconSize: [20, 20]
                    })
                }).addTo(map).bindPopup(`Delivery ${delivery.id}`);
            });
        })
        .catch(error => {
            console.error('Error loading map points:', error);
            alert("Could not load map points: " + error.message);
        });
}

function addDeliveryToServer(pickupLocation, deliveryLocation, pickupTime, deliveryTime, courierId) {
    const requestBody = new URLSearchParams({
        pickupLocation: pickupLocation,
        deliveryLocation: deliveryLocation,
        pickupTime: pickupTime,
        deliveryTime: deliveryTime,
        courierId: courierId
    });

    console.log('Request Body:', requestBody.toString());

    fetch('/addDelivery', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded'
        },
        body: requestBody
    })
        .then(response => {
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            return response.json();
        })
        .then(data => {
            if (data.status === "success") {
                console.log(data.message);
                // Add the new delivery in deliveries variable with an id
                const newDelivery = {
                    id: deliveries.length + 1,
                    pickupLocation: pickupLocation,
                    deliveryLocation: deliveryLocation,
                    pickupTime: pickupTime,
                    deliveryTime: deliveryTime,
                    courierId: courierId
                };
                deliveries.push(newDelivery);

                // Clear the input fields
                document.getElementById('pickupLocationInput').value = "";
                document.getElementById('deliveryLocationInput').value = "";
                document.querySelector("input[placeholder='Pickup time:']").value = "";
                document.querySelector("input[placeholder='Delivery time:']").value = "";

                // Refresh the deliveries list box to display the new delivery
                loadDeliveries();

                // Refresh the map to display the new point
                loadMapPoints();

                // Refresh the details of the delivery tour
                fetchOptimalTour();
            } else {
                console.error(data.message);
            }
        })
        .catch(error => console.error('Error:', error));
}


// Function to initialize the event listeners on the map
function initializeMapClickListener() {
    map.on('click', function (e) {
        const latlng = e.latlng;
        let elementIdPickup;
        let elementIdDelivery;

        if(modyfingDelivery){
            elementIdPickup = 'editPickupLocation';
            elementIdDelivery = 'editDeliveryLocation';
        }
        else {
            elementIdPickup = 'pickupLocationInput';
            elementIdDelivery = 'deliveryLocationInput';
        }

        if (isSelectingPickup) {
            const pickupInput = document.getElementById(elementIdPickup);
            if (pickupInput) {
                pickupInput.value = `${latlng.lat}, ${latlng.lng}`;
                isSelectingPickup = false; // Disable Pickup selection mode
            } else {
                console.error("Pickup location input not found.");
            }
        } else if (isSelectingDelivery) {
            const deliveryInput = document.getElementById(elementIdDelivery);
            if (deliveryInput) {
                deliveryInput.value = `${latlng.lat}, ${latlng.lng}`;
                isSelectingDelivery = false; // Disable Delivery selection mode
            } else {
                console.error("Delivery location input not found.");
            }
        }
    });
}

// Fonction pour activer le mode de sélection de Pickup
function enablePickupSelection() {
    isSelectingPickup = true;
    isSelectingDelivery = false;
    alert("Click on the map to select the Pickup location.");
}

// Fonction pour activer le mode de sélection de Delivery
function enableDeliverySelection() {
    isSelectingPickup = false;
    isSelectingDelivery = true;
    alert("Click on the map to select the Delivery location.");
}
