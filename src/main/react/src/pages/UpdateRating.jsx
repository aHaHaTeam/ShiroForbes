import Header from "../components/Header.jsx";
import React, {useEffect, useState} from "react";
import RatingDiff from "../components/RatingDiff.jsx";
import authFetch from "../scripts/util/authFetch.jsx";
import {getRights} from "../scripts/util/userInfo.jsx";

async function fetchCountryside() {
    const response = await authFetch(`/api/rating/countryside`);
    return response.json();
}

async function fetchUrban() {
    const response = await authFetch(`/api/rating/urban`);
    return response.json();
}

function updateRating() {
    const [showCountryside, setShowCountryside] = React.useState(true);
    const [countrysideStudents, setCountrysideStudents] = React.useState([]);
    const [urbanStudents, setUrbanStudents] = React.useState([]);
    const [updatePending, setUpdatePending] = useState(false);
    const [pageLoading, setPageLoading] = useState(true);
    const rights = getRights();


    useEffect(() => {
        fetchCountryside().then(setCountrysideStudents)
            .catch(error => console.error("Failed to fetch countryside students info", error));

        fetchUrban().then(setUrbanStudents)
            .catch(error => console.error("Failed to fetch urban students info", error));

        setPageLoading(false);
    }, [])

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

    if(pageLoading) {
        return (<div>Loading...</div>)
    }
    const publish = () => {
        setUpdatePending(true);
        const url = "/api/rating/update/" + (showCountryside ? "countryside" : "urban");
        authFetch(url, {
            method: "POST",
            headers: {'Content-Type': 'application/json'},
            body: "{}"
        }).then(response => {
            if (!response.ok) {
                alert("Something went wrong")
            }
        })
        setUpdatePending(false)
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
                            <button className={"border left-round max " + (showCountryside && "fill")} id="countryside"
                                    onClick={setCountryside}>
                                <i className="small">nature</i>
                                <span>16:00/17:00</span>
                            </button>
                            <button className={"border right-round max " + (!showCountryside && "fill")} id="urban"
                                    onClick={setUrban}>
                                <i className="small">home</i>
                                <span>18:00</span>
                            </button>
                        </nav>
                        <div className="s2 m3 l3"></div>
                    </div>

                    <main className="responsive">
                        {showCountryside && <div id="countrysideContent">
                            <RatingDiff students={countrysideStudents}/>
                        </div>}

                        {!showCountryside && <div id="urbanContent">
                            <RatingDiff students={urbanStudents}/>
                        </div>}

                        {rights === 'Admin' &&
                            <form id="countrysideForm">
                                <button className="extra round" id="publishButton"
                                        type="submit" onClick={publish}>
                                    {!updatePending && <span id="publishSpan">Опубликовать</span>}
                                    {updatePending && <progress className="circle" id="publishProgress"></progress>}
                                </button>
                            </form>}
                    </main>
                </div>
                <div className="s0 m1 l2"></div>
            </div>
        </div>

    )
}

export default updateRating
