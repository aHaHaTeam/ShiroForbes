const eventName = document.getElementById("eventName")
const timeAndPlace = document.getElementById("timeAndPlace")
const eventDescription = document.getElementById("eventDescription")

const form = document.querySelector("form")


form.addEventListener("submit", (e) => {
    e.preventDefault();
    const data = {
        "eventName": eventName.value,
        "timeAndPlace": timeAndPlace.value,
        "eventDescription": eventDescription.value,
    }


    fetch("/event/new", {
        method: "POST",
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify(data)
    }).then(response => {
        if (response.redirected) {
            window.location.assign(response.url)
        } else {
            if(response.ok){
                alert("Успех")
            }
        }
    })
})