const LOGIN_KEY = 'shv_login';

const RIGHTS_KEY = 'shv_rights';


const setLogin = (login) => {
    localStorage.setItem(
        LOGIN_KEY,
        JSON.stringify({
            value: login,
        })
    );
};

const setRights = (rights) => {
    localStorage.setItem(
        RIGHTS_KEY,
        JSON.stringify({
            value: rights
        })
    )
}
const removeIdentity = () => {
    localStorage.removeItem(LOGIN_KEY);
    localStorage.removeItem(RIGHTS_KEY);
};

const getLogin = () => {
    let result = null;

    const storedLogin = localStorage.getItem(LOGIN_KEY);
    storedLogin && (result = JSON.parse(storedLogin));

    return result;
};

const getRights = () => {
    let result = null;

    const storedRights = localStorage.getItem(RIGHTS_KEY);
    storedRights && (result = JSON.parse(storedRights));

    return result;
};

export {getRights, getLogin, removeIdentity, setLogin, setRights};
