import React from 'react';
import Logo from "./Logo.jsx";
import ThemeButton from "./ThemeButton.jsx";
import LogoutButton from "./LogoutButton.jsx";


function Header() {
    return (
        <header className="fixed fill small-round">
            <nav className="grid">
                <div className="s1 left-align">
                    <div>
                        <a className="circle transparent horizontal" href="/grobarium">
                            <i className="extra middle icon-overleaf">
                            </i>
                        </a>
                    </div>
                </div>
                <div className="s1"></div>

                <div className="center s8"><Logo/></div>

                <div className="s1 center-align"><ThemeButton/></div>
                <div className="s1 right-align"><LogoutButton/></div>
            </nav>
        </header>
    )
}

export default Header;
