import {BrowserRouter, Routes, Route} from "react-router-dom";
import Home from "./pages/Home";
import About from "./pages/About";
import Users from "./pages/Users.jsx";
import Login from "./pages/Login.jsx";
import Header from "./components/Header.jsx";
import Snow from "./components/Snow.jsx";
import UpdateRating from "./pages/UpdateRating.jsx";
import Profile from "./pages/Profile.jsx";

function Routs() {
    return (
        <BrowserRouter>
            <Routes>
                <Route path="/" element={<Login/>}/>
                <Route path="/about" element={<About/>}/>
                <Route path="/users" element={<Users/>}/>
                <Route path="/login" element={<Login/>}/>
                <Route path="/update_rating" element={<UpdateRating/>}/>
                <Route path="/profile/:login" element={<Profile/>}/>
            </Routes>
            {/*<Snow/>*/}
        </BrowserRouter>
    );
}

export default Routs;
