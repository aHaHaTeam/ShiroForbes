const investorStatus = document.getElementById("investorStatus")
const investorCheckbox = document.getElementById("investorCheckbox")

if (investorCheckbox.checked) {
    investorStatus.textContent = "Точно инвестирую!";
} else {
    investorStatus.textContent = "Думаю, не сегодня...";
}
const initialValue = investorCheckbox.checked
investorCheckbox.addEventListener("change", () => {
    this.checked = !this.checked
    if (this.checked !== initialValue) {
        investorStatus.textContent = "Точно инвестирую!";
    } else {
        investorStatus.textContent = "Думаю, не сегодня...";
    }
})
