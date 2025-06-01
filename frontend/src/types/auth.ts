export interface User {
  id: string;
  nome: string;
  email: string;
  tipoUsuario: 'ADMIN' | 'FUNCIONARIO' | 'CLIENTE';
}

export interface LoginData {
  email: string;
  senha: string;
}

export interface RegisterData {
  nome: string;
  email: string;
  senha: string;
  telefone: string;
  cpf: string;
  tipoUsuario: 'ADMIN' | 'FUNCIONARIO' | 'CLIENTE';
}

export interface LoginRegisterModalProps {
  isOpen: boolean;
  onClose: () => void;
  onLoginSuccess?: (userData: User) => void;
}