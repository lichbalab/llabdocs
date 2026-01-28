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
import Button from "@mui/material/Button";
import Avatar from "@mui/material/Avatar";
import Popover from "@mui/material/Popover";
import Accordion from "@mui/material/Accordion";
import AccordionSummary from "@mui/material/AccordionSummary";
import AccordionDetails from "@mui/material/AccordionDetails";
import ExpandMoreIcon from "@mui/icons-material/ExpandMore";
import GitHubIcon from "@mui/icons-material/GitHub";
import ApiIcon from "@mui/icons-material/Api";
import IconButton from "@mui/material/IconButton";
import LogoutIcon from "@mui/icons-material/Logout";
import FileUpload from "./components/FileUpload.jsx";
import ResultCard from "./components/ResultCard.jsx";
import { validateAdes, validateQes } from "./api/validationApi.js";

export default function App() {
  const [tab, setTab] = useState("ades");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [result, setResult] = useState(null);
  const [selectedFile, setSelectedFile] = useState(null);
  // restore auth token from localStorage (if present) right away
  const [authToken, setAuthToken] = useState(() => localStorage.getItem("authToken") || "");
  const [isAuthenticated, setIsAuthenticated] = useState(() => !!localStorage.getItem("authToken"));
  const [userName, setUserName] = useState("");
  const [userPicture, setUserPicture] = useState("");
  const [googleClientId, setGoogleClientId] = useState("");
  const [helpAnchorEl, setHelpAnchorEl] = useState(null);

  const isHelpOpen = Boolean(helpAnchorEl);

  const handleGoogleCredential = (response) => {
    // persist token so user stays logged-in after page reload
    localStorage.setItem("authToken", response.credential);
    setAuthToken(response.credential);
    try {
      const payload = JSON.parse(atob(response.credential.split(".")[1]));
      setUserName(payload.name || payload.email || "");
      setUserPicture(payload.picture || "");
    } catch (e) {
      console.error("Failed to decode ID token", e);
    }
    setIsAuthenticated(true);
  };

  const handleLogout = () => {
    try {
      window.google?.accounts.id.disableAutoSelect();
    } catch (e) {
      /* ignore */
    }
    localStorage.removeItem("authToken");
    setAuthToken("");
    setIsAuthenticated(false);
    setUserName("");
    setUserPicture("");
  };

  // Restore user info from stored token on mount
  useEffect(() => {
    const storedToken = localStorage.getItem("authToken");
    if (storedToken) {
      try {
        const payload = JSON.parse(atob(storedToken.split(".")[1]));
        setUserName(payload.name || payload.email || "");
        setUserPicture(payload.picture || "");
        setIsAuthenticated(true);
      } catch (e) {
        console.error("Failed to decode stored ID token", e);
        localStorage.removeItem("authToken");
      }
    }
  }, []);

  // listen for token refresh events from axiosInstance
  useEffect(() => {
    const handler = (e) => {
      setAuthToken(e.detail);
      setIsAuthenticated(true);
    };
    window.addEventListener("tokenRefreshed", handler);
    return () => window.removeEventListener("tokenRefreshed", handler);
  }, []);

  // Fetch Google OAuth client-id from backend once on mount
  useEffect(() => {
    fetch("/api/config/google-client-id")
      .then((res) => res.text())
      .then((id) => {
        setGoogleClientId(id);
        window.GOOGLE_CLIENT_ID = id;
      })
      .catch((err) => console.error("Failed to load google-client-id:", err));
  }, []);

  useEffect(() => {
    /* global google */
    if (!isAuthenticated && window.google && window.google.accounts) {
      window.google.accounts.id.initialize({
        client_id: googleClientId || import.meta.env.VITE_GOOGLE_CLIENT_ID || "YOUR_GOOGLE_CLIENT_ID",
        callback: handleGoogleCredential
      });
      window.google.accounts.id.renderButton(
        document.getElementById("g_id_signin"),
        { theme: "outline", size: "large" }
      );
    }
  }, [isAuthenticated, googleClientId]);

  const doValidate = async (file, type) => {
    setError("");
    setResult(null);
    setLoading(true);
    try {
      const res = type === "ades" ? await validateAdes(file) : await validateQes(file);
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

  const handleHelpOpen = (event) => {
    setHelpAnchorEl(event.currentTarget);
  };

  const handleHelpClose = () => {
    setHelpAnchorEl(null);
  };

  return (
    <Container maxWidth="md" sx={{ position: "relative" }} className="app-shell">
      <Box my={4} className="page-content">
        <Typography variant="h3" component="h1" gutterBottom align="center" className="hero-title">
          LLabDocs Signature Validator
        </Typography>
        <Typography variant="subtitle1" align="center" gutterBottom className="hero-subtitle">
          Validate and inspect digital signatures in your PDF or XML documents
        </Typography>

        <Box
          sx={{
            position: "fixed",
            top: 16,
            right: 16,
            zIndex: (theme) => theme.zIndex.tooltip
          }}
        >
          <Stack direction="row" spacing={2} alignItems="center" sx={{ flexWrap: "wrap", justifyContent: "flex-end" }}>
            <button
              className="help-trigger"
              type="button"
              onClick={handleHelpOpen}
              aria-haspopup="dialog"
              aria-expanded={isHelpOpen}
              aria-controls={isHelpOpen ? "help-popover" : undefined}
            >
              <span className="help-dot">?</span>
              Help
            </button>
            <Box className={`user-area ${isAuthenticated ? "is-auth" : "is-guest"}`}>
              {!isAuthenticated ? (
                <>
                  <div id="g_id_signin"></div>
                </>
              ) : (
                <Stack direction="row" spacing={1.2} alignItems="center">
                  {userPicture ? (
                    <Avatar alt={userName} src={userPicture} />
                  ) : (
                    <Avatar>{userName.charAt(0)}</Avatar>
                  )}
                  <Typography className="user-name" variant="subtitle1" noWrap>
                    {userName}
                  </Typography>
                  <IconButton
                    size="small"
                    onClick={handleLogout}
                    className="logout-button"
                    aria-label="Log out"
                  >
                    <LogoutIcon fontSize="small" />
                  </IconButton>
                </Stack>
              )}
            </Box>
          </Stack>
        </Box>
        <Popover
          id="help-popover"
          open={isHelpOpen}
          anchorEl={helpAnchorEl}
          onClose={handleHelpClose}
          anchorOrigin={{ vertical: "bottom", horizontal: "right" }}
          transformOrigin={{ vertical: "top", horizontal: "right" }}
          PaperProps={{ className: "help-popover" }}
        >
          <Box className="help-content">
            <Typography variant="subtitle1" className="help-title">
              Help Center
            </Typography>
            <button className="help-item" type="button">
              <span className="help-item-title">What this tool does</span>
              <span className="help-item-desc">
                The LLabDocs Signature Validator checks whether an electronic signature in a document is
                valid, authentic, and legally compliant. It supports both Qualified Electronic Signatures
                (QES) and Advanced Electronic Signatures (AdES) in accordance with eIDAS requirements.
                This allows you to verify that a document was signed by the stated signer and that the
                signature can be trusted for legal and compliance purposes.
              </span>
            </button>
            <button className="help-item" type="button">
              <span className="help-item-title">How to verify a document</span>
              <span className="help-item-desc">
                To validate a document, first log in to your account. Select the type of validation
                (AdES or QES), then upload the document you want to verify. After validation, you will
                see whether the signature is valid, along with details about the signing certificate,
                signing time, validation time, and any errors or warnings found during the process.
                Each user can validate up to 5 documents for free. If you need help or additional access,
                please contact lichbalab@gmail.com. Developers can also validate documents programmatically
                using the API. See the OpenAPI specification below for details.
              </span>
            </button>
            <Box className="faq-block">
              <Typography variant="subtitle2" className="faq-title">
                Common questions (FAQs)
              </Typography>
              <Accordion className="help-accordion">
                <AccordionSummary expandIcon={<ExpandMoreIcon />} className="help-accordion-summary">
                  Why is this tool secure, and how is my personal data protected?
                </AccordionSummary>
                <AccordionDetails className="help-accordion-details">
                  The LLabDocs Signature Validator does not store or retain the documents you upload.
                  Validated documents are processed in memory only and are not saved to a database or file system.
                  The tool performs signature validation using temporary CPU-based processing solely to verify the
                  document’s signature. Once validation is complete, the document data is discarded. For full
                  transparency, the source code of the validator is publicly available. You can review how the tool
                  works in our GitHub repository, linked below on this page.
                </AccordionDetails>
              </Accordion>
            </Box>
            <button className="help-item" type="button">
              <span className="help-item-title">Contact / Report a problem</span>
              <span className="help-item-desc">
                Share details if something looks off or fails. Contact: lichbalab@gmail.com
              </span>
            </button>
          </Box>
        </Popover>
        <Paper elevation={2} sx={{ p: 3, mt: 3 }} className="main-panel">
          <Tabs
            value={tab}
            onChange={handleTabChange}
            aria-label="signature type"
            centered
            sx={{ mb: 2 }}
            className="signature-tabs"
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
      <Box className="page-footer">
        <a
          className="footer-link"
          href="https://github.com/lichbalab/llabdocs"
          target="_blank"
          rel="noreferrer"
        >
          <GitHubIcon fontSize="small" />
          GitHub Repository
        </a>
        <a className="footer-link" href="/swagger-ui/index.html">
          <ApiIcon fontSize="small" />
          OpenAPI Specification
        </a>
      </Box>
    </Container>
  );
}
