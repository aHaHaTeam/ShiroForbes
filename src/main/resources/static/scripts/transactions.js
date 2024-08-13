const searchBar = document.getElementById("searchBar")
const options = document.getElementsByClassName("option")
const listOfTransactions = document.getElementById("listOfTransactions")
let transactions = new Map();

let logins = new Map();

const form = document.querySelector("form")

for (let i = 0; i < options.length; i++) {
    let div = options[i].getElementsByTagName("div")[0];
    let div2 = options[i].getElementsByTagName("div")[1];
    let txtValue = div.textContent || div.innerText;
    let login = div2.textContent || div2.innerText
    transactions.set(txtValue.toLowerCase(), false)
    logins.set(txtValue.toLowerCase(), login)
}

searchBar.onfocus = () => {
    updateOptions()
}
searchBar.oninput = () => {
    updateOptions()
}

function updateOptions() {
    let filter = searchBar.value.toLowerCase();
    for (let i = 0; i < options.length; i++) {
        let div = options[i].getElementsByTagName("div")[0];
        let name = (div.textContent || div.innerText).toLowerCase();
        if ((transactions.get(name) === true) || (name.indexOf(filter) <= -1)) {
            options[i].style.display = "none"
        } else {
            options[i].style.display = ""
        }
    }
}

for (let i = 0; i < options.length; ++i) {
    options[i].onclick = () => {
        addTransaction(options[i].getElementsByTagName("div")[0].innerText)
    }
}

function addTransaction(name) {
    transactions.set(name.toLowerCase(), true)
    listOfTransactions.insertAdjacentHTML("afterbegin",
        "<div>" +
        "<a class=\"row\">" +
        "<i class='remove' onclick='removeTransaction(this)'>remove</i>" +
        "<div class=\"max\">" +
        "<h6 class=\"small\">" + name + "</h6>" +
        "</div>" +
        "<div class=\"field label border round\">" +
        "<input type=\"number\" id='" + name.toLowerCase() + "' required name='" + name + "'>" +
        "<label>ZLT</label>" +
        "</div>" +
        "</a>" +
        "<div class=\"small-space\"></div>" +
        "</div>"
    )
}

function removeTransaction(icon) {
    let div = icon.parentElement.getElementsByTagName("div")[0];
    let name = (div.textContent || div.innerText).toLowerCase();
    transactions.set(name, false)
    listOfTransactions.removeChild(icon.parentElement.parentElement)
}

form.addEventListener("submit", (e) => {
    e.preventDefault();
    let data = {
        "transactionName": document.getElementById("transactionName").value,
        "date": document.getElementById("datePicker").value,
        "time": document.getElementById("timePicker").value,
    }
    for (let [n, b] of transactions.entries()) {
        if (b) {
            data[logins.get(n)] = document.getElementById(n).value
        }
    }
    fetch("/transactions/transactions", {
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