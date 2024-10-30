const TEST = true

document.addEventListener("DOMContentLoaded", function() {

    document.getElementById('confirmMapButton').style.visibility = "hidden"

    var locationButtons = document.querySelectorAll(".locationButton")
    var addButtons =  document.querySelectorAll(".addButton")

    locationButtons.forEach(function(button) {
        button.style.backgroundColor = TEST? "rgb(72, 140, 137)" : "rgb(189, 221, 210)"

        button.addEventListener("mouseover", function() {
            button.style.backgroundColor = TEST? "rgb(42, 110, 107)" : "rgb(189, 221, 210)"
        })

        button.addEventListener("mouseout", function() {
            button.style.backgroundColor = TEST? "rgb(72, 140, 137)" : "rgb(189, 221, 210)"
        })
    })

    addButtons.forEach(function(button) {
        button.style.backgroundColor = TEST? "rgb(72, 140, 137)" : "rgb(189, 221, 210)"

        button.addEventListener("mouseover", function() {
            button.style.backgroundColor = TEST? "rgb(42, 110, 107)" : "rgb(189, 221, 210)"
        })

        button.addEventListener("mouseout", function() {
            button.style.backgroundColor = TEST? "rgb(72, 140, 137)" : "rgb(189, 221, 210)"
        })
    })
})

function updateFileName(inputId, titleId, confirmButtonId) {
    const input = document.getElementById(inputId);
    const fileTitle = document.getElementById(titleId);
    const button = document.getElementById(confirmButtonId)

    if (input && input.files.length > 0 && fileTitle) {
        const fileName = input.files[0].name;
        fileTitle.textContent = fileName;
        fileTitle.style.color = "rgb(0, 0, 255)";
        if (button) button.style.visibility = "visible";
    } else {
        if (fileTitle) fileTitle.textContent = "select a file";
    }
}

// Fonction pour envoyer le fichier de livraison XML au serveur
function uploadTourFile() {
    const tourFileInput = document.getElementById('tourFileInput');
    const tourFile = tourFileInput.files[0];

    if (!tourFile) {
        alert("Please select a tour file to upload.");
        return;
    }

    const formData = new FormData();
    formData.append("file", tourFile);

    fetch('/uploadTour', {
        method: 'POST',
        body: formData
    })
        .then(response => response.text())
        .then(data => {
            console.log("Tour file uploaded:", data);
            alert(data);  // Affiche le message du serveur

            loadDeliveries();  // Charge la liste des livraisons dans le div

            // Debug: vérifiez si la carte est initialisée
            console.log("Map initialized:", typeof map !== 'undefined');

            if (typeof map !== 'undefined') {
                console.log("Calling loadMapPoints...");
                loadMapPoints();   // Affiche les points sur la carte
            } else {
                console.error("La carte n'est pas initialisée. Assurez-vous d'avoir appelé displayMap().");
            }
        })
        .catch(error => {
            console.error('Error uploading tour file:', error);
            alert("Error uploading the tour file: " + error.message);
        });
}


// Fonction pour récupérer les tournées optimisées
async function fetchOptimalTour() {
    try {
        const response = await fetch('/optimalTour');
        const data = await response.json();

        if (data.error) {
            throw new Error(data.error);
        }

        console.log("Optimal Tour Data:", data.optimalTour);

        // Afficher la tournée optimisée
        displayOptimalTour(data.optimalTour);

    } catch (error) {
        console.error('Error fetching optimal tour:', error);
        alert("Error fetching optimal tour: " + error.message);
    }
}

// Fonction pour récupérer et afficher les livraisons dans la div deliveriesListBox
async function loadDeliveries() {
    try {
        const response = await fetch('/deliveries');
        if (!response.ok) {
            throw new Error(`Failed to load deliveries: ${response.statusText}`);
        }

        const deliveries = await response.json();
        const deliveriesListBox = document.querySelector(".deliveriesListBox");
        deliveriesListBox.innerHTML = "";  // Clear any existing content

        // Remplir deliveriesListBox avec chaque livraison
        deliveries.forEach(delivery => {
            const deliveryItem = document.createElement("div");
            deliveryItem.classList.add("delivery-item");
            deliveryItem.innerHTML = `
                <p><strong>Pickup:</strong> ${delivery.pickupLocation}</p>
                <p><strong>Delivery:</strong> ${delivery.deliveryLocation}</p>
                <p><strong>Pickup Time:</strong> ${delivery.pickupTime}</p>
                <p><strong>Delivery Time:</strong> ${delivery.deliveryTime}</p>
            `;
            deliveriesListBox.appendChild(deliveryItem);
        });

    } catch (error) {
        console.error('Error loading deliveries:', error);
        alert("Could not load deliveries: " + error.message);
    }
}

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
                }).addTo(map).bindPopup(`Pickup ${index + 1}`);

                const nodeDelivery = nodes.find(node => node.id === delivery.deliveryLocation);
                L.marker([nodeDelivery.latitude, nodeDelivery.longitude], {
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
