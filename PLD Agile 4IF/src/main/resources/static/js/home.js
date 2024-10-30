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