import React, { useState, useEffect } from 'react';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Textarea } from '@/components/ui/textarea';
import { Command, CommandEmpty, CommandGroup, CommandInput, CommandItem } from '@/components/ui/command';
import { Popover, PopoverContent, PopoverTrigger } from '@/components/ui/popover';
import { Check, ChevronsUpDown } from 'lucide-react';
import { cn } from '@/lib/utils';
import { TypeAgendamento, TypeCliente, TypeVeiculo, TypeServico, StatusAgendamento } from '../types/TypeAgendamento';
import { useAuth } from '../contexts/AuthContext';
import axios from 'axios';

interface AgendamentoFormProps {
  onSubmit: (agendamento: Omit<TypeAgendamento, 'id' | 'createdAt' | 'updatedAt'>) => void;
  initialData?: TypeAgendamento | null;
}

const AgendamentoForm: React.FC<AgendamentoFormProps> = ({ onSubmit, initialData }) => {
  const { user } = useAuth();

  const [formData, setFormData] = useState({
    clienteId: initialData?.clienteId || 0,
    veiculoId: initialData?.veiculoId || 0,
    servicoId: initialData?.servicoId || 0,
    dataAgendamento: initialData?.dataAgendamento || '',
    horaInicio: initialData?.horaInicio || '',
    horaFim: initialData?.horaFim || '',
    status: initialData?.status || StatusAgendamento.AGENDADO,
    observacoes: initialData?.observacoes || '',
    valorTotal: initialData?.valorTotal || 0,
  });

  const [clientes, setClientes] = useState<TypeCliente[]>([]);
  const [veiculos, setVeiculos] = useState<TypeVeiculo[]>([]);
  const [servicos, setServicos] = useState<TypeServico[]>([]);
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [errors, setErrors] = useState<Record<string, string>>({});

  // Estados para controlar abertura dos popovers
  const [clienteOpen, setClienteOpen] = useState(false);
  const [veiculoOpen, setVeiculoOpen] = useState(false);
  const [servicoOpen, setServicoOpen] = useState(false);

  useEffect(() => {
    const carregarDados = async () => {
      try {
        const [clientesRes, veiculosRes, servicosRes] = await Promise.all([
          axios.get<TypeCliente[]>('http://localhost:8080/api/clientes/todos'),
          axios.get<TypeVeiculo[]>('http://localhost:8080/api/veiculos/todos'),
          axios.get<TypeServico[]>('http://localhost:8080/api/servicos/todos')
        ]);

        setClientes(clientesRes.data || []);
        
        // Enriquecer veículos com nome do cliente para exibição
        const veiculosComCliente = (veiculosRes.data || []).map(veiculo => {
          const cliente = clientesRes.data?.find(c => c.id === veiculo.clienteId);
          return {
            ...veiculo,
            clienteNome: cliente ? cliente.nome : 'Cliente não encontrado'
          };
        });
        
        setVeiculos(veiculosComCliente);
        setServicos((servicosRes.data || []).filter(s => s.ativo));
      } catch (error) {
        console.error('Erro ao carregar dados:', error);
      } finally {
        setLoading(false);
      }
    };

    carregarDados();
  }, []);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    
    if (submitting) return; // Prevenir múltiplos submits
    
    // Validações
    const newErrors: Record<string, string> = {};
    
    if (!formData.clienteId) newErrors.clienteId = 'Cliente é obrigatório';
    if (!formData.veiculoId) newErrors.veiculoId = 'Veículo é obrigatório';
    if (!formData.servicoId) newErrors.servicoId = 'Serviço é obrigatório';
    if (!formData.dataAgendamento) newErrors.dataAgendamento = 'Data é obrigatória';
    if (!formData.horaInicio) newErrors.horaInicio = 'Hora de início é obrigatória';
    
    // Validar se a data não é no passado
    const hoje = new Date();
    const dataAgendamento = new Date(formData.dataAgendamento);
    if (dataAgendamento < new Date(hoje.getFullYear(), hoje.getMonth(), hoje.getDate())) {
      newErrors.dataAgendamento = 'Não é possível agendar para datas passadas';
    }

    if (Object.keys(newErrors).length > 0) {
      setErrors(newErrors);
      return;
    }

    setErrors({});
    setSubmitting(true);
    
    try {
      // Preparar dados para envio - adicionar funcionário logado automaticamente
      const dadosParaEnvio = {
        ...formData,
        funcionarioResponsavelId: user?.id || null
      };
      
      await onSubmit(dadosParaEnvio);
    } catch (error) {
      console.error('Erro ao submeter formulário:', error);
    } finally {
      setSubmitting(false);
    }
  };

  const handleInputChange = (field: string, value: any) => {
    setFormData(prev => ({ ...prev, [field]: value }));
    if (errors[field]) {
      setErrors(prev => ({ ...prev, [field]: '' }));
    }
  };

  const getClienteNome = (clienteId: number) => {
    const cliente = clientes.find(c => c.id === clienteId);
    return cliente ? `${cliente.nome} - ${cliente.cpf || cliente.email}` : 'Selecione um cliente';
  };

  const getVeiculoInfo = (veiculoId: number) => {
    const veiculo = veiculos.find(v => v.id === veiculoId);
    return veiculo 
      ? `${veiculo.marca} ${veiculo.modelo} - ${veiculo.placa} (${veiculo.clienteNome})`
      : 'Selecione um veículo';
  };

  const getServicoInfo = (servicoId: number) => {
    const servico = servicos.find(s => s.id === servicoId);
    return servico 
      ? `${servico.nome} - R$ ${servico.preco.toFixed(2)}`
      : 'Selecione um serviço';
  };

  if (loading) {
    return (
      <div className="flex items-center justify-center p-8">
        <div className="text-center">
          <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-primary mx-auto mb-2"></div>
          <p className="text-sm text-muted-foreground">Carregando dados...</p>
        </div>
      </div>
    );
  }

  return (
    <form onSubmit={handleSubmit} className="space-y-6">
      {/* Cliente */}
      <div className="space-y-2">
        <Label htmlFor="cliente">Cliente *</Label>
        <Popover open={clienteOpen} onOpenChange={setClienteOpen}>
          <PopoverTrigger asChild>
            <Button
              variant="outline"
              role="combobox"
              aria-expanded={clienteOpen}
              className="w-full justify-between"
            >
              {formData.clienteId ? getClienteNome(formData.clienteId) : "Selecione um cliente"}
              <ChevronsUpDown className="ml-2 h-4 w-4 shrink-0 opacity-50" />
            </Button>
          </PopoverTrigger>
          <PopoverContent className="w-full p-0">
            <Command>
              <CommandInput placeholder="Buscar cliente..." />
              <CommandEmpty>Nenhum cliente encontrado.</CommandEmpty>
              <CommandGroup className="max-h-64 overflow-auto">
                {clientes.map((cliente) => (
                  <CommandItem
                    key={cliente.id}
                    value={`${cliente.nome} ${cliente.cpf} ${cliente.email}`}
                    onSelect={() => {
                      handleInputChange('clienteId', cliente.id);
                      setClienteOpen(false);
                    }}
                  >
                    <Check
                      className={cn(
                        "mr-2 h-4 w-4",
                        formData.clienteId === cliente.id ? "opacity-100" : "opacity-0"
                      )}
                    />
                    <div>
                      <div className="font-medium">{cliente.nome}</div>
                      <div className="text-sm text-muted-foreground">{cliente.cpf || cliente.email}</div>
                    </div>
                  </CommandItem>
                ))}
              </CommandGroup>
            </Command>
          </PopoverContent>
        </Popover>
        {errors.clienteId && <p className="text-sm text-red-600">{errors.clienteId}</p>}
      </div>

      {/* Veículo */}
      <div className="space-y-2">
        <Label htmlFor="veiculo">Veículo *</Label>
        <Popover open={veiculoOpen} onOpenChange={setVeiculoOpen}>
          <PopoverTrigger asChild>
            <Button
              variant="outline"
              role="combobox"
              aria-expanded={veiculoOpen}
              className="w-full justify-between"
            >
              {formData.veiculoId ? getVeiculoInfo(formData.veiculoId) : "Selecione um veículo"}
              <ChevronsUpDown className="ml-2 h-4 w-4 shrink-0 opacity-50" />
            </Button>
          </PopoverTrigger>
          <PopoverContent className="w-full p-0">
            <Command>
              <CommandInput placeholder="Buscar veículo..." />
              <CommandEmpty>Nenhum veículo encontrado.</CommandEmpty>
              <CommandGroup className="max-h-64 overflow-auto">
                {veiculos.map((veiculo) => (
                  <CommandItem
                    key={veiculo.id}
                    value={`${veiculo.marca} ${veiculo.modelo} ${veiculo.placa} ${veiculo.clienteNome}`}
                    onSelect={() => {
                      handleInputChange('veiculoId', veiculo.id);
                      setVeiculoOpen(false);
                    }}
                  >
                    <Check
                      className={cn(
                        "mr-2 h-4 w-4",
                        formData.veiculoId === veiculo.id ? "opacity-100" : "opacity-0"
                      )}
                    />
                    <div>
                      <div className="font-medium">{veiculo.marca} {veiculo.modelo} - {veiculo.placa}</div>
                      <div className="text-sm text-muted-foreground">Proprietário: {veiculo.clienteNome}</div>
                    </div>
                  </CommandItem>
                ))}
              </CommandGroup>
            </Command>
          </PopoverContent>
        </Popover>
        {errors.veiculoId && <p className="text-sm text-red-600">{errors.veiculoId}</p>}
        {veiculos.length === 0 && (
          <p className="text-sm text-amber-600">Nenhum veículo cadastrado no sistema</p>
        )}
      </div>

      {/* Serviço */}
      <div className="space-y-2">
        <Label htmlFor="servico">Serviço *</Label>
        <Popover open={servicoOpen} onOpenChange={setServicoOpen}>
          <PopoverTrigger asChild>
            <Button
              variant="outline"
              role="combobox"
              aria-expanded={servicoOpen}
              className="w-full justify-between"
            >
              {formData.servicoId ? getServicoInfo(formData.servicoId) : "Selecione um serviço"}
              <ChevronsUpDown className="ml-2 h-4 w-4 shrink-0 opacity-50" />
            </Button>
          </PopoverTrigger>
          <PopoverContent className="w-full p-0">
            <Command>
              <CommandInput placeholder="Buscar serviço..." />
              <CommandEmpty>Nenhum serviço encontrado.</CommandEmpty>
              <CommandGroup className="max-h-64 overflow-auto">
                {servicos.map((servico) => (
                  <CommandItem
                    key={servico.id}
                    value={`${servico.nome} ${servico.preco}`}
                    onSelect={() => {
                      handleInputChange('servicoId', servico.id);
                      // Auto-preencher valor total se não foi definido
                      if (!formData.valorTotal) {
                        handleInputChange('valorTotal', servico.preco);
                      }
                      setServicoOpen(false);
                    }}
                  >
                    <Check
                      className={cn(
                        "mr-2 h-4 w-4",
                        formData.servicoId === servico.id ? "opacity-100" : "opacity-0"
                      )}
                    />
                    <div>
                      <div className="font-medium">{servico.nome}</div>
                      <div className="text-sm text-muted-foreground">R$ {servico.preco.toFixed(2)}</div>
                    </div>
                  </CommandItem>
                ))}
              </CommandGroup>
            </Command>
          </PopoverContent>
        </Popover>
        {errors.servicoId && <p className="text-sm text-red-600">{errors.servicoId}</p>}
      </div>

      {/* Data e Hora */}
      <div className="grid grid-cols-2 gap-4">
        <div className="space-y-2">
          <Label htmlFor="dataAgendamento">Data *</Label>
          <Input
            type="date"
            value={formData.dataAgendamento}
            onChange={(e) => handleInputChange('dataAgendamento', e.target.value)}
            min={new Date().toISOString().split('T')[0]}
            className="w-full"
          />
          {errors.dataAgendamento && <p className="text-sm text-red-600">{errors.dataAgendamento}</p>}
        </div>

        <div className="space-y-2">
          <Label htmlFor="horaInicio">Hora de Início *</Label>
          <Input
            type="time"
            value={formData.horaInicio}
            onChange={(e) => handleInputChange('horaInicio', e.target.value)}
            className="w-full"
          />
          {errors.horaInicio && <p className="text-sm text-red-600">{errors.horaInicio}</p>}
        </div>
      </div>

      {/* Hora Fim */}
      <div className="space-y-2">
        <Label htmlFor="horaFim">Hora de Término (opcional)</Label>
        <Input
          type="time"
          value={formData.horaFim}
          onChange={(e) => handleInputChange('horaFim', e.target.value)}
          className="w-full"
        />
      </div>

      {/* Status (apenas para edição) */}
      {initialData && (
        <div className="space-y-2">
          <Label htmlFor="status">Status</Label>
          <select
            value={formData.status}
            onChange={(e) => handleInputChange('status', e.target.value)}
            className="w-full p-2 border border-gray-300 rounded-md focus:ring-2 focus:ring-primary focus:border-transparent"
          >
            <option value={StatusAgendamento.AGENDADO}>Agendado</option>
            <option value={StatusAgendamento.EM_ANDAMENTO}>Em Andamento</option>
            <option value={StatusAgendamento.CONCLUIDO}>Concluído</option>
            <option value={StatusAgendamento.CANCELADO}>Cancelado</option>
          </select>
        </div>
      )}

      {/* Valor Total */}
      <div className="space-y-2">
        <Label htmlFor="valorTotal">Valor Total (R$)</Label>
        <Input
          type="number"
          step="0.01"
          min="0"
          value={formData.valorTotal}
          onChange={(e) => handleInputChange('valorTotal', parseFloat(e.target.value) || 0)}
          placeholder="0.00"
          className="w-full"
        />
      </div>

      {/* Observações */}
      <div className="space-y-2">
        <Label htmlFor="observacoes">Observações</Label>
        <Textarea
          value={formData.observacoes}
          onChange={(e) => handleInputChange('observacoes', e.target.value)}
          placeholder="Observações sobre o agendamento..."
          rows={3}
          className="w-full"
        />
      </div>

      <div className="flex justify-end space-x-3 pt-4">
        <Button 
          type="submit" 
          disabled={submitting}
          className="min-w-[120px]"
        >
          {submitting ? (
            <div className="flex items-center">
              <div className="animate-spin rounded-full h-4 w-4 border-b-2 border-white mr-2"></div>
              {initialData ? 'Atualizando...' : 'Criando...'}
            </div>
          ) : (
            `${initialData ? 'Atualizar' : 'Criar'} Agendamento`
          )}
        </Button>
      </div>
    </form>
  );
};

export default AgendamentoForm;