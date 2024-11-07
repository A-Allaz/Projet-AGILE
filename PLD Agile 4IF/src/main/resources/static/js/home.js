const TEST = true
var deliveries;
var modyfingDelivery = false;   // Indique si une livraison est en cours de modification

document.addEventListener("DOMContentLoaded", function() {

    document.getElementById('confirmMapButton').style.visibility = "hidden"

    var locationButtons = document.querySelectorAll(".locationButton")
    var addButtons =  document.querySelectorAll(".addButton")

    locationButtons.forEach(function(button) {
        button.style.backgroundColor = TEST? "rgb(72, 140, 137)" : "rgb(189, 221, 210)"

        button.addEventListener("mouseover", function () {
            button.style.backgroundColor = TEST ? "rgb(42, 110, 107)" : "rgb(189, 221, 210)"
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
        displayTimeEstimates(data.timeEstimates);

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

        deliveries = (await response.json()).map((delivery, index) => ({ ...delivery, id: index + 1 }));
        const deliveriesListBox = document.querySelector(".deliveriesListBox");
        deliveriesListBox.innerHTML = "";  // Clear any existing content

        // Remplir deliveriesListBox avec chaque livraison
        deliveries.forEach(delivery => {
            const deliveryItem = document.createElement("div");
            deliveryItem.classList.add("delivery-item");
            deliveryItem.innerHTML = `
                <h3>Delivery #${delivery.id}</h3>
                <p><strong>Pickup:</strong> ${delivery.pickupLocation}</p>
                <p><strong>Delivery:</strong> ${delivery.deliveryLocation}</p>
                <p><strong>Pickup Time:</strong> ${delivery.pickupTime}</p>
                <p><strong>Delivery Time:</strong> ${delivery.deliveryTime}</p>
                <button onclick="deleteDelivery(${delivery.id})">Delete</button>
                <button onclick="showEditForm(${delivery.id})">Modify</button>
            `;
            deliveriesListBox.appendChild(deliveryItem);
        });

    } catch (error) {
        console.error('Error loading deliveries:', error);
        alert("Could not load deliveries: " + error.message);
    }
}

// Function to send a new delivery to the server
function addNewDelivery() {
    // Get the input values
    const pickupLocationInput = document.getElementById('pickupLocationInput');
    const deliveryLocationInput = document.getElementById('deliveryLocationInput');
    const pickupTimeInput = document.querySelector("input[placeholder='Pickup time:']");
    const deliveryTimeInput = document.querySelector("input[placeholder='Delivery time:']");

    if (!pickupLocationInput || !deliveryLocationInput || !pickupTimeInput || !deliveryTimeInput) {
        alert("Please fill in all fields.");
        return;
    }

    const pickupLocation = pickupLocationInput.value;
    const deliveryLocation = deliveryLocationInput.value;
    const pickupTime = pickupTimeInput.value;
    const deliveryTime = deliveryTimeInput.value;

    // Validate the input values
    if (!pickupLocation || !deliveryLocation || !pickupTime || !deliveryTime) {
        alert("Please fill in all fields.");
        return;
    }

    // Split the coordinates into latitude and longitude
    const [pickupLat, pickupLng] = pickupLocation.split(',').map(coord => parseFloat(coord.trim()));
    const [deliveryLat, deliveryLng] = deliveryLocation.split(',').map(coord => parseFloat(coord.trim()));

    // Find the nearest nodes
    const nearestPickupNode = findNearestNode(pickupLat, pickupLng);
    const nearestDeliveryNode = findNearestNode(deliveryLat, deliveryLng);

    if (!nearestPickupNode || !nearestDeliveryNode) {
        alert("Could not find nearest nodes for the selected locations.");
        return;
    }

    // Call the function to send the new delivery to the back-end
    addDeliveryToServer(nearestPickupNode.id, nearestDeliveryNode.id, pickupTime, deliveryTime);
}

// Fonction pour supprimer une livraison
function deleteDelivery(deliveryId) {
    fetch(`/deleteDelivery/${deliveryId}`, { method: 'DELETE' })
        .then(response => response.json())
        .then(data => {
            if (data.status === 'success') {
                alert(data.message);
                deliveries = deliveries.filter(d => d.id !== deliveryId);  // Remove the deleted delivery
                loadDeliveries(); // Recharger la liste des livraisons
                loadMapPoints();  // Recharger les points sur la carte
            } else {
                alert("Error: " + data.message);
            }
        })
        .catch(error => console.error('Error deleting delivery:', error));
}

function showEditForm(deliveryId) {
    modyfingDelivery = true;
    const delivery = deliveries.find(d => d.id === deliveryId);
    if (!delivery) return;

    const editForm = document.createElement("div");
    editForm.classList.add("edit-form");
    editForm.innerHTML = `
        <button class="locationButton" style="margin-left: 5%;" onclick="enablePickupSelection()">Change Pickup Location</button>
        <button class="locationButton" style="margin-left: 10%;" onclick="enableDeliverySelection()">Change Delivery Location</button>
        
        <input type="text" id="editPickupLocation" class="timeInput" style="margin-left: 5%;" placeholder="Pickup location:" value="${delivery.pickupLocation}" readonly>
        <input type="text" id="editDeliveryLocation" class="timeInput" style="margin-left: 10%;" placeholder="Delivery location:" value="${delivery.deliveryLocation}" readonly>
        
        <input type="number" id="editPickupTime" class="timeInput" style="margin-left: 5%;" placeholder="Pickup time:" value="${delivery.pickupTime}">
        <input type="number" id="editDeliveryTime" class="timeInput" style="margin-left: 10%;" placeholder="Delivery time:" value="${delivery.deliveryTime}">
        
        <button class="confirmButton" style="margin-left: 5%;" onclick="submitEditForm(${deliveryId})">Save Changes</button>
        <button class="cancelButton" style="margin-left: 10%;" onclick="cancelEdit()">Cancel</button>
`   ;

    // Add the form to the DOM
    const deliveriesListBox = document.querySelector(".deliveriesListBox");
    deliveriesListBox.innerHTML = "";  // Clear the list
    deliveriesListBox.appendChild(editForm);
}

// Function to hide the edit form and reload the list
function cancelEdit() {
    loadDeliveries();
}

function submitEditForm(deliveryId) {
    // Get the input values
    const pickupLocationInput = document.getElementById('editPickupLocation');
    const deliveryLocationInput = document.getElementById('editDeliveryLocation');
    const pickupTimeInput = document.getElementById('editPickupTime');
    const deliveryTimeInput = document.getElementById('editDeliveryTime');

    const pickupLocation = pickupLocationInput.value;
    const deliveryLocation = deliveryLocationInput.value;
    const pickupTime = pickupTimeInput.value;
    const deliveryTime = deliveryTimeInput.value;

    // Validate the input values
    if (!pickupLocation || !deliveryLocation || !pickupTime || !deliveryTime) {
        alert("Please fill in all fields.");
        return;
    }

    let nearestPickupNode, nearestDeliveryNode;

    // Split the coordinates into latitude and longitude if they are modified
    if (pickupLocation.includes(",")) {
        const [pickupLat, pickupLng] = pickupLocation.split(',').map(coord => parseFloat(coord.trim()));
        nearestPickupNode = findNearestNode(pickupLat, pickupLng);
    } else {
        console.log("pickupLocation:", pickupLocation);
        console.log(nodes.find(node => node.id === parseInt(pickupLocation)));
        nearestPickupNode = nodes.find(node => node.id === parseInt(pickupLocation));
    }

    if (deliveryLocation.includes(",")) {
        const [deliveryLat, deliveryLng] = deliveryLocation.split(',').map(coord => parseFloat(coord.trim()));
        nearestDeliveryNode = findNearestNode(deliveryLat, deliveryLng);
    } else {
        console.log("deliveryLocation:", deliveryLocation);
        console.log(nodes.find(node => node.id === parseInt((deliveryLocation))));
        nearestDeliveryNode = nodes.find(node => node.id === parseInt(deliveryLocation));
    }

    console.log(nearestPickupNode, nearestDeliveryNode)

    if (!nearestPickupNode || !nearestDeliveryNode) {
        alert("Could not find nearest nodes for the selected locations.");
        return;
    }

    // Call the function to send the updated delivery to the back-end
    updateDelivery(deliveryId, nearestPickupNode.id, nearestDeliveryNode.id, pickupTime, deliveryTime);
}

function updateDelivery(deliveryId, pickupLocation, deliveryLocation, pickupTime, deliveryTime) {
    const requestBody = {
        pickupLocation,
        deliveryLocation,
        pickupTime,
        deliveryTime
    };

    fetch(`/updateDelivery/${deliveryId}`, {
        method: 'PUT',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify(requestBody)
    })
        .then(response => response.json())
        .then(data => {
            if (data.status === "success") {
                alert(data.message);
                // Update the delivery in the list
                const updatedDelivery = deliveries.find(d => d.id === deliveryId);
                updatedDelivery.pickupLocation = pickupLocation;
                updatedDelivery.deliveryLocation = deliveryLocation;
                updatedDelivery.pickupTime = pickupTime;
                updatedDelivery.deliveryTime = deliveryTime;

                modyfingDelivery = false;

                loadDeliveries();  // Rafraîchir la liste des livraisons
                loadMapPoints();   // Mettre à jour les points sur la carte
            } else {
                console.error("Error:", data.message);
            }
        })
        .catch(error => console.error('Error updating delivery:', error));
}