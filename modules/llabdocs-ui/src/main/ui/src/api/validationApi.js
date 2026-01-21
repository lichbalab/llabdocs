import axios from "axios";

export function validateAdes(file, token) {
  const formData = new FormData();
  formData.append("document", file);
  return axios.post("/docs/validate/ades-signatures", formData, {
    headers: {
      "Content-Type": "multipart/form-data",
      ...(token ? { Authorization: `Bearer ${token}` } : {})
    }
  });
}

export function validateQes(file, token) {
  const formData = new FormData();
  formData.append("document", file);
  return axios.post("/docs/validate/qes-signatures", formData, {
    headers: {
      "Content-Type": "multipart/form-data",
      ...(token ? { Authorization: `Bearer ${token}` } : {})
    }
  });
}
