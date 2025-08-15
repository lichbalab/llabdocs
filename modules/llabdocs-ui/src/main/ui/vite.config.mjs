import { defineConfig } from "vite";
import react from "@vitejs/plugin-react";

export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
    proxy: {
      "/docs": {
        target: "http://localhost:8081",
        changeOrigin: true
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
