const offerName = document.getElementById("offerName")
const offerDescription = document.getElementById("offerDescription")
const price = document.getElementById("price")

const form = document.querySelector("form")


form.addEventListener("submit", (e) => {
    e.preventDefault();
    let data = {
        "action": "add",
        "offerName": offerName.value,
        "offerDescription": offerDescription.value,
        "offerPrice": price.value,
    }


    fetch("/marketplace", {
        method: "POST",
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify(data)
    }).then(response => {
        if (response.redirected) {
            window.location.assign(response.url)
        } else {
            if (response.ok) {
                alert("Успех")
            }
        }
    })
})

document.getElementById("listOfOffers").addEventListener('click', (event) => {
    const isButton = event.target.nodeName === 'BUTTON';
    if (!isButton) {
        return;
    }
    const offerId = event.target.id
    let data = {
        "action": "remove",
        "id" : offerId,
    }

    fetch("/marketplace", {
        method: "POST",
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify(data)
    }).then(response => {
        if (response.redirected) {
            window.location.assign(response.url)
        } else {
            if (response.ok) {
                alert("Успех")
            }
        }
    })
})