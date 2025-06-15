import React, { useState, useEffect } from 'react';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select';
import { useToast } from '@/hooks/use-toast';
import { TypeVeiculo } from '@/types/TypeVeiculo';

interface Cliente {
  id: number;
  nome: string;
  cpf?: string;
  email?: string;
}

interface VeiculoFormData {
  marca: string;
  modelo: string;
  ano: string;
  placa: string;
  cor: string;
  clienteId: string;
}

interface VeiculoFormProps {
  onSubmit: (veiculo: VeiculoFormData, id?: number) => void;
  veiculoEditando?: TypeVeiculo | null;
}

const FormVeiculo: React.FC<VeiculoFormProps> = ({ onSubmit, veiculoEditando }) => {
  const { toast } = useToast();

  const [formData, setFormData] = useState<VeiculoFormData>({
    marca: '',
    modelo: '',
    ano: '',
    placa: '',
    cor: '',
    clienteId: '',
  });

  const [errors, setErrors] = useState<Partial<VeiculoFormData>>({});
  const [isLoading, setIsLoading] = useState(false);
  const [clientes, setClientes] = useState<Cliente[]>([]);
  const [loadingClientes, setLoadingClientes] = useState(false);

  // Buscar clientes da API
  const buscarClientes = async () => {
    setLoadingClientes(true);
    try {
      const response = await fetch('http://localhost:8080/api/clientes/todos');
      if (response.ok) {
        const clientesData = await response.json();
        setClientes(clientesData);
      } else {
        toast({
          title: 'Aviso',
          description: 'Não foi possível carregar a lista de clientes.',
          variant: 'destructive',
        });
      }
    } catch (error) {
      console.error('Erro ao buscar clientes:', error);
      toast({
        title: 'Erro',
        description: 'Erro de conexão ao buscar clientes.',
        variant: 'destructive',
      });
    } finally {
      setLoadingClientes(false);
    }
  };

  // Carregar clientes quando o componente montar
  useEffect(() => {
    buscarClientes();
  }, []);

  const handleInputChange = (field: keyof VeiculoFormData, value: string) => {
    let processedValue = value;
    
    // Processamento específico por campo
    switch (field) {
      case 'placa':
        // Remove espaços e converte para maiúsculo
        processedValue = value.replace(/\s/g, '').toUpperCase();
        // Limita a 7 caracteres
        if (processedValue.length > 7) {
          processedValue = processedValue.slice(0, 7);
        }
        break;
      case 'marca':
      case 'modelo':
        // Limita a 50 caracteres e capitaliza primeira letra
        if (value.length > 50) {
          processedValue = value.slice(0, 50);
        }
        processedValue = processedValue.charAt(0).toUpperCase() + processedValue.slice(1).toLowerCase();
        break;
      case 'cor':
        // Limita a 20 caracteres, remove números e caracteres especiais
        processedValue = value.replace(/[^a-zA-ZÀ-ÿ\s]/g, '');
        if (processedValue.length > 20) {
          processedValue = processedValue.slice(0, 20);
        }
        processedValue = processedValue.charAt(0).toUpperCase() + processedValue.slice(1).toLowerCase();
        break;
      case 'ano':
        // Remove caracteres não numéricos e limita a 4 dígitos
        processedValue = value.replace(/\D/g, '');
        if (processedValue.length > 4) {
          processedValue = processedValue.slice(0, 4);
        }
        break;
    }
    
    setFormData(prev => ({ ...prev, [field]: processedValue }));
    if (errors[field]) {
      setErrors(prev => ({ ...prev, [field]: '' }));
    }
  };

  const handleClienteChange = (value: string) => {
    setFormData(prev => ({ ...prev, clienteId: value }));
    if (errors.clienteId) {
      setErrors(prev => ({ ...prev, clienteId: '' }));
    }
  };

  const validateForm = (): boolean => {
    const newErrors: Partial<VeiculoFormData> = {};
    const currentYear = new Date().getFullYear();
    
    // Validação da marca
    if (!formData.marca.trim()) {
      newErrors.marca = 'Marca é obrigatória';
    } else if (formData.marca.trim().length < 2) {
      newErrors.marca = 'Marca deve ter pelo menos 2 caracteres';
    } else if (formData.marca.trim().length > 50) {
      newErrors.marca = 'Marca não pode ter mais de 50 caracteres';
    }
    
    // Validação do modelo
    if (!formData.modelo.trim()) {
      newErrors.modelo = 'Modelo é obrigatório';
    } else if (formData.modelo.trim().length < 2) {
      newErrors.modelo = 'Modelo deve ter pelo menos 2 caracteres';
    } else if (formData.modelo.trim().length > 50) {
      newErrors.modelo = 'Modelo não pode ter mais de 50 caracteres';
    }
    
    // Validação do ano
    if (!formData.ano.trim()) {
      newErrors.ano = 'Ano é obrigatório';
    } else if (isNaN(parseInt(formData.ano, 10))) {
      newErrors.ano = 'Ano deve ser um número válido';
    } else {
      const ano = parseInt(formData.ano, 10);
      if (ano < 1900) {
        newErrors.ano = 'Ano deve ser maior que 1900';
      } else if (ano > currentYear + 1) {
        newErrors.ano = `Ano não pode ser maior que ${currentYear + 1}`;
      }
    }
    
    // Validação da placa (formato brasileiro)
    if (!formData.placa.trim()) {
      newErrors.placa = 'Placa é obrigatória';
    } else {
      const placa = formData.placa.trim().toUpperCase();
      // Formato antigo: ABC1234 ou novo: ABC1D23
      const formatoAntigo = /^[A-Z]{3}[0-9]{4}$/;
      const formatoNovo = /^[A-Z]{3}[0-9][A-Z][0-9]{2}$/;
      
      if (placa.length !== 7) {
        newErrors.placa = 'Placa deve ter exatamente 7 caracteres';
      } else if (!formatoAntigo.test(placa) && !formatoNovo.test(placa)) {
        newErrors.placa = 'Formato de placa inválido (Ex: ABC1234 ou ABC1D23)';
      }
    }
    
    // Validação da cor
    if (!formData.cor.trim()) {
      newErrors.cor = 'Cor é obrigatória';
    } else if (formData.cor.trim().length < 3) {
      newErrors.cor = 'Cor deve ter pelo menos 3 caracteres';
    } else if (formData.cor.trim().length > 20) {
      newErrors.cor = 'Cor não pode ter mais de 20 caracteres';
    } else if (!/^[a-zA-ZÀ-ÿ\s]+$/.test(formData.cor.trim())) {
      newErrors.cor = 'Cor deve conter apenas letras';
    }
    
    // Validação do clienteId
    if (!formData.clienteId.trim()) {
      newErrors.clienteId = 'Cliente é obrigatório';
    } else if (isNaN(parseInt(formData.clienteId, 10))) {
      newErrors.clienteId = 'ID do cliente deve ser um número válido';
    } else {
      const clienteId = parseInt(formData.clienteId, 10);
      if (clienteId <= 0) {
        newErrors.clienteId = 'ID do cliente deve ser maior que zero';
      }
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!validateForm()) return;

    setIsLoading(true);
    
    const dadosParaEnviar = {
        marca: formData.marca.trim(),
        modelo: formData.modelo.trim(),
        ano: parseInt(formData.ano, 10),
        placa: formData.placa.trim(),
        cor: formData.cor.trim(),
        clienteId: parseInt(formData.clienteId, 10),
    };

    console.log('=== DEBUG ENVIO ===');
    console.log('Dados para enviar:', dadosParaEnviar);
    console.log('Veículo editando:', veiculoEditando);

    try {
        if (veiculoEditando) {
        // Para edição, apenas chama a função onSubmit com os dados
        onSubmit(dadosParaEnviar, veiculoEditando.id);
        } else {
        // Para criação, mantém a lógica atual com query parameters
        const queryParams = new URLSearchParams();
        queryParams.append('marca', dadosParaEnviar.marca);
        queryParams.append('modelo', dadosParaEnviar.modelo);
        queryParams.append('ano', dadosParaEnviar.ano.toString());
        queryParams.append('placa', dadosParaEnviar.placa);
        queryParams.append('cor', dadosParaEnviar.cor);
        queryParams.append('clienteId', dadosParaEnviar.clienteId.toString());

        const url = `http://localhost:8080/api/veiculos/adicionar?${queryParams.toString()}`;
        
        const response = await fetch(url, {
            method: 'POST',
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        });

        const result = await response.json();

        if (response.ok) {
            onSubmit(result);
            toast({
            title: 'Sucesso!',
            description: 'Veículo adicionado com sucesso.',
            });

            setFormData({
            marca: '',
            modelo: '',
            ano: '',
            placa: '',
            cor: '',
            clienteId: '',
            });
        } else {
            toast({
            title: 'Erro',
            description: result?.message || 'Erro ao salvar veículo.',
            variant: 'destructive',
            });
        }
        }
    } catch (error) {
        console.error('Erro ao enviar dados:', error);
        toast({
        title: 'Erro',
        description: 'Erro de conexão com o servidor.',
        variant: 'destructive',
        });
    } finally {
        setIsLoading(false);
    }
    };

  // Helper function to safely convert values to strings
  const safeToString = (value: any): string => {
    if (value === null || value === undefined) return '';
    return value.toString();
  };

  useEffect(() => {
    if (veiculoEditando) {
      console.log('Dados do veículo para edição:', veiculoEditando);
      setFormData({
        marca: veiculoEditando.marca || '',
        modelo: veiculoEditando.modelo || '',
        ano: safeToString(veiculoEditando.ano),
        placa: veiculoEditando.placa || '',
        cor: veiculoEditando.cor || '',
        clienteId: safeToString(veiculoEditando.clientId),
      });
    } else {
      setFormData({
        marca: '',
        modelo: '',
        ano: '',
        placa: '',
        cor: '',
        clienteId: '',
      });
    }
  }, [veiculoEditando]);

  return (
    <form onSubmit={handleSubmit} className="space-y-4">
      {[
        { id: 'marca', label: 'Marca', type: 'text', maxLength: 50, placeholder: 'Ex: Toyota, Ford, Volkswagen' },
        { id: 'modelo', label: 'Modelo', type: 'text', maxLength: 50, placeholder: 'Ex: Corolla, Fiesta, Gol' },
        { id: 'ano', label: 'Ano', type: 'text', maxLength: 4, placeholder: 'Ex: 2020' },
        { id: 'placa', label: 'Placa', type: 'text', maxLength: 7, placeholder: 'Ex: ABC1234 ou ABC1D23' },
        { id: 'cor', label: 'Cor', type: 'text', maxLength: 20, placeholder: 'Ex: Branco, Prata, Preto' },
      ].map(({ id, label, type, maxLength, placeholder }) => (
        <div key={id} className="space-y-2">
          <Label htmlFor={id}>{label} *</Label>
          <Input
            id={id}
            type={type}
            maxLength={maxLength}
            value={formData[id as keyof VeiculoFormData]}
            onChange={(e) => handleInputChange(id as keyof VeiculoFormData, e.target.value)}
            placeholder={placeholder}
            className={errors[id as keyof VeiculoFormData] ? 'border-destructive' : ''}
          />
          {errors[id as keyof VeiculoFormData] && (
            <p className="text-sm text-destructive">{errors[id as keyof VeiculoFormData]}</p>
          )}
        </div>
      ))}

      {/* Select para Cliente */}
      <div className="space-y-2">
        <Label htmlFor="clienteId">Cliente *</Label>
        <Select
          value={formData.clienteId}
          onValueChange={handleClienteChange}
          disabled={loadingClientes}
        >
          <SelectTrigger className={errors.clienteId ? 'border-destructive' : ''}>
            <SelectValue 
              placeholder={loadingClientes ? 'Carregando clientes...' : 'Selecione um cliente'} 
            />
          </SelectTrigger>
          <SelectContent>
            {clientes.length === 0 && !loadingClientes ? (
              <SelectItem value="" disabled>
                Nenhum cliente encontrado
              </SelectItem>
            ) : (
              clientes.map((cliente) => (
                <SelectItem key={cliente.id} value={cliente.id.toString()}>
                  {cliente.nome} - ID: {cliente.id}
                  {cliente.cpf && ` (CPF: ${cliente.cpf})`}
                </SelectItem>
              ))
            )}
          </SelectContent>
        </Select>
        {errors.clienteId && (
          <p className="text-sm text-destructive">{errors.clienteId}</p>
        )}
        {clientes.length === 0 && !loadingClientes && (
          <div className="flex items-center gap-2">
            <p className="text-sm text-muted-foreground">
              Nenhum cliente encontrado.
            </p>
            <Button
              type="button"
              variant="outline"
              size="sm"
              onClick={buscarClientes}
            >
              Recarregar
            </Button>
          </div>
        )}
      </div>

      <div className="flex gap-3 pt-4">
        <Button type="submit" className="flex-1" disabled={isLoading}>
          {isLoading
            ? 'Salvando...'
            : veiculoEditando
            ? 'Salvar Alterações'
            : 'Adicionar Veículo'}
        </Button>
      </div>
    </form>
  );
};

export default FormVeiculo;