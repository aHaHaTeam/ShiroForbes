import React from "react";

const setTheme = (body, theme) => {
    if (theme === 'dark') {
        body.classList.add('dark-theme');
        body.classList.remove('light-theme');
    } else {
        body.classList.add('light-theme');
        body.classList.remove('dark-theme');
    }
};

export function setDefaultTheme(body) {
    const prefersDarkScheme = window.matchMedia('(prefers-color-scheme: dark)').matches;
    body.classList.remove('light');
    body.classList.remove('dark');
    setTheme(body,prefersDarkScheme ? 'dark' : 'light');
}

function ThemeButton(props) {
    const body = props.body;
    const [isDark, setDark] = React.useState(body.classList.contains('dark-theme'));

    const changeTheme = () => {
        setTheme(body, !isDark ? 'dark' : 'light');
        setDark(!isDark);
    }

    return (
        <div id="ThemeButton" onClick={changeTheme}>
            <button className="circle small transparent horizontal no-margin" id="themeButton">
                {!isDark && <i id="moon">dark_mode</i>}
                {isDark && <i id="sun">light_mode</i>}
            </button>
        </div>
    )
}

export default ThemeButton