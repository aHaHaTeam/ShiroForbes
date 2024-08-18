const datetimePicker = document.getElementById("datetimePicker")

function getDateTime() {
    if (datetimePicker.value === "") {
        const date = new Date();
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
        return currentDate + "T" + currentTime
    }
    return datetimePicker.value
}
