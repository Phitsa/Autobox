import React, { useEffect, useState } from 'react';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import { 
  ArrowLeft, 
  History, 
  Search, 
  Filter, 
  Calendar, 
  User, 
  FileText,
  Clock,
  BarChart3,
  RefreshCw,
  X
} from 'lucide-react';

// Tipos para o histórico
interface HistoricoAgendamento {
  id: number;
  agendamento: {
    id: number;
    clienteId: number;
    veiculoId: number;
    servicoId: number;
    dataAgendamento: string;
    horaInicio: string;
    status: string;
    valorTotal?: number;
    observacoes?: string;
  };
  funcionarioId: number;
  acao: string;
  dataAcao: string;
  observacoes?: string;
  statusAnterior?: string;
  statusNovo?: string;
}

interface Estatisticas {
  total: number;
  criados: number;
  atualizados: number;
  statusAlterados: number;
}

const HistoricoAgendamentos = () => {
  // Estados principais
  const [historicos, setHistoricos] = useState<HistoricoAgendamento[]>([]);
  const [loading, setLoading] = useState(true);
  const [erro, setErro] = useState<string | null>(null);
  
  // Estados de paginação
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(1);
  const [totalElements, setTotalElements] = useState(0);
  const [size] = useState(10);
  
  // Estados de filtros
  const [filtros, setFiltros] = useState({
    busca: '',
    acao: 'todas',
    funcionarioId: '',
    agendamentoId: '',
    dataInicio: '',
    dataFim: ''
  });
  
  // Estados de ordenação
  const [sortBy, setSortBy] = useState('dataAcao');
  const [sortDir, setSortDir] = useState('desc');
  
  // Estados de estatísticas
  const [estatisticas, setEstatisticas] = useState<Estatisticas>({
    total: 0,
    criados: 0,
    atualizados: 0,
    statusAlterados: 0
  });

  // Carregar históricos
  const carregarHistoricos = async () => {
    try {
      setLoading(true);
      setErro(null);
      
      const params = new URLSearchParams({
        page: page.toString(),
        size: size.toString(),
        sortBy,
        sortDir
      });
      
      const response = await fetch(`http://localhost:8080/api/historico-agendamentos?${params}`);
      
      if (!response.ok) {
        throw new Error(`Erro HTTP: ${response.status}`);
      }
      
      const data = await response.json();
      
      if (data && Array.isArray(data.content)) {
        setHistoricos(data.content);
        setTotalPages(data.totalPages || 1);
        setTotalElements(data.totalElements || data.content.length);
      } else if (Array.isArray(data)) {
        // Caso a API retorne diretamente um array
        setHistoricos(data);
        setTotalPages(1);
        setTotalElements(data.length);
      } else {
        console.warn('Formato de dados inesperado:', data);
        setHistoricos([]);
        setTotalPages(1);
        setTotalElements(0);
      }
    } catch (error) {
      console.error('Erro ao carregar históricos:', error);
      setErro('Erro ao carregar histórico de agendamentos. Verifique sua conexão.');
      setHistoricos([]);
    } finally {
      setLoading(false);
    }
  };

  // Carregar estatísticas
  const carregarEstatisticas = async () => {
    try {
      const response = await fetch('http://localhost:8080/api/historico-agendamentos/stats');
      
      if (!response.ok) {
        console.warn('Erro ao carregar estatísticas:', response.status);
        return;
      }
      
      const data = await response.json();
      if (data && typeof data === 'object') {
        setEstatisticas({
          total: data.total || 0,
          criados: data.criados || 0,
          atualizados: data.atualizados || 0,
          statusAlterados: data.statusAlterados || 0
        });
      }
    } catch (error) {
      console.error('Erro ao carregar estatísticas:', error);
      // Não exibir erro para o usuário, apenas logar
    }
  };

  // Aplicar filtros
  const aplicarFiltros = async () => {
    try {
      setLoading(true);
      setErro(null);
      setPage(0); // Resetar para primeira página
      
      let url = 'http://localhost:8080/api/historico-agendamentos';
      let historicosData: HistoricoAgendamento[] = [];
      
      // Aplicar filtros específicos
      if (filtros.acao !== 'todas') {
        const response = await fetch(`http://localhost:8080/api/historico-agendamentos/acao/${filtros.acao}`);
        if (response.ok) {
          const data = await response.json();
          historicosData = Array.isArray(data) ? data : [];
        }
      } else if (filtros.funcionarioId) {
        const response = await fetch(`http://localhost:8080/api/historico-agendamentos/funcionario/${filtros.funcionarioId}`);
        if (response.ok) {
          const data = await response.json();
          historicosData = Array.isArray(data) ? data : [];
        }
      } else if (filtros.agendamentoId) {
        const response = await fetch(`http://localhost:8080/api/historico-agendamentos/agendamento/${filtros.agendamentoId}`);
        if (response.ok) {
          const data = await response.json();
          historicosData = Array.isArray(data) ? data : [];
        }
      } else {
        // Busca paginada normal
        const params = new URLSearchParams({
          page: '0',
          size: size.toString(),
          sortBy,
          sortDir
        });
        const response = await fetch(`${url}?${params}`);
        if (response.ok) {
          const data = await response.json();
          if (data && Array.isArray(data.content)) {
            historicosData = data.content;
            setTotalPages(data.totalPages || 1);
            setTotalElements(data.totalElements || data.content.length);
          } else if (Array.isArray(data)) {
            historicosData = data;
            setTotalPages(1);
            setTotalElements(data.length);
          }
        }
      }
      
      // Aplicar filtros de busca local se necessário
      if (filtros.busca && historicosData.length > 0) {
        historicosData = historicosData.filter(h => {
          if (!h) return false;
          const buscaLower = filtros.busca.toLowerCase();
          return (
            (h.acao && h.acao.toLowerCase().includes(buscaLower)) ||
            (h.observacoes && h.observacoes.toLowerCase().includes(buscaLower)) ||
            (h.agendamento && h.agendamento.id && h.agendamento.id.toString().includes(filtros.busca))
          );
        });
      }
      
      // Aplicar filtros de data
      if ((filtros.dataInicio || filtros.dataFim) && historicosData.length > 0) {
        historicosData = historicosData.filter(h => {
          if (!h || !h.dataAcao) return false;
          
          try {
            const dataAcao = new Date(h.dataAcao);
            const dataInicio = filtros.dataInicio ? new Date(filtros.dataInicio) : null;
            const dataFim = filtros.dataFim ? new Date(filtros.dataFim + 'T23:59:59') : null;
            
            if (dataInicio && dataAcao < dataInicio) return false;
            if (dataFim && dataAcao > dataFim) return false;
            return true;
          } catch (error) {
            console.warn('Erro ao filtrar por data:', error);
            return false;
          }
        });
      }
      
      setHistoricos(historicosData || []);
    } catch (error) {
      console.error('Erro ao aplicar filtros:', error);
      setErro('Erro ao aplicar filtros. Tente novamente.');
      setHistoricos([]);
    } finally {
      setLoading(false);
    }
  };

  // Limpar filtros
  const limparFiltros = () => {
    setFiltros({
      busca: '',
      acao: 'todas',
      funcionarioId: '',
      agendamentoId: '',
      dataInicio: '',
      dataFim: ''
    });
    setPage(0);
  };

  // Effects
  useEffect(() => {
    carregarHistoricos();
    carregarEstatisticas();
  }, [page, sortBy, sortDir]);

  useEffect(() => {
    const hasFilters = filtros.busca || filtros.acao !== 'todas' || filtros.funcionarioId || 
                     filtros.agendamentoId || filtros.dataInicio || filtros.dataFim;
    
    if (hasFilters) {
      const debounceTimer = setTimeout(() => {
        aplicarFiltros();
      }, 500);
      return () => clearTimeout(debounceTimer);
    } else {
      carregarHistoricos();
    }
  }, [filtros]);

  // Formatação
  const formatarDataHora = (data: string) => {
    try {
      const date = new Date(data);
      return date.toLocaleString('pt-BR', {
        day: '2-digit',
        month: '2-digit',
        year: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
      });
    } catch {
      return data;
    }
  };

  const formatarData = (data: string) => {
    try {
      const date = new Date(data);
      return date.toLocaleDateString('pt-BR');
    } catch {
      return data;
    }
  };

  const getAcaoBadge = (acao: string) => {
    const acaoConfig: Record<string, { label: string; className: string }> = {
      'CRIADO': { label: 'Criado', className: 'bg-blue-100 text-blue-800' },
      'ATUALIZADO': { label: 'Atualizado', className: 'bg-yellow-100 text-yellow-800' },
      'STATUS_ALTERADO': { label: 'Status Alterado', className: 'bg-purple-100 text-purple-800' },
      'CANCELADO': { label: 'Cancelado', className: 'bg-red-100 text-red-800' },
      'CONCLUIDO': { label: 'Concluído', className: 'bg-green-100 text-green-800' }
    };

    const config = acaoConfig[acao] || { label: acao, className: 'bg-gray-100 text-gray-800' };
    return <Badge className={config.className}>{config.label}</Badge>;
  };

  if (loading && page === 0) {
    return (
      <div className="flex justify-center items-center min-h-screen bg-background">
        <div className="text-center">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary mx-auto mb-4"></div>
          <p className="text-lg font-medium text-foreground">Carregando histórico...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-background p-6">
      <div className="max-w-7xl mx-auto">
        {/* Header */}
        <div className="flex items-center justify-between mb-8">
          <div className="flex items-center gap-3">
            <Button
              variant="ghost"
              size="icon"
              onClick={() => window.history.back()}
              className="mr-2"
            >
              <ArrowLeft className="w-5 h-5" />
            </Button>
            <div className="w-12 h-12 bg-orange-100 rounded-lg flex items-center justify-center">
              <History className="w-6 h-6 text-orange-600" />
            </div>
            <div>
              <h1 className="text-3xl font-bold text-foreground">Histórico de Agendamentos</h1>
              <p className="text-muted-foreground">Visualize todo o histórico de mudanças</p>
            </div>
          </div>
          
          <Button 
            variant="outline" 
            onClick={() => {
              carregarHistoricos();
              carregarEstatisticas();
            }}
            className="flex items-center gap-2"
          >
            <RefreshCw className="w-4 h-4" />
            Atualizar
          </Button>
        </div>

        {/* Estatísticas */}
        <div className="grid grid-cols-1 md:grid-cols-4 gap-6 mb-8">
          <Card>
            <CardContent className="p-6">
              <div className="flex items-center justify-between">
                <div>
                  <p className="text-sm font-medium text-muted-foreground">Total</p>
                  <p className="text-2xl font-bold text-foreground">{estatisticas.total}</p>
                </div>
                <div className="w-12 h-12 bg-blue-100 rounded-lg flex items-center justify-center">
                  <BarChart3 className="w-6 h-6 text-blue-600" />
                </div>
              </div>
            </CardContent>
          </Card>

          <Card>
            <CardContent className="p-6">
              <div className="flex items-center justify-between">
                <div>
                  <p className="text-sm font-medium text-muted-foreground">Criados</p>
                  <p className="text-2xl font-bold text-foreground">{estatisticas.criados}</p>
                </div>
                <div className="w-12 h-12 bg-green-100 rounded-lg flex items-center justify-center">
                  <FileText className="w-6 h-6 text-green-600" />
                </div>
              </div>
            </CardContent>
          </Card>

          <Card>
            <CardContent className="p-6">
              <div className="flex items-center justify-between">
                <div>
                  <p className="text-sm font-medium text-muted-foreground">Atualizados</p>
                  <p className="text-2xl font-bold text-foreground">{estatisticas.atualizados}</p>
                </div>
                <div className="w-12 h-12 bg-yellow-100 rounded-lg flex items-center justify-center">
                  <Clock className="w-6 h-6 text-yellow-600" />
                </div>
              </div>
            </CardContent>
          </Card>

          <Card>
            <CardContent className="p-6">
              <div className="flex items-center justify-between">
                <div>
                  <p className="text-sm font-medium text-muted-foreground">Status Alterados</p>
                  <p className="text-2xl font-bold text-foreground">{estatisticas.statusAlterados}</p>
                </div>
                <div className="w-12 h-12 bg-purple-100 rounded-lg flex items-center justify-center">
                  <History className="w-6 h-6 text-purple-600" />
                </div>
              </div>
            </CardContent>
          </Card>
        </div>

        {/* Filtros */}
        <Card className="mb-6">
          <CardHeader>
            <CardTitle className="flex items-center gap-2">
              <Filter className="w-5 h-5" />
              Filtros de Busca
            </CardTitle>
          </CardHeader>
          <CardContent>
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
              {/* Busca Global */}
              <div className="space-y-2">
                <Label htmlFor="busca">Busca Global</Label>
                <div className="relative">
                  <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-muted-foreground w-4 h-4" />
                  <Input
                    id="busca"
                    placeholder="Buscar por ação, observação, ID..."
                    value={filtros.busca}
                    onChange={(e) => setFiltros(prev => ({ ...prev, busca: e.target.value }))}
                    className="pl-10"
                  />
                </div>
              </div>

              {/* Filtro por Ação */}
              <div className="space-y-2">
                <Label htmlFor="acao">Tipo de Ação</Label>
                <select
                  id="acao"
                  value={filtros.acao}
                  onChange={(e) => setFiltros(prev => ({ ...prev, acao: e.target.value }))}
                  className="w-full p-2 border border-gray-300 rounded-md focus:ring-2 focus:ring-primary focus:border-transparent"
                >
                  <option value="todas">Todas as ações</option>
                  <option value="CRIADO">Criado</option>
                  <option value="ATUALIZADO">Atualizado</option>
                  <option value="STATUS_ALTERADO">Status Alterado</option>
                  <option value="CANCELADO">Cancelado</option>
                  <option value="CONCLUIDO">Concluído</option>
                </select>
              </div>

              {/* ID do Agendamento */}
              <div className="space-y-2">
                <Label htmlFor="agendamentoId">ID do Agendamento</Label>
                <Input
                  id="agendamentoId"
                  type="number"
                  placeholder="Ex: 123"
                  value={filtros.agendamentoId}
                  onChange={(e) => setFiltros(prev => ({ ...prev, agendamentoId: e.target.value }))}
                />
              </div>

              {/* ID do Funcionário */}
              <div className="space-y-2">
                <Label htmlFor="funcionarioId">ID do Funcionário</Label>
                <Input
                  id="funcionarioId"
                  type="number"
                  placeholder="Ex: 1"
                  value={filtros.funcionarioId}
                  onChange={(e) => setFiltros(prev => ({ ...prev, funcionarioId: e.target.value }))}
                />
              </div>

              {/* Data Início */}
              <div className="space-y-2">
                <Label htmlFor="dataInicio">Data Início</Label>
                <Input
                  id="dataInicio"
                  type="date"
                  value={filtros.dataInicio}
                  onChange={(e) => setFiltros(prev => ({ ...prev, dataInicio: e.target.value }))}
                />
              </div>

              {/* Data Fim */}
              <div className="space-y-2">
                <Label htmlFor="dataFim">Data Fim</Label>
                <Input
                  id="dataFim"
                  type="date"
                  value={filtros.dataFim}
                  onChange={(e) => setFiltros(prev => ({ ...prev, dataFim: e.target.value }))}
                />
              </div>
            </div>

            {/* Botões de ação */}
            <div className="flex justify-end gap-2 mt-4">
              <Button 
                variant="outline" 
                onClick={limparFiltros}
                className="flex items-center gap-2"
              >
                <X className="w-4 h-4" />
                Limpar Filtros
              </Button>
            </div>
          </CardContent>
        </Card>

        {/* Lista de Históricos */}
        <Card>
          <CardHeader>
            <CardTitle className="flex items-center justify-between">
              <span>Histórico de Mudanças ({totalElements} registros)</span>
              <div className="flex items-center gap-2">
                <Label className="text-sm">Ordenar por:</Label>
                <select
                  value={`${sortBy}-${sortDir}`}
                  onChange={(e) => {
                    const [field, direction] = e.target.value.split('-');
                    setSortBy(field);
                    setSortDir(direction);
                  }}
                  className="text-sm p-1 border border-gray-300 rounded"
                >
                  <option value="dataAcao-desc">Data (Mais recente)</option>
                  <option value="dataAcao-asc">Data (Mais antigo)</option>
                  <option value="acao-asc">Ação (A-Z)</option>
                  <option value="acao-desc">Ação (Z-A)</option>
                </select>
              </div>
            </CardTitle>
          </CardHeader>
          <CardContent>
            {loading ? (
              <div className="flex justify-center py-8">
                <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-primary"></div>
              </div>
            ) : !historicos || historicos.length === 0 ? (
              <div className="text-center py-8">
                <History className="w-12 h-12 mx-auto text-muted-foreground mb-4" />
                <p className="text-muted-foreground">Nenhum histórico encontrado.</p>
                <p className="text-sm text-muted-foreground">Tente ajustar os filtros de busca.</p>
              </div>
            ) : (
              <div className="space-y-4">
                {historicos.filter(h => h && h.id).map((historico) => {
                  // Verificações de segurança
                  if (!historico || !historico.id) return null;
                  
                  const agendamento = historico.agendamento || {};
                  const agendamentoId = agendamento.id || 'N/A';
                  const dataAgendamento = agendamento.dataAgendamento || '';
                  const horaInicio = agendamento.horaInicio || 'N/A';
                  const status = agendamento.status || 'N/A';
                  const valorTotal = agendamento.valorTotal;
                  const observacoesAgendamento = agendamento.observacoes;
                  
                  return (
                    <Card key={historico.id} className="hover:shadow-md transition-shadow">
                      <CardContent className="p-6">
                        <div className="flex items-start justify-between mb-4">
                          <div className="flex items-center gap-3">
                            {getAcaoBadge(historico.acao || 'DESCONHECIDO')}
                            <span className="text-sm text-muted-foreground">
                              Agendamento #{agendamentoId}
                            </span>
                            <span className="text-sm text-muted-foreground">
                              Funcionário #{historico.funcionarioId || 'N/A'}
                            </span>
                          </div>
                          <div className="text-right">
                            <p className="text-sm font-medium">
                              {historico.dataAcao ? formatarDataHora(historico.dataAcao) : 'Data não disponível'}
                            </p>
                            <p className="text-xs text-muted-foreground">ID: {historico.id}</p>
                          </div>
                        </div>

                        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                          <div>
                            <h4 className="font-medium mb-2">Detalhes do Agendamento</h4>
                            <div className="space-y-1 text-sm">
                              <p><span className="font-medium">Data:</span> {dataAgendamento ? formatarData(dataAgendamento) : 'N/A'}</p>
                              <p><span className="font-medium">Hora:</span> {horaInicio}</p>
                              <p><span className="font-medium">Status Atual:</span> {status}</p>
                              {valorTotal && (
                                <p><span className="font-medium">Valor:</span> R$ {valorTotal.toFixed(2)}</p>
                              )}
                            </div>
                          </div>

                          <div>
                            <h4 className="font-medium mb-2">Detalhes da Ação</h4>
                            <div className="space-y-1 text-sm">
                              {historico.statusAnterior && (
                                <p><span className="font-medium">Status Anterior:</span> {historico.statusAnterior}</p>
                              )}
                              {historico.statusNovo && (
                                <p><span className="font-medium">Status Novo:</span> {historico.statusNovo}</p>
                              )}
                              {historico.observacoes && (
                                <div>
                                  <span className="font-medium">Observações:</span>
                                  <p className="text-muted-foreground bg-muted/50 p-2 rounded mt-1">
                                    {historico.observacoes}
                                  </p>
                                </div>
                              )}
                            </div>
                          </div>
                        </div>

                        {observacoesAgendamento && (
                          <div className="mt-4 pt-4 border-t">
                            <h4 className="font-medium mb-2">Observações do Agendamento</h4>
                            <p className="text-sm text-muted-foreground bg-muted/30 p-2 rounded">
                              {observacoesAgendamento}
                            </p>
                          </div>
                        )}
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
              disabled={page === 0 || loading} 
              onClick={() => setPage(page - 1)}
              variant="outline"
            >
              Anterior
            </Button>
            <span className="text-sm text-muted-foreground">
              Página {page + 1} de {totalPages}
            </span>
            <Button 
              disabled={page + 1 >= totalPages || loading} 
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
      </div>
    </div>
  );
};

export default HistoricoAgendamentos;