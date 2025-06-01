import { useState, useEffect } from 'react';
import { User } from '../types/auth';

export const useAuth = () => {
  const [user, setUser] = useState<User | null>(null);
  const [isAuthenticated, setIsAuthenticated] = useState(false);

  useEffect(() => {
    // Verificar se há usuário logado no localStorage
    const savedUser = localStorage.getItem('boxpro_user');
    const token = localStorage.getItem('boxpro_token');

    if (savedUser && token) {
      setUser(JSON.parse(savedUser));
      setIsAuthenticated(true);
    }
  }, []);

  const login = (userData: User) => {
    setUser(userData);
    setIsAuthenticated(true);
  };

  const logout = () => {
    localStorage.removeItem('boxpro_user');
    localStorage.removeItem('boxpro_token');
    setUser(null);
    setIsAuthenticated(false);
  };

  return { user, isAuthenticated, login, logout };
};