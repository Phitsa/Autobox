import { useAuth } from "@/hooks/useAuth";
import { useEffect } from "react";

interface ProtectedRouteProps {
  children: React.ReactNode;
  requiredRole?: 'ADMIN' | 'FUNCIONARIO' | 'CLIENTE';
}

const ProtectedRoute: React.FC<ProtectedRouteProps> = ({ 
  children, 
  requiredRole = 'ADMIN' 
}) => {
  const { isAuthenticated, user } = useAuth();

  useEffect(() => {
    if (!isAuthenticated) {
      // Redireciona para home se não estiver logado
      window.location.href = '/';
      return;
    }

    if (requiredRole && user?.tipoUsuario !== requiredRole) {
      // Redireciona se não tiver a role necessária
      alert('Acesso negado! Você não tem permissão para acessar esta área.');
      window.location.href = '/';
      return;
    }
  }, [isAuthenticated, user, requiredRole]);

  if (!isAuthenticated) {
    return <div>Redirecionando...</div>;
  }

  return <>{children}</>;
};

export default ProtectedRoute;