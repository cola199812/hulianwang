import axios from 'axios'

const http = axios.create({
  baseURL: import.meta.env.DEV ? 'http://localhost:8080/api' : '/api',
  withCredentials: true,
  headers: { 'Content-Type': 'application/json' }
})

export default http
