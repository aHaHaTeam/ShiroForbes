function addTransaction(listOfTransactions, transaction) {
    let color = "gray"
    if (transaction.size < 0) {
        color = "red"
    } else if (transaction.size > 0) {
        color = "green"
    }
    let sign = ""
    if (transaction.size >= 0) {
        sign = "+"
    }
    listOfTransactions.insertAdjacentHTML("afterbegin",
        "<tr>" +
        "   <td class='center-align'>\n" +
        "       <button class='circle remove-button fill'>\n" +
        "           <span hidden='hidden'>" + transaction.id + "</span>\n" +
        "           <i>remove</i>\n" +
        "       </button>\n" +
        "   </td>" +
        "   <td class='center-align'>" + transaction.time + "</td>\n" +
        "   <td class='center-align'>" + transaction.date + "</td>\n" +
        "   <td class='center-align'>" + transaction.student.name + "</td>\n" +
        "   <td class='center-align'>\n" +
        "       <span style='color: " + color + "'>" + sign + transaction.size + "</span>\n" +
        "   </td>\n" +
        "   <td class='center-align' style='text-wrap: pretty;'>" + transaction.description + "</td>\n" +
        "</tr>"
    )
}

fetch("/transactions/all").then(response => {
    if (!response.ok) {
        document.getElementById("progress")
        return
    }
    return response.json()
}).then((transactions) => {
    if (transactions.hasOwnProperty("Countryside")) {
        transactions["Countryside"].map(function (transaction) {
            let countrysideList = document.getElementById("countrysideContent").getElementsByTagName("tbody")[0];
            addTransaction(countrysideList, transaction)
        })
    }
    if (transactions.hasOwnProperty("Urban")) {
        transactions["Urban"].map(function (transaction) {
            let urbanList = document.getElementById("urbanContent").getElementsByTagName("tbody")[0];
            addTransaction(urbanList, transaction)
        })
    }

    const removeButtons = document.getElementsByClassName("remove-button")
    for (let i = 0; i < removeButtons.length; i++) {
        removeButtons[i].addEventListener("click", () => {
            console.log(removeButtons[i].childNodes[1].textContent)
            const transactionId = removeButtons[i].childNodes[1].textContent
            fetch("/transactions/delete/" + transactionId, {
                method: "POST",
                headers: {'Content-Type': 'application/json'},
                body: JSON.stringify({})
            }).then(() => {
                removeButtons[i].parentElement.parentElement.hidden = true
            })
        })
    }
})
