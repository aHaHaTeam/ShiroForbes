import {Link} from "react-router-dom";

function Navbar() {
    return (
        <nav>
            <ul>
                <li><Link to="/">Главная</Link></li>
                <li><Link to="/about">О нас</Link></li>
                <li><Link to="/users">Пользователи</Link></li>
                <li><Link to="/login">Login</Link></li>
                <li><Link to={"/rating"}>Rating</Link></li>
            </ul>
        </nav>
    );
}

export default Navbar;
