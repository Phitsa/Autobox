
import React, { useState, useEffect } from 'react';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { useToast } from '@/hooks/use-toast';
import { TypeCliente } from '@/types/TypeCliente';

interface ClienteFormData {
  nome: string;
  email: string;
  telefone: string;
  cpf: string;
}

interface ClienteFormProps {
  onSubmit: (cliente: ClienteFormData, id?: number) => void;
  clienteEditando?: TypeCliente | null;
}


const ClienteForm: React.FC<ClienteFormProps> = ({ onSubmit, clienteEditando }) => {
  const { toast } = useToast();
  const [formData, setFormData] = useState<ClienteFormData>({
    nome: '',
    email: '',
    telefone: '',
    cpf: '',
  });

  const [errors, setErrors] = useState<Partial<ClienteFormData>>({});
  const [isLoading, setIsLoading] = useState(false);

  const handleInputChange = (field: keyof ClienteFormData, value: string) => {
    setFormData(prev => ({ ...prev, [field]: value }));
    // Limpar erro quando o usuário começar a digitar
    if (errors[field]) {
      setErrors(prev => ({ ...prev, [field]: '' }));
    }
  };

  const validateForm = (): boolean => {
    const newErrors: Partial<ClienteFormData> = {};

    if (!formData.nome.trim()) {
      newErrors.nome = 'Nome é obrigatório';
    }

    if (!formData.email.trim()) {
      newErrors.email = 'Email é obrigatório';
    } else if (!/\S+@\S+\.\S+/.test(formData.email)) {
      newErrors.email = 'Email inválido';
    }

    if (!formData.telefone.trim()) {
      newErrors.telefone = 'Telefone é obrigatório';
    }

    if (!formData.cpf.trim()) {
      newErrors.cpf = 'CPF é obrigatório';
    } else if (!/^\d{3}\.\d{3}\.\d{3}-\d{2}$/.test(formData.cpf) && !/^\d{11}$/.test(formData.cpf)) {
      newErrors.cpf = 'CPF inválido';
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (validateForm()) {
      setIsLoading(true);
      const url = clienteEditando
        ? `http://localhost:8080/api/clientes/editar/${clienteEditando.id}`
        : 'http://localhost:8080/api/clientes';
      console.log(url)
      const method = clienteEditando ? 'PUT' : 'POST';

      try {
        const response = await fetch(url, {
          method,
          headers: {
            'Content-Type': 'application/json',
          },
          body: JSON.stringify(formData),
        });

        const cliente = await response.json();

        if (response.ok) {
          onSubmit(cliente, clienteEditando?.id);

          toast({
            title: 'Sucesso!',
            description: clienteEditando
              ? 'Cliente atualizado com sucesso.'
              : 'Cliente adicionado com sucesso.',
          });

          if (!clienteEditando) {
            setFormData({
              nome: '',
              email: '',
              telefone: '',
              cpf: '',
            });
          }
        } else {
          toast({
            title: 'Erro',
            description: cliente.message || 'Erro ao salvar cliente.',
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
    }
  };


  useEffect(() => {
    if (clienteEditando) {
      setFormData({
        nome: clienteEditando.nome,
        email: clienteEditando.email,
        telefone: clienteEditando.telefone,
        cpf: clienteEditando.cpf,
      });
    } else {
      setFormData({
        nome: '',
        email: '',
        telefone: '',
        cpf: '',
      });
    }
  }, [clienteEditando]);

  return (
    <form onSubmit={handleSubmit} className="space-y-4">
      <div className="space-y-2">
        <Label htmlFor="nome">Nome *</Label>
        <Input
          id="nome"
          type="text"
          value={formData.nome}
          onChange={(e) => handleInputChange('nome', e.target.value)}
          placeholder="Digite o nome completo"
          className={errors.nome ? 'border-destructive' : ''}
        />
        {errors.nome && <p className="text-sm text-destructive">{errors.nome}</p>}
      </div>

      <div className="space-y-2">
        <Label htmlFor="email">Email *</Label>
        <Input
          id="email"
          type="email"
          value={formData.email}
          onChange={(e) => handleInputChange('email', e.target.value)}
          placeholder="Digite o email"
          className={errors.email ? 'border-destructive' : ''}
        />
        {errors.email && <p className="text-sm text-destructive">{errors.email}</p>}
      </div>

      <div className="space-y-2">
        <Label htmlFor="telefone">Telefone *</Label>
        <Input
          id="telefone"
          type="tel"
          value={formData.telefone}
          onChange={(e) => handleInputChange('telefone', e.target.value)}
          placeholder="(11) 99999-9999"
          className={errors.telefone ? 'border-destructive' : ''}
        />
        {errors.telefone && <p className="text-sm text-destructive">{errors.telefone}</p>}
      </div>

      <div className="space-y-2">
        <Label htmlFor="cpf">CPF *</Label>
        <Input
          id="cpf"
          type="text"
          value={formData.cpf}
          onChange={(e) => handleInputChange('cpf', e.target.value)}
          placeholder="000.000.000-00"
          className={errors.cpf ? 'border-destructive' : ''}
        />
        {errors.cpf && <p className="text-sm text-destructive">{errors.cpf}</p>}
      </div>

      <div className="flex gap-3 pt-4">
        <Button type="submit" className="flex-1" disabled={isLoading}>
          {isLoading
            ? 'Salvando...'
            : clienteEditando
            ? 'Salvar Alterações'
            : 'Adicionar Cliente'}
        </Button>
      </div>
    </form>
  );
};

export default ClienteForm;