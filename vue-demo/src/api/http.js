import axios from 'axios'

const http = axios.create({
  // In Docker/Nginx setup, /api will be proxied to backend
  // For local dev, vite.config.js usually handles proxy or we keep full URL if no proxy
  baseURL: '/api', 
  withCredentials: true,
  headers: { 'Content-Type': 'application/json' }
})

export default http

