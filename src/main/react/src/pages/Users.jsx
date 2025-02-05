import { useEffect, useState } from "react";
import authFetch from "../scripts/util/authFetch.jsx";

async function fetchUsers() {
    const response = await authFetch("/api/users");
    return response.json();
}

function Users() {
    const [users, setUsers] = useState([]);

    useEffect(() => {
        fetchUsers()
            .then(setUsers)
            .catch(error => console.error(error));
    }, [null]);


    return (
        <div>
            <h1>Список пользователей</h1>
            <ul>
                {users.map(user => (
                    <li key={user.id}>{user.name}</li>
                ))}
            </ul>
        </div>
    );
}

export default Users;
