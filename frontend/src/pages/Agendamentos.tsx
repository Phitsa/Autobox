import React, { useEffect, useState } from 'react';
import { Button } from '@/components/ui/button';
import axios from 'axios';
import { TypeAgendamento, TypeCliente, TypeVeiculo, TypeServico, StatusAgendamento } from "../types/TypeAgendamento";
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogTrigger } from '@/components/ui/dialog';
import { useNavigate } from 'react-router-dom';
import { Plus, Calendar, ArrowLeft, Edit, Trash2, Clock, DollarSign, User, Car, Settings } from 'lucide-react';
import AgendamentoForm from '@/components/AgendamentoForm';
import { parseISO, isSameMonth, format, isToday, isFuture } from 'date-fns';
import { ptBR } from 'date-fns/locale';
import { useAuth } from '../contexts/AuthContext';
import {
  AlertDialog,
  AlertDialogTrigger,
  AlertDialogContent,
  AlertDialogHeader,
  AlertDialogTitle,
  AlertDialogDescription,
  AlertDialogFooter,
  AlertDialogCancel,
  AlertDialogAction
} from '@/components/ui/alert-dialog';
import { Badge } from '@/components/ui/badge';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';

const Agendamentos = () => {
  const navigate = useNavigate();
  const { user } = useAuth(); // Pegar usuário logado do contexto
  const [editingAgendamento, setEditingAgendamento] = useState<TypeAgendamento | null>(null);
  const [agendamentos, setAgendamentos] = useState<TypeAgendamento[]>([]);
  const [totalAgendamentos, setTotalAgendamentos] = useState<TypeAgendamento[]>([]);
  const [clientes, setClientes] = useState<TypeCliente[]>([]);
  const [veiculos, setVeiculos] = useState<TypeVeiculo[]>([]);
  const [servicos, setServicos] = useState<TypeServico[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [erro, setErro] = useState<string | null>(null);
  const [sucesso, setSucesso] = useState<string | null>(null);
  const [dialogOpen, setDialogOpen] = useState(false);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(1);
  const [filtroStatus, setFiltroStatus] = useState<string>('todos');

  // Buscar agendamentos paginados
  useEffect(() => {
    const buscarAgendamentos = async () => {
      try {
        const response = await axios.get(`http://localhost:8080/api/agendamentos?page=${page}&size=8`);
        
        // Verificar se a resposta é válida
        if (response.data && typeof response.data === 'object') {
          const content = response.data.content;
          if (Array.isArray(content)) {
            // Limpar dados para evitar referências circulares
            const agendamentosLimpos = content.map(ag => ({
              ...ag,
              historicos: undefined // Remover historicos para evitar referência circular
            }));
            setAgendamentos(agendamentosLimpos);
          } else {
            console.warn('Content não é um array:', content);
            setAgendamentos([]);
          }
          setTotalPages(response.data.totalPages || 1);
        } else {
          console.warn('Resposta inválida da API:', response.data);
          setAgendamentos([]);
          setTotalPages(1);
        }
      } catch (error) {
        console.error("Erro ao carregar agendamentos:", error);
        setErro("Erro ao carregar agendamentos");
        setAgendamentos([]);
      } finally {
        setLoading(false);
      }
    };

    buscarAgendamentos();
  }, [page]);

  // Buscar todos os agendamentos para estatísticas
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
      setTotalAgendamentos([]); // Garantir que seja sempre um array vazio em caso de erro
    }
  };

  // Buscar dados iniciais
  useEffect(() => {
    const buscarDadosIniciais = async () => {
      try {
        setLoading(true);
        
        // Configurar interceptor do axios para tratar respostas malformadas
        const responseInterceptor = axios.interceptors.response.use(
          (response) => {
            // Se a resposta contém referências circulares, limpar
            if (response.data && typeof response.data === 'object') {
              try {
                JSON.stringify(response.data);
                return response;
              } catch (error) {
                console.warn('Resposta com referência circular detectada, limpando dados...');
                if (response.data.content && Array.isArray(response.data.content)) {
                  response.data.content = response.data.content.map((item: any) => ({
                    ...item,
                    historicos: undefined
                  }));
                }
                return response;
              }
            }
            return response;
          },
          (error) => {
            console.error('Erro na resposta da API:', error);
            return Promise.reject(error);
          }
        );
        
        const [clientesResponse, veiculosResponse, servicosResponse] = await Promise.all([
          axios.get<TypeCliente[]>("http://localhost:8080/api/clientes/todos"),
          axios.get<TypeVeiculo[]>("http://localhost:8080/api/veiculos/todos"),
          axios.get<TypeServico[]>("http://localhost:8080/api/servicos/todos")
        ]);
        
        setClientes(clientesResponse.data || []);
        
        // Enriquecer veículos com nome do cliente para exibição
        const veiculosComCliente = (veiculosResponse.data || []).map(veiculo => {
          const cliente = clientesResponse.data?.find(c => c.id === veiculo.clienteId);
          return {
            ...veiculo,
            clienteNome: cliente ? cliente.nome : 'Proprietário não encontrado'
          };
        });
        
        setVeiculos(veiculosComCliente);
        setServicos(servicosResponse.data || []);
        await buscarTotalAgendamentos();
        
        // Remover interceptor ao finalizar
        axios.interceptors.response.eject(responseInterceptor);
      } catch (error) {
        console.error("Erro ao carregar dados:", error);
        setErro("Erro ao carregar dados do servidor");
      } finally {
        setLoading(false);
      }
    };

    buscarDadosIniciais();
  }, []);

  if (loading) return (
    <div className="flex justify-center items-center min-h-screen bg-background">
      <div className="text-center">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary mx-auto mb-4"></div>
        <p className="text-lg font-medium text-foreground">Carregando agendamentos...</p>
        <p className="text-sm text-muted-foreground">Aguarde um momento</p>
      </div>
    </div>
  );
  if (erro) return <div className="flex justify-center items-center min-h-screen"><p className="text-red-600">{erro}</p></div>;

  const handleNovoAgendamento = async (novoAgendamento: Omit<TypeAgendamento, 'id' | 'createdAt' | 'updatedAt'>) => {
    try {
      setLoading(true); // Mostrar loading durante a operação
      
      const response = await axios.post<TypeAgendamento>("http://localhost:8080/api/agendamentos", novoAgendamento);
      
      // Limpar dados da resposta
      const agendamentoLimpo = {
        ...response.data,
        historicos: undefined
      };
      
      // Sempre recarregar a lista completa para garantir consistência
      const agendamentosResponse = await axios.get(`http://localhost:8080/api/agendamentos?page=${page}&size=8`);
      if (agendamentosResponse.data && Array.isArray(agendamentosResponse.data.content)) {
        const agendamentosLimpos = agendamentosResponse.data.content.map((ag: any) => ({
          ...ag,
          historicos: undefined
        }));
        setAgendamentos(agendamentosLimpos);
        setTotalPages(agendamentosResponse.data.totalPages || 1);
      }
      
      // Atualizar estatísticas
      await buscarTotalAgendamentos();
      
      // Fechar dialog
      setDialogOpen(false);
      
      // Mostrar mensagem de sucesso
      setSucesso('Agendamento criado com sucesso!');
      setTimeout(() => setSucesso(null), 3000);
      
    } catch (error: any) {
      console.error("Erro ao criar agendamento:", error);
      const errorMessage = error.response?.data?.error || "Erro ao criar agendamento";
      setErro(errorMessage);
    } finally {
      setLoading(false);
    }
  };

  const handleEditarAgendamento = (agendamento: TypeAgendamento) => {
    setEditingAgendamento(agendamento);
    setDialogOpen(true);
  };

  const handleAtualizarAgendamento = async (agendamentoAtualizado: Omit<TypeAgendamento, 'id' | 'createdAt' | 'updatedAt'>) => {
    if (!editingAgendamento) return;
    
    try {
      setLoading(true); // Mostrar loading durante a operação
      
      const response = await axios.put<TypeAgendamento>(
        `http://localhost:8080/api/agendamentos/${editingAgendamento.id}`, 
        agendamentoAtualizado
      );
      
      // Sempre recarregar a lista completa para garantir consistência
      const agendamentosResponse = await axios.get(`http://localhost:8080/api/agendamentos?page=${page}&size=8`);
      if (agendamentosResponse.data && Array.isArray(agendamentosResponse.data.content)) {
        const agendamentosLimpos = agendamentosResponse.data.content.map((ag: any) => ({
          ...ag,
          historicos: undefined
        }));
        setAgendamentos(agendamentosLimpos);
        setTotalPages(agendamentosResponse.data.totalPages || 1);
      }
      
      // Atualizar estatísticas
      await buscarTotalAgendamentos();
      
      // Fechar dialog e limpar estado de edição
      setDialogOpen(false);
      setEditingAgendamento(null);
      
      // Mostrar mensagem de sucesso
      setSucesso('Agendamento atualizado com sucesso!');
      setTimeout(() => setSucesso(null), 3000);
      
    } catch (error: any) {
      console.error("Erro ao atualizar agendamento:", error);
      const errorMessage = error.response?.data?.error || "Erro ao atualizar agendamento";
      setErro(errorMessage);
    } finally {
      setLoading(false);
    }
  };

  const handleDeletarAgendamento = async (id: number) => {
    try {
      setLoading(true); // Mostrar loading durante a operação
      
      await axios.delete(`http://localhost:8080/api/agendamentos/${id}`);
      
      // Sempre recarregar a lista completa
      const agendamentosResponse = await axios.get(`http://localhost:8080/api/agendamentos?page=${page}&size=8`);
      if (agendamentosResponse.data && Array.isArray(agendamentosResponse.data.content)) {
        const agendamentosLimpos = agendamentosResponse.data.content.map((ag: any) => ({
          ...ag,
          historicos: undefined
        }));
        setAgendamentos(agendamentosLimpos);
        setTotalPages(agendamentosResponse.data.totalPages || 1);
      }
      
      // Se a página atual ficou vazia e não é a primeira, voltar uma página
      if (agendamentosResponse.data.content.length === 0 && page > 0) {
        setPage(page - 1);
      }
      
      // Atualizar estatísticas
      await buscarTotalAgendamentos();
      
      // Mostrar mensagem de sucesso
      setSucesso('Agendamento excluído com sucesso!');
      setTimeout(() => setSucesso(null), 3000);
      
    } catch (error: any) {
      console.error("Erro ao deletar agendamento:", error);
      const errorMessage = error.response?.data?.error || "Erro ao deletar agendamento";
      setErro(errorMessage);
    } finally {
      setLoading(false);
    }
  };

  const handleAtualizarStatus = async (id: number, novoStatus: string, motivo?: string) => {
    try {
      // Usar funcionário logado do contexto
      const funcionarioId = user?.id;
      
      if (!funcionarioId) {
        setErro("Usuário não autenticado");
        return;
      }
      
      await axios.put(`http://localhost:8080/api/agendamentos/${id}/status`, null, {
        params: {
          status: novoStatus,
          funcionarioId: funcionarioId,
          motivo: motivo || ''
        }
      });
      
      // Recarregar agendamentos
      const agendamentosResponse = await axios.get(`http://localhost:8080/api/agendamentos?page=${page}&size=8`);
      const content = agendamentosResponse.data?.content;
      setAgendamentos(Array.isArray(content) ? content : []);
      buscarTotalAgendamentos();
    } catch (error: any) {
      console.error("Erro ao atualizar status:", error);
      const errorMessage = error.response?.data?.error || "Erro ao atualizar status";
      setErro(errorMessage);
    }
  };

  const handleVoltarAdmin = () => {
    navigate('/admin');
  };

  const formatarData = (data: string) => {
    return format(parseISO(data), 'dd/MM/yyyy', { locale: ptBR });
  };

  const formatarDataHora = (data: string, hora: string) => {
    return `${formatarData(data)} às ${hora}`;
  };

  const formatarPreco = (preco: number) => {
    return preco.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' });
  };

  const getClienteNome = (clienteId: number) => {
    const cliente = clientes.find(c => c.id === clienteId);
    return cliente ? cliente.nome : 'Cliente não encontrado';
  };

  const getVeiculoInfo = (veiculoId: number) => {
    const veiculo = veiculos.find(v => v.id === veiculoId);
    if (!veiculo) return 'Veículo não encontrado';
    
    // Mostrar info do veículo e proprietário
    return `${veiculo.marca} ${veiculo.modelo} - ${veiculo.placa} (Prop: ${veiculo.clienteNome})`;
  };

  const getServicoInfo = (servicoId: number) => {
    const servico = servicos.find(s => s.id === servicoId);
    return servico ? { nome: servico.nome, preco: servico.preco } : { nome: 'Serviço não encontrado', preco: 0 };
  };

  const getStatusBadge = (status: string) => {
    const statusConfig = {
      [StatusAgendamento.AGENDADO]: { label: 'Agendado', className: 'bg-blue-100 text-blue-800' },
      [StatusAgendamento.EM_ANDAMENTO]: { label: 'Em Andamento', className: 'bg-yellow-100 text-yellow-800' },
      [StatusAgendamento.CONCLUIDO]: { label: 'Concluído', className: 'bg-green-100 text-green-800' },
      [StatusAgendamento.CANCELADO]: { label: 'Cancelado', className: 'bg-red-100 text-red-800' }
    };

    const config = statusConfig[status as keyof typeof statusConfig] || 
                  { label: status, className: 'bg-gray-100 text-gray-800' };

    return (
      <Badge className={config.className}>
        {config.label}
      </Badge>
    );
  };

  const agendamentosFiltrados = filtroStatus === 'todos' 
    ? agendamentos || []
    : (agendamentos || []).filter(ag => ag.status === filtroStatus);

  // Estatísticas
  const hoje = new Date();
  const totalAgendamentosArray = Array.isArray(totalAgendamentos) ? totalAgendamentos : [];
  
  const agendamentosHoje = totalAgendamentosArray.filter(ag => 
    ag.dataAgendamento && isToday(parseISO(ag.dataAgendamento))
  ).length;

  const agendamentosFuturos = totalAgendamentosArray.filter(ag => 
    ag.dataAgendamento && (isFuture(parseISO(ag.dataAgendamento)) || isToday(parseISO(ag.dataAgendamento)))
  ).length;

  const agendamentosDoMes = totalAgendamentosArray.filter(agendamento => 
    agendamento.createdAt && isSameMonth(parseISO(agendamento.createdAt), hoje)
  ).length;

  const agendamentosConcluidos = totalAgendamentosArray.filter(ag => 
    ag.status === StatusAgendamento.CONCLUIDO
  ).length;

  const receitaTotal = totalAgendamentosArray
    .filter(ag => ag.status === StatusAgendamento.CONCLUIDO && ag.valorTotal)
    .reduce((acc, ag) => acc + (ag.valorTotal || 0), 0);

  return (
    <div className="min-h-screen bg-background p-6">
      <div className="max-w-7xl mx-auto">
        {/* Header */}
        <div className="flex items-center justify-between mb-8">
          <div className="flex items-center gap-3">
            <Button
              variant="ghost"
              size="icon"
              onClick={handleVoltarAdmin}
              className="mr-2"
            >
              <ArrowLeft className="w-5 h-5" />
            </Button>
            <div className="w-12 h-12 bg-primary/10 rounded-lg flex items-center justify-center">
              <Calendar className="w-6 h-6 text-primary" />
            </div>
            <div>
              <h1 className="text-3xl font-bold text-foreground">Agendamentos</h1>
              <p className="text-muted-foreground">Gerencie os agendamentos de serviços</p>
            </div>
          </div>
          
          <div className="flex items-center gap-3">
            {/* Botão Novo Agendamento */}
            <Dialog open={dialogOpen} onOpenChange={(open) => {
                setDialogOpen(open);
                if (!open) setEditingAgendamento(null);
              }}>
              <DialogTrigger asChild>
                <Button className="flex items-center gap-2">
                  <Plus className="w-4 h-4" />
                  Novo Agendamento
                </Button>
              </DialogTrigger>
              <DialogContent className="max-w-3xl max-h-[90vh] overflow-y-auto">
                <DialogHeader>
                  <DialogTitle className="text-xl font-semibold">
                    {editingAgendamento ? 'Editar Agendamento' : 'Criar Novo Agendamento'}
                  </DialogTitle>
                </DialogHeader>
                <div className="mt-4">
                  <AgendamentoForm 
                    onSubmit={editingAgendamento ? handleAtualizarAgendamento : handleNovoAgendamento} 
                    initialData={editingAgendamento}
                  />
                </div>
              </DialogContent>
            </Dialog>
          </div>
        </div>

        {/* Estatísticas */}
        <div className="grid grid-cols-1 md:grid-cols-5 gap-6 mb-8">
          <Card>
            <CardContent className="p-6">
              <div className="flex items-center justify-between">
                <div>
                  <p className="text-sm font-medium text-muted-foreground">Total</p>
                  <p className="text-2xl font-bold text-foreground">{totalAgendamentosArray.length}</p>
                </div>
                <div className="w-12 h-12 bg-blue-100 rounded-lg flex items-center justify-center">
                  <Calendar className="w-6 h-6 text-blue-600" />
                </div>
              </div>
            </CardContent>
          </Card>

          <Card>
            <CardContent className="p-6">
              <div className="flex items-center justify-between">
                <div>
                  <p className="text-sm font-medium text-muted-foreground">Hoje</p>
                  <p className="text-2xl font-bold text-foreground">{agendamentosHoje}</p>
                </div>
                <div className="w-12 h-12 bg-green-100 rounded-lg flex items-center justify-center">
                  <Clock className="w-6 h-6 text-green-600" />
                </div>
              </div>
            </CardContent>
          </Card>

          <Card>
            <CardContent className="p-6">
              <div className="flex items-center justify-between">
                <div>
                  <p className="text-sm font-medium text-muted-foreground">Futuros</p>
                  <p className="text-2xl font-bold text-foreground">{agendamentosFuturos}</p>
                </div>
                <div className="w-12 h-12 bg-purple-100 rounded-lg flex items-center justify-center">
                  <Calendar className="w-6 h-6 text-purple-600" />
                </div>
              </div>
            </CardContent>
          </Card>

          <Card>
            <CardContent className="p-6">
              <div className="flex items-center justify-between">
                <div>
                  <p className="text-sm font-medium text-muted-foreground">Concluídos</p>
                  <p className="text-2xl font-bold text-foreground">{agendamentosConcluidos}</p>
                </div>
                <div className="w-12 h-12 bg-green-100 rounded-lg flex items-center justify-center">
                  <Settings className="w-6 h-6 text-green-600" />
                </div>
              </div>
            </CardContent>
          </Card>

          <Card>
            <CardContent className="p-6">
              <div className="flex items-center justify-between">
                <div>
                  <p className="text-sm font-medium text-muted-foreground">Receita</p>
                  <p className="text-2xl font-bold text-foreground">
                    {formatarPreco(receitaTotal)}
                  </p>
                </div>
                <div className="w-12 h-12 bg-orange-100 rounded-lg flex items-center justify-center">
                  <DollarSign className="w-6 h-6 text-orange-600" />
                </div>
              </div>
            </CardContent>
          </Card>
        </div>

        {/* Filtros */}
        <div className="mb-6">
          <Tabs value={filtroStatus} onValueChange={setFiltroStatus}>
            <TabsList>
              <TabsTrigger value="todos">Todos</TabsTrigger>
              <TabsTrigger value={StatusAgendamento.AGENDADO}>Agendados</TabsTrigger>
              <TabsTrigger value={StatusAgendamento.EM_ANDAMENTO}>Em Andamento</TabsTrigger>
              <TabsTrigger value={StatusAgendamento.CONCLUIDO}>Concluídos</TabsTrigger>
              <TabsTrigger value={StatusAgendamento.CANCELADO}>Cancelados</TabsTrigger>
            </TabsList>
          </Tabs>
        </div>

        {/* Lista de Agendamentos */}
        <Card>
          <CardHeader>
            <CardTitle>
              Lista de Agendamentos ({agendamentosFiltrados?.length || 0} de {totalAgendamentosArray.length})
            </CardTitle>
          </CardHeader>
          <CardContent>
            {agendamentosFiltrados?.length === 0 ? (
              <div className="text-center py-8">
                <Calendar className="w-12 h-12 mx-auto text-muted-foreground mb-4" />
                <p className="text-muted-foreground">
                  {filtroStatus === 'todos' 
                    ? 'Nenhum agendamento cadastrado ainda.' 
                    : `Nenhum agendamento ${filtroStatus} encontrado.`
                  }
                </p>
                <p className="text-sm text-muted-foreground">
                  {filtroStatus === 'todos' && 'Clique em "Novo Agendamento" para começar.'}
                </p>
              </div>
            ) : (
              <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-2 gap-6">
                {(agendamentosFiltrados || []).map((agendamento) => {
                  const servicoInfo = getServicoInfo(agendamento.servicoId);
                  const isPassado = parseISO(agendamento.dataAgendamento) < new Date();
                  
                  return (
                    <Card key={agendamento.id} className="hover:shadow-md transition-shadow">
                      <CardContent className="p-6">
                        <div className="space-y-4">
                          {/* Header do Card */}
                          <div className="flex items-start justify-between">
                            <div className="flex items-center gap-2">
                              {getStatusBadge(agendamento.status)}
                              {isPassado && agendamento.status === StatusAgendamento.AGENDADO && (
                                <Badge className="bg-orange-100 text-orange-800">Atrasado</Badge>
                              )}
                            </div>
                            <div className="flex items-center gap-2">
                              <Button
                                variant="ghost"
                                size="icon"
                                onClick={() => handleEditarAgendamento(agendamento)}
                                className="h-8 w-8"
                                title="Editar agendamento"
                              >
                                <Edit className="w-4 h-4" />
                              </Button>
                              
                              <AlertDialog>
                                <AlertDialogTrigger asChild>
                                  <Button
                                    variant="ghost"
                                    size="icon"
                                    className="h-8 w-8 text-destructive hover:text-destructive"
                                    title="Excluir agendamento"
                                  >
                                    <Trash2 className="w-4 h-4" />
                                  </Button>
                                </AlertDialogTrigger>
                                <AlertDialogContent>
                                  <AlertDialogHeader>
                                    <AlertDialogTitle>Confirmar exclusão</AlertDialogTitle>
                                    <AlertDialogDescription>
                                      Tem certeza que deseja excluir este agendamento? 
                                      Esta ação não pode ser desfeita.
                                    </AlertDialogDescription>
                                  </AlertDialogHeader>
                                  <AlertDialogFooter>
                                    <AlertDialogCancel>Cancelar</AlertDialogCancel>
                                    <AlertDialogAction
                                      onClick={() => handleDeletarAgendamento(agendamento.id)}
                                      className="bg-destructive text-destructive-foreground hover:bg-destructive/90"
                                    >
                                      Excluir
                                    </AlertDialogAction>
                                  </AlertDialogFooter>
                                </AlertDialogContent>
                              </AlertDialog>
                            </div>
                          </div>

                          {/* Informações principais */}
                          <div className="space-y-3">
                            <div className="flex items-center gap-2">
                              <User className="w-4 h-4 text-muted-foreground" />
                              <span className="font-medium">{getClienteNome(agendamento.clienteId)}</span>
                            </div>

                            <div className="flex items-center gap-2">
                              <Car className="w-4 h-4 text-muted-foreground" />
                              <span className="text-sm">{getVeiculoInfo(agendamento.veiculoId)}</span>
                            </div>

                            <div className="flex items-center gap-2">
                              <Settings className="w-4 h-4 text-muted-foreground" />
                              <span className="text-sm">{servicoInfo.nome}</span>
                            </div>

                            <div className="flex items-center gap-2">
                              <Calendar className="w-4 h-4 text-muted-foreground" />
                              <span className="text-sm">
                                {formatarDataHora(agendamento.dataAgendamento, agendamento.horaInicio)}
                                {agendamento.horaFim && ` - ${agendamento.horaFim}`}
                              </span>
                            </div>

                            {agendamento.valorTotal && agendamento.valorTotal > 0 && (
                              <div className="flex items-center gap-2">
                                <DollarSign className="w-4 h-4 text-muted-foreground" />
                                <span className="text-sm font-medium text-green-600">
                                  {formatarPreco(agendamento.valorTotal)}
                                </span>
                              </div>
                            )}

                            {agendamento.observacoes && (
                              <div className="text-sm text-muted-foreground bg-muted/50 p-2 rounded">
                                <strong>Obs:</strong> {agendamento.observacoes}
                              </div>
                            )}
                          </div>

                          {/* Ações de status */}
                          {agendamento.status === StatusAgendamento.AGENDADO && (
                            <div className="flex gap-2 pt-2 border-t">
                              <Button
                                size="sm"
                                variant="outline"
                                onClick={() => handleAtualizarStatus(agendamento.id, StatusAgendamento.EM_ANDAMENTO)}
                                className="flex-1"
                              >
                                Iniciar
                              </Button>
                              <Button
                                size="sm"
                                variant="outline"
                                onClick={() => handleAtualizarStatus(agendamento.id, StatusAgendamento.CANCELADO, 'Cancelado pelo sistema')}
                                className="flex-1 text-red-600 border-red-200 hover:bg-red-50"
                              >
                                Cancelar
                              </Button>
                            </div>
                          )}

                          {agendamento.status === StatusAgendamento.EM_ANDAMENTO && (
                            <div className="flex gap-2 pt-2 border-t">
                              <Button
                                size="sm"
                                onClick={() => handleAtualizarStatus(agendamento.id, StatusAgendamento.CONCLUIDO)}
                                className="flex-1 bg-green-600 hover:bg-green-700"
                              >
                                Concluir
                              </Button>
                              <Button
                                size="sm"
                                variant="outline"
                                onClick={() => handleAtualizarStatus(agendamento.id, StatusAgendamento.CANCELADO, 'Cancelado durante execução')}
                                className="flex-1 text-red-600 border-red-200 hover:bg-red-50"
                              >
                                Cancelar
                              </Button>
                            </div>
                          )}

                          {/* Data de criação */}
                          <div className="text-xs text-muted-foreground pt-2 border-t">
                            Criado em {formatarData(agendamento.createdAt)}
                          </div>
                        </div>
                      </CardContent>
                    </Card>
                  );
                })}
              </div>
            )}
          </CardContent>
        </Card>

        {/* Paginação */}
        {totalPages > 1 && (
          <div className="flex justify-center items-center gap-4 mt-6">
            <Button 
              disabled={page === 0} 
              onClick={() => setPage(page - 1)}
              variant="outline"
            >
              Anterior
            </Button>
            <span className="text-sm text-muted-foreground">
              Página {page + 1} de {totalPages}
            </span>
            <Button 
              disabled={page + 1 >= totalPages} 
              onClick={() => setPage(page + 1)}
              variant="outline"
            >
              Próxima
            </Button>
          </div>
        )}

        {/* Mensagem de erro */}
        {erro && (
          <div className="fixed bottom-4 right-4 max-w-md p-4 bg-red-50 border border-red-200 rounded-md shadow-lg z-50">
            <div className="flex items-start justify-between">
              <div>
                <p className="text-red-800 font-medium">Erro</p>
                <p className="text-red-700 text-sm">{erro}</p>
              </div>
              <Button 
                variant="ghost" 
                size="sm" 
                onClick={() => setErro(null)}
                className="text-red-600 hover:text-red-800 h-auto p-1"
              >
                ✕
              </Button>
            </div>
          </div>
        )}

        {/* Mensagem de sucesso */}
        {sucesso && (
          <div className="fixed bottom-4 right-4 max-w-md p-4 bg-green-50 border border-green-200 rounded-md shadow-lg z-50">
            <div className="flex items-start justify-between">
              <div>
                <p className="text-green-800 font-medium">Sucesso</p>
                <p className="text-green-700 text-sm">{sucesso}</p>
              </div>
              <Button 
                variant="ghost" 
                size="sm" 
                onClick={() => setSucesso(null)}
                className="text-green-600 hover:text-green-800 h-auto p-1"
              >
                ✕
              </Button>
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

export default Agendamentos;