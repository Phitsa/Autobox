import React, { useEffect, useState } from 'react';
import { Button } from '@/components/ui/button';
import { TypeCategoria } from "../types/TypeCategoria";
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogTrigger } from '@/components/ui/dialog';
import { Plus, Settings, ArrowLeft, Edit, Trash2, Calendar, FolderOpen } from 'lucide-react';
import CategoriaForm from '@/components/CategoriaFormProps';
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

// Mock data para demonstração
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
  const [categorias, setCategorias] = useState<TypeCategoria[]>(mockCategorias);
  const [loading, setLoading] = useState<boolean>(false);
  const [erro, setErro] = useState<string | null>(null);
  const [dialogOpen, setDialogOpen] = useState(false);

  // Função para simular chamada à API
  const buscarCategorias = async () => {
    try {
      setLoading(true);
      // Simular delay da API
      await new Promise(resolve => setTimeout(resolve, 500));
      // Em produção, substituir por: const response = await axios.get<TypeCategoria[]>("http://localhost:8080/api/categorias");
      setCategorias(mockCategorias);
    } catch (error) {
      setErro("Erro ao carregar categorias");
      console.error(error);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    buscarCategorias();
  }, []);

  if (loading) return <p>Carregando...</p>;
  if (erro) return <p className="text-red-600">{erro}</p>;

  const handleNovaCategoria = async (novaCategoria: Omit<TypeCategoria, 'id' | 'createdAt' | 'updatedAt'>) => {
    try {
      // Em produção: const response = await axios.post<TypeCategoria>("http://localhost:8080/api/categorias", novaCategoria);
      const novaId = Math.max(...categorias.map(c => c.id)) + 1;
      const categoria: TypeCategoria = {
        ...novaCategoria,
        id: novaId,
        createdAt: new Date().toISOString(),
        updatedAt: new Date().toISOString()
      };
      setCategorias([...categorias, categoria]);
      setDialogOpen(false);
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
      // Em produção: const response = await axios.put<TypeCategoria>(`http://localhost:8080/api/categorias/${editingCategoria.id}`, categoriaAtualizada);
      setCategorias(prev => 
        prev.map(cat => 
          cat.id === editingCategoria.id 
            ? { ...cat, ...categoriaAtualizada, updatedAt: new Date().toISOString() }
            : cat
        )
      );
      setDialogOpen(false);
      setEditingCategoria(null);
    } catch (error) {
      console.error("Erro ao atualizar categoria:", error);
      setErro("Erro ao atualizar categoria");
    }
  };

  const handleDeletarCategoria = async (id: number) => {
    try {
      // Em produção: await axios.delete(`http://localhost:8080/api/categorias/${id}`);
      setCategorias(prev => prev.filter(categoria => categoria.id !== id));
    } catch (error) {
      console.error("Erro ao deletar categoria:", error);
      setErro("Erro ao deletar categoria");
    }
  };

  const handleVoltarAdmin = () => {
    // Em produção: navigate('/admin');
    console.log('Voltar para admin');
  };

  const formatarData = (data: string) => {
    return new Date(data).toLocaleDateString('pt-BR');
  };

  // Função helper para verificar se é o mesmo mês
  const isSameMonth = (date1: Date, date2: Date) => {
    return date1.getMonth() === date2.getMonth() && date1.getFullYear() === date2.getFullYear();
  };

  const novasDoMes = categorias.filter(categoria => 
    isSameMonth(new Date(categoria.createdAt), new Date())
  ).length;

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
                  <p className="text-2xl font-bold text-foreground">{categorias.length}</p>
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
                  <p className="text-2xl font-bold text-foreground">{categorias.length}</p>
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
            <CardTitle>Lista de Categorias</CardTitle>
          </CardHeader>
          <CardContent>
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
                        >
                          <Edit className="w-4 h-4" />
                        </Button>
                        
                        <AlertDialog>
                          <AlertDialogTrigger asChild>
                            <Button
                              variant="ghost"
                              size="icon"
                              className="h-8 w-8 text-destructive hover:text-destructive"
                            >
                              <Trash2 className="w-4 h-4" />
                            </Button>
                          </AlertDialogTrigger>
                          <AlertDialogContent>
                            <AlertDialogHeader>
                              <AlertDialogTitle>Confirmar exclusão</AlertDialogTitle>
                              <AlertDialogDescription>
                                Tem certeza que deseja excluir a categoria "{categoria.nome}"? 
                                Esta ação não pode ser desfeita.
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
          </CardContent>
        </Card>
      </div>
    </div>
  );
};

export default Categorias;