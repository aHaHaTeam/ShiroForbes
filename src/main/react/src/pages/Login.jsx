import Logo from "../components/Logo.jsx";
import {useState} from "react";
import {setToken} from "../scripts/util/localToken.jsx";

const API_URL = import.meta.env.VITE_API_URL;

function Login() {
    const [isLoading, setIsLoading] = useState(false);
    const [login, setLogin] = useState("");
    const [password, setPassword] = useState("");

    const handleSubmit = async (event) => {
        setIsLoading(true);
        event.preventDefault();

        try {
            const response = await fetch(`${API_URL}api/login`, {
                method: "POST",
                headers: {"Content-Type": "application/json"},
                body: JSON.stringify({login, password}),
            });

            if (!response.ok) {
                throw new Error("Неверные учетные данные");
            }

            const data = await response.json();
            setToken(data.token);
        } catch (err) {
            alert(err);
        }
        setIsLoading(false);
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
                                    onChange={(e) => setLogin(e.target.value)}
                                    required
                                />
                                <label htmlFor="login">Login</label>
                            </div>
                            <div className="medium-space"></div>
                            <div className="field label border">
                                <input
                                    id="password"
                                    type="text"
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