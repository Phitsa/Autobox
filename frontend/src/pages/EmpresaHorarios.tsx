import React, { useEffect, useState } from 'react';
import { Button } from '@/components/ui/button';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { useNavigate } from 'react-router-dom';
import { ArrowLeft, Clock, Save, AlertCircle, Plus, Trash2, CheckCircle, XCircle } from 'lucide-react';
import { Switch } from '@/components/ui/switch';

// Tipos
interface EmpresaHorario {
  id?: number;
  empresaId: number;
  diaSemana: number;
  nomeDiaSemana?: string;
  horarioAbertura?: string;
  horarioFechamento?: string;
  horarioAberturaTarde?: string;
  horarioFechamentoTarde?: string;
  fechado: boolean;
  observacoes?: string;
  ativo: boolean;
  aberto?: boolean;
}

interface Empresa {
  id: number;
  nomeFantasia: string;
}

// Mock data como fallback
const mockEmpresa: Empresa = {
  id: 1,
  nomeFantasia: "BoxPro Lavagem"
};

const mockHorarios: EmpresaHorario[] = [
  { id: 1, empresaId: 1, diaSemana: 0, nomeDiaSemana: "Domingo", fechado: true, ativo: true, observacoes: "Fechado" },
  { id: 2, empresaId: 1, diaSemana: 1, nomeDiaSemana: "Segunda-feira", horarioAbertura: "08:00", horarioFechamento: "12:00", horarioAberturaTarde: "13:00", horarioFechamentoTarde: "18:00", fechado: false, ativo: true },
  { id: 3, empresaId: 1, diaSemana: 2, nomeDiaSemana: "Terça-feira", horarioAbertura: "08:00", horarioFechamento: "12:00", horarioAberturaTarde: "13:00", horarioFechamentoTarde: "18:00", fechado: false, ativo: true },
  { id: 4, empresaId: 1, diaSemana: 3, nomeDiaSemana: "Quarta-feira", horarioAbertura: "08:00", horarioFechamento: "12:00", horarioAberturaTarde: "13:00", horarioFechamentoTarde: "18:00", fechado: false, ativo: true },
  { id: 5, empresaId: 1, diaSemana: 4, nomeDiaSemana: "Quinta-feira", horarioAbertura: "08:00", horarioFechamento: "12:00", horarioAberturaTarde: "13:00", horarioFechamentoTarde: "18:00", fechado: false, ativo: true },
  { id: 6, empresaId: 1, diaSemana: 5, nomeDiaSemana: "Sexta-feira", horarioAbertura: "08:00", horarioFechamento: "12:00", horarioAberturaTarde: "13:00", horarioFechamentoTarde: "18:00", fechado: false, ativo: true },
  { id: 7, empresaId: 1, diaSemana: 6, nomeDiaSemana: "Sábado", horarioAbertura: "08:00", horarioFechamento: "12:00", fechado: false, ativo: true },
];

const diasSemana = [
  { codigo: 0, nome: "Domingo" },
  { codigo: 1, nome: "Segunda-feira" },
  { codigo: 2, nome: "Terça-feira" },
  { codigo: 3, nome: "Quarta-feira" },
  { codigo: 4, nome: "Quinta-feira" },
  { codigo: 5, nome: "Sexta-feira" },
  { codigo: 6, nome: "Sábado" }
];

