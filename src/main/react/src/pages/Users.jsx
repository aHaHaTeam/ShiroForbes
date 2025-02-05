import { useEffect, useState } from "react";

async function fetchUsers() {
    const response = await fetch("http://localhost:80/api/users");
    return response.json();
}

function Users() {
    const [users, setUsers] = useState([]);

    useEffect(() => {
        fetchUsers()
            .then(setUsers)
            .catch(error => console.error("Ошибка загрузки:", error));
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
