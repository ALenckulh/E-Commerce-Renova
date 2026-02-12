// API Configuration
// Axios instance com configurações globais, interceptadores e tratamento de erros
// TODO: Configurar baseURL, interceptadores, headers padrão

import axios from 'axios';

const api = axios.create({
  baseURL: process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080/api',
  timeout: 10000,
});

// TODO: Adicionar interceptadores
// - Requisição: Adicionar token JWT nos headers
// - Resposta: Tratar erros 401 (refresh token), 403, 500, etc

export default api;
