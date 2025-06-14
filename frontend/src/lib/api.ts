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

// ===== AUTENTICAÇÃO =====
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

// ===== CLIENTES =====
export const apiClientes = {
  getAll: () => api.get('/api/clientes'), // ⭐ Corrigido: faltava /api/
  getById: (id: number) => api.get(`/api/clientes/${id}`), // ⭐ Corrigido
  create: (cliente: any) => api.post('/api/clientes', cliente), // ⭐ Corrigido
  update: (id: number, cliente: any) => api.put(`/api/clientes/${id}`, cliente), // ⭐ Corrigido
  delete: (id: number) => api.delete(`/api/clientes/${id}`) // ⭐ Corrigido
};

// ===== CATEGORIAS =====
export const apiCategorias = {
  getAll: () => api.get('/api/categorias'),
  getById: (id: number) => api.get(`/api/categorias/${id}`),
  create: (categoria: any) => api.post('/api/categorias', categoria),
  update: (id: number, categoria: any) => api.put(`/api/categorias/${id}`, categoria),
  delete: (id: number) => api.delete(`/api/categorias/${id}`),
  teste: () => api.get('/api/categorias/teste')
};

// ===== SERVIÇOS =====
export const apiServicos = {
  getAll: () => api.get('/api/servicos'),
  getById: (id: number) => api.get(`/api/servicos/${id}`),
  create: (servico: any) => api.post('/api/servicos', servico),
  update: (id: number, servico: any) => api.put(`/api/servicos/${id}`, servico),
  delete: (id: number) => api.delete(`/api/servicos/${id}`)
};

// ===== FUNCIONÁRIOS =====
export const apiFuncionarios = {
  // GET - Listar todos os funcionários
  getAll: () => api.get('/api/funcionarios'), // ⭐ Corrigido: adicionado /api/

  // GET - Listar apenas funcionários ativos
  getAtivos: () => api.get('/api/funcionarios/ativos'), // ⭐ Corrigido

  // GET - Listar funcionários disponíveis (ativos e não bloqueados)
  getDisponiveis: () => api.get('/api/funcionarios/disponiveis'), // ⭐ Corrigido

  // GET - Buscar funcionário por ID
  getById: (id: number) => api.get(`/api/funcionarios/${id}`), // ⭐ Corrigido

  // GET - Buscar funcionário por email
  getByEmail: (email: string) => api.get(`/api/funcionarios/buscar/email/${email}`), // ⭐ Corrigido

  // GET - Buscar funcionários por nome
  getByNome: (nome: string) => api.get(`/api/funcionarios/buscar/nome/${nome}`), // ⭐ Corrigido

  // GET - Listar funcionários por tipo (ADMIN ou FUNCIONARIO)
  getByTipo: (tipo: 'ADMIN' | 'FUNCIONARIO') => api.get(`/api/funcionarios/tipo/${tipo}`), // ⭐ Corrigido

  // GET - Estatísticas dos funcionários
  getStats: () => api.get('/api/funcionarios/stats'), // ⭐ Corrigido

  // GET - Status do controller
  getStatus: () => api.get('/api/funcionarios/status'), // ⭐ Corrigido

  // POST - Criar novo funcionário
  create: (funcionario: {
    nome: string;
    email: string;
    senha: string;
    telefone?: string;
    cpf?: string;
    tipoFuncionario?: 'ADMIN' | 'FUNCIONARIO';
    ativo?: boolean;
  }) => api.post('/api/funcionarios', funcionario), // ⭐ Corrigido

  // PUT - Atualizar funcionário
  update: (id: number, funcionario: {
    nome: string;
    email: string;
    senha?: string;
    telefone?: string;
    cpf?: string;
    tipoFuncionario?: 'ADMIN' | 'FUNCIONARIO';
    ativo?: boolean;
  }) => api.put(`/api/funcionarios/${id}`, funcionario), // ⭐ Corrigido

  // DELETE - Desativar funcionário (soft delete)
  delete: (id: number) => api.delete(`/api/funcionarios/${id}`), // ⭐ Corrigido

  // PUT - Bloquear funcionário
  bloquear: (id: number) => api.put(`/api/funcionarios/${id}/bloquear`), // ⭐ Corrigido

  // PUT - Desbloquear funcionário
  desbloquear: (id: number) => api.put(`/api/funcionarios/${id}/desbloquear`), // ⭐ Corrigido
};

// ===== TIPOS TYPESCRIPT =====

export interface TypeFuncionario {
  id: number;
  nome: string;
  email: string;
  telefone?: string;
  cpf?: string;
  tipoFuncionario: 'ADMIN' | 'FUNCIONARIO';
  ativo: boolean;
  dataCriacao: string;
  dataAtualizacao?: string;
  ultimoLogin?: string;
  tentativasLogin: number;
  bloqueado: boolean;
}

export interface TypeFuncionarioForm {
  nome: string;
  email: string;
  senha: string;
  telefone?: string;
  cpf?: string;
  tipoFuncionario: 'ADMIN' | 'FUNCIONARIO';
}

export interface TypeFuncionarioStats {
  total: number;
  ativos: number;
  disponiveis: number;
  admins: number;
  funcionarios: number;
  inativos: number;
}

export default api;