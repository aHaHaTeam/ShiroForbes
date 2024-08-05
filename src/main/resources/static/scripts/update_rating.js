const span = document.getElementById("span")
const progress = document.getElementById("progress")
const form = document.querySelector("form")


form.addEventListener("submit", (e) => {
    e.preventDefault();

    span.style.display = "none"
    progress.style.display = ""
    fetch("/update/rating", {
        method: "POST",
        headers: {'Content-Type': 'application/json'},
        body: "{}"
    }).then(response => {
        if (response.redirected) {
            window.location.assign(response.url)
        } else {
            alert("Something went wrong")
            span.style.display = ""
            progress.style.display = "none"
        }
    })
})