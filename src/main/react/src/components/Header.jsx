import React from 'react';
import Logo from "./Logo.jsx";
import ThemeButton from "./ThemeButton.jsx";


function Header(props) {
    const user = props.user;
    return (
        <header className="fixed fill small-round">
            <nav className="grid">
                <div className="s1 left-align">
                    {user !== 0 && <div>
                        <a className="circle transparent horizontal" href="/grobarium">
                            <i className="extra middle icon-overleaf">
                            </i>
                        </a>
                    </div>}
                </div>
                <div className="s1"></div>

                <div className="center s8"><Logo/></div>

                <div className="s1 center-align"><ThemeButton body={document.body}/></div>
                <div className="s1 right-align">
                    {user !== 0 && <div>
                        <a className="circle transparent horizontal" href="/logout">
                            <i>logout</i>
                        </a>
                    </div>}
                </div>
            </nav>
        </header>
    )
}

export default Header;