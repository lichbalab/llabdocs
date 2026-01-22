import { defineConfig } from "vite";
import react from "@vitejs/plugin-react";

export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
    proxy: {
      "/docs": {
        target: "https://localhost:8081",
        changeOrigin: true,
        secure: false
      },
      "/api/config/google-client-id": {
        target: "https://localhost:8081",
        changeOrigin: true,
        secure: false
      }
    }
  },
  resolve: {
    dedupe: ["react", "react-dom"]
  },
  build: {
    outDir: "dist",
    sourcemap: true
  }
});
