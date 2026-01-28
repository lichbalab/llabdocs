import React from "react";
import Card from "@mui/material/Card";
import CardContent from "@mui/material/CardContent";
import Typography from "@mui/material/Typography";
import Chip from "@mui/material/Chip";
import Stack from "@mui/material/Stack";
import Divider from "@mui/material/Divider";
import Table from "@mui/material/Table";
import TableBody from "@mui/material/TableBody";
import TableCell from "@mui/material/TableCell";
import TableRow from "@mui/material/TableRow";
import Paper from "@mui/material/Paper";

function CertificateTable({ certificates }) {
  if (!certificates?.length) return null;
  const wrapCell = { sx: { wordBreak: "break-all" } };
  return (
    <Paper variant="outlined" sx={{ my: 2 }}>
      <Table size="small" sx={{ tableLayout: "fixed", width: "100%" }}>
        <TableBody>
          {certificates.map((c, idx) => (
            <React.Fragment key={idx}>
              <TableRow>
                <TableCell colSpan={2}>
                  <Typography variant="subtitle2">
                    Certificate #{idx + 1}
                  </Typography>
                </TableCell>
              </TableRow>
              <TableRow>
                <TableCell>Subject</TableCell>
                <TableCell {...wrapCell}>{c.subject}</TableCell>
              </TableRow>
              <TableRow>
                <TableCell>Issuer</TableCell>
                <TableCell {...wrapCell}>{c.issuer}</TableCell>
              </TableRow>
              <TableRow>
                <TableCell>Serial Number</TableCell>
                <TableCell {...wrapCell}>{c.serialNumber}</TableCell>
              </TableRow>
              <TableRow>
                <TableCell>Valid From</TableCell>
                <TableCell {...wrapCell}>{c.validFrom}</TableCell>
              </TableRow>
              <TableRow>
                <TableCell>Valid To</TableCell>
                <TableCell {...wrapCell}>{c.validTo}</TableCell>
              </TableRow>
              {idx < certificates.length - 1 && (
                <TableRow>
                  <TableCell colSpan={2}>
                    <Divider />
                  </TableCell>
                </TableRow>
              )}
            </React.Fragment>
          ))}
        </TableBody>
      </Table>
    </Paper>
  );
}

export default function ResultCard({ data }) {
  return (
    <Card variant="outlined" sx={{ mt: 4 }} className="result-card">
      <CardContent>
        <Typography variant="h6" gutterBottom>
          {data.documentName}
        </Typography>
        <Stack direction="row" spacing={1} sx={{ flexWrap: "wrap", mb: 2 }}>
          <Chip
            label={`Signatures: ${data.summary.totalSignatures}`}
            color="primary"
            variant="outlined"
          />
          <Chip
            label={`Valid: ${data.summary.validSignatures}`}
            color={
              data.summary.validSignatures === data.summary.totalSignatures
                ? "success"
                : "warning"
            }
            variant="outlined"
          />
        </Stack>

        {data.signatures?.map((sig) => (
          <Paper
            key={sig.signatureId}
            variant="outlined"
            sx={{ p: 2, mb: 2 }}
            className="signature-card"
          >
            <Typography variant="subtitle1" gutterBottom>
              Signature ID: {sig.signatureId}
            </Typography>
            <Stack direction="row" spacing={1} sx={{ mb: 1 }}>
              <Chip
                label={sig.indication}
                color={sig.indication === "TOTAL-PASSED" ? "success" : "error"}
              />
              {sig.signer && <Chip label={`Signer: ${sig.signer}`} />}
            </Stack>

            {sig.indicationDetails && (
              <Typography variant="body2" color="text.secondary" sx={{ mb: 1 }}>
                {sig.indicationDetails}
              </Typography>
            )}

            <Typography variant="subtitle2" sx={{ mt: 1 }}>
              Signing Certificate
            </Typography>
            <CertificateTable certificates={[sig.signingCertificate]} />

            {sig.certificateChain?.length > 0 && (
              <>
                <Typography variant="subtitle2">Certificate Chain</Typography>
                <CertificateTable certificates={sig.certificateChain} />
              </>
            )}

            {sig.signatureDetails?.errors?.length > 0 && (
              <>
                <Typography variant="subtitle2" color="error">
                  Errors
                </Typography>
                <ul>
                  {sig.signatureDetails.errors.map((err, idx) => (
                    <li key={idx}>{err}</li>
                  ))}
                </ul>
              </>
            )}

            {sig.signatureDetails?.warns?.length > 0 && (
              <>
                <Typography variant="subtitle2" color="warning.main">
                  Warnings
                </Typography>
                <ul>
                  {sig.signatureDetails.warns.map((w, idx) => (
                    <li key={idx}>{w}</li>
                  ))}
                </ul>
              </>
            )}
          </Paper>
        ))}
      </CardContent>
    </Card>
  );
}
