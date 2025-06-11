
import React, { useState } from 'react';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { useToast } from '@/hooks/use-toast';

interface ClienteFormData {
  nome: string;
  email: string;
  senha: string;
  telefone: string;
  cpf: string;
}

interface ClienteFormProps {
  onSubmit: (cliente: ClienteFormData) => void;
}

const ClienteForm: React.FC<ClienteFormProps> = ({ onSubmit }) => {
  const { toast } = useToast();
  const [formData, setFormData] = useState<ClienteFormData>({
    nome: '',
    email: '',
    senha: '',
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

    if (!formData.senha.trim()) {
      newErrors.senha = 'Senha é obrigatória';
    } else if (formData.senha.length < 6) {
      newErrors.senha = 'Senha deve ter pelo menos 6 caracteres';
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
      
      try {
        const response = await fetch('http://localhost:8080/api/clientes', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
          },
          body: JSON.stringify(formData),
        });

        if (response.ok) {
          const novoCliente = await response.json();
          onSubmit(novoCliente);
          
          // Limpar formulário
          setFormData({
            nome: '',
            email: '',
            senha: '',
            telefone: '',
            cpf: '',
          });

          toast({
            title: "Sucesso!",
            description: "Cliente adicionado com sucesso.",
          });
        } else {
          const errorData = await response.json();
          toast({
            title: "Erro",
            description: errorData.message || "Erro ao adicionar cliente.",
            variant: "destructive",
          });
        }
      } catch (error) {
        console.error('Erro ao enviar dados:', error);
        toast({
          title: "Erro",
          description: "Erro de conexão com o servidor.",
          variant: "destructive",
        });
      } finally {
        setIsLoading(false);
      }
    }
  };

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
        <Label htmlFor="senha">Senha *</Label>
        <Input
          id="senha"
          type="password"
          value={formData.senha}
          onChange={(e) => handleInputChange('senha', e.target.value)}
          placeholder="Digite a senha (mín. 6 caracteres)"
          className={errors.senha ? 'border-destructive' : ''}
        />
        {errors.senha && <p className="text-sm text-destructive">{errors.senha}</p>}
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
          {isLoading ? 'Salvando...' : 'Adicionar Cliente'}
        </Button>
      </div>
    </form>
  );
};

export default ClienteForm;