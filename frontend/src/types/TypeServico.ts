export interface TypeServico {
  id: number;
  categoriaId: number;
  nome: string;
  descricao: string;
  preco: number;
  duracaoEstimada: string; // formato "HH:mm"
  ativo: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface TypeCategoria {
  id: number;
  nome: string;
  descricao: string;
  createdAt: string;
  updatedAt: string;
}