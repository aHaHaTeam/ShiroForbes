const eventName = document.getElementById("eventName")
const timeAndPlace = document.getElementById("timeAndPlace")
const eventDescription = document.getElementById("eventDescription")

const eventNamePreview = document.getElementById("eventNamePreview")
const timeAndPlacePreview = document.getElementById("timeAndPlacePreview")
const eventDescriptionPreview = document.getElementById("eventDescriptionPreview")

const writeTab = document.getElementById("writeTab")
const previewTab = document.getElementById("previewTab")

const write = document.getElementById("writeBlock")
const preview = document.getElementById("previewBlock")
const progress = document.getElementById("progress")
const content = document.getElementById("content")

const form = document.querySelector("form")

writeTab.onclick = () => {
    writeTab.classList.add("active")
    previewTab.classList.remove("active")
    write.style.display = "block"
    preview.hidden = true
}

previewTab.onclick = async () => {
    writeTab.classList.remove("active")
    previewTab.classList.add("active")
    write.style.display = "none"
    preview.hidden = false

    content.style.display = "none"
    progress.style.display = ""
    let html = await fetch("/convert/markdown", {
        method: "POST",
        headers: {'Content-Type': 'application/json'},
        body: eventDescription.value

    }).then(response => {
        let html
        if (response.redirected) {
            window.location.assign(response.url)
        }
        if (response.ok) {
            return response.text().finally()
        } else {
            return response.statusText
        }
    })
    content.style.display = ""
    progress.style.display = "none"
    eventNamePreview.innerText = eventName.value
    timeAndPlacePreview.innerText = timeAndPlace.value
    eventDescriptionPreview.innerHTML = html
}

form.addEventListener("submit", (e) => {
    e.preventDefault();
    const group = (document.getElementById("countryside").classList.contains("fill")) ? "Countryside" : "Urban";
    const data = {
        "group": group,
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
            if (response.ok) {
                alert("Успех")
            }
        }
    })
})

// Textarea resizing
const tx = document.getElementsByTagName("textarea");

for (let i = 0; i < tx.length; i++) {
    tx[i].setAttribute("style", "height:" + (tx[i].scrollHeight) + "px;overflow-y:hidden;");
    tx[i].addEventListener("input", resize, false);
}

function resize() {
    this.style.height = 'auto';
    this.style.height = (this.scrollHeight) + "px";
    this.style.setProperty("--size", (this.scrollHeight) + "px")
    this.parentElement.style.setProperty("---size", this.scrollHeight + this.paddingTop + "px");
}

