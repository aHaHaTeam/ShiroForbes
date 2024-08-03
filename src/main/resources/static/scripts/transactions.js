const searchBar = document.getElementById("searchBar")
const options = document.getElementsByClassName("option")
const listOfTransactions = document.getElementById("listOfTransactions")
let transactions = new Map();

for (let i = 0; i < options.length; i++) {
    let div = options[i].getElementsByTagName("div")[0];
    let txtValue = div.textContent || div.innerText;
    transactions.set(txtValue.toLowerCase(), false)
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
        "<input type=\"number\" required name='" + name + "'>" +
        "<label>ZLT</label>" +
        "</div>" +
        "</a>" +
        "<div class=\"small-space\"></div>" +
        "</div>")
}

function removeTransaction(icon) {
    let div = icon.parentElement.getElementsByTagName("div")[0];
    let name = (div.textContent || div.innerText).toLowerCase();
    transactions.set(name, false)
    listOfTransactions.removeChild(icon.parentElement.parentElement)
}