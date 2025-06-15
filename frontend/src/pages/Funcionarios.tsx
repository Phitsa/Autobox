import React, { useEffect, useState } from 'react';
import { Button } from '@/components/ui/button';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogTrigger } from '@/components/ui/dialog';
import { useNavigate } from 'react-router-dom';
import { Plus, Users, ArrowLeft, Edit, Trash2, Calendar, UserCheck, Shield, Lock, Unlock, RotateCcw, AlertCircle } from 'lucide-react';
import { parseISO, isSameMonth } from 'date-fns';
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

// Tipos
interface TypeFuncionario {
  id: number;
  nome: string;
  email: string;
  telefone?: string;
  cpf?: string;
  tipoFuncionario: 'ADMIN' | 'FUNCIONARIO';
  ativo: boolean;
  dataCriacao: string;
  dataAtualizacao?: string;
  ultimoLogin?: string;
  tentativasLogin: number;
  bloqueado: boolean;
}

interface TypeFuncionarioForm {
  nome: string;
  email: string;
  senha: string;
  telefone?: string;
  cpf?: string;
  tipoFuncionario: 'ADMIN' | 'FUNCIONARIO';
}

interface TypeFuncionarioStats {
  total: number;
  ativos: number;
  disponiveis: number;
  admins: number;
  funcionarios: number;
  inativos: number;
}

// Mock data como fallback
const mockFuncionarios: TypeFuncionario[] = [
  {
    id: 1,
    nome: "Admin Sistema",
    email: "admin@boxpro.com",
    telefone: "(11) 99999-9999",
    cpf: "123.456.789-00",
    tipoFuncionario: 'ADMIN',
    ativo: true,
    dataCriacao: "2025-01-15T10:00:00Z",
    dataAtualizacao: "2025-01-15T10:00:00Z",
    ultimoLogin: "2025-01-15T09:00:00Z",
    tentativasLogin: 0,
    bloqueado: false
  },
  {
    id: 2,
    nome: "João Silva",
    email: "joao@boxpro.com",
    telefone: "(11) 98888-8888",
    cpf: "987.654.321-00",
    tipoFuncionario: 'FUNCIONARIO',
    ativo: true,
    dataCriacao: "2025-01-10T15:30:00Z",
    dataAtualizacao: "2025-01-10T15:30:00Z",
    ultimoLogin: "2025-01-14T14:00:00Z",
    tentativasLogin: 0,
    bloqueado: false
  },
  {
    id: 3,
    nome: "Maria Santos",
    email: "maria@boxpro.com",
    telefone: "(11) 97777-7777",
    cpf: "456.789.123-00",
    tipoFuncionario: 'FUNCIONARIO',
    ativo: false,
    dataCriacao: "2025-01-05T08:00:00Z",
    dataAtualizacao: "2025-01-12T16:00:00Z",
    ultimoLogin: "2025-01-12T15:30:00Z",
    tentativasLogin: 0,
    bloqueado: false
  }
];

const mockStats: TypeFuncionarioStats = {
  total: 3,
  ativos: 2,
  disponiveis: 2,
  admins: 1,
  funcionarios: 2,
  inativos: 1
};

