import React, { useState, useEffect } from 'react';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Textarea } from '@/components/ui/textarea';
import { TypeServico, TypeCategoria } from '../types/TypeServico';

interface ServicoFormProps {
  onSubmit: (servico: Omit<TypeServico, 'id' | 'createdAt' | 'updatedAt'>) => void;
  initialData?: TypeServico | null;
  categorias?: TypeCategoria[];
}

const ServicoForm: React.FC<ServicoFormProps> = ({ onSubmit, initialData, categorias = [] }) => {
  const [formData, setFormData] = useState({
    categoriaId: 0,
    nome: '',
    descricao: '',
    preco: 0,
    duracaoEstimada: '01:00',
    ativo: true
  });

  useEffect(() => {
    if (initialData) {
      setFormData({
        categoriaId: initialData.categoriaId,
        nome: initialData.nome,
        descricao: initialData.descricao || '',
        preco: initialData.preco,
        duracaoEstimada: initialData.duracaoEstimada || '01:00',
        ativo: initialData.ativo
      });
    }
  }, [initialData]);

  const handleSubmit = () => {
    if (!formData.nome || !formData.preco || !formData.categoriaId) {
      alert('Preencha todos os campos obrigatórios');
      return;
    }
    onSubmit(formData);
  };

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>) => {
    const { name, value, type } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: type === 'number' ? parseFloat(value) || 0 : 
               type === 'checkbox' ? (e.target as HTMLInputElement).checked : 
               value
    }));
  };

  return (
    <div className="space-y-4">
      <div className="space-y-2">
        <Label htmlFor="categoriaId">Categoria *</Label>
        <select
          id="categoriaId"
          name="categoriaId"
          value={formData.categoriaId}
          onChange={handleChange}
          className="w-full p-2 border rounded-md"
          required
        >
          <option value={0}>Selecione uma categoria</option>
          {categorias.map(categoria => (
            <option key={categoria.id} value={categoria.id}>
              {categoria.nome}
            </option>
          ))}
        </select>
      </div>

      <div className="space-y-2">
        <Label htmlFor="nome">Nome do Serviço *</Label>
        <Input
          id="nome"
          name="nome"
          type="text"
          value={formData.nome}
          onChange={handleChange}
          placeholder="Ex: Lavagem simples, Enceramento premium..."
          required
        />
      </div>

      <div className="space-y-2">
        <Label htmlFor="descricao">Descrição</Label>
        <Textarea
          id="descricao"
          name="descricao"
          value={formData.descricao}
          onChange={handleChange}
          placeholder="Descreva o que está incluído neste serviço..."
          className="min-h-[80px]"
        />
      </div>

      <div className="grid grid-cols-2 gap-4">
        <div className="space-y-2">
          <Label htmlFor="preco">Preço (R$) *</Label>
          <Input
            id="preco"
            name="preco"
            type="number"
            step="0.01"
            min="0"
            value={formData.preco}
            onChange={handleChange}
            placeholder="0.00"
            required
          />
        </div>

        <div className="space-y-2">
          <Label htmlFor="duracaoEstimada">Duração Estimada</Label>
          <Input
            id="duracaoEstimada"
            name="duracaoEstimada"
            type="time"
            value={formData.duracaoEstimada}
            onChange={handleChange}
          />
        </div>
      </div>

      <div className="flex items-center space-x-2">
        <input
          id="ativo"
          name="ativo"
          type="checkbox"
          checked={formData.ativo}
          onChange={handleChange}
          className="rounded"
        />
        <Label htmlFor="ativo">Serviço ativo</Label>
      </div>

      <Button onClick={handleSubmit} className="w-full">
        {initialData ? 'Atualizar Serviço' : 'Criar Serviço'}
      </Button>
    </div>
  );
};

export default ServicoForm;