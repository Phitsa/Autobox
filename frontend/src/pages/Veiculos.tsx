import React, { useEffect, useState } from 'react';
import { Button } from '@/components/ui/button';
import axios from 'axios';
import { TypeVeiculo } from "../types/TypeVeiculo";
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from '@/components/ui/table';
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogTrigger } from '@/components/ui/dialog';
import { Badge } from '@/components/ui/badge';
import { useNavigate } from 'react-router-dom';
import { Plus, Car, Calendar, ArrowLeft, Edit, Trash2, User, Hash } from 'lucide-react';
import FormVeiculo from '@/components/vehicle/FormVeiculo';
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

const Veiculos = () => {
  const navigate = useNavigate();
  const [editingVeiculo, setEditingVeiculo] = useState<TypeVeiculo | null>(null);
  const [veiculos, setVeiculos] = useState<TypeVeiculo[]>([]);
  const [totalVeiculos, setTotalVeiculos] = useState<TypeVeiculo[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [erro, setErro] = useState<string | null>(null);
  const [dialogOpen, setDialogOpen] = useState(false);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(1);

  useEffect(() => {
    const buscarVeiculos = async () => {
      try {
        const response = await axios.get(`http://localhost:8080/api/veiculos/pagina?page=${page}&size=5`);
        setVeiculos(response.data.content); // conteúdo da página
      } catch (error) {
        setErro("Erro ao carregar veículos");
        console.error(error);
      } finally {
        setLoading(false);
      }
    };

    buscarVeiculos();
  }, [page]);

  const buscarTotalVeiculos = async () => {
    try {
      const response = await axios.get<TypeVeiculo[]>("http://localhost:8080/api/veiculos/todos");
      setTotalVeiculos(response.data);
      setTotalPages(Math.ceil(response.data.length / 5)); // Atualiza o total de páginas com base no tamanho da página
    } catch (error) {
      console.error("Erro ao carregar total de veículos:", error);
    }
  };

  useEffect(() => {
    buscarTotalVeiculos();
  }, []);

  const handleAtualizarVeiculo = async (veiculoAtualizado: any, id?: number) => {
    try {
      const response = await axios.put<TypeVeiculo>(
        `http://localhost:8080/api/veiculos/editar/${id || editingVeiculo?.id}`,
        veiculoAtualizado
      );

      // Atualiza o veículo na lista
      setVeiculos(prev =>
        prev.map(v => (v.id === response.data.id ? response.data : v))
      );

      setDialogOpen(false);
      setEditingVeiculo(null);
      buscarTotalVeiculos();
    } catch (error) {
      console.error("Erro ao atualizar veículo:", error);
    }
  };

  if (loading) return <p>Carregando...</p>;
  if (erro) return <p className="text-red-600">{erro}</p>;

  const handleNovoVeiculo = async (novoVeiculo: any) => {
    try {
      const response = await axios.post<TypeVeiculo>("http://localhost:8080/api/veiculos/adicionar", novoVeiculo);
      setVeiculos(prev => [...prev, response.data]);
      setDialogOpen(false);
      buscarTotalVeiculos();
    } catch (error) {
      console.error("Erro ao criar veículo:", error);
    }
  };

  const handleEditarVeiculo = (veiculo: TypeVeiculo) => {
    console.log('Veículo selecionado para editar:', veiculo);
    setEditingVeiculo(veiculo);
    setDialogOpen(true);
  };

  const handleDeletarVeiculo = (id: number) => {
    axios.delete(`http://localhost:8080/api/veiculos/delete/${id}`)
      .then(() => {
        setVeiculos(prev => prev.filter(veiculo => veiculo.id !== id));
        buscarTotalVeiculos(); // Atualiza a lista total de veículos
        console.log('Deletando veículo com ID:', id);
      })
      .catch(error => {
        console.error("Erro ao deletar veículo:", error);
      });
  };

  const handleVoltarAdmin = () => {
    navigate('/admin');
  };

  const formatarData = (data: string) => {
    return new Date(data).toLocaleDateString('pt-BR');
  };

  const getCorCircle = (cor: string) => {
    const coresMap: { [key: string]: string } = {
      'branco': '#ffffff',
      'preto': '#000000',
      'prata': '#c0c0c0',
      'azul': '#0066cc',
      'vermelho': '#cc0000',
      'verde': '#00aa00',
      'amarelo': '#ffcc00',
      'cinza': '#808080',
      'marrom': '#8B4513'
    };
    
    return coresMap[cor.toLowerCase()] || '#808080';
  };

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
              <h1 className="text-3xl font-bold text-foreground">Veículos</h1>
              <p className="text-muted-foreground">Gerencie a frota de veículos dos seus clientes</p>
            </div>
          </div>
          
          <Dialog open={dialogOpen} onOpenChange={(open) => {
              setDialogOpen(open);
              if (!open) setEditingVeiculo(null);
            }}>
            <DialogTrigger asChild>
              <Button className="flex items-center gap-2">
                <Plus className="w-4 h-4" />
                Novo Veículo
              </Button>
            </DialogTrigger>
            <DialogContent className="max-w-md max-h-[90vh] overflow-y-auto">
              <DialogHeader>
                <DialogTitle>
                  {editingVeiculo ? 'Editar Veículo' : 'Adicionar Novo Veículo'}
                </DialogTitle>
              </DialogHeader>
              <FormVeiculo 
                onSubmit={editingVeiculo ? handleAtualizarVeiculo : handleNovoVeiculo}
                veiculoEditando={editingVeiculo}
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
                  <p className="text-sm font-medium text-muted-foreground">Total de Veículos</p>
                  <p className="text-2xl font-bold text-foreground">{totalVeiculos.length}</p>
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
                  <p className="text-2xl font-bold text-foreground">oioi</p>
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
                  <p className="text-sm font-medium text-muted-foreground">Crescimento percentual no mês</p>
                  <p className="text-2xl font-bold text-foreground">oioi</p>
                </div>
                <div className="w-12 h-12 bg-purple-100 rounded-lg flex items-center justify-center">
                  <Car className="w-6 h-6 text-purple-600" />
                </div>
              </div>
            </CardContent>
          </Card> 
        </div>

        {/* Tabela de Veículos */}
        <Card>
          <CardHeader>
            <CardTitle>Lista de Veículos</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="overflow-x-auto">
              <Table>
                <TableHeader>
                  <TableRow>
                    <TableHead>Marca/Modelo</TableHead>
                    <TableHead>Ano</TableHead>
                    <TableHead>Placa</TableHead>
                    <TableHead>Cor</TableHead>
                    <TableHead>Cliente</TableHead>
                    <TableHead>Data Criação</TableHead>
                    <TableHead>Ações</TableHead>
                  </TableRow>
                </TableHeader>
                <TableBody>
                  {veiculos.map((veiculo) => (
                    <TableRow key={veiculo.id} className="hover:bg-muted/50">
                      <TableCell className="font-medium">
                        <div>
                          <p className="font-semibold">{veiculo.marca}</p>
                          <p className="text-sm text-muted-foreground">{veiculo.modelo}</p>
                        </div>
                      </TableCell>
                      <TableCell>
                        <Badge variant="outline">{veiculo.ano}</Badge>
                      </TableCell>
                      <TableCell>
                        <div className="flex items-center gap-2">
                          <Hash className="w-4 h-4 text-muted-foreground" />
                          {veiculo.placa}
                        </div>
                      </TableCell>
                      <TableCell>
                        <div className="flex items-center gap-2">
                          <div 
                            className="w-4 h-4 rounded-full border border-gray-300"
                            style={{ backgroundColor: getCorCircle(veiculo.cor) }}
                          />
                          {veiculo.cor}
                        </div>
                      </TableCell>
                      <TableCell>
                        <div className="flex items-center gap-2">
                          <User className="w-4 h-4 text-muted-foreground" />
                          <div>
                            <p className="text-sm">{veiculo.nomeCliente || `Cliente ${veiculo.clienteId}`}</p>
                            <p className="text-xs text-muted-foreground">ID: {veiculo.clienteId}</p>
                          </div>
                        </div>
                      </TableCell>
                      <TableCell>{formatarData(veiculo.dataCriacao)}</TableCell>
                      <TableCell>
                        <div className="flex items-center gap-2">
                          <Button
                            variant="ghost"
                            size="icon"
                            onClick={() => handleEditarVeiculo(veiculo)}
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
                                  Tem certeza que deseja excluir o veículo "{veiculo.marca} {veiculo.modelo}" placa {veiculo.placa}? 
                                  Esta ação não pode ser desfeita.
                                </AlertDialogDescription>
                              </AlertDialogHeader>
                              <AlertDialogFooter>
                                <AlertDialogCancel>Cancelar</AlertDialogCancel>
                                <AlertDialogAction
                                  onClick={() => handleDeletarVeiculo(veiculo.id)}
                                  className="bg-destructive text-destructive-foreground hover:bg-destructive/90"
                                >
                                  Excluir
                                </AlertDialogAction>
                              </AlertDialogFooter>
                            </AlertDialogContent>
                          </AlertDialog>
                        </div>
                      </TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            </div>
          </CardContent>
        </Card>

        {/* Paginação */}
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
      </div>
    </div>
  );
};

export default Veiculos;