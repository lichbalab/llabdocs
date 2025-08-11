import React, { useState } from "react";
import Container from "@mui/material/Container";
import Typography from "@mui/material/Typography";
import Box from "@mui/material/Box";
import Tabs from "@mui/material/Tabs";
import Tab from "@mui/material/Tab";
import CircularProgress from "@mui/material/CircularProgress";
import Alert from "@mui/material/Alert";
import FileUpload from "./components/FileUpload.jsx";
import ResultCard from "./components/ResultCard.jsx";
import { validateAdes, validateQes } from "./api/validationApi.js";

export default function App() {
  const [tab, setTab] = useState("ades");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [result, setResult] = useState(null);

  const handleValidate = async (file) => {
    setError("");
    setResult(null);
    setLoading(true);
    try {
      const res =
        tab === "ades"
          ? await validateAdes(file)
          : await validateQes(file);
      setResult(res.data);
    } catch (e) {
      setError(
        e.response?.data?.message ||
          "Unexpected error occurred during validation."
      );
    } finally {
      setLoading(false);
    }
  };

  return (
    <Container maxWidth="md">
      <Box my={4}>
        <Typography variant="h4" component="h1" gutterBottom>
          Document Signature Validator
        </Typography>

        <Tabs
          value={tab}
          onChange={(e, v) => setTab(v)}
          aria-label="signature type"
          sx={{ mb: 2 }}
        >
          <Tab value="ades" label="AdES Validation" />
          <Tab value="qes" label="QES Validation" />
        </Tabs>

        <FileUpload onValidate={handleValidate} />

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
