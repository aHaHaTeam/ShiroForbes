const offerName = document.getElementById("offerName")
const offerDescription = document.getElementById("offerDescription")
const price = document.getElementById("price")

const form = document.querySelector("form")


form.addEventListener("submit", (e) => {
    e.preventDefault();
    let data = {
        "action": "add",
        "offerName": offerName.value,
        "offerDescription": offerName.value,
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
            document.getElementById("submitButton").enable()
            if (response.ok) {
                alert("Успех")
            }
        }
    })
})

document.querySelectorAll("button").forEach(function (elem) {
    elem.addEventListener('click', (event) => {
        const isButton = event.target.classList.contains("removeOffer");
        if (!isButton) {
            return;
        }
        const offerId = elem.id
        let data = {
            "action": "remove",
            "id": offerId,
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
                    elem.hidden = true
                }
            }
        })
    })
})