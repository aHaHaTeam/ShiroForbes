const exercisesTab = document.getElementById("exercisesTab")
const promenadeTab = document.getElementById("promenadeTab")
const curfewTab = document.getElementById("curfewTab")
const activityType = document.getElementById("activityType")

exercisesTab.onclick = () => {
    exercisesTab.classList.add("active")
    promenadeTab.classList.remove("active")
    curfewTab.classList.remove("active")
    activityType.textContent = "exercises"
}

promenadeTab.onclick = () => {
    exercisesTab.classList.remove("active")
    promenadeTab.classList.add("active")
    curfewTab.classList.remove("active")
    activityType.textContent = "promenade"
}

curfewTab.onclick = () => {
    exercisesTab.classList.remove("active")
    promenadeTab.classList.remove("active")
    curfewTab.classList.add("active")
    activityType.textContent = "curfew"
}

const switchAll = document.getElementById("switchAll")
const switches = document.getElementsByClassName("regularSwitch")
switchAll.onclick = () => {
    for (let i = 0; i < switches.length; i++) {
        if (switches[i].checked !== switchAll.checked) {
            switches[i].checked = switchAll.checked
        }
    }
}

for (let j = 0; j < switches.length; j++) {
    switches[j].onclick = () => {
        for (let i = 0; i < switches.length; i++) {
            if (switches[i].checked !== switches[j].checked) {
                if (switchAll.checked === true) {
                    switchAll.checked = false
                }
                return
            }
        }
        if (switchAll.checked !== switches[j].checked) {
            switchAll.checked = true
        }
    }
}