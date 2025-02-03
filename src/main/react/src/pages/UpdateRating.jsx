import Header from "../components/Header.jsx";
import React from "react";
import RatingDiff from "../components/RatingDiff.jsx";

function updateRating(props) {
    const [showCountryside, setShowCountryside] = React.useState(true);
    const [countrysideStudents, setCountrysideStudents] = React.useState([]);
    const [urbanStudents, setUrbanStudents] = React.useState([]);

    return (
        <div>
            <Header user={1}/>
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
