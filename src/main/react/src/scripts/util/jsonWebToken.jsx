import {jwtDecode} from "jwt-decode";

const TOKEN_KEY = 'shv_token';

const setToken = (access_token) => {
    localStorage.setItem(
        TOKEN_KEY,
        JSON.stringify({
            value: access_token,
            timeStamp: new Date().getTime(),
        })
    );
};

const getToken = () => {
    let result = null;

    const storedToken = localStorage.getItem(TOKEN_KEY);
    storedToken && (result = JSON.parse(storedToken));

    return result;
};

function getUserRights() {
    const token = localStorage.getItem("token");
    if (!token) return null;

    try {
        const decoded = jwtDecode(token);
        return decoded.role;
    } catch (error) {
        return null;
    }
}

export {getToken, setToken, getUserRights};
