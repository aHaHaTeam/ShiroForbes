import {BrowserRouter, Routes, Route} from "react-router-dom";
import Home from "./pages/Home";
import About from "./pages/About";
import Users from "./pages/Users.jsx";
import Navbar from "./Navbar.jsx";
import Login from "./pages/Login.jsx";
import Header from "./components/Header.jsx";
import Snow from "./components/Snow.jsx";
import UpdateRating from "./pages/UpdateRating.jsx";

function Routs() {
    return (
        <BrowserRouter>
            <Navbar/>
            <Routes>
                <Route path="/" element={<Home/>}/>
                <Route path="/about" element={<About/>}/>
                <Route path="/users" element={<Users/>}/>
                <Route path="/login" element={<Login/>}/>
                <Route path="/rating" element={<UpdateRating/>}/>
            </Routes>
            {/*<Snow/>*/}
        </BrowserRouter>
    );
}

export default Routs;
