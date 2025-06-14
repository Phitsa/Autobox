import React, { useState, useEffect } from 'react';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Textarea } from '@/components/ui/textarea';
import { TypeCategoria } from '../types/TypeCategoria';

interface CategoriaFormProps {
  onSubmit: (categoria: Omit<TypeCategoria, 'id' | 'createdAt' | 'updatedAt'>) => void;
  initialData?: TypeCategoria | null;
}

const CategoriaForm: React.FC<CategoriaFormProps> = ({ onSubmit, initialData }) => {
  const [formData, setFormData] = useState({
    nome: '',
    descricao: ''
  });

  useEffect(() => {
    if (initialData) {
      setFormData({
        nome: initialData.nome,
        descricao: initialData.descricao || ''
      });
    }
  }, [initialData]);

  const handleSubmit = () => {
    onSubmit(formData);
  };

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
  };

  return (
    <div className="space-y-4">
      <div className="space-y-2">
        <Label htmlFor="nome">Nome da Categoria</Label>
        <Input
          id="nome"
          name="nome"
          type="text"
          value={formData.nome}
          onChange={handleChange}
          placeholder="Ex: Lavagem, Enceramento, Pintura..."
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
          placeholder="Descreva os serviços incluídos nesta categoria..."
          className="min-h-[100px]"
        />
      </div>

      <Button onClick={handleSubmit} className="w-full">
        {initialData ? 'Atualizar Categoria' : 'Criar Categoria'}
      </Button>
    </div>
  );
};

export default CategoriaForm;