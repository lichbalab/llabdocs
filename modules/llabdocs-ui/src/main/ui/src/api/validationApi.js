import api from "./axiosInstance.js";

export function validateAdes(file) {
  const formData = new FormData();
  formData.append("document", file);
  return api.post("/docs/validate/ades-signatures", formData, {
    headers: { "Content-Type": "multipart/form-data" }
  });
}

export function validateQes(file) {
  const formData = new FormData();
  formData.append("document", file);
  return api.post("/docs/validate/qes-signatures", formData, {
    headers: { "Content-Type": "multipart/form-data" }
  });
}
