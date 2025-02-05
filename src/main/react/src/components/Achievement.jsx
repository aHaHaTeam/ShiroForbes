function Achievement(props) {
    const icon = props.icon
    const title = props.title
    const description = props.description
    return (
        <button className="chip circle">
            <i>{icon}</i>
            <div className="tooltip max">
                <b>{title}</b>
                <p>{description}</p>
            </div>
        </button>
    )
}

export default Achievement;
