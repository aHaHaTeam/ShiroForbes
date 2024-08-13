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