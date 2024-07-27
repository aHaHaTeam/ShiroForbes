const investorStatus = document.getElementById("investorStatus")
const investorCheckbox = document.getElementById("investorCheckbox")

if (investorCheckbox.checked) {
    investorStatus.textContent = "Точно инвестирую!";
} else {
    investorStatus.textContent = "Думаю, не сегодня...";
}
const initialValue = investorCheckbox.checked
userId = /*[[${user.id}]]*/""
investorCheckbox.addEventListener("change", () => {
    this.checked = !this.checked
    if (this.checked !== initialValue) {
        investorStatus.textContent = "Точно инвестирую!";
    } else {
        investorStatus.textContent = "Думаю, не сегодня...";
    }
    const data = {
        isInvesting: this.checked !== initialValue,
    }
    fetch("/profile/investing/" + userId, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json' // Send data as JSON
        },
        body: JSON.stringify(data)
    }).then(() => {
        console.log("updated")
    })
})
