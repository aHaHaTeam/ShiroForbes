const semesters12Button = document.getElementById("semesters12Button");
const semester2Button = document.getElementById("semester2Button");
const semesters12Content = document.getElementById("semesters12Content")
const semester2Content = document.getElementById("semester2Content")

semesters12Button.addEventListener("click", () => {
        semesters12Button.classList.add("fill");
        semester2Button.classList.remove("fill");

        semesters12Content.hidden = false;
        semester2Content.hidden = true;
    }
)

semester2Button.addEventListener("click", () => {
        semester2Button.classList.add("fill");
        semesters12Button.classList.remove("fill");
        semesters12Content.hidden = true;
        semester2Content.hidden = false;
    }
)

