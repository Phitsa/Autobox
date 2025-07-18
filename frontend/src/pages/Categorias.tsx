import React, { useEffect, useState } from 'react';
import { Button } from '@/components/ui/button';
import { TypeCategoria } from "../types/TypeCategoria";
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogTrigger } from '@/components/ui/dialog';
import { Plus, Settings, ArrowLeft, Edit, Trash2, Calendar, FolderOpen, AlertCircle } from 'lucide-react';
import CategoriaForm from '@/components/CategoriaFormProps';
import { useNavigate } from 'react-router-dom';

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

// Mock data como fallback
const mockCategorias: TypeCategoria[] = [
  {
    id: 1,
    nome: "Lavagem Completa",
    descricao: "Serviços de lavagem externa e interna do veículo",
    createdAt: "2025-01-15T10:00:00Z",
    updatedAt: "2025-01-15T10:00:00Z"
  },
  {
    id: 2,
    nome: "Enceramento",
    descricao: "Aplicação de cera protetora na pintura",
    createdAt: "2025-01-10T15:30:00Z",
    updatedAt: "2025-01-10T15:30:00Z"
  },
  {
    id: 3,
    nome: "Detalhamento",
    descricao: "Limpeza detalhada e cuidados especiais",
    createdAt: "2025-06-01T08:00:00Z",
    updatedAt: "2025-06-01T08:00:00Z"
  }
];

