import {getToken} from "./localToken.jsx";

const API_URL = import.meta.env.VITE_API_URL;

const authFetch = async (
    input,
    init = {},
    token
) => {
    const url = API_URL + input;
    const access_token = token || getToken()?.value || 'no_token';

    if (access_token === 'no_token') {
        // eslint-disable-next-line no-console
        console.warn('Making secure API call without an auth token');
    }

    const options = {...init};

    options.headers = {
        ...init.headers,
        Authorization: `Bearer ${access_token}`,
    };

    return fetch(input, options);
};

export default authFetch;
