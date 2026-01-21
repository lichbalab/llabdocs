import React, { useRef, useState } from "react";
import Button from "@mui/material/Button";
import CloudUploadIcon from "@mui/icons-material/CloudUpload";
import Stack from "@mui/material/Stack";
import Typography from "@mui/material/Typography";

export default function FileUpload({ onValidate, disabled = false }) {
  const inputRef = useRef(null);
  const [fileName, setFileName] = useState("");

  const handleChange = (e) => {
    const file = e.target.files?.[0];
    if (file) {
      setFileName(file.name);
      onValidate(file);
    }
  };

  return (
    <Stack direction="column" spacing={2} alignItems="center">
      <input
        ref={inputRef}
        type="file"
        accept=".pdf,.xml"
        hidden
        onChange={handleChange}
        disabled={disabled}
      />
      <Button
        variant="contained"
        startIcon={<CloudUploadIcon />}
        onClick={() => inputRef.current.click()}
        disabled={disabled}
      >
        Select Document
      </Button>
      {fileName && (
        <Typography variant="body2" color="text.secondary">
          {fileName}
        </Typography>
      )}
    </Stack>
  );
}
