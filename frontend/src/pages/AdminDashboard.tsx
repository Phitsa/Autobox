import React from 'react';
import {
  Calendar,
  Users,
  Car,
  BarChart3,
  Settings,
  LogOut,
  Bell,
  Search,
  Plus,
  DollarSign,
  Clock,
  CheckCircle,
  Lock
} from 'lucide-react';
import { Button } from "@/components/ui/button";
import { useAuth } from "@/hooks/useAuth";
import CardClientes from '@/components/Client/CardClientes';
import { useNavigate } from 'react-router-dom';

const AdminDashboard = () => {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    window.location.href = '/';
  };

  // Verificar se o usuário é ADMIN
  const isAdmin = user?.tipoFuncionario === 'ADMIN';

  const handleFuncionariosClick = () => {
    if (isAdmin) {
      navigate('/funcionarios');
    } else {
      // Apenas mostrar que não tem acesso, mas não navegar
      alert('Acesso restrito! Apenas administradores podem gerenciar funcionários.');
    }
  };

  const handleConfiguracoesClick = () => {
    if (isAdmin) {
      navigate('/configuracoes');
    } else {
      // Apenas mostrar que não tem acesso, mas não navegar
      alert('Acesso restrito! Apenas administradores podem acessar as configurações.');
    }
  };

  return (
    <div className="min-h-screen bg-gray-50">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {/* Estatísticas */}
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
          <div className="bg-white rounded-lg shadow p-6">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm font-medium text-gray-600">Agendamentos Hoje</p>
                <p className="text-2xl font-bold text-gray-900">12</p>
              </div>
              <div className="w-12 h-12 bg-blue-100 rounded-lg flex items-center justify-center">
                <Calendar className="w-6 h-6 text-blue-600" />
              </div>
            </div>
            <div className="mt-2 flex items-center text-sm text-green-600">
              <span>+8% vs ontem</span>
            </div>
          </div>

          <div className="bg-white rounded-lg shadow p-6">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm font-medium text-gray-600">Receita do Mês</p>
                <p className="text-2xl font-bold text-gray-900">R$ 12.450</p>
              </div>
              <div className="w-12 h-12 bg-green-100 rounded-lg flex items-center justify-center">
                <DollarSign className="w-6 h-6 text-green-600" />
              </div>
            </div>
            <div className="mt-2 flex items-center text-sm text-green-600">
              <span>+15% vs mês anterior</span>
            </div>
          </div>

          <div className="bg-white rounded-lg shadow p-6">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm font-medium text-gray-600">Clientes Ativos</p>
                <p className="text-2xl font-bold text-gray-900">248</p>
              </div>
              <div className="w-12 h-12 bg-purple-100 rounded-lg flex items-center justify-center">
                <Users className="w-6 h-6 text-purple-600" />
              </div>
            </div>
            <div className="mt-2 flex items-center text-sm text-green-600">
              <span>+5 novos esta semana</span>
            </div>
          </div>

          <div className="bg-white rounded-lg shadow p-6">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm font-medium text-gray-600">Serviços Concluídos</p>
                <p className="text-2xl font-bold text-gray-900">89</p>
              </div>
              <div className="w-12 h-12 bg-orange-100 rounded-lg flex items-center justify-center">
                <CheckCircle className="w-6 h-6 text-orange-600" />
              </div>
            </div>
            <div className="mt-2 flex items-center text-sm text-green-600">
              <span>+12% vs semana anterior</span>
            </div>
          </div>
        </div>

        {/* Menu Principal */}
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6 mb-8">
          <div className="bg-white rounded-lg shadow-md hover:shadow-lg transition-shadow cursor-pointer">
            <div className="p-6">
              <div className="flex items-center justify-between mb-4">
                <div className="w-12 h-12 bg-blue-100 rounded-lg flex items-center justify-center">
                  <Calendar className="w-6 h-6 text-blue-600" />
                </div>
                <Button size="sm" variant="outline">
                  Acessar
                </Button>
              </div>
              <h3 className="text-lg font-semibold text-gray-900 mb-2">Agendamentos</h3>
              <p className="text-gray-600 text-sm">
                Gerencie agendamentos, horários disponíveis e confirmações de serviços.
              </p>
            </div>
          </div>

          {/* Card de clientes */}
          <CardClientes></CardClientes>

          <div className="bg-white rounded-lg shadow-md hover:shadow-lg transition-shadow cursor-pointer">
            <div className="p-6">
              <div className="flex items-center justify-between mb-4">
                <div className="w-12 h-12 bg-purple-100 rounded-lg flex items-center justify-center">
                  <Car className="w-6 h-6 text-purple-600" />
                </div>
                <Button size="sm" variant="outline" onClick={() => navigate('/servicos')}>
                  Acessar
                </Button>
              </div>
              <h3 className="text-lg font-semibold text-gray-900 mb-2">Serviços</h3>
              <p className="text-gray-600 text-sm">
                Configure tipos de serviços, preços e tempo de execução.
              </p>
            </div>
          </div>

          {/* Card de Funcionários - Com Restrição */}
          <div
            className={`rounded-lg shadow-md transition-shadow cursor-pointer ${isAdmin
                ? 'bg-white hover:shadow-lg'
                : 'bg-gray-100 opacity-75'
              }`}
            onClick={handleFuncionariosClick}
          >
            <div className="p-6">
              <div className="flex items-center justify-between mb-4">
                <div className={`w-12 h-12 rounded-lg flex items-center justify-center ${isAdmin
                    ? 'bg-orange-100'
                    : 'bg-gray-200'
                  }`}>
                  {isAdmin ? (
                    <Users className="w-6 h-6 text-orange-600" />
                  ) : (
                    <Lock className="w-6 h-6 text-gray-500" />
                  )}
                </div>
                <Button
                  size="sm"
                  variant="outline"
                  disabled={!isAdmin}
                  className={!isAdmin ? 'opacity-60' : ''}
                >
                  {isAdmin ? 'Acessar' : 'Restrito'}
                </Button>
              </div>
              <h3 className={`text-lg font-semibold mb-2 ${isAdmin ? 'text-gray-900' : 'text-gray-500'
                }`}>
                Funcionários
              </h3>
              <p className={`text-sm ${isAdmin ? 'text-gray-600' : 'text-gray-400'
                }`}>
                {isAdmin
                  ? 'Gerencie funcionários, permissões e controle de acesso.'
                  : 'Acesso restrito apenas para administradores.'
                }
              </p>
              {!isAdmin && (
                <div className="mt-2 flex items-center text-xs text-gray-500">
                  <Lock className="w-3 h-3 mr-1" />
                  <span>Apenas ADMIN</span>
                </div>
              )}
            </div>
          </div>

          <div className="bg-white rounded-lg shadow-md hover:shadow-lg transition-shadow cursor-pointer">
            <div className="p-6">
              <div className="flex items-center justify-between mb-4">
                <div className="w-12 h-12 bg-yellow-100 rounded-lg flex items-center justify-center">
                  <DollarSign className="w-6 h-6 text-yellow-600" />
                </div>
                <Button size="sm" variant="outline">
                  Acessar
                </Button>
              </div>
              <h3 className="text-lg font-semibold text-gray-900 mb-2">Financeiro</h3>
              <p className="text-gray-600 text-sm">
                Controle de receitas, despesas e fluxo de caixa da empresa.
              </p>
            </div>
          </div>

          {/* Card de Configurações - Com Restrição ADMIN */}
          <div
            className={`rounded-lg shadow-md transition-shadow cursor-pointer ${isAdmin
                ? 'bg-white hover:shadow-lg'
                : 'bg-gray-100 opacity-75'
              }`}
            onClick={handleConfiguracoesClick}
          >
            <div className="p-6">
              <div className="flex items-center justify-between mb-4">
                <div className={`w-12 h-12 rounded-lg flex items-center justify-center ${isAdmin
                    ? 'bg-gray-100'
                    : 'bg-gray-200'
                  }`}>
                  {isAdmin ? (
                    <Settings className="w-6 h-6 text-gray-600" />
                  ) : (
                    <Lock className="w-6 h-6 text-gray-500" />
                  )}
                </div>
                <Button
                  size="sm"
                  variant="outline"
                  disabled={!isAdmin}
                  className={!isAdmin ? 'opacity-60' : ''}
                >
                  {isAdmin ? 'Acessar' : 'Restrito'}
                </Button>
              </div>
              <h3 className={`text-lg font-semibold mb-2 ${isAdmin ? 'text-gray-900' : 'text-gray-500'
                }`}>
                Configurações
              </h3>
              <p className={`text-sm ${isAdmin ? 'text-gray-600' : 'text-gray-400'
                }`}>
                {isAdmin
                  ? 'Ajuste configurações do sistema, empresa, horários e contatos.'
                  : 'Acesso restrito apenas para administradores.'
                }
              </p>
              {!isAdmin && (
                <div className="mt-2 flex items-center text-xs text-gray-500">
                  <Lock className="w-3 h-3 mr-1" />
                  <span>Apenas ADMIN</span>
                </div>
              )}
            </div>
          </div>
        </div>

        {/* Ações Rápidas */}
        <div className="bg-white rounded-lg shadow p-6">
          <h3 className="text-lg font-semibold text-gray-900 mb-4">Ações Rápidas</h3>
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
            <Button className="bg-blue-600 hover:bg-blue-700 text-white">
              <Plus className="w-4 h-4 mr-2" />
              Novo Agendamento
            </Button>
            <Button variant="outline">
              <Users className="w-4 h-4 mr-2" />
              Cadastrar Cliente
            </Button>
            <Button variant="outline">
              <Car className="w-4 h-4 mr-2" />
              Adicionar Serviço
            </Button>
            <Button
              variant="outline"
              disabled={!isAdmin}
              onClick={handleFuncionariosClick}
              className={!isAdmin ? 'opacity-60' : ''}
            >
              <Users className="w-4 h-4 mr-2" />
              {isAdmin ? 'Gerenciar Funcionários' : 'Funcionários (Restrito)'}
            </Button>
          </div>
        </div>
      </div>
    </div>
  );
};

export default AdminDashboard;