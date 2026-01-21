import React, { useState, useEffect } from "react";
import Container from "@mui/material/Container";
import Typography from "@mui/material/Typography";
import Box from "@mui/material/Box";
import Tabs from "@mui/material/Tabs";
import Tab from "@mui/material/Tab";
import CircularProgress from "@mui/material/CircularProgress";
import Alert from "@mui/material/Alert";
import Paper from "@mui/material/Paper";
import Stack from "@mui/material/Stack";
import FileUpload from "./components/FileUpload.jsx";
import ResultCard from "./components/ResultCard.jsx";
import { validateAdes, validateQes } from "./api/validationApi.js";

export default function App() {
  const [tab, setTab] = useState("ades");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [result, setResult] = useState(null);
  const [selectedFile, setSelectedFile] = useState(null);
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [authToken, setAuthToken] = useState("");

  const handleGoogleCredential = (response) => {
    setAuthToken(response.credential);
    setIsAuthenticated(true);
  };

  useEffect(() => {
    /* global google */
    if (!isAuthenticated && window.google && window.google.accounts) {
      window.google.accounts.id.initialize({
        client_id: import.meta.env.VITE_GOOGLE_CLIENT_ID || "YOUR_GOOGLE_CLIENT_ID",
        callback: handleGoogleCredential
      });
      window.google.accounts.id.renderButton(
        document.getElementById("g_id_signin"),
        { theme: "outline", size: "large" }
      );
    }
  }, [isAuthenticated]);

  const doValidate = async (file, type) => {
    setError("");
    setResult(null);
    setLoading(true);
    try {
      const res = type === "ades" ? await validateAdes(file, authToken) : await validateQes(file, authToken);
      setResult(res.data);
    } catch (e) {
      setError(e.response?.data?.message || "Unexpected error occurred during validation.");
    } finally {
      setLoading(false);
    }
  };

  const handleValidate = async (file) => {
    setSelectedFile(file);
    await doValidate(file, tab);
  };

  const handleTabChange = async (e, v) => {
    setTab(v);
    if (selectedFile) {
      await doValidate(selectedFile, v);
    }
  };

  return (
    <Container maxWidth="md">
      <Box my={4}>
        <Typography variant="h3" component="h1" gutterBottom align="center" className="hero-title">
          LLabDocs Signature Validator
        </Typography>
        <Typography variant="subtitle1" align="center" gutterBottom>
          Validate and inspect digital signatures in your PDF or XML documents
        </Typography>

        {!isAuthenticated && (
          <Box textAlign="center" mt={2}>
            <div id="g_id_signin"></div>
          </Box>
        )}
        <Paper elevation={2} sx={{ p: 3, mt: 3 }}>
          <Tabs
            value={tab}
            onChange={handleTabChange}
            aria-label="signature type"
            centered
            sx={{ mb: 2 }}
          >
          <Tab value="ades" label="AdES Validation" />
          <Tab value="qes" label="QES Validation" />
        </Tabs>

        <Stack direction="column" spacing={2} alignItems="center">
          <FileUpload onValidate={handleValidate} disabled={!isAuthenticated} />
        </Stack>
        </Paper>

        {loading && (
          <Box textAlign="center" mt={4}>
            <CircularProgress />
          </Box>
        )}

        {error && (
          <Alert severity="error" sx={{ mt: 4 }}>
            {error}
          </Alert>
        )}

        {result && <ResultCard data={result} />}
      </Box>
    </Container>
  );
}
