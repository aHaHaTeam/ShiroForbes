import React from "react";
import {setToken} from "../scripts/util/jsonWebToken.jsx";
import {setLogin, setRights} from "../scripts/util/userInfo.jsx";

function logout() {
    setToken("no-token")
    setRights("")
    setLogin("")
    document.location.assign("/login");
}


function LogoutButton() {
    return (
        <div className="s1 right-align">
            <div>
                <button className="circle transparent horizontal" onClick={logout}>
                    <i>logout</i>
                </button>
            </div>
        </div>
    )
}

export default LogoutButton
