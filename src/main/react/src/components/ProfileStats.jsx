function ProfileStats(props) {

    const ratings = props.ratings
    const user = props.user
    const numberOfPeople = props.numberOfPeople
    return (
        <div className="grid">
            {ratings[0].position * 2 <= numberOfPeople && (
                <article className="s12 round amber239 middle-align">
                    <div className="row" style={{columnGap: 0, width: "85%"}}>
                        <button className="circle amber239 large">
                            <i className="large">Trophy</i>
                        </button>
                        <div className="max center center-align row">
                            <p className="bold">Место: </p>
                            <p>{ratings[0].position}</p>
                        </div>
                    </div>
                </article>
            )}

            {ratings[0].grobs > 0 && (
                <article className="s12 round purple239 middle-align">
                    <div className="row" style={{columnGap: 0, width: "85%"}}>
                        <button className="circle purple239 large">
                            <i className="large">Skull</i>
                        </button>
                        <div className="max center center-align row">
                            <p className="bold">Гробы: </p>
                            <p>{ratings[0].grobs}</p>
                        </div>
                    </div>
                </article>
            )}

            <article className="s12 m6 l4 round pink239 middle-align">
                <div className="row" style={{columnGap: 0, width: "85%"}}>
                    <button className="circle pink239 large">
                        <i className="large">functions</i>
                    </button>
                    <div className="max center center-align">
                        <span className="bold"> Задачи: </span>
                        <span style={{whiteSpace: "nowrap"}}>
                    {ratings[0].total} ({ratings[0].totalPercent}%)
                </span>
                    </div>
                </div>
            </article>

            <article className="s12 m6 l4 round blue239 middle-align">
                <div className="row" style={{columnGap: 0, width: "85%"}}>
                    <button className="circle blue239 large">
                        <i className="large">function</i>
                    </button>
                    <div className="max center center-align">
                        <span className="bold">Алгебра: </span>
                        <span style={{whiteSpace: "nowrap"}}>
                    {ratings[0].algebra} ({ratings[0].algebraPercent}%)
                </span>
                    </div>
                </div>
            </article>

            <article className="s12 m6 l4 round orange239 middle-align">
                <div className="row" style={{columnGap: 0, width: "85%"}}>
                    <button className="circle orange239 large">
                        <i className="large">calculate</i>
                    </button>
                    <div className="max center center-align">
                        <span className="bold">Теория чисел: </span>
                        <span style={{whiteSpace: "nowrap"}}>
                    {ratings[0].numbersTheory} ({ratings[0].numbersTheoryPercent}%)
                </span>
                    </div>
                </div>
            </article>

            <article className="s12 m6 l4 round deep-orange239 middle-align">
                <div className="row" style={{columnGap: 0, width: "85%"}}>
                    <button className="circle deep-orange239 large">
                        <i className="large">playing_cards</i>
                    </button>
                    <div className="max center center-align">
                <span className="bold" style={{wordBreak: "break-all"}}>
                    Комбинаторика:
                </span>
                        <span style={{whiteSpace: "nowrap"}}>
                    {ratings[0].combinatorics} ({ratings[0].combinatoricsPercent}%)
                </span>
                    </div>
                </div>
            </article>

            <article className="s12 m6 l4 round green239 middle-align">
                <div className="row" style={{columnGap: 0, width: "85%"}}>
                    <button className="circle green239 large">
                        <i className="large">architecture</i>
                    </button>
                    <div className="max center center-align">
                <span className="bold" style={{wordBreak: "break-all"}}>
                    Геометрия:
                </span>
                        <span style={{whiteSpace: "nowrap"}}>
                    {ratings[0].geometry} ({ratings[0].geometryPercent}%)
                </span>
                    </div>
                </div>
            </article>
        </div>

    )
}

export default ProfileStats
