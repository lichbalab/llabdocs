import axios from "axios";

/**
 * Axios instance with:
 *  • request-interceptor that adds Bearer token from localStorage
 *  • response-interceptor that tries to silently refresh Google ID-token
 *    once if backend responds with 401, then retries the original request.
 *
 * NOTE: token refresh is implemented with Google Identity Services One-Tap.
 *       It relies on `window.GOOGLE_CLIENT_ID` being set (done in App.jsx).
 */
const api = axios.create();

api.interceptors.request.use((config) => {
  const token = localStorage.getItem("authToken");
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

api.interceptors.response.use(
  (response) => response,
  async (error) => {
    const { response, config } = error;
    if (response && response.status === 401 && !config._retry) {
      config._retry = true;
      try {
        const newToken = await refreshGoogleToken();
        if (newToken) {
          localStorage.setItem("authToken", newToken);
          window.dispatchEvent(new CustomEvent("tokenRefreshed", { detail: newToken }));
          config.headers.Authorization = `Bearer ${newToken}`;
          return api(config);
        }
      } catch (e) {
        /* silent fail -> fall through */
      }
    }
    return Promise.reject(error);
  }
);

export default api;

/**
 * Request a fresh ID token from Google Identity Services.
 * Returns Promise<string|null>
 */
function refreshGoogleToken() {
  return new Promise((resolve, reject) => {
    if (!window.google || !window.GOOGLE_CLIENT_ID) {
      return reject(new Error("Google library not initialised"));
    }

    window.google.accounts.id.initialize({
      client_id: window.GOOGLE_CLIENT_ID,
      callback: (resp) => {
        if (resp && resp.credential) {
          resolve(resp.credential);
        } else {
          resolve(null);
        }
      }
    });

    // trigger silent prompt (may show One Tap if allowed)
    window.google.accounts.id.prompt((notification) => {
      if (notification.isNotDisplayed() || notification.isSkippedMoment()) {
        resolve(null);
      }
    });
  });
}
