import Header from "../components/Header.jsx";
import React, {useEffect} from "react";
import RatingDiff from "../components/RatingDiff.jsx";
import authFetch from "../scripts/util/authFetch.jsx";

async function fetchCountryside() {
    const response = await authFetch(`/api/rating/countryside`);
    return response.json();
}

async function fetchUrban() {
    const response = await authFetch(`/api/rating/urban`);
    return response.json();
}

function updateRating(props) {
    const [showCountryside, setShowCountryside] = React.useState(true);
    const [countrysideStudents, setCountrysideStudents] = React.useState([]);
    const [urbanStudents, setUrbanStudents] = React.useState([]);

    useEffect(() => {
        fetchCountryside().then(setCountrysideStudents)
            .catch(error => console.error("Failed to fetch countryside students info", error));

        fetchUrban().then(setUrbanStudents)
            .catch(error => console.error("Failed to fetch urban students info", error));
    })

    const setGroup = (group) => {
        if (group === "urban") {
            setShowCountryside(false)
        }
        if (group === "countryside") {
            setShowCountryside(true)
        }
    }

    const setUrban = () => {
        setGroup("urban")
    }
    const setCountryside = () => {
        setGroup("countryside")
    }

    return (
        <div>
            <Header/>
            <div className="grid">
                <div className="s0 m1 l2"></div>
                <div className="s12 m10 l8">
                    <div className="grid">
                        <div className="s2 m3 l3"></div>
                        <nav className="no-space center-align middle-align s8 m6 l6">
                            <button className={"border left-round max " + (showCountryside && "fill")} id="countryside">
                                <i className="small">nature</i>
                                <span>16:00/17:00</span>
                            </button>
                            <button className={"border right-round max" + (!showCountryside && "fill")} id="urban">
                                <i className="small">home</i>
                                <span>18:00</span>
                            </button>
                        </nav>
                        <div className="s2 m3 l3"></div>
                    </div>

                    <main className="responsive">
                        {showCountryside && <div id="countrysideContent">
                            <RatingDiff students={countrysideStudents}/>
                            {user.rights === 'Admin' &&
                                <form id="countrysideForm">
                                    <button className="extra round center" id="countrysidePublishButton"
                                            type="submit">
                                        <span id="countrysideSpan">Опубликовать</span>
                                        <progress className="circle" id="countrysideProgress"
                                                  style="display: none"></progress>
                                    </button>
                                </form>}
                        </div>}

                        {showCountryside && <div id="urbanContent">
                            <RatingDiff students={urbanStudents}/>
                            <form id="urbanForm">
                                {user.rights === 'Admin' &&
                                    <button className="extra round center" id="urbanPublishButton"
                                            type="submit">
                                        <span id="urbanSpan">Опубликовать</span>
                                        <progress className="circle" id="urbanProgress"
                                                  style="display: none"></progress>
                                    </button>}
                            </form>
                        </div>}
                    </main>
                </div>
                <div className="s0 m1 l2"></div>
            </div>
            <script src="/static/scripts/switch_camps.js"></script>
            <script src="/static/scripts/update_rating.js"></script>
            <script src="/static/scripts/snowflakes.js"></script>
        </div>

    )
}

export default {updateRating}
