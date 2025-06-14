// lib/api.ts
import axios from 'axios';

// Configuração base da API
export const api = axios.create({
  baseURL: 'http://localhost:8080',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  }
});

// Interceptor para adicionar token automaticamente
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('boxpro_token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Interceptor para tratar respostas
api.interceptors.response.use(
  (response) => {
    return response;
  },
  (error) => {
    // Importar dinamicamente o handler de erro para evitar dependência circular
    import('./errorHandler').then(({ handleError }) => {
      handleError(error);
    });
    
    return Promise.reject(error);
  }
);

// Funções específicas para diferentes endpoints
export const apiAuth = {
  login: (credentials: { email: string; senha: string }) =>
    api.post('/auth/login', credentials),
  
  register: (userData: any) =>
    api.post('/auth/register', userData),
  
  validateToken: () =>
    api.get('/auth/validate-token'),
  
  getCurrentUser: () =>
    api.get('/auth/me'),
  
  logout: () =>
    api.post('/auth/logout')
};

export const apiClientes = {
  getAll: () => api.get('/clientes'),
  getById: (id: number) => api.get(`/clientes/${id}`),
  create: (cliente: any) => api.post('/clientes', cliente),
  update: (id: number, cliente: any) => api.put(`/clientes/${id}`, cliente),
  delete: (id: number) => api.delete(`/clientes/${id}`)
};

export const apiCategorias = {
  getAll: () => api.get('/api/categorias'),
  getById: (id: number) => api.get(`/api/categorias/${id}`),
  create: (categoria: any) => api.post('/api/categorias', categoria),
  update: (id: number, categoria: any) => api.put(`/api/categorias/${id}`, categoria),
  delete: (id: number) => api.delete(`/api/categorias/${id}`),
  teste: () => api.get('/api/categorias/teste')
};

export const apiServicos = {
  getAll: () => api.get('/api/servicos'),
  getById: (id: number) => api.get(`/api/servicos/${id}`),
  create: (servico: any) => api.post('/api/servicos', servico),
  update: (id: number, servico: any) => api.put(`/api/servicos/${id}`, servico),
  delete: (id: number) => api.delete(`/api/servicos/${id}`)
};

export default api;