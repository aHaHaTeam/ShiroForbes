const countrysideButton = document.getElementById("countryside");
const urbanButton = document.getElementById("urban");
const countrysideTable = document.getElementById("countrysideTable")
const urbanTable = document.getElementById("urbanTable")
const ratingDownloadButton = document.getElementById("ratingDownloadButton")

countrysideButton.addEventListener("click",() => {
        countrysideButton.classList.add("fill");
        urbanButton.classList.remove("fill");
    ratingDownloadButton.onclick =
        countrysideTable.hidden = false;
        urbanTable.hidden = true;
    }
)

urbanButton.addEventListener("click",() => {
        urbanButton.classList.add("fill");
        countrysideButton.classList.remove("fill");
        countrysideTable.hidden = true;
        urbanTable.hidden = false;
    }
)
