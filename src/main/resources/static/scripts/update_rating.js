const urbanSpan = document.getElementById("urbanSpan")
const urbanProgress = document.getElementById("urbanProgress")
const urbanForm = document.getElementById("urbanForm")
const countrysideSpan = document.getElementById("countrysideSpan")
const countrysideProgress = document.getElementById("countrysideProgress")
const countrysideForm = document.getElementById("countrysideForm")


urbanForm.addEventListener("submit", (e) => {
    e.preventDefault();
    urbanSpan.style.display = "none"
    urbanProgress.style.display = ""
    fetch("/update/urban/rating", {
        method: "POST",
        headers: {'Content-Type': 'application/json'},
        body: "{}"
    }).then(response => {
        if (response.redirected) {
            window.location.assign(response.url)
        } else {
            alert("Something went wrong")
            urbanSpan.style.display = ""
            urbanProgress.style.display = "none"
        }
    })
})

countrysideForm.addEventListener("submit", (e) => {
    e.preventDefault();
    countrysideSpan.style.display = "none"
    countrysideProgress.style.display = ""
    fetch("/update/countryside/rating", {
        method: "POST",
        headers: {'Content-Type': 'application/json'},
        body: "{}"
    }).then(response => {
        if (response.redirected) {
            window.location.assign(response.url)
        } else {
            alert("Something went wrong")
            countrysideSpan.style.display = ""
            countrysideProgress.style.display = "none"
        }
    })
})