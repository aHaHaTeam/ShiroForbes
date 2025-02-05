import {getToken} from "./jsonWebToken.jsx";

const API_URL = import.meta.env.VITE_API_URL;

const authFetch = async (
    input,
    init = {},
    token
) => {
    const url = API_URL + input;
    const access_token = token || getToken()?.value || 'no_token';

    if (access_token === 'no_token') {
        console.warn('Making secure API call without an auth token');
    }

    const options = {...init};

    options.headers = {
        ...init.headers,
        Authorization: `Bearer ${access_token}`,
    };

    return fetch(url, options);
};

export default authFetch;
