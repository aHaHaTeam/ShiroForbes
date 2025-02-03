import Logo from "../components/Logo.jsx"
import {useState} from "react";

function Home() {
    const [inProgress, setInProgress] = useState(false);

    return (
        <div>
            <Logo />
            <h1>Главная страница</h1>
            <p>Добро пожаловать!</p>
            <button onClick={() => setInProgress(!inProgress)} className="extra round center" id="signIn" type="submit">
                {!inProgress && <span id="span">Sign In</span>}
                {inProgress && <progress className="circle" id="progress"></progress>}
            </button>
        </div>

    );
}

export default Home;
