import React, { useEffect, useState } from 'react';
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
  History,
  CheckCircle,
  Lock
} from 'lucide-react';
import { Button } from "@/components/ui/button";
import { useAuth } from "@/hooks/useAuth";
import CardClientes from '@/components/Client/CardClientes';
import { useNavigate } from 'react-router-dom';
import CardVeiculos from '@/components/Vehicle/CardVeiculo';
import axios from 'axios';
import { TypeCliente } from '@/types/TypeCliente';
import { isSameDay, isSameMonth, isSameWeek, parseISO, subMonths, subWeeks } from 'date-fns';
import { StatusAgendamento, TypeAgendamento } from '@/types/TypeAgendamento';

const AdminDashboard = () => {
  const { user, logout } = useAuth();
  const [clientes, setClientes] = useState<TypeCliente[]>([]);
  const [totalClientes, setTotalClientes] = useState<number>(0);
  const [totalAgendamentos, setTotalAgendamentos] = useState<TypeAgendamento[]>([]);
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
  const handleHistoricoClick = () => {
    if (isAdmin) {
      navigate('/historico-agendamentos');
    } else {
      // Apenas mostrar que não tem acesso, mas não navegar
      alert('Acesso restrito! Apenas administradores podem visualizar histórico de agendamentos.');
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


  //dinamicidade das informações do dashboard
  // Dinamicinade do card de clientes
  const buscarTotalClientes = async () => {
          try {
            const response = await axios.get<TypeCliente[]>("http://localhost:8080/api/clientes/todos");
            setTotalClientes(response.data.length);
            setClientes(response.data);
          } catch (error) {
            console.error("Erro ao carregar total de clientes:", error);
          }
        };
    

  const novosDoMes = clientes.filter(clientes => 
      isSameWeek(parseISO(clientes.dataCriacao), new Date())
    ).length;

  // Dinamicidade do card de agendamentos

   const buscarTotalAgendamentos = async () => {
    try {
      const response = await axios.get<TypeAgendamento[]>("http://localhost:8080/api/agendamentos/todos");
      
      if (Array.isArray(response.data)) {
        // Limpar dados para evitar referências circulares
        const agendamentosLimpos = response.data.map(ag => ({
          ...ag,
          historicos: undefined // Remover historicos para evitar referência circular
        }));
        setTotalAgendamentos(agendamentosLimpos);
      } else {
        console.warn('Response.data não é um array:', response.data);
        setTotalAgendamentos([]);
      }
    } catch (error) {
      console.error("Erro ao carregar total de agendamentos:", error);
      setTotalAgendamentos([]);
    }
  };

  const totalAgendamentosHoje = totalAgendamentos.filter(totalAgendamentos =>
    isSameDay(parseISO(totalAgendamentos.dataAgendamento), new Date())
  ).length;

  const agendamentosEmAndamento = totalAgendamentos.filter(totalAgendamentos =>
    totalAgendamentos.status === 'em_andamento').length;

  // Dinamicidade do card de receita do mês

  const totalAgendamentosArray = Array.isArray(totalAgendamentos) ? totalAgendamentos : [];

  const receitaMes = totalAgendamentos
    .filter(ag => isSameMonth(parseISO(ag.dataAgendamento), new Date()))
    .filter(ag => ag.status === StatusAgendamento.CONCLUIDO && ag.valorTotal)
    .reduce((acc, ag) => acc + (ag.valorTotal || 0), 0);

  const receitaMesPassado = totalAgendamentos
    .filter(ag => isSameMonth(parseISO(ag.dataAgendamento), subMonths(new Date(), 1)))
    .filter(ag => ag.status === StatusAgendamento.CONCLUIDO && ag.valorTotal)
    .reduce((acc, ag) => acc + (ag.valorTotal || 0), 0);

  const formatarPreco = (preco: number) => {
    return preco.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' });
  };
  
  // Percentual em relação ao mês anterior
  const percentualMesAnterior = receitaMes / (receitaMesPassado == 0 ? 1 : receitaMesPassado);
  const percentualMesAnterior100 = (percentualMesAnterior == receitaMes ? receitaMes : percentualMesAnterior * 100);
  const percentualMesAnteriorFormatado = Number.isInteger(percentualMesAnterior100) ? percentualMesAnterior100 : percentualMesAnterior100.toFixed(2);
  
  const agendamentosConcluidos = totalAgendamentos
    .filter(totalAgendamentos => isSameWeek(parseISO(totalAgendamentos.dataAgendamento), new Date()))
    .filter(totalAgendamentos => totalAgendamentos.status === 'concluido').length;

  const agendamentosConcluidosSemanaPassada = totalAgendamentos
    .filter(ag => isSameWeek(parseISO(ag.dataAgendamento), subWeeks(new Date(), 1)))
    .filter(ag => ag.status === 'concluido').length;

  const percentualConcluidosSemanaAnterior = (agendamentosConcluidos / (agendamentosConcluidosSemanaPassada == 0 ? 1 : agendamentosConcluidosSemanaPassada))* 100;
  const percentualConcluidosSemanaAnteriorFormatado = Number.isInteger(percentualConcluidosSemanaAnterior) ? percentualConcluidosSemanaAnterior : percentualConcluidosSemanaAnterior.toFixed(2);

  // Efeito para buscar dados ao carregar o componente
  useEffect(() => {
    buscarTotalClientes();
    buscarTotalAgendamentos();
  }, []);


  return (
    <div className="min-h-screen bg-gray-50">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {/* Estatísticas */}
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
          <div className="bg-white rounded-lg shadow p-6">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm font-medium text-gray-600">Agendamentos Hoje</p>
                <p className="text-2xl font-bold text-gray-900">{totalAgendamentosHoje}</p>
              </div>
              <div className="w-12 h-12 bg-blue-100 rounded-lg flex items-center justify-center">
                <Calendar className="w-6 h-6 text-blue-600" />
              </div>
            </div>
            <div className={agendamentosEmAndamento == 0 ? 'text-orange-600 mt-2 flex items-center text-sm' : 'text-green-600 mt-2 flex items-center text-sm'}>
              <span>{agendamentosEmAndamento == 1 ? (agendamentosEmAndamento + ' serviço em andamento') : (agendamentosEmAndamento + ' serviços em andamento')  }</span>
            </div>
          </div>

          <div className="bg-white rounded-lg shadow p-6">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm font-medium text-gray-600">Receita do Mês</p>
                <p className="text-2xl font-bold text-gray-900">{formatarPreco(receitaMes)}</p>
              </div>
              <div className="w-12 h-12 bg-green-100 rounded-lg flex items-center justify-center">
                <DollarSign className="w-6 h-6 text-green-600" />
              </div>
            </div>
            <div className={percentualMesAnterior100 >= 100 ? "mt-2 flex items-center text-sm text-green-600" : "mt-2 flex items-center text-sm text-orange-600"}>
              <span>{percentualMesAnterior100 >= 100 ? '+' : '-'}{percentualMesAnteriorFormatado}% vs mês anterior</span>
            </div>
          </div>

          <div className="bg-white rounded-lg shadow p-6">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm font-medium text-gray-600">Total de Clientes</p>
                <p className="text-2xl font-bold text-gray-900">{totalClientes}</p>
              </div>
              <div className="w-12 h-12 bg-purple-100 rounded-lg flex items-center justify-center">
                <Users className="w-6 h-6 text-purple-600" />
              </div>
            </div>
            <div className={novosDoMes == 0 ? "mt-2 flex items-center text-sm text-orange-600" : "mt-2 flex items-center text-sm text-green-600"}>
              <span>{novosDoMes} novos esta semana</span>
            </div>
          </div>

          <div className="bg-white rounded-lg shadow p-6">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm font-medium text-gray-600">Serviços Concluídos</p>
                <p className="text-2xl font-bold text-gray-900">{agendamentosConcluidos}</p>
              </div>
              <div className="w-12 h-12 bg-orange-100 rounded-lg flex items-center justify-center">
                <CheckCircle className="w-6 h-6 text-orange-600" />
              </div>
            </div>
            <div className={percentualConcluidosSemanaAnterior >= 100 ? 'mt-2 flex items-center text-sm text-green-600' : 'mt-2 flex items-center text-sm text-orange-600' }>
              <span>{percentualConcluidosSemanaAnterior >= 100 ? '+' : '-' }{percentualConcluidosSemanaAnteriorFormatado}% vs semana anterior</span>
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
                <Button size="sm" variant="outline" onClick={() => navigate('/agendamentos')}>
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

          <CardVeiculos></CardVeiculos>

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
            <Button className="bg-blue-600 hover:bg-blue-700 text-white" onClick={() => navigate('/agendamentos')}>
              <Plus className="w-4 h-4 mr-2" />
              Novo Agendamento
            </Button>
            <Button variant="outline" onClick={() => navigate('/clientes')}>
              <Users className="w-4 h-4 mr-2" />
              Cadastrar Cliente
            </Button>
            <Button variant="outline" onClick={() => navigate('/servicos')}>
              <Car className="w-4 h-4 mr-2" />
              Adicionar Serviço
            </Button>
            <Button
              variant="outline"
              disabled={!isAdmin}
              onClick={handleHistoricoClick}
              className={!isAdmin ? 'opacity-60' : ''}
            >
              <History className="w-4 h-4 mr-2" />
              {isAdmin ? 'Histórico Agendamentos' : 'Histórico Agendamentos (Restrito)'}
            </Button>
          </div>
        </div>
      </div>
    </div>
  );
};

export default AdminDashboard;