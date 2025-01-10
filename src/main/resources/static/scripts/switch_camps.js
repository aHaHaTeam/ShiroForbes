const countrysideButton = document.getElementById("countryside");
const urbanButton = document.getElementById("urban");
const countrysideContent = document.getElementById("countrysideContent")
const urbanContent = document.getElementById("urbanContent")

countrysideButton.addEventListener("click", () => {
        countrysideButton.classList.add("fill");
        urbanButton.classList.remove("fill");

        countrysideContent.hidden = false;
        urbanContent.hidden = true;
    }
)

urbanButton.addEventListener("click", () => {
        urbanButton.classList.add("fill");
        countrysideButton.classList.remove("fill");
        countrysideContent.hidden = true;
        urbanContent.hidden = false;
    }
)
