const countrysideButton = document.getElementById("countryside");
const urbanButton = document.getElementById("urban");
const countrysideContent = document.getElementById("countrysideContent")
const urbanContent = document.getElementById("urbanContent")
const ratingDownloadButton = document.getElementById("ratingDownloadButton")

countrysideButton.addEventListener("click",() => {
        countrysideButton.classList.add("fill");
        urbanButton.classList.remove("fill");
    ratingDownloadButton.onclick =
        countrysideContent.hidden = false;
    urbanContent.hidden = true;
    }
)

urbanButton.addEventListener("click",() => {
        urbanButton.classList.add("fill");
        countrysideButton.classList.remove("fill");
    countrysideContent.hidden = true;
    urbanContent.hidden = false;
    }
)
