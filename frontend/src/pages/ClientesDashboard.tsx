import React, { useEffect, useState } from 'react';
import { Button } from '@/components/ui/button';
import axios from 'axios';
import { TypeCliente } from "../types/TypeCliente";
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from '@/components/ui/table';
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogTrigger } from '@/components/ui/dialog';
import { Badge } from '@/components/ui/badge';
import { useNavigate } from 'react-router-dom';
import { Plus, Users, Mail, Phone, MapPin, Calendar, ArrowLeft, CreditCard, Edit, Trash2 } from 'lucide-react';
import ClienteForm from '@/components/Client/ClientForm';
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

const Clientes = () => {
  const navigate = useNavigate();
  const [editingCliente, setEditingCliente] = useState<TypeCliente | null>(null);
  const [clientes, setClientes] = useState<TypeCliente[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [erro, setErro] = useState<string | null>(null);
  const [dialogOpen, setDialogOpen] = useState(false);


  useEffect(() => {
    const buscarClientes = async () => {
      try {
        const response = await axios.get<TypeCliente[]>("http://localhost:8080/api/clientes");
        setClientes(response.data);
      } catch (error) {
        setErro("Erro ao carregar clientes");
        console.error(error);
      } finally {
        setLoading(false);
      }
    };

    buscarClientes();
  }, []);

  const handleAtualizarCliente = async (clienteAtualizado: TypeCliente) => {
    try {
      const response = await axios.put<TypeCliente>(
        `http://localhost:8080/api/clientes/editar/${clienteAtualizado.id}`,
        clienteAtualizado
      );

      // Atualiza o cliente na lista
      setClientes(prev =>
        prev.map(c => (c.id === response.data.id ? response.data : c))
      );

      setDialogOpen(false);
      setEditingCliente(null);
    } catch (error) {
      console.error("Erro ao atualizar cliente:", error);
    }
  };

  if (loading) return <p>Carregando...</p>;
  if (erro) return <p className="text-red-600">{erro}</p>;


  const handleNovoCliente = async (novoCliente: Omit<TypeCliente, 'id' | 'dataCriacao'>) => {
    try {
      const response = await axios.post<TypeCliente>("http://localhost:8080/api/clientes", novoCliente);
      setClientes(prev => [...prev, response.data]);
      setDialogOpen(false);
    } catch (error) {
      console.error("Erro ao criar cliente:", error);
    }
  };

  const handleEditarCliente = (cliente: TypeCliente) => {
  console.log('Cliente selecionado para editar:', cliente);
  setEditingCliente(cliente);
  setDialogOpen(true);
  };

  const handleDeletarCliente = (id: number) => {
  setClientes((prev) => prev.filter((cliente) => cliente.id !== id));
  };

  const handleVoltarAdmin = () => {
    navigate('/admin');
  };

  const formatarData = (data: string) => {
    return new Date(data).toLocaleDateString('pt-BR');
  };

  const novosDoMes = clientes.filter(cliente => 
  isSameMonth(parseISO(cliente.dataCriacao), new Date())
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
              <Users className="w-6 h-6 text-primary" />
            </div>
            <div>
              <h1 className="text-3xl font-bold text-foreground">Clientes</h1>
              <p className="text-muted-foreground">Gerencie informações dos seus clientes</p>
            </div>
          </div>
          
          <Dialog open={dialogOpen} onOpenChange={(open) => {
              setDialogOpen(open);
              if (!open) setEditingCliente(null); // Opcional: limpa o estado ao fechar
            }}>
            <DialogTrigger asChild>
              <Button className="flex items-center gap-2">
                <Plus className="w-4 h-4" />
                Novo Cliente
              </Button>
            </DialogTrigger>
            <DialogContent className="max-w-md max-h-[90vh] overflow-y-auto">
              <DialogHeader>
                <DialogTitle>
                  {editingCliente ? 'Editar Cliente' : 'Adicionar Novo Cliente'}
                </DialogTitle>
              </DialogHeader>
              <ClienteForm 
                onSubmit={editingCliente ? handleAtualizarCliente : handleNovoCliente}
                clienteEditando={editingCliente}
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
                  <p className="text-sm font-medium text-muted-foreground">Total de Clientes</p>
                  <p className="text-2xl font-bold text-foreground">{clientes.length}</p>
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
                  <p className="text-2xl font-bold text-foreground">{clientes.length}</p>
                </div>
                <div className="w-12 h-12 bg-purple-100 rounded-lg flex items-center justify-center">
                  <Users className="w-6 h-6 text-purple-600" />
                </div>
              </div>
            </CardContent>
          </Card>
        </div>

        {/* Tabela de Clientes */}
        <Card>
          <CardHeader>
            <CardTitle>Lista de Clientes</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="overflow-x-auto">
              <Table>
                <TableHeader>
                  <TableRow>
                    <TableHead>Nome</TableHead>
                    <TableHead>Email</TableHead>
                    <TableHead>Telefone</TableHead>
                    <TableHead>CPF</TableHead>
                    <TableHead>Data Criação</TableHead>
                    <TableHead>Ações</TableHead>
                  </TableRow>
                </TableHeader>
                <TableBody>
                  {clientes.map((cliente) => (
                    <TableRow key={cliente.id} className="hover:bg-muted/50">
                      <TableCell className="font-medium">{cliente.nome}</TableCell>
                      <TableCell>
                        <div className="flex items-center gap-2">
                          <Mail className="w-4 h-4 text-muted-foreground" />
                          {cliente.email}
                        </div>
                      </TableCell>
                      <TableCell>
                        <div className="flex items-center gap-2">
                          <Phone className="w-4 h-4 text-muted-foreground" />
                          {cliente.telefone}
                        </div>
                      </TableCell>
                      <TableCell>
                        <div className="flex items-center gap-2">
                          <CreditCard className="w-4 h-4 text-muted-foreground" />
                          {cliente.cpf}
                        </div>
                      </TableCell>
                      <TableCell>{formatarData(cliente.dataCriacao)}</TableCell>
                      <TableCell>
                        <div className="flex items-center gap-2">
                          <Button
                            variant="ghost"
                            size="icon"
                            onClick={() => handleEditarCliente(cliente)}
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
                                  Tem certeza que deseja excluir o cliente "{cliente.nome}"? 
                                  Esta ação não pode ser desfeita.
                                </AlertDialogDescription>
                              </AlertDialogHeader>
                              <AlertDialogFooter>
                                <AlertDialogCancel>Cancelar</AlertDialogCancel>
                                <AlertDialogAction
                                  onClick={() => handleDeletarCliente(cliente.id)}
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
      </div>
    </div>
  );
};

export default Clientes;