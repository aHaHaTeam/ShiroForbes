import {getToken} from "./localToken.jsx";

const authFetch = async (
    input: RequestInfo,
    init: RequestInit | undefined = {},
    token?: string
): Promise<Response> => {
    const access_token = token || getToken()?.value || 'no_token';

    if (access_token === 'no_token') {
        // eslint-disable-next-line no-console
        console.warn('Making secure API call without an auth token');
    }

    const options = { ...init };

    options.headers = {
        ...init.headers,
        Authorization: `Bearer ${access_token}`,
    };

    return fetch(input, options);
};

export default authFetch;
