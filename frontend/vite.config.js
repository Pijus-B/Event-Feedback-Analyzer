import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

export default defineConfig({
  plugins: [react()],
  server: {
    port: 5176,
    host: true,
    allowedHosts: [
        'localhost',
      'event-feedback-analyzer-frontend-react1-328172629172.europe-west1.run.app'
    ],
    proxy: {
      '/api': {
        target: 'https://event-feedback-analyzer-frontend-react1-328172629172.europe-west1.run.app/',
        changeOrigin: true,
        secure: false,
      }
    }
  },
  preview: {
    host: true,
    allowedHosts: [
        'localhost',
      'event-feedback-analyzer-frontend-react1-328172629172.europe-west1.run.app'
    ]
  }
})
