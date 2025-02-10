import React, {useEffect, useState} from "react";
import authFetch from "../scripts/util/authFetch.jsx";
import Header from "../components/Header.jsx";
import ProfileStats from "../components/ProfileStats.jsx";
import {getLogin} from "../scripts/util/userInfo.jsx";
import {useParams} from "react-router-dom";

async function fetchProfile(login) {
    const response = await authFetch(`/api/profile/${login}`);
    return response.json();
}


function Profile(props) {
    const login = useParams().login;
    const [user, setUser] = useState({undefined})
    const [numberOfPeople, setNumberOfPeople] = useState(0)
    const [ratings, setRatings] = useState({"Semester1": [], "Semester12": []});
    const [show2Sem, setShow2Sem] = useState(true);
    const [pageLoading, setPageLoading] = useState(true);


    const setTimePeriod = (period) => {
        if (period === "2") {
            setShow2Sem(true);
        } else {
            setShow2Sem(false);
        }
    }
    const set2Sem = () => {
        setTimePeriod("2");
    }
    const set12Sem = () => {
        setTimePeriod("1")
    }

    useEffect(() => {
        fetchProfile(login).then(data => {
            setUser(data.user);
            setRatings(data.ratings);
            setNumberOfPeople(data.numberOfPeople);
            setPageLoading(false);
        }).catch(error => console.error("Failed to fetch user info", error));
    }, [])

    if (pageLoading) {
        return (<div>Loading...</div>)
    }
    return (

        <div>
            <div>
                {/*it is only important that user is not 0*/}
                {/*have to reconsider even passing user to header*/}
                <Header/>
            </div>
            <div className="grid">

                <main className="responsive grid">
                    <div className="s12">
                        <h4 className="bold center-align" id="name">{user.name}</h4>
                        <br></br>

                        <div className="s12 grid">
                            <div className="s2 m3 l3"></div>
                            <nav className="no-space center-align middle-align s8 m6 l6">
                                <button className={"border left-round max " + (show2Sem && "fill")} id="semester2Button"
                                        onClick={set2Sem}>
                                    <span>Второе полугодие</span>
                                </button>
                                <button className={"border right-round max " + (!show2Sem && "fill")}
                                        id="semesters12Button" onClick={set12Sem}>
                                    <span>Весь год</span>
                                </button>
                            </nav>
                            <div className="s2 m3 l3"></div>
                        </div>
                        <br></br>

                        <div className="s0 l2"></div>
                        {show2Sem && <div className="s12 l8" id="semester2Content">
                            <ProfileStats user={user} ratings={ratings.Semester2} numberOfPeople={numberOfPeople}/>
                        </div>}
                        {!show2Sem && <div className="s12 l8" id="semesters12Content">
                            <ProfileStats user={user} ratings={ratings.Semesters12} numberOfPeople={numberOfPeople}/>
                        </div>}
                        <div className="s0 l2"></div>

                        <br></br>
                        {/*TODO make achievements*/}
                        {/*<div className="grid">*/}
                        {/*    <div className="s4 m3 l2" th:each="achievement : ${achievements}">*/}
                        {/*        <div*/}
                        {/*            th:insert="~{components/achievement :: achievement(${user.icon}, ${user.title}, ${user.description})}"></div>*/}
                        {/*                           TODO why user.icon and not achievement.icon above??*/}
                        {/*    </div>*/}
                        {/*</div>*/}
                    </div>
                </main>
            </div>
        </div>
    )
}

export default Profile;