// Componente do Formulário
const FuncionarioForm: React.FC<{
  onSubmit: (funcionario: TypeFuncionarioForm) => void;
  initialData?: TypeFuncionario | null;
}> = ({ onSubmit, initialData }) => {
  const [formData, setFormData] = useState<TypeFuncionarioForm>({
    nome: initialData?.nome || '',
    email: initialData?.email || '',
    senha: '',
    telefone: initialData?.telefone || '',
    cpf: initialData?.cpf || '',
    tipoFuncionario: initialData?.tipoFuncionario || 'FUNCIONARIO'
  });

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    onSubmit(formData);
  };

  return (
    <form onSubmit={handleSubmit} className="space-y-4">
      <div>
        <label className="block text-sm font-medium mb-1">Nome *</label>
        <input
          type="text"
          required
          className="w-full p-2 border rounded-md"
          value={formData.nome}
          onChange={(e) => setFormData({...formData, nome: e.target.value})}
          placeholder="Nome completo do funcionário"
        />
      </div>

      <div>
        <label className="block text-sm font-medium mb-1">Email *</label>
        <input
          type="email"
          required
          className="w-full p-2 border rounded-md"
          value={formData.email}
          onChange={(e) => setFormData({...formData, email: e.target.value})}
          placeholder="email@exemplo.com"
        />
      </div>

      <div>
        <label className="block text-sm font-medium mb-1">
          {initialData ? 'Nova Senha (deixe vazio para manter atual)' : 'Senha *'}
        </label>
        <input
          type="password"
          required={!initialData}
          className="w-full p-2 border rounded-md"
          value={formData.senha}
          onChange={(e) => setFormData({...formData, senha: e.target.value})}
          placeholder="Mínimo 6 caracteres"
          minLength={6}
        />
      </div>

      <div>
        <label className="block text-sm font-medium mb-1">Telefone</label>
        <input
          type="tel"
          className="w-full p-2 border rounded-md"
          value={formData.telefone}
          onChange={(e) => setFormData({...formData, telefone: e.target.value})}
          placeholder="(11) 99999-9999"
        />
      </div>

      <div>
        <label className="block text-sm font-medium mb-1">CPF</label>
        <input
          type="text"
          className="w-full p-2 border rounded-md"
          value={formData.cpf}
          onChange={(e) => setFormData({...formData, cpf: e.target.value})}
          placeholder="000.000.000-00"
          maxLength={14}
        />
      </div>

      <div>
        <label className="block text-sm font-medium mb-1">Tipo de Funcionário *</label>
        <select
          required
          className="w-full p-2 border rounded-md"
          value={formData.tipoFuncionario}
          onChange={(e) => setFormData({...formData, tipoFuncionario: e.target.value as 'ADMIN' | 'FUNCIONARIO'})}
        >
          <option value="FUNCIONARIO">Funcionário</option>
          <option value="ADMIN">Administrador</option>
        </select>
      </div>

      <Button type="submit" className="w-full">
        {initialData ? 'Atualizar Funcionário' : 'Criar Funcionário'}
      </Button>
    </form>
  );
};

