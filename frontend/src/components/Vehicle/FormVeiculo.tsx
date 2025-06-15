import React, { useState, useEffect } from 'react';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { useToast } from '@/hooks/use-toast';
import { TypeVeiculo } from '@/types/TypeVeiculo';

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

  const handleInputChange = (field: keyof VeiculoFormData, value: string) => {
    setFormData(prev => ({ ...prev, [field]: value }));
    if (errors[field]) {
      setErrors(prev => ({ ...prev, [field]: '' }));
    }
  };

  const validateForm = (): boolean => {
    const newErrors: Partial<VeiculoFormData> = {};
    
    if (!formData.marca.trim()) newErrors.marca = 'Marca é obrigatória';
    if (!formData.modelo.trim()) newErrors.modelo = 'Modelo é obrigatório';
    
    if (!formData.ano.trim()) {
      newErrors.ano = 'Ano é obrigatório';
    } else if (isNaN(Number(formData.ano)) || Number(formData.ano) <= 0) {
      newErrors.ano = 'Ano deve ser um número válido';
    }
    
    if (!formData.placa.trim()) newErrors.placa = 'Placa é obrigatória';
    if (!formData.cor.trim()) newErrors.cor = 'Cor é obrigatória';
    
    if (!formData.clienteId.trim()) {
      newErrors.clienteId = 'Cliente é obrigatório';
    } else if (isNaN(Number(formData.clienteId)) || Number(formData.clienteId) <= 0) {
      newErrors.clienteId = 'ID do cliente deve ser um número válido';
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!validateForm()) return;

    setIsLoading(true);
    
    const dadosParaEnviar = {
      ...formData,
      ano: Number(formData.ano),
      clienteId: Number(formData.clienteId),
    };

    // Log apenas em desenvolvimento
    if (process.env.NODE_ENV === 'development') {
      console.log('Dados enviados na requisição:', dadosParaEnviar);
    }

    const url = veiculoEditando
      ? `http://localhost:8080/api/veiculos/editar/${veiculoEditando.id}`
      : 'http://localhost:8080/api/veiculos/adicionar';
    const method = veiculoEditando ? 'PUT' : 'POST';

    try {
      const response = await fetch(url, {
        method,
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(dadosParaEnviar),
      });
      console.log(`Enviando dados para ${url}:`, dadosParaEnviar);

      const result = await response.json();

      if (response.ok) {
        onSubmit(result, veiculoEditando?.id);
        toast({
          title: 'Sucesso!',
          description: veiculoEditando
            ? 'Veículo atualizado com sucesso.'
            : 'Veículo adicionado com sucesso.',
        });

        if (!veiculoEditando) {
          setFormData({
            marca: '',
            modelo: '',
            ano: '',
            placa: '',
            cor: '',
            clienteId: '',
          });
        }
      } else {
        toast({
          title: 'Erro',
          description: result?.message || 'Erro ao salvar veículo.',
          variant: 'destructive',
        });
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

  useEffect(() => {
    if (veiculoEditando) {
      setFormData({
        marca: veiculoEditando.marca,
        modelo: veiculoEditando.modelo,
        ano: veiculoEditando.ano.toString(),
        placa: veiculoEditando.placa,
        cor: veiculoEditando.cor,
        clienteId: veiculoEditando.clienteId.toString(),
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
        { id: 'marca', label: 'Marca', type: 'text' },
        { id: 'modelo', label: 'Modelo', type: 'text' },
        { id: 'ano', label: 'Ano', type: 'number' },
        { id: 'placa', label: 'Placa', type: 'text' },
        { id: 'cor', label: 'Cor', type: 'text' },
        { id: 'clienteId', label: 'ID do Cliente', type: 'number' },
      ].map(({ id, label, type }) => (
        <div key={id} className="space-y-2">
          <Label htmlFor={id}>{label} *</Label>
          <Input
            id={id}
            type={type}
            value={formData[id as keyof VeiculoFormData]}
            onChange={(e) => handleInputChange(id as keyof VeiculoFormData, e.target.value)}
            placeholder={`Digite ${label.toLowerCase()}`}
            className={errors[id as keyof VeiculoFormData] ? 'border-destructive' : ''}
          />
          {errors[id as keyof VeiculoFormData] && (
            <p className="text-sm text-destructive">{errors[id as keyof VeiculoFormData]}</p>
          )}
        </div>
      ))}

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