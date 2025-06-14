// components/ProtectedRoute.tsx
import { useState, useEffect, ReactNode } from 'react';
import { useAuth } from '../contexts/AuthContext';
import { AlertCircle, Shield } from 'lucide-react';

interface ProtectedRouteProps {
  children: ReactNode;
}

interface UnauthorizedAccessProps {
  requestedPath: string;
}

// Componente para acesso não autorizado
const UnauthorizedAccess = ({ requestedPath }: UnauthorizedAccessProps) => {
  const [countdown, setCountdown] = useState<number>(5);

  useEffect(() => {
    const timer = setInterval(() => {
      setCountdown((prev) => {
        if (prev <= 1) {
          window.location.href = '/';
          return 0;
        }
        return prev - 1;
      });
    }, 1000);

    return () => clearInterval(timer);
  }, []);

  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-50 px-4">
      <div className="max-w-md w-full">
        <div className="bg-white rounded-lg shadow-lg p-8 text-center">
          {/* Ícone */}
          <div className="mx-auto flex items-center justify-center w-16 h-16 bg-red-100 rounded-full mb-6">
            <Shield className="w-8 h-8 text-red-600" />
          </div>

          {/* Título */}
          <h1 className="text-2xl font-bold text-gray-900 mb-4">
            Acesso Restrito
          </h1>

          {/* Mensagem */}
          <div className="space-y-4 mb-6">
            <div className="bg-yellow-50 border border-yellow-200 rounded-lg p-4 flex items-start space-x-3">
              <AlertCircle className="w-5 h-5 text-yellow-600 mt-0.5 flex-shrink-0" />
              <div className="text-left">
                <p className="text-yellow-800 font-medium">Autenticação Necessária</p>
                <p className="text-yellow-700 text-sm mt-1">
                  Você precisa fazer login para acessar esta página.
                </p>
              </div>
            </div>

            {requestedPath !== '/' && (
              <p className="text-gray-600 text-sm">
                Página solicitada: <code className="bg-gray-100 px-2 py-1 rounded">{requestedPath}</code>
              </p>
            )}
          </div>

          {/* Countdown */}
          <div className="bg-blue-50 border border-blue-200 rounded-lg p-4 mb-6">
            <p className="text-blue-800">
              Redirecionando para a página inicial em{' '}
              <span className="font-bold text-blue-600">{countdown}</span> segundos...
            </p>
          </div>

          {/* Botões */}
          <div className="space-y-3">
            <button
              onClick={() => window.location.href = '/'}
              className="w-full bg-blue-600 hover:bg-blue-700 text-white font-medium py-3 px-4 rounded-lg transition-colors"
            >
              Ir para Página Inicial Agora
            </button>
            
            <button
              onClick={() => window.location.reload()}
              className="w-full bg-gray-100 hover:bg-gray-200 text-gray-700 font-medium py-2 px-4 rounded-lg transition-colors"
            >
              Tentar Novamente
            </button>
          </div>

          {/* Rodapé */}
          <div className="mt-8 pt-4 border-t border-gray-200">
            <p className="text-xs text-gray-500">
              BoxPro - Sistema de Gestão Automotiva
            </p>
          </div>
        </div>
      </div>
    </div>
  );
};

// Componente para rotas protegidas
const ProtectedRoute = ({ children }: ProtectedRouteProps) => {
  const { isAuthenticated, loading } = useAuth();
  const requestedPath = window.location.pathname;

  // Mostrar loading enquanto verifica autenticação
  if (loading) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-gray-50">
        <div className="text-center">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600 mx-auto mb-4"></div>
          <p className="text-gray-600">Verificando autenticação...</p>
        </div>
      </div>
    );
  }

  // Se não autenticado, mostrar mensagem e redirecionar
  if (!isAuthenticated) {
    return <UnauthorizedAccess requestedPath={requestedPath} />;
  }

  return <>{children}</>;
};

export default ProtectedRoute;