import {useEffect, useState} from "react";
import authFetch from "../scripts/util/authFetch.jsx";
import Header from "../components/Header.jsx";
import ProfileStats from "../components/ProfileStats.jsx";

async function fetchUser(login) {
    const response = await authFetch(`/api/profile/${login}`);
    return response.json();
}

async function fetchRatings(login) {
    const response = await authFetch(`/api/ratings/${login}`);
    return response.json();
}


function Profile(props) {
    const login = props.login
    const [user, setUser] = useState({undefined})
    const [ratings, setRatings] = useState([]);
    const [show2Sem, setShow2Sem] = useState(true);

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
        fetchUser(login)
            .then(setUser)
            .catch(error => console.error("Failed to fetch user info", error));

        fetchRatings(login)
            .then(setRatings)
            .catch(error => console.error("Failed to fetch ratings", error));
    })
    return (
        <div>
            <div>
                {/*it is only important that user is not 0*/}
                {/*have to reconsider even passing user to header*/}
                <Header />
            </div>
            <div className="grid">

                <main className="responsive grid">
                    <div className="s12">
                        <h4 className="bold center-align" id="name">{user.name}</h4>
                        <br></br>

                        <div className="s12 grid">
                            <div className="s2 m3 l3"></div>
                            <nav className="no-space center-align middle-align s8 m6 l6">
                                <button className={"border left-round max"+ (show2Sem && "fill")} id="semester2Button" onClick={set2Sem}>
                                    <span>Второе полугодие</span>
                                </button>
                                <button className={"border left-round max"+ (!show2Sem && "fill")} id="semesters12Button" onClick={set12Sem}>
                                    <span>Весь год</span>
                                </button>
                            </nav>
                            <div className="s2 m3 l3"></div>
                        </div>
                        <br></br>

                        <div className="s0 l2"></div>
                        {!show2Sem && <div className="s12 l8" id="semester2Content">
                            <ProfileStats user={user} ratings={ratings.ratings1} numberOfPeople={100500}/>
                        </div>}
                        {show2Sem && <div className="s12 l8" id="semesters12Content" hidden="hidden">
                            <ProfileStats user={user} ratings={ratings.ratings12} numberOfPeople={100500}/>
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
