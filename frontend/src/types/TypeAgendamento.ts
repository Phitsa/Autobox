// types/TypeAgendamento.ts
export interface TypeAgendamento {
  id: number;
  clienteId: number;
  veiculoId: number;
  servicoId: number;
  funcionarioResponsavelId?: number;
  dataAgendamento: string; // formato: YYYY-MM-DD
  horaInicio: string; // formato: HH:mm
  horaFim?: string; // formato: HH:mm
  status: string;
  observacoes?: string;
  valorTotal?: number;
  dataCancelamento?: string;
  motivoCancelamento?: string;
  taxaCancelamento?: number;
  createdAt: string;
  updatedAt: string;
}

export interface TypeCliente {
  id: number;
  nome: string;
  email: string;
  telefone?: string;
  cpf?: string;
  endereco?: string;
  createdAt: string;
  updatedAt: string;
}

export interface TypeVeiculo {
  id: number;
  clienteId: number;
  placa: string;
  marca: string;
  modelo: string;
  ano: number;
  cor?: string;
  observacoes?: string;
  createdAt: string;
  updatedAt: string;
  clienteNome?: string; // Para exibição
}

export interface TypeServico {
  id: number;
  nome: string;
  descricao?: string;
  preco: number;
  duracaoEstimada?: string;
  categoriaId: number;
  ativo: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface TypeFuncionario {
  id: number;
  nome: string;
  email: string;
  telefone?: string;
  cpf?: string;
  tipoFuncionario: 'ADMIN' | 'FUNCIONARIO';
  ativo: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface TypeHistoricoAgendamento {
  id: number;
  agendamentoId: number;
  funcionarioId: number;
  acao: string;
  detalhes?: string;
  dataAcao: string;
}

// Status possíveis para agendamentos
export const StatusAgendamento = {
  AGENDADO: 'agendado',
  EM_ANDAMENTO: 'em_andamento',
  CONCLUIDO: 'concluido',
  CANCELADO: 'cancelado'
} as const;

export type StatusAgendamentoType = typeof StatusAgendamento[keyof typeof StatusAgendamento];