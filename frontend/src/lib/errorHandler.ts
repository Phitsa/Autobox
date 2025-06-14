// lib/errorHandler.ts
import { toast } from '@/components/ui/use-toast';

export interface ApiError {
  status: number;
  message: string;
  action?: 'reload' | 'redirect' | 'logout';
}

export const handleApiError = (error: any): ApiError => {
  console.error('🚨 Erro da API:', error);

  // Erro de rede/conexão
  if (!error.response) {
    return {
      status: 0,
      message: '🔌 Erro de conexão. Verifique sua internet e tente novamente.',
      action: 'reload'
    };
  }

  const status = error.response.status;
  const serverMessage = error.response.data?.message || error.response.data?.error;

  switch (status) {
    case 401:
      return {
        status: 401,
        message: '🔐 Sua sessão expirou. Você será redirecionado para fazer login novamente.',
        action: 'logout'
      };

    case 403:
      return {
        status: 403,
        message: '🚫 Você não tem permissão para realizar esta ação. Entre em contato com o administrador.',
        action: 'reload'
      };

    case 404:
      return {
        status: 404,
        message: '🔍 Recurso não encontrado. O item pode ter sido removido.',
        action: 'reload'
      };

    case 409:
      return {
        status: 409,
        message: `⚠️ Conflito: ${serverMessage || 'Este item já existe ou está em uso.'}`,
        action: 'reload'
      };

    case 422:
      return {
        status: 422,
        message: `📝 Dados inválidos: ${serverMessage || 'Verifique os campos e tente novamente.'}`,
        action: 'reload'
      };

    case 500:
      return {
        status: 500,
        message: '💥 Erro interno do servidor. Tente novamente em alguns minutos.',
        action: 'reload'
      };

    default:
      return {
        status: status,
        message: serverMessage || `❌ Erro ${status}: Algo deu errado. Tente novamente.`,
        action: 'reload'
      };
  }
};

export const showErrorToast = (apiError: ApiError) => {
  const titles = {
    401: 'Sessão Expirada',
    403: 'Acesso Negado', 
    404: 'Não Encontrado',
    409: 'Conflito',
    422: 'Dados Inválidos',
    500: 'Erro do Servidor',
    0: 'Erro de Conexão'
  };

  const title = titles[apiError.status as keyof typeof titles] || 'Erro';
  
  toast({
    variant: 'destructive',
    title: title,
    description: apiError.message,
    duration: 5000,
  });
};

export const executeErrorAction = (apiError: ApiError) => {
  switch (apiError.action) {
    case 'reload':
      setTimeout(() => {
        console.log('🔄 Recarregando página devido ao erro...');
        window.location.reload();
      }, 2000);
      break;

    case 'logout':
      setTimeout(() => {
        console.log('🚪 Fazendo logout devido ao erro...');
        localStorage.removeItem('boxpro_token');
        localStorage.removeItem('boxpro_user');
        window.location.href = '/';
      }, 2000);
      break;

    case 'redirect':
      setTimeout(() => {
        console.log('🔀 Redirecionando devido ao erro...');
        window.location.href = '/';
      }, 2000);
      break;
  }
};

// Função principal para tratar erros
export const handleError = (error: any) => {
  const apiError = handleApiError(error);
  showErrorToast(apiError);
  executeErrorAction(apiError);
  return apiError;
};

// Hook personalizado para usar em componentes React
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';

export const useErrorHandler = () => {
  const navigate = useNavigate();
  const { logout } = useAuth();

  const handleError = (error: any) => {
    const apiError = handleApiError(error);
    showErrorToast(apiError);

    switch (apiError.action) {
      case 'reload':
        setTimeout(() => {
          window.location.reload();
        }, 2000);
        break;

      case 'logout':
        setTimeout(() => {
          logout();
          navigate('/');
        }, 2000);
        break;

      case 'redirect':
        setTimeout(() => {
          navigate('/');
        }, 2000);
        break;
    }

    return apiError;
  };

  return { handleError };
};