const Categorias = () => {
  const [editingCategoria, setEditingCategoria] = useState<TypeCategoria | null>(null);
  const [categorias, setCategorias] = useState<TypeCategoria[]>([]);
  const [totalCategorias, setTotalCategorias] = useState<TypeCategoria[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [erro, setErro] = useState<string | null>(null);
  const [dialogOpen, setDialogOpen] = useState(false);
  const [usingMockData, setUsingMockData] = useState(false);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(1);
  const navigate = useNavigate();

  // Função para testar conexão com o backend
  const testarConexao = async () => {
    try {
      const response = await fetch('http://localhost:8080/api/categorias/teste');
      return response.ok;
    } catch (error) {
      return false;
    }
  };

  // Buscar categorias paginadas
  useEffect(() => {
    const buscarCategorias = async () => {
      try {
        // Primeiro testa se o backend está rodando
        const backendOnline = await testarConexao();
        
        if (!backendOnline) {
          console.warn("Backend não está disponível, usando dados mock");
          // Simular paginação com dados mock
          const startIndex = page * 5;
          const endIndex = startIndex + 5;
          const paginatedMock = mockCategorias.slice(startIndex, endIndex);
          setCategorias(paginatedMock);
          setTotalPages(Math.ceil(mockCategorias.length / 5));
          setUsingMockData(true);
          setErro("⚠️ Usando dados de demonstração - Backend não conectado");
          return;
        }

        const response = await fetch(`http://localhost:8080/api/categorias?page=${page}&size=5`);
        
        if (!response.ok) {
          throw new Error(`Erro HTTP: ${response.status}`);
        }
        
        const data = await response.json();
        setCategorias(data.content);
        setTotalPages(data.totalPages);
        setUsingMockData(false);
        
      } catch (error) {
        console.error("Erro ao carregar categorias:", error);
        // Em caso de erro, usa dados mock paginados
        const startIndex = page * 5;
        const endIndex = startIndex + 5;
        const paginatedMock = mockCategorias.slice(startIndex, endIndex);
        setCategorias(paginatedMock);
        setTotalPages(Math.ceil(mockCategorias.length / 5));
        setUsingMockData(true);
        setErro("⚠️ Erro de conexão - Usando dados de demonstração");
      } finally {
        setLoading(false);
      }
    };

    buscarCategorias();
  }, [page]);

  // Buscar todas as categorias para estatísticas
  const buscarTotalCategorias = async () => {
    try {
      const backendOnline = await testarConexao();
      
      if (!backendOnline) {
        setTotalCategorias(mockCategorias);
        return;
      }

      const response = await fetch('http://localhost:8080/api/categorias/todas');
      
      if (response.ok) {
        const data = await response.json();
        setTotalCategorias(data);
      } else {
        setTotalCategorias(mockCategorias);
      }
    } catch (error) {
      console.error("Erro ao carregar total de categorias:", error);
      setTotalCategorias(mockCategorias);
    }
  };

  // Buscar dados iniciais
  useEffect(() => {
    buscarTotalCategorias();
  }, []);

  const handleNovaCategoria = async (novaCategoria: Omit<TypeCategoria, 'id' | 'createdAt' | 'updatedAt'>) => {
    try {
      if (usingMockData) {
        // Modo mock
        const novaId = Math.max(...totalCategorias.map(c => c.id)) + 1;
        const categoria: TypeCategoria = {
          ...novaCategoria,
          id: novaId,
          createdAt: new Date().toISOString(),
          updatedAt: new Date().toISOString()
        };
        
        // Atualizar dados totais
        const novoTotal = [...totalCategorias, categoria];
        setTotalCategorias(novoTotal);
        
        // Atualizar página atual se houver espaço
        if (categorias.length < 5) {
          setCategorias(prev => [...prev, categoria]);
        } else {
          // Simular recarregamento da página
          const startIndex = page * 5;
          const endIndex = startIndex + 5;
          setCategorias(novoTotal.slice(startIndex, endIndex));
          setTotalPages(Math.ceil(novoTotal.length / 5));
        }
        
        setDialogOpen(false);
        return;
      }

      // Modo real - chamada à API
      const response = await fetch('http://localhost:8080/api/categorias', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(novaCategoria),
      });
      
      if (response.ok) {
        const novaCategoriaData = await response.json();
        
        // Atualizar página atual se houver espaço
        if (categorias.length < 5) {
          setCategorias(prev => [...prev, novaCategoriaData]);
        } else {
          // Recarregar página atual
          const categoriasResponse = await fetch(`http://localhost:8080/api/categorias?page=${page}&size=5`);
          const data = await categoriasResponse.json();
          setCategorias(data.content);
          setTotalPages(data.totalPages);
        }
        
        buscarTotalCategorias(); // Atualizar estatísticas
        setDialogOpen(false);
      } else {
        const errorText = await response.text();
        throw new Error(`Erro ao criar categoria: ${errorText}`);
      }
    } catch (error) {
      console.error("Erro ao criar categoria:", error);
      setErro("Erro ao criar categoria");
    }
  };

  const handleEditarCategoria = (categoria: TypeCategoria) => {
    setEditingCategoria(categoria);
    setDialogOpen(true);
  };

  const handleAtualizarCategoria = async (categoriaAtualizada: Omit<TypeCategoria, 'id' | 'createdAt' | 'updatedAt'>) => {
    if (!editingCategoria) return;
    
    try {
      if (usingMockData) {
        // Modo mock
        const categoriaEditada = {
          ...editingCategoria,
          ...categoriaAtualizada,
          updatedAt: new Date().toISOString()
        };
        
        // Atualizar dados totais
        const novoTotal = totalCategorias.map(cat => 
          cat.id === editingCategoria.id ? categoriaEditada : cat
        );
        setTotalCategorias(novoTotal);
        
        // Atualizar página atual
        setCategorias(prev => 
          prev.map(cat => cat.id === editingCategoria.id ? categoriaEditada : cat)
        );
        
        setDialogOpen(false);
        setEditingCategoria(null);
        return;
      }

      // Modo real - chamada à API
      const response = await fetch(`http://localhost:8080/api/categorias/${editingCategoria.id}`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(categoriaAtualizada),
      });
      
      if (response.ok) {
        const categoriaAtualizadaData = await response.json();
        
        // Atualizar página atual
        setCategorias(prev => 
          prev.map(cat => cat.id === editingCategoria.id ? categoriaAtualizadaData : cat)
        );
        
        buscarTotalCategorias(); // Atualizar estatísticas
        setDialogOpen(false);
        setEditingCategoria(null);
      } else {
        const errorText = await response.text();
        throw new Error(`Erro ao atualizar categoria: ${errorText}`);
      }
    } catch (error) {
      console.error("Erro ao atualizar categoria:", error);
      setErro("Erro ao atualizar categoria");
    }
  };

  const handleDeletarCategoria = async (id: number) => {
    try {
      if (usingMockData) {
        // Modo mock
        const novoTotal = totalCategorias.filter(categoria => categoria.id !== id);
        setTotalCategorias(novoTotal);
        
        // Remover da página atual
        setCategorias(prev => prev.filter(categoria => categoria.id !== id));
        
        // Se a página ficou vazia e não é a primeira, voltar uma página
        if (categorias.length === 1 && page > 0) {
          setPage(page - 1);
        } else {
          // Reajustar paginação
          const startIndex = page * 5;
          const endIndex = startIndex + 5;
          setCategorias(novoTotal.slice(startIndex, endIndex));
          setTotalPages(Math.ceil(novoTotal.length / 5));
        }
        return;
      }

      // Modo real - chamada à API
      const response = await fetch(`http://localhost:8080/api/categorias/${id}`, {
        method: 'DELETE',
      });
      
      if (response.ok) {
        // Remover da página atual
        setCategorias(prev => prev.filter(categoria => categoria.id !== id));
        
        // Se a página ficou vazia e não é a primeira, voltar uma página
        if (categorias.length === 1 && page > 0) {
          setPage(page - 1);
        } else {
          // Recarregar página atual
          const categoriasResponse = await fetch(`http://localhost:8080/api/categorias?page=${page}&size=5`);
          const data = await categoriasResponse.json();
          setCategorias(data.content);
          setTotalPages(data.totalPages);
        }
        
        buscarTotalCategorias(); // Atualizar estatísticas
      } else {
        const errorText = await response.text();
        throw new Error(`Erro ao deletar categoria: ${errorText}`);
      }
    } catch (error) {
      console.error("Erro ao deletar categoria:", error);
      setErro("Erro ao deletar categoria");
    }
  };

  const handleVoltarAdmin = () => {
    navigate('/servicos');
  };

  const formatarData = (data: string) => {
    return new Date(data).toLocaleDateString('pt-BR');
  };

  // Função helper para verificar se é o mesmo mês
  const isSameMonth = (date1: Date, date2: Date) => {
    return date1.getMonth() === date2.getMonth() && date1.getFullYear() === date2.getFullYear();
  };

  const novasDoMes = totalCategorias.filter(categoria => 
    isSameMonth(new Date(categoria.createdAt), new Date())
  ).length;

  if (loading) {
    return (
      <div className="flex justify-center items-center min-h-screen">
        <div className="text-center">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary mx-auto mb-4"></div>
          <p>Carregando categorias...</p>
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
              <FolderOpen className="w-6 h-6 text-primary" />
            </div>
            <div>
              <h1 className="text-3xl font-bold text-foreground">Categorias de Serviços</h1>
              <p className="text-muted-foreground">Organize e gerencie as categorias dos seus serviços</p>
            </div>
          </div>
          
          <Dialog open={dialogOpen} onOpenChange={(open) => {
              setDialogOpen(open);
              if (!open) setEditingCategoria(null);
            }}>
            <DialogTrigger asChild>
              <Button className="flex items-center gap-2">
                <Plus className="w-4 h-4" />
                Nova Categoria
              </Button>
            </DialogTrigger>
            <DialogContent className="max-w-md max-h-[90vh] overflow-y-auto">
              <DialogHeader>
                <DialogTitle>
                  {editingCategoria ? 'Editar Categoria' : 'Adicionar Nova Categoria'}
                </DialogTitle>
              </DialogHeader>
              <CategoriaForm 
                onSubmit={editingCategoria ? handleAtualizarCategoria : handleNovaCategoria} 
                initialData={editingCategoria}
              />
            </DialogContent>
          </Dialog>
        </div>

        {/* Estatísticas */}
        <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8">
          <Card>
            <CardContent className="p-6">
              <div className="flex items-center justify-between">
                <div>
                  <p className="text-sm font-medium text-muted-foreground">Total de Categorias</p>
                  <p className="text-2xl font-bold text-foreground">{totalCategorias.length}</p>
                </div>
                <div className="w-12 h-12 bg-blue-100 rounded-lg flex items-center justify-center">
                  <FolderOpen className="w-6 h-6 text-blue-600" />
                </div>
              </div>
            </CardContent>
          </Card>

          <Card>
            <CardContent className="p-6">
              <div className="flex items-center justify-between">
                <div>
                  <p className="text-sm font-medium text-muted-foreground">Novas este mês</p>
                  <p className="text-2xl font-bold text-foreground">{novasDoMes}</p>
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
                  <p className="text-sm font-medium text-muted-foreground">Ativas</p>
                  <p className="text-2xl font-bold text-foreground">{totalCategorias.length}</p>
                </div>
                <div className="w-12 h-12 bg-purple-100 rounded-lg flex items-center justify-center">
                  <Settings className="w-6 h-6 text-purple-600" />
                </div>
              </div>
            </CardContent>
          </Card>
        </div>

        {/* Tabela de Categorias */}
        <Card>
          <CardHeader>
            <CardTitle>Lista de Categorias ({totalCategorias.length} total)</CardTitle>
          </CardHeader>
          <CardContent>
            {categorias.length === 0 ? (
              <div className="text-center py-8">
                <FolderOpen className="w-12 h-12 mx-auto text-muted-foreground mb-4" />
                <p className="text-muted-foreground">Nenhuma categoria cadastrada ainda.</p>
                <p className="text-sm text-muted-foreground">Clique em "Nova Categoria" para começar.</p>
              </div>
            ) : (
              <div className="overflow-x-auto">
                <div className="w-full">
                  <div className="grid grid-cols-5 gap-4 p-4 bg-muted/50 rounded-t-lg font-medium text-sm">
                    <div>Nome</div>
                    <div>Descrição</div>
                    <div>Data Criação</div>
                    <div>Última Atualização</div>
                    <div>Ações</div>
                  </div>
                  {categorias.map((categoria) => (
                    <div key={categoria.id} className="grid grid-cols-5 gap-4 p-4 border-b hover:bg-muted/30 transition-colors">
                      <div className="font-medium">{categoria.nome}</div>
                      <div>
                        <div className="max-w-xs">
                          <p className="text-sm text-muted-foreground truncate">
                            {categoria.descricao || 'Sem descrição'}
                          </p>
                        </div>
                      </div>
                      <div>{formatarData(categoria.createdAt)}</div>
                      <div>{formatarData(categoria.updatedAt)}</div>
                      <div>
                        <div className="flex items-center gap-2">
                          <Button
                            variant="ghost"
                            size="icon"
                            onClick={() => handleEditarCategoria(categoria)}
                            className="h-8 w-8"
                            title="Editar categoria"
                          >
                            <Edit className="w-4 h-4" />
                          </Button>
                          
                          <AlertDialog>
                            <AlertDialogTrigger asChild>
                              <Button
                                variant="ghost"
                                size="icon"
                                className="h-8 w-8 text-destructive hover:text-destructive"
                                title="Excluir categoria"
                              >
                                <Trash2 className="w-4 h-4" />
                              </Button>
                            </AlertDialogTrigger>
                            <AlertDialogContent>
                              <AlertDialogHeader>
                                <AlertDialogTitle>Confirmar exclusão</AlertDialogTitle>
                                <AlertDialogDescription>
                                  Tem certeza que deseja excluir a categoria "{categoria.nome}"? 
                                  Esta ação não pode ser desfeita e pode afetar serviços vinculados.
                                </AlertDialogDescription>
                              </AlertDialogHeader>
                              <AlertDialogFooter>
                                <AlertDialogCancel>Cancelar</AlertDialogCancel>
                                <AlertDialogAction
                                  onClick={() => handleDeletarCategoria(categoria.id)}
                                  className="bg-destructive text-destructive-foreground hover:bg-destructive/90"
                                >
                                  Excluir
                                </AlertDialogAction>
                              </AlertDialogFooter>
                            </AlertDialogContent>
                          </AlertDialog>
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

export default Categorias;