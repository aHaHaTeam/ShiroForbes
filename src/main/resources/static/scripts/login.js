const login = document.getElementById("login")
const password = document.getElementById("password")
const span = document.getElementById("span")
const progress = document.getElementById("progress")
const form = document.querySelector("form")

form.addEventListener("submit", (e) => {
    // document.getElementById("signIn").disable()
    e.preventDefault();
    const data = {
        "login": login.value,
        "password": CryptoJS.SHA256(password.value).toString(),
    }

    span.style.display = "none"
    progress.style.display = ""
    fetch("/login", {
        method: "POST",
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify(data)
    }).then(response => {
        console.log(response)
        if (response.redirected) {
            window.location.assign(response.url)
        } else {
            span.style.display = ""
            progress.style.display = "none"
            password.parentElement.classList.add("invalid")
            login.parentElement.classList.add("invalid")
            password.parentElement.insertAdjacentHTML("beforeend", "<span class='error'>Invalid login or password</span>")
        }
    })
})
