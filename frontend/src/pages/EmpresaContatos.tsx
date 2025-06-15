import React, { useEffect, useState } from 'react';
import {
    ArrowLeft,
    Phone,
    Save,
    AlertCircle,
    Plus,
    Trash2,
    CheckCircle,
    Edit,
    Star,
    Mail,
    MessageCircle,
    Smartphone,
    Printer
} from 'lucide-react';
import { useNavigate } from 'react-router-dom';

// Tipos corrigidos para corresponder ao backend
interface EmpresaContato {
    id?: number;
    empresaId: number;
    tipoContato: TipoContato;
    nomeTipoContato?: string;
    valor: string;
    valorFormatado?: string;
    descricao?: string;
    principal: boolean;
    ativo: boolean;
    createdAt?: string;
    updatedAt?: string;
}

interface Empresa {
    id: number;
    nomeFantasia: string;
}

// Enum corrigido para corresponder EXATAMENTE ao backend Java
enum TipoContato {
    TELEFONE = 'TELEFONE',
    CELULAR = 'CELULAR',
    WHATSAPP = 'WHATSAPP',
    EMAIL = 'EMAIL',
    FAX = 'FAX'
}

const tiposContato = [
    { value: TipoContato.TELEFONE, label: 'Telefone', icon: Phone },
    { value: TipoContato.CELULAR, label: 'Celular', icon: Smartphone },
    { value: TipoContato.WHATSAPP, label: 'WhatsApp', icon: MessageCircle },
    { value: TipoContato.EMAIL, label: 'E-mail', icon: Mail },
    { value: TipoContato.FAX, label: 'Fax', icon: Printer }
];

// Mock data como fallback
const mockEmpresa: Empresa = {
    id: 1,
    nomeFantasia: "BoxPro Lavagem"
};

const mockContatos: EmpresaContato[] = [
    {
        id: 1,
        empresaId: 1,
        tipoContato: TipoContato.TELEFONE,
        nomeTipoContato: 'Telefone',
        valor: '8433334444',
        valorFormatado: '(84) 3333-4444',
        descricao: 'Comercial',
        principal: true,
        ativo: true
    },
    {
        id: 2,
        empresaId: 1,
        tipoContato: TipoContato.CELULAR,
        nomeTipoContato: 'Celular',
        valor: '84999887766',
        valorFormatado: '(84) 99988-7766',
        descricao: 'Atendimento',
        principal: true,
        ativo: true
    },
    {
        id: 3,
        empresaId: 1,
        tipoContato: TipoContato.EMAIL,
        nomeTipoContato: 'E-mail',
        valor: 'contato@boxpro.com',
        valorFormatado: 'contato@boxpro.com',
        descricao: 'Principal',
        principal: true,
        ativo: true
    }
];

