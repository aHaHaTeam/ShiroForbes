import authFetch from "../scripts/util/authFetch.jsx";
import React, {useEffect} from "react";


function RatingDiff(props) {
    const [students, setStudents] = React.useState([]);

    useEffect(() => {
        setStudents(props.students);
    })
    return (
        <div className="grid">

            <h2 className="s12 center-align middle">Рейтинг</h2>

            <div className="s0 m1 l2"></div>
            <div className="s12 m10 l8">
                <table className="stripes">
                    <thead>
                    <tr>
                        <th>Место</th>
                        <th className="max">Шировласик</th>
                        <th className="right-align">Задачи</th>
                        <th className="right-align">Рейтинг</th>
                    </tr>
                    </thead>
                    <tbody>
                    {students.map((student, j) => (
                        <tr>
                            <td>
                                <span>{student.newRank}</span>
                                {student.newRank < student.oldRank && <i style="color: green">arrow_drop_up</i>}
                                {student.newRank < student.oldRank &&
                                    <span style="color: green">{student.oldRank - student.newRank}</span>}
                                {student.newRank > student.oldRank && <i style="color: red">arrow_drop_down</i>}
                                {student.newRank > student.oldRank &&
                                    <span style="color: red">{student.oldRank - student.newRank}</span>}
                                {student.newRank === student.oldRank && <i style="color: gray">remove</i>}
                                {student.newRank === student.oldRank && <span style="color: gray">(0)</span>}
                            </td>
                            <td><a href={`/profile/${student.login}`}>{student.name}</a></td>
                            <td className="right-align">
                                <span>{student.solved}</span>
                                {student.solvedDelta > 0 && <span style="color: green">+{student.solvedDelta}</span>}
                                {student.solvedDelta < 0 && <span style="color: red"
                                >{student.solvedDelta}</span>}
                                {student.solvedDelta === 0 && <span style="color: gray">(+0)</span>}
                            </td>
                            <td className="right-align">
                                <span>{student.rating}</span>
                                {student.ratingDelta > 0 && <span style="color: green"
                                >+{student.ratingDelta}</span>}
                                {student.ratingDelta < 0 && <span style="color: red"
                                >{student.ratingDelta}</span>}
                                {student.ratingDelta === 0 && <span style="color: gray">(+0)</span>}
                            </td>
                        </tr>
                    ))}
                    </tbody>
                </table>
            </div>
            <div className="s0 m1 l2"></div>
        </div>
    )
}

export default RatingDiff
