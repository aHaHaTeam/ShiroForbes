const exercisesTab = document.getElementById("exercisesTab")
const promenadeTab = document.getElementById("promenadeTab")
const curfewTab = document.getElementById("curfewTab")
const activityType = document.getElementById("activityType")
const form = document.querySelector("form")

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

form.addEventListener("submit", (e) => {
    e.preventDefault();
    let data = {
        "activityType": activityType.textContent,
    }
    for (let i = 0; i < 1000; i++){
        const row = document.getElementById(i)
        if (row==null){
            continue
        }
        data[i] = row.checked
    }
    fetch("/transactions/exercises/countryside", {
        method: "POST",
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify(data)
    }).then(response => {
        if (response.redirected) {
            window.location.assign(response.url)
        } else {
            if(response.ok){
                alert("Успех")
            }
        }
    })
})