const EmpresaContatos = () => {
    const [empresa, setEmpresa] = useState<Empresa | null>(null);
    const [contatos, setContatos] = useState<EmpresaContato[]>([]);
    const [novoContato, setNovoContato] = useState<Partial<EmpresaContato>>({
        tipoContato: TipoContato.TELEFONE,
        valor: '',
        descricao: '',
        principal: false,
        ativo: true
    });
    const navigate = useNavigate();
    const [editandoId, setEditandoId] = useState<number | null>(null);
    const [loading, setLoading] = useState<boolean>(true);
    const [saving, setSaving] = useState<boolean>(false);
    const [erro, setErro] = useState<string | null>(null);
    const [sucesso, setSucesso] = useState<string | null>(null);
    const [usingMockData, setUsingMockData] = useState(false);

    // Função para testar conexão com o backend
    const testarConexao = async () => {
        try {
            const response = await fetch('http://localhost:8080/api/empresa-contatos/status');
            return response.ok;
        } catch (error) {
            return false;
        }
    };

    // Headers para requisições
    const getHeaders = () => {
        const token = localStorage.getItem('boxpro_token');
        const headers: HeadersInit = {
            'Content-Type': 'application/json'
        };

        if (token) {
            headers['Authorization'] = `Bearer ${token}`;
        }
        return headers;
    };

    // Buscar dados da empresa e contatos
    useEffect(() => {
        const buscarDados = async () => {
            try {
                setLoading(true);
                setErro(null);

                // Primeiro testa se o backend está rodando
                const backendOnline = await testarConexao();

                if (!backendOnline) {
                    console.warn("Backend não está disponível, usando dados mock");
                    setEmpresa(mockEmpresa);
                    setContatos(mockContatos);
                    setUsingMockData(true);
                    setErro("⚠️ Usando dados de demonstração - Backend não conectado");
                    return;
                }

                const headers = getHeaders();

                // Buscar dados da empresa
                const empresaResponse = await fetch('http://localhost:8080/api/empresa', { headers });

                if (!empresaResponse.ok) {
                    throw new Error('Empresa não encontrada. Configure os dados da empresa primeiro.');
                }

                const empresaData = await empresaResponse.json();
                const empresaInfo = {
                    id: empresaData.id,
                    nomeFantasia: empresaData.nomeFantasia || empresaData.nome_fantasia
                };
                setEmpresa(empresaInfo);

                // Buscar contatos existentes
                const contatosResponse = await fetch(`http://localhost:8080/api/empresa-contatos/empresa/${empresaData.id}`, { headers });

                if (contatosResponse.ok) {
                    const contatosData = await contatosResponse.json();
                    console.log('Contatos recebidos:', contatosData); // Debug
                    setContatos(contatosData);
                } else {
                    console.warn('Erro ao buscar contatos:', contatosResponse.status);
                    setContatos([]);
                }

                setUsingMockData(false);

            } catch (error) {
                console.error("Erro ao carregar dados:", error);
                setEmpresa(mockEmpresa);
                setContatos(mockContatos);
                setUsingMockData(true);
                setErro("⚠️ Erro de conexão - Usando dados de demonstração");
            } finally {
                setLoading(false);
            }
        };

        buscarDados();
    }, []);

    const handleSalvarContato = async () => {
        if (!empresa || !novoContato.tipoContato || !novoContato.valor) {
            setErro("Por favor, preencha todos os campos obrigatórios");
            return;
        }

        try {
            setSaving(true);
            setErro(null);

            if (usingMockData) {
                // Modo mock - simula salvamento
                const novoId = Math.max(...contatos.map(c => c.id || 0)) + 1;
                const contatoCompleto: EmpresaContato = {
                    id: novoId,
                    empresaId: empresa.id,
                    tipoContato: novoContato.tipoContato!,
                    nomeTipoContato: tiposContato.find(t => t.value === novoContato.tipoContato)?.label,
                    valor: novoContato.valor!,
                    valorFormatado: formatarValor(novoContato.valor!, novoContato.tipoContato!),
                    descricao: novoContato.descricao || '',
                    principal: novoContato.principal || false,
                    ativo: true
                };

                setContatos(prev => [...prev, contatoCompleto]);
                setNovoContato({
                    tipoContato: TipoContato.TELEFONE,
                    valor: '',
                    descricao: '',
                    principal: false,
                    ativo: true
                });
                setSucesso("Contato adicionado com sucesso!");
                return;
            }

            // Dados corrigidos para corresponder ao DTO do backend
            const dadosParaEnviar = {
                empresaId: empresa.id,
                tipoContato: novoContato.tipoContato, // Enviando enum como string maiúscula
                valor: novoContato.valor.trim(),
                descricao: novoContato.descricao?.trim() || null,
                principal: Boolean(novoContato.principal),
                ativo: true
            };

            console.log('Enviando dados:', dadosParaEnviar); // Debug

            const headers = getHeaders();

            let response;
            if (editandoId) {
                // Atualizar contato existente
                response = await fetch(`http://localhost:8080/api/empresa-contatos/${editandoId}`, {
                    method: 'PUT',
                    headers,
                    body: JSON.stringify(dadosParaEnviar),
                });
            } else {
                // Criar novo contato
                response = await fetch('http://localhost:8080/api/empresa-contatos', {
                    method: 'POST',
                    headers,
                    body: JSON.stringify(dadosParaEnviar),
                });
            }

            if (response.ok) {
                const data = await response.json();
                console.log('Resposta do servidor:', data); // Debug
                const contatoSalvo = data.contato;

                if (editandoId) {
                    setContatos(prev => prev.map(c => c.id === editandoId ? contatoSalvo : c));
                    setSucesso("Contato atualizado com sucesso!");
                } else {
                    setContatos(prev => [...prev, contatoSalvo]);
                    setSucesso("Contato adicionado com sucesso!");
                }

                // Limpar formulário
                setNovoContato({
                    tipoContato: TipoContato.TELEFONE,
                    valor: '',
                    descricao: '',
                    principal: false,
                    ativo: true
                });
                setEditandoId(null);
            } else {
                const errorText = await response.text();
                console.error('Erro da API:', errorText); // Debug
                let errorData;
                try {
                    errorData = JSON.parse(errorText);
                } catch {
                    errorData = { error: errorText };
                }
                throw new Error(errorData.error || `Erro HTTP ${response.status}`);
            }

        } catch (error: any) {
            console.error("Erro ao salvar contato:", error);
            setErro(`Erro ao salvar contato: ${error.message}`);
        } finally {
            setSaving(false);
        }
    };

    const handleEditarContato = (contato: EmpresaContato) => {
        setNovoContato({
            tipoContato: contato.tipoContato,
            valor: contato.valor,
            descricao: contato.descricao,
            principal: contato.principal,
            ativo: contato.ativo
        });
        setEditandoId(contato.id!);
    };

    const handleDeletarContato = async (id: number) => {
        if (!confirm('Tem certeza que deseja deletar este contato?')) return;

        try {
            if (usingMockData) {
                setContatos(prev => prev.filter(c => c.id !== id));
                setSucesso("Contato deletado com sucesso!");
                return;
            }

            const headers = getHeaders();

            const response = await fetch(`http://localhost:8080/api/empresa-contatos/${id}`, {
                method: 'DELETE',
                headers,
            });

            if (response.ok) {
                setContatos(prev => prev.filter(c => c.id !== id));
                setSucesso("Contato deletado com sucesso!");
            } else {
                const errorText = await response.text();
                console.error('Erro ao deletar:', errorText);
                throw new Error('Erro ao deletar contato');
            }

        } catch (error: any) {
            console.error("Erro ao deletar contato:", error);
            setErro(`Erro ao deletar contato: ${error.message}`);
        }
    };

    const handleDefinirPrincipal = async (id: number) => {
        try {
            if (usingMockData) {
                setContatos(prev => prev.map(c =>
                    c.id === id ? { ...c, principal: true } :
                        c.tipoContato === contatos.find(ct => ct.id === id)?.tipoContato ? { ...c, principal: false } : c
                ));
                setSucesso("Contato definido como principal!");
                return;
            }

            const headers = getHeaders();

            const response = await fetch(`http://localhost:8080/api/empresa-contatos/${id}/principal`, {
                method: 'PATCH',
                headers,
            });

            if (response.ok) {
                const data = await response.json();
                console.log('Resposta definir principal:', data);
                const contatoAtualizado = data.contato;

                // Atualizar lista local
                setContatos(prev => prev.map(c =>
                    c.id === id ? contatoAtualizado :
                        c.tipoContato === contatoAtualizado.tipoContato ? { ...c, principal: false } : c
                ));
                setSucesso("Contato definido como principal!");
            } else {
                const errorText = await response.text();
                console.error('Erro ao definir principal:', errorText);
                throw new Error('Erro ao definir contato como principal');
            }

        } catch (error: any) {
            console.error("Erro ao definir principal:", error);
            setErro(`Erro ao definir contato como principal: ${error.message}`);
        }
    };

    const cancelarEdicao = () => {
        setNovoContato({
            tipoContato: TipoContato.TELEFONE,
            valor: '',
            descricao: '',
            principal: false,
            ativo: true
        });
        setEditandoId(null);
    };

    const formatarValor = (valor: string, tipo: TipoContato) => {
        if (tipo === TipoContato.TELEFONE || tipo === TipoContato.CELULAR || tipo === TipoContato.WHATSAPP || tipo === TipoContato.FAX) {
            const numbers = valor.replace(/\D/g, '');
            if (numbers.length === 11) {
                return numbers.replace(/(\d{2})(\d{5})(\d{4})/, '($1) $2-$3');
            } else if (numbers.length === 10) {
                return numbers.replace(/(\d{2})(\d{4})(\d{4})/, '($1) $2-$3');
            }
        }
        return valor;
    };

    const handleInputChange = (field: keyof EmpresaContato, value: string | boolean | TipoContato) => {
        setNovoContato(prev => ({
            ...prev,
            [field]: value
        }));
    };

    const getIconeContato = (tipo: TipoContato) => {
        const tipoInfo = tiposContato.find(t => t.value === tipo);
        return tipoInfo?.icon || Phone;
    };

    const handleVoltarConfiguracoes = () => {
        navigate('/configuracoes');
    };

    // Auto-clear messages
    useEffect(() => {
        if (sucesso) {
            const timer = setTimeout(() => setSucesso(null), 5000);
            return () => clearTimeout(timer);
        }
    }, [sucesso]);

    useEffect(() => {
        if (erro && !erro.includes('demonstração')) {
            const timer = setTimeout(() => setErro(null), 10000);
            return () => clearTimeout(timer);
        }
    }, [erro]);

    if (loading) {
        return (
            <div className="flex justify-center items-center min-h-screen">
                <div className="text-center">
                    <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600 mx-auto mb-4"></div>
                    <p>Carregando contatos...</p>
                </div>
            </div>
        );
    }

    return (
        <div className="min-h-screen bg-gray-50 p-6">
            <div className="max-w-6xl mx-auto">
                {/* Alerta sobre modo mock */}
                {usingMockData && (
                    <div className="mb-6 p-4 bg-yellow-50 border border-yellow-200 rounded-md flex items-center gap-3">
                        <AlertCircle className="w-5 h-5 text-yellow-600" />
                        <div>
                            <p className="text-yellow-800 font-medium">Modo Demonstração</p>
                            <p className="text-yellow-700 text-sm">
                                Backend não conectado. Verifique se o servidor Spring Boot está rodando em http://localhost:8080
                            </p>
                        </div>
                    </div>
                )}

                {/* Header */}
                <div className="flex items-center justify-between mb-8">
                    <div className="flex items-center gap-3">
                        <button
                            onClick={handleVoltarConfiguracoes}
                            className="mr-2 p-2 hover:bg-gray-100 rounded-lg transition-colors"
                        >
                            <ArrowLeft className="w-5 h-5" />
                        </button>
                        <div className="w-12 h-12 bg-blue-100 rounded-lg flex items-center justify-center">
                            <Phone className="w-6 h-6 text-blue-600" />
                        </div>
                        <div>
                            <h1 className="text-3xl font-bold text-gray-900">Contatos da Empresa</h1>
                            <p className="text-gray-600">
                                {empresa?.nomeFantasia} - Gerencie telefones, emails e contatos
                            </p>
                        </div>
                    </div>
                </div>

                {/* Alertas de feedback */}
                {erro && (
                    <div className="mb-6 p-4 bg-red-50 border border-red-200 rounded-md flex items-center gap-3">
                        <AlertCircle className="w-5 h-5 text-red-600" />
                        <p className="text-red-800">{erro}</p>
                        <button
                            onClick={() => setErro(null)}
                            className="ml-auto text-red-600 hover:text-red-800"
                        >
                            ×
                        </button>
                    </div>
                )}

                {sucesso && (
                    <div className="mb-6 p-4 bg-green-50 border border-green-200 rounded-md flex items-center gap-3">
                        <CheckCircle className="w-5 h-5 text-green-600" />
                        <p className="text-green-800">{sucesso}</p>
                        <button
                            onClick={() => setSucesso(null)}
                            className="ml-auto text-green-600 hover:text-green-800"
                        >
                            ×
                        </button>
                    </div>
                )}

                <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
                    {/* Formulário de Novo Contato */}
                    <div className="lg:col-span-1">
                        <div className="bg-white rounded-lg shadow p-6">
                            <div className="flex items-center gap-2 mb-4">
                                <Plus className="w-5 h-5" />
                                <h2 className="text-xl font-semibold">
                                    {editandoId ? 'Editar Contato' : 'Adicionar Contato'}
                                </h2>
                            </div>

                            <div className="space-y-4">
                                <div>
                                    <label className="block text-sm font-medium mb-1">Tipo de Contato *</label>
                                    <select
                                        className="w-full p-3 border rounded-md focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                                        value={novoContato.tipoContato}
                                        onChange={(e) => handleInputChange('tipoContato', e.target.value as TipoContato)}
                                    >
                                        {tiposContato.map(tipo => (
                                            <option key={tipo.value} value={tipo.value}>{tipo.label}</option>
                                        ))}
                                    </select>
                                </div>

                                <div>
                                    <label className="block text-sm font-medium mb-1">Valor *</label>
                                    <input
                                        type="text"
                                        required
                                        className="w-full p-3 border rounded-md focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                                        value={novoContato.valor || ''}
                                        onChange={(e) => handleInputChange('valor', e.target.value)}
                                        placeholder={
                                            novoContato.tipoContato === TipoContato.EMAIL
                                                ? 'exemplo@empresa.com'
                                                : '(84) 99999-9999'
                                        }
                                    />
                                </div>

                                <div>
                                    <label className="block text-sm font-medium mb-1">Descrição</label>
                                    <input
                                        type="text"
                                        className="w-full p-3 border rounded-md focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                                        value={novoContato.descricao || ''}
                                        onChange={(e) => handleInputChange('descricao', e.target.value)}
                                        placeholder="Ex: Comercial, Atendimento, Emergência"
                                        maxLength={100}
                                    />
                                </div>

                                <div className="flex items-center gap-2">
                                    <input
                                        type="checkbox"
                                        id="principal"
                                        checked={novoContato.principal || false}
                                        onChange={(e) => handleInputChange('principal', e.target.checked)}
                                        className="rounded"
                                    />
                                    <label htmlFor="principal" className="text-sm">
                                        Definir como contato principal deste tipo
                                    </label>
                                </div>

                                <div className="flex gap-2">
                                    {editandoId && (
                                        <button
                                            type="button"
                                            onClick={cancelarEdicao}
                                            className="flex-1 px-4 py-2 border border-gray-300 rounded-md hover:bg-gray-50 transition-colors"
                                        >
                                            Cancelar
                                        </button>
                                    )}
                                    <button
                                        onClick={handleSalvarContato}
                                        disabled={saving || !novoContato.valor}
                                        className="flex-1 px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
                                    >
                                        {saving ? (
                                            <div className="flex items-center gap-2 justify-center">
                                                <div className="animate-spin rounded-full h-4 w-4 border-b-2 border-white"></div>
                                                Salvando...
                                            </div>
                                        ) : (
                                            <div className="flex items-center gap-2 justify-center">
                                                <Save className="w-4 h-4" />
                                                {editandoId ? 'Atualizar' : 'Adicionar'}
                                            </div>
                                        )}
                                    </button>
                                </div>
                            </div>
                        </div>
                    </div>

                    {/* Lista de Contatos */}
                    <div className="lg:col-span-2">
                        <div className="bg-white rounded-lg shadow p-6">
                            <div className="flex items-center justify-between mb-4">
                                <div className="flex items-center gap-2">
                                    <Phone className="w-5 h-5" />
                                    <h2 className="text-xl font-semibold">Contatos Cadastrados</h2>
                                </div>
                                <span className="text-sm text-gray-600">
                                    {contatos.length} contato(s)
                                </span>
                            </div>

                            {contatos.length === 0 ? (
                                <div className="text-center py-8 text-gray-500">
                                    <Phone className="w-12 h-12 mx-auto mb-4 opacity-50" />
                                    <p className="text-lg mb-2">Nenhum contato cadastrado</p>
                                    <p className="text-sm">Adicione o primeiro contato da empresa</p>
                                </div>
                            ) : (
                                <div className="space-y-4">
                                    {contatos.map((contato) => {
                                        const IconeContato = getIconeContato(contato.tipoContato);

                                        return (
                                            <div key={contato.id} className="p-4 border rounded-lg hover:bg-gray-50 transition-colors">
                                                <div className="flex items-center justify-between">
                                                    <div className="flex items-center gap-3">
                                                        <div className={`w-10 h-10 rounded-lg flex items-center justify-center ${
                                                            contato.tipoContato === TipoContato.TELEFONE ? 'bg-blue-100' :
                                                            contato.tipoContato === TipoContato.CELULAR ? 'bg-green-100' :
                                                            contato.tipoContato === TipoContato.WHATSAPP ? 'bg-green-100' :
                                                            contato.tipoContato === TipoContato.EMAIL ? 'bg-purple-100' :
                                                            'bg-gray-100'
                                                        }`}>
                                                            <IconeContato className={`w-5 h-5 ${
                                                                contato.tipoContato === TipoContato.TELEFONE ? 'text-blue-600' :
                                                                contato.tipoContato === TipoContato.CELULAR ? 'text-green-600' :
                                                                contato.tipoContato === TipoContato.WHATSAPP ? 'text-green-600' :
                                                                contato.tipoContato === TipoContato.EMAIL ? 'text-purple-600' :
                                                                'text-gray-600'
                                                            }`} />
                                                        </div>

                                                        <div className="flex-1">
                                                            <div className="flex items-center gap-2">
                                                                <span className="font-medium">
                                                                    {contato.valorFormatado || contato.valor}
                                                                </span>
                                                                {contato.principal && (
                                                                    <Star className="w-4 h-4 text-yellow-500 fill-current" />
                                                                )}
                                                            </div>
                                                            <div className="flex items-center gap-2 text-sm text-gray-600">
                                                                <span>{contato.nomeTipoContato || contato.tipoContato}</span>
                                                                {contato.descricao && (
                                                                    <>
                                                                        <span>•</span>
                                                                        <span>{contato.descricao}</span>
                                                                    </>
                                                                )}
                                                                {contato.principal && (
                                                                    <>
                                                                        <span>•</span>
                                                                        <span className="text-yellow-600 font-medium">Principal</span>
                                                                    </>
                                                                )}
                                                            </div>
                                                        </div>
                                                    </div>

                                                    <div className="flex items-center gap-2">
                                                        {!contato.principal && (
                                                            <button
                                                                onClick={() => handleDefinirPrincipal(contato.id!)}
                                                                className="p-1 hover:bg-gray-100 rounded"
                                                                title="Definir como principal"
                                                            >
                                                                <Star className="w-4 h-4" />
                                                            </button>
                                                        )}
                                                        <button
                                                            onClick={() => handleEditarContato(contato)}
                                                            className="p-1 hover:bg-gray-100 rounded"
                                                            title="Editar contato"
                                                        >
                                                            <Edit className="w-4 h-4" />
                                                        </button>
                                                        <button
                                                            onClick={() => handleDeletarContato(contato.id!)}
                                                            className="p-1 hover:bg-red-50 text-red-600 rounded"
                                                            title="Deletar contato"
                                                        >
                                                            <Trash2 className="w-4 h-4" />
                                                        </button>
                                                    </div>
                                                </div>
                                            </div>
                                        );
                                    })}
                                </div>
                            )}
                        </div>
                    </div>
                </div>

                {/* Resumo dos Contatos por Tipo */}
                {contatos.length > 0 && (
                    <div className="mt-8 bg-white rounded-lg shadow p-6">
                        <h3 className="text-lg font-semibold mb-4">Resumo dos Contatos</h3>
                        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-5 gap-4">
                            {tiposContato.map(tipo => {
                                const contatosTipo = contatos.filter(c => c.tipoContato === tipo.value);
                                const principal = contatosTipo.find(c => c.principal);
                                const Icone = tipo.icon;

                                return (
                                    <div key={tipo.value} className="p-4 border rounded-lg">
                                        <div className="flex items-center gap-2 mb-2">
                                            <Icone className="w-5 h-5 text-gray-600" />
                                            <span className="font-medium text-sm">{tipo.label}</span>
                                        </div>

                                        {contatosTipo.length === 0 ? (
                                            <p className="text-sm text-gray-500">Nenhum cadastrado</p>
                                        ) : (
                                            <div className="space-y-1">
                                                <p className="text-sm">
                                                    <span className="font-medium">{contatosTipo.length}</span> cadastrado(s)
                                                </p>
                                                {principal && (
                                                    <p className="text-xs text-gray-500">
                                                        Principal: {principal.valorFormatado || principal.valor}
                                                    </p>
                                                )}
                                            </div>
                                        )}
                                    </div>
                                );
                            })}
                        </div>
                    </div>
                )}
            </div>
        </div>
    );
};

export default EmpresaContatos;