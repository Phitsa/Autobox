import React, { useEffect, useState } from 'react';
import { Button } from '@/components/ui/button';
import axios from 'axios';
import { TypeServico, TypeCategoria } from "../types/TypeServico";
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogTrigger } from '@/components/ui/dialog';
import { useNavigate } from 'react-router-dom';
import { Plus, Car, ArrowLeft, Edit, Trash2, Calendar, DollarSign, Clock, Settings } from 'lucide-react';
import ServicoForm from '@/components/ServicoFormProps';
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

const Servicos = () => {
  const navigate = useNavigate();
  const [editingServico, setEditingServico] = useState<TypeServico | null>(null);
  const [servicos, setServicos] = useState<TypeServico[]>([]);
  const [totalServicos, setTotalServicos] = useState<TypeServico[]>([]);
  const [categorias, setCategorias] = useState<TypeCategoria[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [erro, setErro] = useState<string | null>(null);
  const [dialogOpen, setDialogOpen] = useState(false);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(1);

  // Buscar serviços paginados
  useEffect(() => {
    const buscarServicos = async () => {
      try {
        const response = await axios.get(`http://localhost:8080/api/servicos?page=${page}&size=5`);
        setServicos(response.data.content); // conteúdo da página
        setTotalPages(response.data.totalPages); // total de páginas
      } catch (error) {
        setErro("Erro ao carregar serviços");
        console.error(error);
      } finally {
        setLoading(false);
      }
    };

    buscarServicos();
  }, [page]);

  // Buscar todos os serviços para estatísticas
  const buscarTotalServicos = async () => {
    try {
      const response = await axios.get<TypeServico[]>("http://localhost:8080/api/servicos/todos");
      setTotalServicos(response.data);
    } catch (error) {
      console.error("Erro ao carregar total de serviços:", error);
    }
  };

  // Buscar categorias e dados iniciais
  useEffect(() => {
    const buscarDadosIniciais = async () => {
      try {
        setLoading(true);
        
        // Buscar categorias e total de serviços em paralelo
        const [categoriasResponse] = await Promise.all([
          axios.get<TypeCategoria[]>("http://localhost:8080/api/categorias")
        ]);
        
        setCategorias(categoriasResponse.data);
        await buscarTotalServicos();
      } catch (error) {
        console.error("Erro ao carregar dados:", error);
        setErro("Erro ao carregar dados do servidor");
      } finally {
        setLoading(false);
      }
    };

    buscarDadosIniciais();
  }, []);

  if (loading) return <div className="flex justify-center items-center min-h-screen"><p>Carregando...</p></div>;
  if (erro) return <div className="flex justify-center items-center min-h-screen"><p className="text-red-600">{erro}</p></div>;

  const handleNovoServico = async (novoServico: Omit<TypeServico, 'id' | 'createdAt' | 'updatedAt'>) => {
    try {
      const response = await axios.post<TypeServico>("http://localhost:8080/api/servicos", novoServico);
      
      // Atualizar a página atual se houver espaço, senão recarregar
      if (servicos.length < 5) {
        setServicos(prev => [...prev, response.data]);
      } else {
        // Recarregar a página atual
        const servicosResponse = await axios.get(`http://localhost:8080/api/servicos?page=${page}&size=5`);
        setServicos(servicosResponse.data.content);
        setTotalPages(servicosResponse.data.totalPages);
      }
      
      buscarTotalServicos(); // Atualizar estatísticas
      setDialogOpen(false);
    } catch (error) {
      console.error("Erro ao criar serviço:", error);
      setErro("Erro ao criar serviço");
    }
  };

  const handleEditarServico = (servico: TypeServico) => {
    setEditingServico(servico);
    setDialogOpen(true);
  };

  const handleAtualizarServico = async (servicoAtualizado: Omit<TypeServico, 'id' | 'createdAt' | 'updatedAt'>) => {
    if (!editingServico) return;
    
    try {
      const response = await axios.put<TypeServico>(
        `http://localhost:8080/api/servicos/${editingServico.id}`, 
        servicoAtualizado
      );
      
      // Atualizar o serviço na lista atual
      setServicos(prev => 
        prev.map(serv => serv.id === editingServico.id ? response.data : serv)
      );
      
      buscarTotalServicos(); // Atualizar estatísticas
      setDialogOpen(false);
      setEditingServico(null);
    } catch (error) {
      console.error("Erro ao atualizar serviço:", error);
      setErro("Erro ao atualizar serviço");
    }
  };

  const handleDeletarServico = async (id: number) => {
    try {
      await axios.delete(`http://localhost:8080/api/servicos/${id}`);
      
      // Remover da lista atual
      setServicos(prev => prev.filter(servico => servico.id !== id));
      
      // Se a página ficou vazia e não é a primeira, voltar uma página
      if (servicos.length === 1 && page > 0) {
        setPage(page - 1);
      } else {
        // Recarregar a página atual para ajustar
        const servicosResponse = await axios.get(`http://localhost:8080/api/servicos?page=${page}&size=5`);
        setServicos(servicosResponse.data.content);
        setTotalPages(servicosResponse.data.totalPages);
      }
      
      buscarTotalServicos(); // Atualizar estatísticas
    } catch (error) {
      console.error("Erro ao deletar serviço:", error);
      setErro("Erro ao deletar serviço");
    }
  };

  const handleVoltarAdmin = () => {
    navigate('/admin');
  };

  const handleIrCategorias = () => {
    navigate('/categorias');
  };

  const formatarData = (data: string) => {
    return new Date(data).toLocaleDateString('pt-BR');
  };

  const formatarPreco = (preco: number) => {
    return preco.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' });
  };

  const getCategoriaNome = (categoriaId: number) => {
    const categoria = categorias.find(c => c.id === categoriaId);
    return categoria ? categoria.nome : 'Categoria não encontrada';
  };

  const novosDoMes = totalServicos.filter(servico => 
    isSameMonth(parseISO(servico.createdAt), new Date())
  ).length;

  const servicosAtivos = totalServicos.filter(servico => servico.ativo).length;
  const precoMedio = totalServicos.length > 0 ? 
    totalServicos.reduce((acc, s) => acc + s.preco, 0) / totalServicos.length : 0;

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
              <Car className="w-6 h-6 text-primary" />
            </div>
            <div>
              <h1 className="text-3xl font-bold text-foreground">Serviços</h1>
              <p className="text-muted-foreground">Gerencie os serviços oferecidos pela sua empresa</p>
            </div>
          </div>
          
          <div className="flex items-center gap-3">
            {/* Atalho para Categorias */}
            <Button 
              variant="outline" 
              onClick={handleIrCategorias}
              className="flex items-center gap-2 hover:bg-green-50 hover:border-green-300 hover:text-green-700 transition-colors"
              title="Gerenciar categorias dos serviços"
            >
              <Settings className="w-4 h-4" />
              Categorias
            </Button>

            {/* Botão Novo Serviço */}
            <Dialog open={dialogOpen} onOpenChange={(open) => {
                setDialogOpen(open);
                if (!open) setEditingServico(null);
              }}>
              <DialogTrigger asChild>
                <Button className="flex items-center gap-2">
                  <Plus className="w-4 h-4" />
                  Novo Serviço
                </Button>
              </DialogTrigger>
              <DialogContent className="max-w-md max-h-[90vh] overflow-y-auto">
                <DialogHeader>
                  <DialogTitle>
                    {editingServico ? 'Editar Serviço' : 'Adicionar Novo Serviço'}
                  </DialogTitle>
                </DialogHeader>
                <ServicoForm 
                  onSubmit={editingServico ? handleAtualizarServico : handleNovoServico} 
                  initialData={editingServico}
                  categorias={categorias}
                />
              </DialogContent>
            </Dialog>
          </div>
        </div>

        {/* Alerta sobre Categorias (opcional) */}
        {categorias.length === 0 && (
          <div className="mb-6 p-4 bg-yellow-50 border border-yellow-200 rounded-md flex items-center justify-between">
            <div className="flex items-center gap-3">
              <Settings className="w-5 h-5 text-yellow-600" />
              <div>
                <p className="text-yellow-800 font-medium">Nenhuma categoria encontrada</p>
                <p className="text-yellow-700 text-sm">
                  Crie categorias primeiro para organizar melhor seus serviços.
                </p>
              </div>
            </div>
            <Button 
              size="sm" 
              onClick={handleIrCategorias}
              className="bg-yellow-600 hover:bg-yellow-700 text-white"
            >
              <Plus className="w-4 h-4 mr-2" />
              Criar Categoria
            </Button>
          </div>
        )}

        {/* Estatísticas */}
        <div className="grid grid-cols-1 md:grid-cols-4 gap-6 mb-8">
          <Card>
            <CardContent className="p-6">
              <div className="flex items-center justify-between">
                <div>
                  <p className="text-sm font-medium text-muted-foreground">Total de Serviços</p>
                  <p className="text-2xl font-bold text-foreground">{totalServicos.length}</p>
                </div>
                <div className="w-12 h-12 bg-blue-100 rounded-lg flex items-center justify-center">
                  <Car className="w-6 h-6 text-blue-600" />
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
                  <p className="text-2xl font-bold text-foreground">{servicosAtivos}</p>
                </div>
                <div className="w-12 h-12 bg-purple-100 rounded-lg flex items-center justify-center">
                  <Car className="w-6 h-6 text-purple-600" />
                </div>
              </div>
            </CardContent>
          </Card>

          <Card>
            <CardContent className="p-6">
              <div className="flex items-center justify-between">
                <div>
                  <p className="text-sm font-medium text-muted-foreground">Preço Médio</p>
                  <p className="text-2xl font-bold text-foreground">
                    {formatarPreco(precoMedio)}
                  </p>
                </div>
                <div className="w-12 h-12 bg-orange-100 rounded-lg flex items-center justify-center">
                  <DollarSign className="w-6 h-6 text-orange-600" />
                </div>
              </div>
            </CardContent>
          </Card>
        </div>

        {/* Tabela de Serviços */}
        <Card>
          <CardHeader>
            <CardTitle>Lista de Serviços ({totalServicos.length} total)</CardTitle>
          </CardHeader>
          <CardContent>
            {servicos.length === 0 ? (
              <div className="text-center py-8">
                <Car className="w-12 h-12 mx-auto text-muted-foreground mb-4" />
                <p className="text-muted-foreground">Nenhum serviço cadastrado ainda.</p>
                <p className="text-sm text-muted-foreground">Clique em "Novo Serviço" para começar.</p>
              </div>
            ) : (
              <div className="overflow-x-auto">
                <div className="w-full">
                  <div className="grid grid-cols-7 gap-4 p-4 bg-muted/50 rounded-t-lg font-medium text-sm">
                    <div>Nome</div>
                    <div>Categoria</div>
                    <div>Preço</div>
                    <div>Duração</div>
                    <div>Status</div>
                    <div>Data Criação</div>
                    <div>Ações</div>
                  </div>
                  {servicos.map((servico) => (
                    <div key={servico.id} className="grid grid-cols-7 gap-4 p-4 border-b hover:bg-muted/30 transition-colors">
                      <div>
                        <div className="font-medium">{servico.nome}</div>
                        <div className="text-sm text-muted-foreground truncate max-w-xs">
                          {servico.descricao || 'Sem descrição'}
                        </div>
                      </div>
                      <div className="text-sm">{getCategoriaNome(servico.categoriaId)}</div>
                      <div className="font-medium text-green-600">{formatarPreco(servico.preco)}</div>
                      <div className="flex items-center gap-1 text-sm">
                        <Clock className="w-4 h-4 text-muted-foreground" />
                        {servico.duracaoEstimada || 'N/A'}
                      </div>
                      <div>
                        <span className={`px-2 py-1 rounded-full text-xs font-medium ${
                          servico.ativo 
                            ? 'bg-green-100 text-green-800' 
                            : 'bg-red-100 text-red-800'
                        }`}>
                          {servico.ativo ? 'Ativo' : 'Inativo'}
                        </span>
                      </div>
                      <div className="text-sm">{formatarData(servico.createdAt)}</div>
                      <div>
                        <div className="flex items-center gap-2">
                          <Button
                            variant="ghost"
                            size="icon"
                            onClick={() => handleEditarServico(servico)}
                            className="h-8 w-8"
                            title="Editar serviço"
                          >
                            <Edit className="w-4 h-4" />
                          </Button>
                          
                          <AlertDialog>
                            <AlertDialogTrigger asChild>
                              <Button
                                variant="ghost"
                                size="icon"
                                className="h-8 w-8 text-destructive hover:text-destructive"
                                title="Excluir serviço"
                              >
                                <Trash2 className="w-4 h-4" />
                              </Button>
                            </AlertDialogTrigger>
                            <AlertDialogContent>
                              <AlertDialogHeader>
                                <AlertDialogTitle>Confirmar exclusão</AlertDialogTitle>
                                <AlertDialogDescription>
                                  Tem certeza que deseja excluir o serviço "{servico.nome}"? 
                                  Esta ação não pode ser desfeita.
                                </AlertDialogDescription>
                              </AlertDialogHeader>
                              <AlertDialogFooter>
                                <AlertDialogCancel>Cancelar</AlertDialogCancel>
                                <AlertDialogAction
                                  onClick={() => handleDeletarServico(servico.id)}
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
        {erro && (
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

export default Servicos;