// Componente Principal
const Funcionarios = () => {
  const navigate = useNavigate();
  
  const [editingFuncionario, setEditingFuncionario] = useState<TypeFuncionario | null>(null);
  const [funcionarios, setFuncionarios] = useState<TypeFuncionario[]>([]);
  const [totalFuncionarios, setTotalFuncionarios] = useState<TypeFuncionario[]>([]);
  const [stats, setStats] = useState<TypeFuncionarioStats | null>(null);
  const [loading, setLoading] = useState<boolean>(true);
  const [erro, setErro] = useState<string | null>(null);
  const [dialogOpen, setDialogOpen] = useState(false);
  const [usingMockData, setUsingMockData] = useState(false);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(1);

  // Função para testar conexão com o backend
  const testarConexao = async () => {
    try {
      const response = await fetch('http://localhost:8080/api/funcionarios/status');
      return response.ok;
    } catch (error) {
      return false;
    }
  };

  // Buscar funcionários paginados
  useEffect(() => {
    const buscarFuncionarios = async () => {
      try {
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
          // Simular paginação com dados mock
          const startIndex = page * 5;
          const endIndex = startIndex + 5;
          const paginatedMock = mockFuncionarios.slice(startIndex, endIndex);
          setFuncionarios(paginatedMock);
          setTotalPages(Math.ceil(mockFuncionarios.length / 5));
          setUsingMockData(true);
          setErro("⚠️ Usando dados de demonstração - Backend não conectado");
          return;
        }

        // Se backend estiver online, busca os dados reais
        const token = localStorage.getItem('boxpro_token');
        const headers: HeadersInit = {
          'Content-Type': 'application/json'
        };
        
        if (token) {
          headers['Authorization'] = `Bearer ${token}`;
        }

        const response = await fetch(`http://localhost:8080/api/funcionarios?page=${page}&size=5`, { headers });
        
        if (!response.ok) {
          throw new Error(`Erro HTTP: ${response.status}`);
        }
        
        const data = await response.json();
        setFuncionarios(data.content);
        setTotalPages(data.totalPages);
        setUsingMockData(false);
        
      } catch (error) {
        console.error("Erro ao carregar funcionários:", error);
        // Em caso de erro, usa dados mock paginados
        const startIndex = page * 5;
        const endIndex = startIndex + 5;
        const paginatedMock = mockFuncionarios.slice(startIndex, endIndex);
        setFuncionarios(paginatedMock);
        setTotalPages(Math.ceil(mockFuncionarios.length / 5));
        setUsingMockData(true);
        setErro("⚠️ Erro de conexão - Usando dados de demonstração");
      } finally {
        setLoading(false);
      }
    };

    buscarFuncionarios();
  }, [page, navigate]);

  // Buscar todos os funcionários para estatísticas
  const buscarTotalFuncionarios = async () => {
    try {
      const backendOnline = await testarConexao();
      
      if (!backendOnline) {
        setTotalFuncionarios(mockFuncionarios);
        setStats(mockStats);
        return;
      }

      const token = localStorage.getItem('boxpro_token');
      const headers: HeadersInit = {
        'Content-Type': 'application/json'
      };
      
      if (token) {
        headers['Authorization'] = `Bearer ${token}`;
      }

      const [funcionariosResponse, statsResponse] = await Promise.all([
        fetch('http://localhost:8080/api/funcionarios/todos', { headers }),
        fetch('http://localhost:8080/api/funcionarios/stats', { headers })
      ]);
      
      if (funcionariosResponse.ok && statsResponse.ok) {
        const funcionariosData = await funcionariosResponse.json();
        const statsData = await statsResponse.json();
        
        setTotalFuncionarios(funcionariosData);
        setStats(statsData);
      } else {
        setTotalFuncionarios(mockFuncionarios);
        setStats(mockStats);
      }
    } catch (error) {
      console.error("Erro ao carregar total de funcionários:", error);
      setTotalFuncionarios(mockFuncionarios);
      setStats(mockStats);
    }
  };

  // Buscar dados iniciais
  useEffect(() => {
    buscarTotalFuncionarios();
  }, []);

  const handleNovoFuncionario = async (novoFuncionario: TypeFuncionarioForm) => {
    try {
      if (usingMockData) {
        // Modo mock
        const novoId = Math.max(...totalFuncionarios.map(f => f.id)) + 1;
        const funcionario: TypeFuncionario = {
          ...novoFuncionario,
          id: novoId,
          ativo: true,
          dataCriacao: new Date().toISOString(),
          dataAtualizacao: new Date().toISOString(),
          tentativasLogin: 0,
          bloqueado: false
        };
        
        // Atualizar dados totais
        const novoTotal = [...totalFuncionarios, funcionario];
        setTotalFuncionarios(novoTotal);
        
        // Atualizar página atual se houver espaço
        if (funcionarios.length < 5) {
          setFuncionarios(prev => [...prev, funcionario]);
        } else {
          // Simular recarregamento da página
          const startIndex = page * 5;
          const endIndex = startIndex + 5;
          setFuncionarios(novoTotal.slice(startIndex, endIndex));
          setTotalPages(Math.ceil(novoTotal.length / 5));
        }
        
        // Atualizar stats
        if (stats) {
          const tipoKey = novoFuncionario.tipoFuncionario.toLowerCase() + 's';
          setStats({
            ...stats,
            total: stats.total + 1,
            ativos: stats.ativos + 1,
            [tipoKey]: (stats[tipoKey as keyof TypeFuncionarioStats] as number) + 1
          });
        }
        
        setDialogOpen(false);
        return;
      }

      // Modo real - chamada à API
      const token = localStorage.getItem('boxpro_token');
      const response = await fetch('http://localhost:8080/api/funcionarios', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          ...(token && { 'Authorization': `Bearer ${token}` })
        },
        body: JSON.stringify(novoFuncionario),
      });
      
      if (response.ok) {
        const responseData = await response.json();
        const novoFuncionarioData = responseData.funcionario || responseData;
        
        // Atualizar página atual se houver espaço
        if (funcionarios.length < 5) {
          setFuncionarios(prev => [...prev, novoFuncionarioData]);
        } else {
          // Recarregar página atual
          const funcionariosResponse = await fetch(`http://localhost:8080/api/funcionarios?page=${page}&size=5`, {
            headers: {
              'Content-Type': 'application/json',
              ...(token && { 'Authorization': `Bearer ${token}` })
            }
          });
          const data = await funcionariosResponse.json();
          setFuncionarios(data.content);
          setTotalPages(data.totalPages);
        }
        
        buscarTotalFuncionarios(); // Atualizar estatísticas
        setDialogOpen(false);
      } else {
        const errorData = await response.json();
        throw new Error(errorData.error || 'Erro ao criar funcionário');
      }
    } catch (error: any) {
      console.error("Erro ao criar funcionário:", error);
      setErro(`Erro ao criar funcionário: ${error.message}`);
    }
  };

  const handleEditarFuncionario = (funcionario: TypeFuncionario) => {
    setEditingFuncionario(funcionario);
    setDialogOpen(true);
  };

  const handleAtualizarFuncionario = async (funcionarioAtualizado: TypeFuncionarioForm) => {
    if (!editingFuncionario) return;
    
    try {
      if (usingMockData) {
        // Modo mock
        const funcionarioEditado = {
          ...editingFuncionario,
          ...funcionarioAtualizado,
          dataAtualizacao: new Date().toISOString(),
          ativo: true
        };
        
        // Atualizar dados totais
        const novoTotal = totalFuncionarios.map(func => 
          func.id === editingFuncionario.id ? funcionarioEditado : func
        );
        setTotalFuncionarios(novoTotal);
        
        // Atualizar página atual
        setFuncionarios(prev => 
          prev.map(func => func.id === editingFuncionario.id ? funcionarioEditado : func)
        );
        
        setDialogOpen(false);
        setEditingFuncionario(null);
        return;
      }

      // Preparar dados para envio - remover campos vazios
      const dadosParaEnviar: any = {
        nome: funcionarioAtualizado.nome,
        email: funcionarioAtualizado.email,
        tipoFuncionario: funcionarioAtualizado.tipoFuncionario
      };

      if (funcionarioAtualizado.telefone && funcionarioAtualizado.telefone.trim() !== '') {
        dadosParaEnviar.telefone = funcionarioAtualizado.telefone;
      }
      
      if (funcionarioAtualizado.cpf && funcionarioAtualizado.cpf.trim() !== '') {
        dadosParaEnviar.cpf = funcionarioAtualizado.cpf;
      }
      
      if (funcionarioAtualizado.senha && funcionarioAtualizado.senha.trim() !== '') {
        dadosParaEnviar.senha = funcionarioAtualizado.senha;
      }

      const token = localStorage.getItem('boxpro_token');
      const response = await fetch(`http://localhost:8080/api/funcionarios/${editingFuncionario.id}`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
          ...(token && { 'Authorization': `Bearer ${token}` })
        },
        body: JSON.stringify(dadosParaEnviar),
      });
      
      if (response.ok) {
        const responseData = await response.json();
        const funcionarioAtualizadoData = responseData.funcionario || responseData;
        
        // Atualizar página atual
        setFuncionarios(prev => 
          prev.map(func => func.id === editingFuncionario.id ? funcionarioAtualizadoData : func)
        );
        
        buscarTotalFuncionarios(); // Atualizar estatísticas
        setDialogOpen(false);
        setEditingFuncionario(null);
      } else {
        const errorData = await response.json();
        throw new Error(errorData.error || 'Erro ao atualizar funcionário');
      }
    } catch (error: any) {
      console.error("Erro ao atualizar funcionário:", error);
      setErro(`Erro ao atualizar funcionário: ${error.message}`);
    }
  };

  const handleDeletarFuncionario = async (id: number) => {
    try {
      if (usingMockData) {
        // Modo mock
        const novoTotal = totalFuncionarios.map(func => 
          func.id === id ? { ...func, ativo: false } : func
        );
        setTotalFuncionarios(novoTotal);
        
        // Atualizar página atual
        setFuncionarios(prev => prev.map(func => 
          func.id === id ? { ...func, ativo: false } : func
        ));
        
        if (stats) {
          setStats({
            ...stats,
            ativos: stats.ativos - 1,
            inativos: stats.inativos + 1
          });
        }
        return;
      }

      const token = localStorage.getItem('boxpro_token');
      const response = await fetch(`http://localhost:8080/api/funcionarios/${id}`, {
        method: 'DELETE',
        headers: {
          'Content-Type': 'application/json',
          ...(token && { 'Authorization': `Bearer ${token}` })
        }
      });
      
      if (response.ok) {
        // Atualizar página atual
        setFuncionarios(prev => prev.map(func => 
          func.id === id ? { ...func, ativo: false } : func
        ));
        
        buscarTotalFuncionarios(); // Atualizar estatísticas
      } else {
        const errorData = await response.json();
        throw new Error(errorData.error || 'Erro ao desativar funcionário');
      }
    } catch (error: any) {
      console.error("Erro ao desativar funcionário:", error);
      setErro(`Erro ao desativar funcionário: ${error.message}`);
    }
  };

  const handleBloquearFuncionario = async (id: number, bloquear: boolean) => {
    try {
      if (usingMockData) {
        // Modo mock
        const novoTotal = totalFuncionarios.map(func => 
          func.id === id ? { ...func, bloqueado: bloquear } : func
        );
        setTotalFuncionarios(novoTotal);
        
        setFuncionarios(prev => 
          prev.map(func => 
            func.id === id ? { ...func, bloqueado: bloquear } : func
          )
        );
        return;
      }

      const token = localStorage.getItem('boxpro_token');
      const endpoint = bloquear ? 'bloquear' : 'desbloquear';
      const response = await fetch(`http://localhost:8080/api/funcionarios/${id}/${endpoint}`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
          ...(token && { 'Authorization': `Bearer ${token}` })
        }
      });
      
      if (response.ok) {
        setFuncionarios(prev => 
          prev.map(func => 
            func.id === id ? { ...func, bloqueado: bloquear } : func
          )
        );
      } else {
        const errorData = await response.json();
        throw new Error(errorData.error || `Erro ao ${bloquear ? 'bloquear' : 'desbloquear'} funcionário`);
      }
    } catch (error: any) {
      console.error(`Erro ao ${bloquear ? 'bloquear' : 'desbloquear'} funcionário:`, error);
      setErro(`Erro ao ${bloquear ? 'bloquear' : 'desbloquear'} funcionário: ${error.message}`);
    }
  };

  const handleReativarFuncionario = async (id: number) => {
    try {
      const funcionario = totalFuncionarios.find(f => f.id === id);
      if (!funcionario) return;

      if (usingMockData) {
        // Modo mock
        const novoTotal = totalFuncionarios.map(func => 
          func.id === id ? { ...func, ativo: true } : func
        );
        setTotalFuncionarios(novoTotal);
        
        setFuncionarios(prev => 
          prev.map(func => func.id === id ? { ...func, ativo: true } : func)
        );
        
        if (stats) {
          setStats({
            ...stats,
            ativos: stats.ativos + 1,
            inativos: stats.inativos - 1
          });
        }
        return;
      }

      const dadosParaReativar: any = {
        nome: funcionario.nome,
        email: funcionario.email,
        tipoFuncionario: funcionario.tipoFuncionario
      };

      if (funcionario.telefone) {
        dadosParaReativar.telefone = funcionario.telefone;
      }
      
      if (funcionario.cpf) {
        dadosParaReativar.cpf = funcionario.cpf;
      }

      const token = localStorage.getItem('boxpro_token');
      const response = await fetch(`http://localhost:8080/api/funcionarios/${id}`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
          ...(token && { 'Authorization': `Bearer ${token}` })
        },
        body: JSON.stringify(dadosParaReativar),
      });
      
      if (response.ok) {
        const responseData = await response.json();
        const funcionarioReativado = responseData.funcionario || responseData;
        
        setFuncionarios(prev => 
          prev.map(func => func.id === id ? { ...funcionarioReativado, ativo: true } : func)
        );
        
        buscarTotalFuncionarios(); // Atualizar estatísticas
      } else {
        const errorData = await response.json();
        throw new Error(errorData.error || 'Erro ao reativar funcionário');
      }
    } catch (error: any) {
      console.error("Erro ao reativar funcionário:", error);
      setErro(`Erro ao reativar funcionário: ${error.message}`);
    }
  };

  const handleVoltarAdmin = () => {
    navigate('/admin');
  };

  const formatarData = (data: string) => {
    return new Date(data).toLocaleDateString('pt-BR');
  };

  const getTipoLabel = (tipo: string) => {
    return tipo === 'ADMIN' ? 'Administrador' : 'Funcionário';
  };

  const getTipoColor = (tipo: string) => {
    return tipo === 'ADMIN' ? 'text-purple-600' : 'text-blue-600';
  };

  const novosDoMes = totalFuncionarios.filter(funcionario => 
    funcionario.dataCriacao && isSameMonth(parseISO(funcionario.dataCriacao), new Date())
  ).length;

  if (loading) {
    return (
      <div className="flex justify-center items-center min-h-screen">
        <div className="text-center">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary mx-auto mb-4"></div>
          <p>Carregando funcionários...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-background p-6">
      <div className="max-w-7xl mx-auto">
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
              onClick={handleVoltarAdmin}
              className="mr-2"
            >
              <ArrowLeft className="w-5 h-5" />
            </Button>
            <div className="w-12 h-12 bg-primary/10 rounded-lg flex items-center justify-center">
              <Users className="w-6 h-6 text-primary" />
            </div>
            <div>
              <h1 className="text-3xl font-bold text-foreground">Funcionários</h1>
              <p className="text-muted-foreground">Gerencie os funcionários do sistema</p>
            </div>
          </div>
          
          <Dialog open={dialogOpen} onOpenChange={(open) => {
              setDialogOpen(open);
              if (!open) setEditingFuncionario(null);
            }}>
            <DialogTrigger asChild>
              <Button className="flex items-center gap-2">
                <Plus className="w-4 h-4" />
                Novo Funcionário
              </Button>
            </DialogTrigger>
            <DialogContent className="max-w-md max-h-[90vh] overflow-y-auto">
              <DialogHeader>
                <DialogTitle>
                  {editingFuncionario ? 'Editar Funcionário' : 'Adicionar Novo Funcionário'}
                </DialogTitle>
              </DialogHeader>
              <FuncionarioForm 
                onSubmit={editingFuncionario ? handleAtualizarFuncionario : handleNovoFuncionario} 
                initialData={editingFuncionario}
              />
            </DialogContent>
          </Dialog>
        </div>

        {/* Estatísticas */}
        {stats && (
          <div className="grid grid-cols-1 md:grid-cols-4 gap-6 mb-8">
            <Card>
              <CardContent className="p-6">
                <div className="flex items-center justify-between">
                  <div>
                    <p className="text-sm font-medium text-muted-foreground">Total de Funcionários</p>
                    <p className="text-2xl font-bold text-foreground">{stats.total}</p>
                  </div>
                  <div className="w-12 h-12 bg-blue-100 rounded-lg flex items-center justify-center">
                    <Users className="w-6 h-6 text-blue-600" />
                  </div>
                </div>
              </CardContent>
            </Card>

            <Card>
              <CardContent className="p-6">
                <div className="flex items-center justify-between">
                  <div>
                    <p className="text-sm font-medium text-muted-foreground">Novos este mês</p>
                    <p className="text-2xl font-bold text-foreground">{novosDoMes}</p>
                  </div>
                  <div className="w-12 h-12 bg-green-100 rounded-lg flex items-center justify-center">
                    <Calendar className="w-6 h-6 text-green-600" />
                  </div>
                </div>
              </CardContent>
            </Card>

            <Card>
              <CardContent className="p-6">
                <div className="flex items-center justify-between">
                  <div>
                    <p className="text-sm font-medium text-muted-foreground">Ativos</p>
                    <p className="text-2xl font-bold text-foreground">{stats.ativos}</p>
                  </div>
                  <div className="w-12 h-12 bg-purple-100 rounded-lg flex items-center justify-center">
                    <UserCheck className="w-6 h-6 text-purple-600" />
                  </div>
                </div>
              </CardContent>
            </Card>

            <Card>
              <CardContent className="p-6">
                <div className="flex items-center justify-between">
                  <div>
                    <p className="text-sm font-medium text-muted-foreground">Administradores</p>
                    <p className="text-2xl font-bold text-foreground">{stats.admins}</p>
                  </div>
                  <div className="w-12 h-12 bg-orange-100 rounded-lg flex items-center justify-center">
                    <Shield className="w-6 h-6 text-orange-600" />
                  </div>
                </div>
              </CardContent>
            </Card>
          </div>
        )}

        {/* Tabela de Funcionários */}
        <Card>
          <CardHeader>
            <CardTitle>Lista de Funcionários ({totalFuncionarios.length} total)</CardTitle>
          </CardHeader>
          <CardContent>
            {funcionarios.length === 0 ? (
              <div className="text-center py-8">
                <Users className="w-12 h-12 mx-auto text-muted-foreground mb-4" />
                <p className="text-muted-foreground">Nenhum funcionário cadastrado ainda.</p>
                <p className="text-sm text-muted-foreground">Clique em "Novo Funcionário" para começar.</p>
              </div>
            ) : (
              <div className="overflow-x-auto">
                <div className="min-w-[1200px]">
                  <div className="grid grid-cols-8 gap-4 p-4 bg-muted/50 rounded-t-lg font-medium text-sm">
                    <div>Nome</div>
                    <div>Email</div>
                    <div>Telefone</div>
                    <div>Tipo</div>
                    <div>Status</div>
                    <div>Último Login</div>
                    <div>Data Criação</div>
                    <div>Ações</div>
                  </div>
                  {funcionarios.map((funcionario) => (
                    <div key={funcionario.id} className="grid grid-cols-8 gap-4 p-4 border-b hover:bg-muted/30 transition-colors">
                      <div className="text-sm font-medium">{funcionario.nome}</div>
                      <div className="text-sm break-words mr-2">{funcionario.email}</div>
                      <div className="text-sm break-words">
                        {funcionario.telefone || 'Não informado'}
                      </div>
                      <div className={`text-sm font-medium ${getTipoColor(funcionario.tipoFuncionario)}`}>
                        {getTipoLabel(funcionario.tipoFuncionario)}
                      </div>
                      <div className="flex flex-col gap-1">
                        <span className={`px-2 py-1 rounded-full text-xs font-medium w-fit ${
                          funcionario.ativo 
                            ? 'bg-green-100 text-green-800' 
                            : 'bg-red-100 text-red-800'
                        }`}>
                          {funcionario.ativo ? 'Ativo' : 'Inativo'}
                        </span>
                        {funcionario.bloqueado && (
                          <span className="px-2 py-1 rounded-full text-xs font-medium bg-yellow-100 text-yellow-800 w-fit">
                            Bloqueado
                          </span>
                        )}
                      </div>
                      <div className="text-sm">
                        {funcionario.ultimoLogin ? formatarData(funcionario.ultimoLogin) : 'Nunca'}
                      </div>
                      <div className="text-sm">{formatarData(funcionario.dataCriacao)}</div>
                      <div>
                        <div className="flex items-center gap-2">
                          <Button
                            variant="ghost"
                            size="icon"
                            onClick={() => handleEditarFuncionario(funcionario)}
                            className="h-8 w-8"
                            title="Editar funcionário"
                          >
                            <Edit className="w-4 h-4" />
                          </Button>

                          {funcionario.ativo ? (
                            <>
                              <Button
                                variant="ghost"
                                size="icon"
                                onClick={() => handleBloquearFuncionario(funcionario.id, !funcionario.bloqueado)}
                                className={`h-8 w-8 ${funcionario.bloqueado ? 'text-green-600' : 'text-yellow-600'}`}
                                title={funcionario.bloqueado ? 'Desbloquear' : 'Bloquear'}
                              >
                                {funcionario.bloqueado ? <Unlock className="w-4 h-4" /> : <Lock className="w-4 h-4" />}
                              </Button>
                              
                              <AlertDialog>
                                <AlertDialogTrigger asChild>
                                  <Button
                                    variant="ghost"
                                    size="icon"
                                    className="h-8 w-8 text-destructive hover:text-destructive"
                                    title="Desativar funcionário"
                                  >
                                    <Trash2 className="w-4 h-4" />
                                  </Button>
                                </AlertDialogTrigger>
                                <AlertDialogContent>
                                  <AlertDialogHeader>
                                    <AlertDialogTitle>Confirmar desativação</AlertDialogTitle>
                                    <AlertDialogDescription>
                                      Tem certeza que deseja desativar o funcionário "{funcionario.nome}"? 
                                      Ele não poderá mais fazer login no sistema.
                                    </AlertDialogDescription>
                                  </AlertDialogHeader>
                                  <AlertDialogFooter>
                                    <AlertDialogCancel>Cancelar</AlertDialogCancel>
                                    <AlertDialogAction
                                      onClick={() => handleDeletarFuncionario(funcionario.id)}
                                      className="bg-destructive text-destructive-foreground hover:bg-destructive/90"
                                    >
                                      Desativar
                                    </AlertDialogAction>
                                  </AlertDialogFooter>
                                </AlertDialogContent>
                              </AlertDialog>
                            </>
                          ) : (
                            <Button
                              variant="ghost"
                              size="icon"
                              onClick={() => handleReativarFuncionario(funcionario.id)}
                              className="h-8 w-8 text-green-600 hover:text-green-700"
                              title="Reativar funcionário"
                            >
                              <RotateCcw className="w-4 h-4" />
                            </Button>
                          )}
                        </div>
                      </div>
                    </div>
                  ))}
                </div>
              </div>
            )}
          </CardContent>
        </Card>

        {/* Paginação */}
        <div className="flex justify-center items-center gap-4 mt-4">
          <Button 
            disabled={page === 0} 
            onClick={() => setPage(page - 1)}
          >
            Anterior
          </Button>
          <span>Página {page + 1} de {totalPages}</span>
          <Button 
            disabled={page + 1 >= totalPages} 
            onClick={() => setPage(page + 1)}
          >
            Próxima
          </Button>
        </div>

        {/* Mensagem de erro */}
        {erro && !usingMockData && (
          <div className="mt-4 p-4 bg-red-50 border border-red-200 rounded-md">
            <p className="text-red-800">{erro}</p>
            <Button 
              variant="outline" 
              size="sm" 
              className="mt-2"
              onClick={() => setErro(null)}
            >
              Fechar
            </Button>
          </div>
        )}
      </div>
    </div>
  );
};

export default Funcionarios;