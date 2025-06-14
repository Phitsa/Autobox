// contexts/AuthContext.tsx
import { createContext, useContext, useState, useEffect, ReactNode } from 'react';

// Tipos TypeScript
interface User {
  id: number;
  nome: string;
  email: string;
  tipoFuncionario: string;
}

interface AuthContextType {
  user: User | null;
  isAuthenticated: boolean;
  loading: boolean;
  login: (userData: User) => void;
  logout: () => void;
  checkAuthStatus: () => Promise<void>;
}

interface AuthProviderProps {
  children: ReactNode;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const useAuth = (): AuthContextType => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth deve ser usado dentro de AuthProvider');
  }
  return context;
};

export const AuthProvider = ({ children }: AuthProviderProps) => {
  const [user, setUser] = useState<User | null>(null);
  const [loading, setLoading] = useState<boolean>(true);
  const [isAuthenticated, setIsAuthenticated] = useState<boolean>(false);

  // Verificar se há token salvo ao carregar a aplicação
  useEffect(() => {
    checkAuthStatus();
  }, []);

  const checkAuthStatus = async (): Promise<void> => {
    try {
      const token = localStorage.getItem('boxpro_token');
      const savedUser = localStorage.getItem('boxpro_user');

      if (!token || !savedUser) {
        setLoading(false);
        return;
      }

      // Validar token com o backend
      const response = await fetch('http://localhost:8080/auth/validate-token', {
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json'
        }
      });

      if (response.ok) {
        const userData: User = JSON.parse(savedUser);
        setUser(userData);
        setIsAuthenticated(true);
        console.log('✅ Usuário autenticado:', userData.email);
      } else {
        // Token inválido, limpar dados
        localStorage.removeItem('boxpro_token');
        localStorage.removeItem('boxpro_user');
        console.log('❌ Token inválido, fazendo logout');
      }
    } catch (error) {
      console.error('Erro ao verificar autenticação:', error);
      // Em caso de erro, assumir que não está autenticado
    } finally {
      setLoading(false);
    }
  };

  const login = (userData: User): void => {
    setUser(userData);
    setIsAuthenticated(true);
    console.log('👤 Login realizado:', userData.email);
  };

  const logout = (): void => {
    localStorage.removeItem('boxpro_token');
    localStorage.removeItem('boxpro_user');
    setUser(null);
    setIsAuthenticated(false);
    console.log('👋 Logout realizado');
    // Redirecionar para home
    window.location.href = '/';
  };

  const value: AuthContextType = {
    user,
    isAuthenticated,
    loading,
    login,
    logout,
    checkAuthStatus
  };

  return (
    <AuthContext.Provider value={value}>
      {children}
    </AuthContext.Provider>
  );
};