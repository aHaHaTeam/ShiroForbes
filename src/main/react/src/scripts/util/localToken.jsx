
const TOKEN_KEY = 'shv_token';

const TOKEN_TTL_MS = 999999999999999;

const isExpired = (timeStamp) => {
    if (!timeStamp) return false;

    const now = new Date().getTime();
    const diff = now - timeStamp;

    return diff > TOKEN_TTL_MS;
};

const setToken = (access_token) => {
    localStorage.setItem(
        TOKEN_KEY,
        JSON.stringify({
            value: access_token,
            timeStamp: new Date().getTime(),
        })
    );
};

const removeToken = () => {
    localStorage.removeItem(TOKEN_KEY);
};

const getToken = () => {
    let result = null;

    const storedToken = localStorage.getItem(TOKEN_KEY);
    storedToken && (result = JSON.parse(storedToken));

    return result;
};

export {getToken, setToken, removeToken, isExpired};
