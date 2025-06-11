// types/auth.ts

export interface LoginData {
  email: string;
  senha: string;
}

export interface User {
  id: number;
  nome: string;
  email: string;
  tipoUsuario: 'ADMIN' | 'FUNCIONARIO';
}

export interface LoginResponse {
  token: string;
  id: number;
  nome: string;
  email: string;
  tipoUsuario: 'ADMIN' | 'FUNCIONARIO';
  message?: string;
}

export interface LoginModalProps {
  isOpen: boolean;
  onClose: () => void;
  onLoginSuccess?: (user: LoginResponse) => void;
}