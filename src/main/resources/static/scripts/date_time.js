const datePicker = document.getElementById("datePicker")
const timePicker = document.getElementById("timePicker")

const date = new Date();
const dates = date.toLocaleDateString().split("/")

if (dates[0].length === 1) {
    dates[0] = "0" + dates[0]
}

const currentDate = dates[2] + "-" + dates[0] + "-" + dates[1];
const currentTime = date.toLocaleTimeString().substring(0, 5);
datePicker.value = currentDate;
timePicker.value = currentTime;