const EmpresaHorarios = () => {
  const navigate = useNavigate();
  
  const [empresa, setEmpresa] = useState<Empresa | null>(null);
  const [horarios, setHorarios] = useState<EmpresaHorario[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [saving, setSaving] = useState<boolean>(false);
  const [erro, setErro] = useState<string | null>(null);
  const [sucesso, setSucesso] = useState<string | null>(null);
  const [usingMockData, setUsingMockData] = useState(false);

  // Função para testar conexão com o backend
  const testarConexao = async () => {
    try {
      const response = await fetch('http://localhost:8080/api/empresa-horarios/status');
      return response.ok;
    } catch (error) {
      return false;
    }
  };

  // Inicializar horários para todos os dias da semana
  const inicializarHorarios = () => {
    return diasSemana.map(dia => ({
      empresaId: empresa?.id || 1,
      diaSemana: dia.codigo,
      nomeDiaSemana: dia.nome,
      horarioAbertura: '',
      horarioFechamento: '',
      horarioAberturaTarde: '',
      horarioFechamentoTarde: '',
      fechado: dia.codigo === 0, // Domingo fechado por padrão
      ativo: true,
      observacoes: ''
    }));
  };

  // Buscar dados da empresa e horários
  useEffect(() => {
    const buscarDados = async () => {
      try {
        setLoading(true);
        setErro(null);
        
        // Verificar se o usuário é ADMIN
        const userStr = localStorage.getItem('boxpro_user');
        if (userStr) {
          const user = JSON.parse(userStr);
          if (user.tipoFuncionario !== 'ADMIN') {
            navigate('/admin');
            return;
          }
        } else {
          navigate('/login');
          return;
        }

        // Primeiro testa se o backend está rodando
        const backendOnline = await testarConexao();
        
        if (!backendOnline) {
          console.warn("Backend não está disponível, usando dados mock");
          setEmpresa(mockEmpresa);
          setHorarios(mockHorarios);
          setUsingMockData(true);
          setErro("⚠️ Usando dados de demonstração - Backend não conectado");
          return;
        }

        const token = localStorage.getItem('boxpro_token');
        const headers: HeadersInit = {
          'Content-Type': 'application/json'
        };
        
        if (token) {
          headers['Authorization'] = `Bearer ${token}`;
        }

        // Buscar dados da empresa
        const empresaResponse = await fetch('http://localhost:8080/api/empresa', { headers });
        
        if (!empresaResponse.ok) {
          throw new Error('Empresa não encontrada. Configure os dados da empresa primeiro.');
        }
        
        const empresaData = await empresaResponse.json();
        setEmpresa({
          id: empresaData.id,
          nomeFantasia: empresaData.nomeFantasia || empresaData.nome_fantasia
        });

        // Buscar horários existentes
        const horariosResponse = await fetch(`http://localhost:8080/api/empresa-horarios/empresa/${empresaData.id}`, { headers });
        
        if (horariosResponse.ok) {
          const horariosData = await horariosResponse.json();
          if (horariosData.length > 0) {
            setHorarios(horariosData);
          } else {
            // Se não tem horários, inicializa com template padrão
            setHorarios(inicializarHorarios());
          }
        } else {
          // Se não tem horários, inicializa com template padrão
          setHorarios(inicializarHorarios());
        }
        
        setUsingMockData(false);
        
      } catch (error) {
        console.error("Erro ao carregar dados:", error);
        setEmpresa(mockEmpresa);
        setHorarios(mockHorarios);
        setUsingMockData(true);
        setErro("⚠️ Erro de conexão - Usando dados de demonstração");
      } finally {
        setLoading(false);
      }
    };

    buscarDados();
  }, [navigate]);

  const handleSalvarTodos = async () => {
    if (!empresa) return;
    
    try {
      setSaving(true);
      setErro(null);
      setSucesso(null);

      if (usingMockData) {
        // Modo mock - simula salvamento
        setTimeout(() => {
          setSucesso("Horários salvos com sucesso!");
          setSaving(false);
        }, 1000);
        return;
      }

      const token = localStorage.getItem('boxpro_token');
      const headers: HeadersInit = {
        'Content-Type': 'application/json'
      };
      
      if (token) {
        headers['Authorization'] = `Bearer ${token}`;
      }

      // Salvar cada horário individualmente
      const promises = horarios.map(async (horario) => {
        const dadosParaEnviar = {
          empresaId: empresa.id,
          diaSemana: horario.diaSemana,
          horarioAbertura: horario.horarioAbertura || null,
          horarioFechamento: horario.horarioFechamento || null,
          horarioAberturaTarde: horario.horarioAberturaTarde || null,
          horarioFechamentoTarde: horario.horarioFechamentoTarde || null,
          fechado: horario.fechado,
          observacoes: horario.observacoes || null,
          ativo: true
        };

        if (horario.id) {
          // Atualizar horário existente
          return fetch(`http://localhost:8080/api/empresa-horarios/${horario.id}`, {
            method: 'PUT',
            headers,
            body: JSON.stringify(dadosParaEnviar),
          });
        } else {
          // Criar novo horário
          return fetch('http://localhost:8080/api/empresa-horarios', {
            method: 'POST',
            headers,
            body: JSON.stringify(dadosParaEnviar),
          });
        }
      });

      const responses = await Promise.all(promises);
      
      // Verificar se todas as requisições foram bem-sucedidas
      const failedResponses = responses.filter(response => !response.ok);
      
      if (failedResponses.length > 0) {
        throw new Error(`Erro ao salvar ${failedResponses.length} horário(s)`);
      }

      // Atualizar os dados dos horários com as respostas
      const updatedHorarios = await Promise.all(
        responses.map(async (response) => {
          const data = await response.json();
          return data.horario || data;
        })
      );

      setHorarios(updatedHorarios);
      setSucesso("Horários salvos com sucesso!");
      
    } catch (error) {
      console.error("Erro ao salvar horários:", error);
      setErro(`Erro ao salvar horários: ${error.message}`);
    } finally {
      setSaving(false);
    }
  };

  const handleInicializarHorarios = async () => {
    if (!empresa) return;
    
    try {
      setSaving(true);
      setErro(null);

      if (usingMockData) {
        setHorarios(mockHorarios);
        setSucesso("Horários padrão inicializados!");
        setSaving(false);
        return;
      }

      const token = localStorage.getItem('boxpro_token');
      const headers: HeadersInit = {
        'Content-Type': 'application/json'
      };
      
      if (token) {
        headers['Authorization'] = `Bearer ${token}`;
      }

      const response = await fetch(`http://localhost:8080/api/empresa-horarios/empresa/${empresa.id}/inicializar`, {
        method: 'POST',
        headers,
      });

      if (response.ok) {
        const data = await response.json();
        setHorarios(data.horarios || []);
        setSucesso("Horários padrão inicializados com sucesso!");
      } else {
        throw new Error('Erro ao inicializar horários padrão');
      }
      
    } catch (error) {
      console.error("Erro ao inicializar horários:", error);
      setErro(`Erro ao inicializar horários: ${error.message}`);
    } finally {
      setSaving(false);
    }
  };

  const handleHorarioChange = (diaSemana: number, field: keyof EmpresaHorario, value: string | boolean) => {
    setHorarios(prev => prev.map(horario => 
      horario.diaSemana === diaSemana 
        ? { ...horario, [field]: value }
        : horario
    ));
  };

  const handleVoltarConfiguracoesEmpresa = () => {
    navigate('/configuracoes');
  };

  const formatarHorario = (value: string) => {
    const numbers = value.replace(/\D/g, '');
    if (numbers.length <= 2) return numbers;
    return `${numbers.slice(0, 2)}:${numbers.slice(2, 4)}`;
  };

  if (loading) {
    return (
      <div className="flex justify-center items-center min-h-screen">
        <div className="text-center">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary mx-auto mb-4"></div>
          <p>Carregando horários...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-background p-6">
      <div className="max-w-6xl mx-auto">
        {/* Alerta sobre modo mock */}
        {usingMockData && (
          <div className="mb-6 p-4 bg-yellow-50 border border-yellow-200 rounded-md flex items-center gap-3">
            <AlertCircle className="w-5 h-5 text-yellow-600" />
            <div>
              <p className="text-yellow-800 font-medium">Modo Demonstração</p>
              <p className="text-yellow-700 text-sm">
                Backend não conectado. Verifique se o servidor Spring Boot está rodando em http://localhost:8080
              </p>
            </div>
          </div>
        )}

        {/* Header */}
        <div className="flex items-center justify-between mb-8">
          <div className="flex items-center gap-3">
            <Button
              variant="ghost"
              size="icon"
              onClick={handleVoltarConfiguracoesEmpresa}
              className="mr-2"
            >
              <ArrowLeft className="w-5 h-5" />
            </Button>
            <div className="w-12 h-12 bg-primary/10 rounded-lg flex items-center justify-center">
              <Clock className="w-6 h-6 text-primary" />
            </div>
            <div>
              <h1 className="text-3xl font-bold text-foreground">Horários de Funcionamento</h1>
              <p className="text-muted-foreground">
                {empresa?.nomeFantasia} - Configure os horários para cada dia da semana
              </p>
            </div>
          </div>

          <div className="flex gap-2">
            {horarios.length === 0 && (
              <Button
                onClick={handleInicializarHorarios}
                disabled={saving}
                variant="outline"
              >
                <Plus className="w-4 h-4 mr-2" />
                Inicializar Horários
              </Button>
            )}
            <Button
              onClick={handleSalvarTodos}
              disabled={saving || horarios.length === 0}
            >
              {saving ? (
                <div className="flex items-center gap-2">
                  <div className="animate-spin rounded-full h-4 w-4 border-b-2 border-white"></div>
                  Salvando...
                </div>
              ) : (
                <div className="flex items-center gap-2">
                  <Save className="w-4 h-4" />
                  Salvar Todos
                </div>
              )}
            </Button>
          </div>
        </div>

        {/* Alertas de feedback */}
        {erro && (
          <div className="mb-6 p-4 bg-red-50 border border-red-200 rounded-md flex items-center gap-3">
            <AlertCircle className="w-5 h-5 text-red-600" />
            <p className="text-red-800">{erro}</p>
            <Button 
              variant="ghost" 
              size="sm" 
              onClick={() => setErro(null)}
              className="ml-auto"
            >
              ×
            </Button>
          </div>
        )}

        {sucesso && (
          <div className="mb-6 p-4 bg-green-50 border border-green-200 rounded-md flex items-center gap-3">
            <CheckCircle className="w-5 h-5 text-green-600" />
            <p className="text-green-800">{sucesso}</p>
            <Button 
              variant="ghost" 
              size="sm" 
              onClick={() => setSucesso(null)}
              className="ml-auto"
            >
              ×
            </Button>
          </div>
        )}

        {/* Cards dos Horários */}
        <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
          {horarios.map((horario) => (
            <Card key={horario.diaSemana} className="relative">
              <CardHeader className="pb-4">
                <CardTitle className="flex items-center justify-between">
                  <span className="flex items-center gap-2">
                    <div className={`w-4 h-4 rounded-full ${horario.fechado ? 'bg-red-500' : 'bg-green-500'}`}></div>
                    {horario.nomeDiaSemana || diasSemana.find(d => d.codigo === horario.diaSemana)?.nome}
                  </span>
                  <div className="flex items-center gap-2">
                    <span className="text-sm text-muted-foreground">
                      {horario.fechado ? 'Fechado' : 'Aberto'}
                    </span>
                    <Switch
                      checked={!horario.fechado}
                      onCheckedChange={(checked) => handleHorarioChange(horario.diaSemana, 'fechado', !checked)}
                    />
                  </div>
                </CardTitle>
              </CardHeader>

              <CardContent className="space-y-4">
                {!horario.fechado ? (
                  <>
                    {/* Horário Manhã */}
                    <div>
                      <label className="block text-sm font-medium mb-2">Horário Manhã</label>
                      <div className="grid grid-cols-2 gap-3">
                        <div>
                          <label className="block text-xs text-muted-foreground mb-1">Abertura</label>
                          <input
                            type="text"
                            className="w-full p-2 border rounded-md focus:ring-2 focus:ring-primary focus:border-transparent text-sm"
                            value={horario.horarioAbertura || ''}
                            onChange={(e) => {
                              const formatted = formatarHorario(e.target.value);
                              if (formatted.length <= 5) {
                                handleHorarioChange(horario.diaSemana, 'horarioAbertura', formatted);
                              }
                            }}
                            placeholder="08:00"
                            maxLength={5}
                          />
                        </div>
                        <div>
                          <label className="block text-xs text-muted-foreground mb-1">Fechamento</label>
                          <input
                            type="text"
                            className="w-full p-2 border rounded-md focus:ring-2 focus:ring-primary focus:border-transparent text-sm"
                            value={horario.horarioFechamento || ''}
                            onChange={(e) => {
                              const formatted = formatarHorario(e.target.value);
                              if (formatted.length <= 5) {
                                handleHorarioChange(horario.diaSemana, 'horarioFechamento', formatted);
                              }
                            }}
                            placeholder="12:00"
                            maxLength={5}
                          />
                        </div>
                      </div>
                    </div>

                    {/* Horário Tarde */}
                    <div>
                      <label className="block text-sm font-medium mb-2">Horário Tarde (Opcional)</label>
                      <div className="grid grid-cols-2 gap-3">
                        <div>
                          <label className="block text-xs text-muted-foreground mb-1">Abertura</label>
                          <input
                            type="text"
                            className="w-full p-2 border rounded-md focus:ring-2 focus:ring-primary focus:border-transparent text-sm"
                            value={horario.horarioAberturaTarde || ''}
                            onChange={(e) => {
                              const formatted = formatarHorario(e.target.value);
                              if (formatted.length <= 5) {
                                handleHorarioChange(horario.diaSemana, 'horarioAberturaTarde', formatted);
                              }
                            }}
                            placeholder="13:00"
                            maxLength={5}
                          />
                        </div>
                        <div>
                          <label className="block text-xs text-muted-foreground mb-1">Fechamento</label>
                          <input
                            type="text"
                            className="w-full p-2 border rounded-md focus:ring-2 focus:ring-primary focus:border-transparent text-sm"
                            value={horario.horarioFechamentoTarde || ''}
                            onChange={(e) => {
                              const formatted = formatarHorario(e.target.value);
                              if (formatted.length <= 5) {
                                handleHorarioChange(horario.diaSemana, 'horarioFechamentoTarde', formatted);
                              }
                            }}
                            placeholder="18:00"
                            maxLength={5}
                          />
                        </div>
                      </div>
                    </div>

                    {/* Observações */}
                    <div>
                      <label className="block text-sm font-medium mb-1">Observações</label>
                      <input
                        type="text"
                        className="w-full p-2 border rounded-md focus:ring-2 focus:ring-primary focus:border-transparent text-sm"
                        value={horario.observacoes || ''}
                        onChange={(e) => handleHorarioChange(horario.diaSemana, 'observacoes', e.target.value)}
                        placeholder="Observações especiais para este dia"
                        maxLength={255}
                      />
                    </div>
                  </>
                ) : (
                  <div className="text-center py-8 text-muted-foreground">
                    <XCircle className="w-12 h-12 mx-auto mb-2 opacity-50" />
                    <p>Estabelecimento fechado neste dia</p>
                    
                    {/* Observações para dia fechado */}
                    <div className="mt-4">
                      <label className="block text-sm font-medium mb-1">Observações</label>
                      <input
                        type="text"
                        className="w-full p-2 border rounded-md focus:ring-2 focus:ring-primary focus:border-transparent text-sm"
                        value={horario.observacoes || ''}
                        onChange={(e) => handleHorarioChange(horario.diaSemana, 'observacoes', e.target.value)}
                        placeholder="Motivo do fechamento ou observações"
                        maxLength={255}
                      />
                    </div>
                  </div>
                )}
              </CardContent>
            </Card>
          ))}
        </div>

        {/* Resumo dos Horários */}
        {horarios.length > 0 && (
          <Card className="mt-8">
            <CardHeader>
              <CardTitle>Resumo dos Horários</CardTitle>
            </CardHeader>
            <CardContent>
              <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
                {horarios.map((horario) => (
                  <div key={horario.diaSemana} className="flex items-center justify-between p-3 bg-muted/30 rounded-lg">
                    <div className="flex items-center gap-2">
                      <div className={`w-3 h-3 rounded-full ${horario.fechado ? 'bg-red-500' : 'bg-green-500'}`}></div>
                      <span className="font-medium text-sm">
                        {horario.nomeDiaSemana || diasSemana.find(d => d.codigo === horario.diaSemana)?.nome}
                      </span>
                    </div>
                    <div className="text-right">
                      {horario.fechado ? (
                        <span className="text-sm text-red-600">Fechado</span>
                      ) : (
                        <div className="text-xs space-y-1">
                          {horario.horarioAbertura && horario.horarioFechamento && (
                            <div>{horario.horarioAbertura} - {horario.horarioFechamento}</div>
                          )}
                          {horario.horarioAberturaTarde && horario.horarioFechamentoTarde && (
                            <div>{horario.horarioAberturaTarde} - {horario.horarioFechamentoTarde}</div>
                          )}
                        </div>
                      )}
                    </div>
                  </div>
                ))}
              </div>
            </CardContent>
          </Card>
        )}
      </div>
    </div>
  );
};

export default EmpresaHorarios;