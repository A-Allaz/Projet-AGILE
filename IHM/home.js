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

    if (input.files.length > 0) {
        const fileName = input.files[0].name
        fileTitle.textContent = fileName
        fileTitle.style.color = "rgb(0, 0, 255)"
        button.style.visibility = "visible"
    } else {
        fileTitle.textContent = "select a file"
    }
}
