import Logo from "../components/Logo.jsx";
import {useState} from "react";
import {setToken} from "../scripts/util/jsonWebToken.jsx";
import sha256 from 'crypto-js/sha256';
import {getLogin, getRights, setLogin, setRights} from "../scripts/util/userInfo.jsx";

const API_URL = import.meta.env.VITE_API_URL;

function Login() {
    const [isLoading, setIsLoading] = useState(false);
    const [login, _setLogin] = useState("");
    const [password, setPassword] = useState("");

    const handleSubmit = async (event) => {
        setIsLoading(true);
        event.preventDefault();

        try {
            const hashedPassword = sha256(password).toString()
            const response = await fetch(`${API_URL}/api/login`, {
                method: "POST",
                headers: {"Content-Type": "application/json"},
                body: JSON.stringify({login, "password": hashedPassword}),
            });

            if (!response.ok) {
                console.error(response.statusText);
            }

            const data = await response.json();
            console.log(data.token)
            setToken(data.token);
            setLogin(login)
            setRights(data.rights)
        } catch (err) {
            console.error(err);
        }
        setIsLoading(false);
        if(getRights()==="Admin" || getRights()==="Teacher"){
            window.location.assign("/update_rating")
        }
        if(getRights()==="Student"){
            window.location.assign(`/profile/${getLogin()}`)
        }
    };

    return (
        <div className="responsive grid">
            <div className="s0 m1 l3"></div>
            <div className="s12 m10 l6">
                <div className="middle grid">
                    <div className="s0 m1 l2"></div>

                    <div className="s12 m10 l8">
                        <form onSubmit={handleSubmit}>
                            <div className="medium-space"></div>
                            <div>
                                <Logo/>
                            </div>
                            <div className="medium-space"></div>
                            <div className="field label border">
                                <input
                                    id="login"
                                    type="text"
                                    value={login}
                                    onChange={(e) => _setLogin(e.target.value)}
                                    required
                                />
                                <label htmlFor="login">Login</label>
                            </div>
                            <div className="medium-space"></div>
                            <div className="field label border">
                                <input
                                    id="password"
                                    type="password"
                                    value={password}
                                    onChange={(e) => setPassword(e.target.value)}
                                    required
                                />
                                <label htmlFor="password">Password</label>
                            </div>
                            <div className="medium-space"></div>
                            <button className="extra round" id="signIn" type="submit">
                                {!isLoading && <span id="span">Sign In</span>}
                                {isLoading && <progress className="circle" id="progress"></progress>}
                            </button>
                            <div className="medium-space"></div>
                        </form>
                    </div>

                    <div className="s0 m1 l2"></div>
                </div>
            </div>
            <div className="s0 m1 l3"></div>
        </div>);
}

export default Login;
