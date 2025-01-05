const themeButton = document.getElementById("themeButton");
const sun = document.getElementById("sun");
const moon = document.getElementById("moon");
const body = document.body;

const setTheme = (theme) => {
    if (theme === 'dark') {
        body.classList.add('dark-theme');
        body.classList.remove('light-theme');
        sun.style.display = "block";
        moon.style.display = "none";
    } else {
        body.classList.add('light-theme');
        body.classList.remove('dark-theme');
        sun.style.display = "none";
        moon.style.display = "block";
    }
};

const prefersDarkScheme = window.matchMedia('(prefers-color-scheme: dark)').matches;

setTheme(prefersDarkScheme ? 'dark' : 'light');

themeButton.addEventListener('click', () => {
    const currentTheme = body.classList.contains('dark-theme') ? 'light' : 'dark';
    setTheme(currentTheme);
});


