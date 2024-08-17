const datePicker = document.getElementById("datePicker")
const timePicker = document.getElementById("timePicker")

const date = new Date();
const dates = date.toLocaleDateString().split(/[./-]/)

let currentDay = date.getDate().toString()
while (currentDay.length < 2) {
    currentDay = "0" + currentDay
}
let currentMonth = (date.getMonth() + 1).toString()
while (currentMonth.length < 2) {
    currentMonth = "0" + currentMonth
}
let currentYear = date.getFullYear().toString()

let currentDate = currentYear + "-" + currentMonth + "-" + currentDay;

let currentMinutes = date.getMinutes().toString()
while (currentMinutes.length < 2) {
    currentMinutes = "0" + currentMinutes
}

let currentHours = date.getHours().toString()
while (currentHours.length < 2) {
    currentHours = "0" + currentHours
}
const currentTime = currentHours + ":" + currentMinutes;

function getTime() {
    if (document.getElementById("timePicker").value === "") {
        return currentTime
    }
    return document.getElementById("timePicker").value
}

function getDate() {
    if (document.getElementById("datePicker").value === "") {
        return currentDate
    }
    return document.getElementById("datePicker").value
}
