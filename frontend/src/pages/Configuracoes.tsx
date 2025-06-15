import React, { useEffect, useState } from 'react';
import { Button } from '@/components/ui/button';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { useNavigate } from 'react-router-dom';
import { ArrowLeft, Settings, Save, AlertCircle, Building2, MapPin, FileText, Clock, Phone } from 'lucide-react';

// Tipos
interface TypeEmpresa {
  id?: number;
  nome_fantasia: string;
  razao_social: string;
  descricao?: string;
  cnpj?: string;
  inscricao_estadual?: string;
  inscricao_municipal?: string;
  endereco?: string;
  cep?: string;
  cidade?: string;
  estado?: string;
  numero?: string;
  complemento?: string;
  ativo: boolean;
  created_at?: string;
  updated_at?: string;
}

// Mock data como fallback
const mockEmpresa: TypeEmpresa = {
  id: 1,
  nome_fantasia: "BoxPro Lavagem",
  razao_social: "BoxPro Serviços Automotivos LTDA",
  descricao: "Lavagem e detalhamento automotivo com excelência e qualidade",
  cnpj: "12.345.678/0001-90",
  inscricao_estadual: "123456789",
  inscricao_municipal: "987654321",
  endereco: "Rua das Flores",
  cep: "59000-000",
  cidade: "Natal",
  estado: "RN",
  numero: "123",
  complemento: "Sala A",
  ativo: true,
  created_at: "2025-01-15T10:00:00Z",
  updated_at: "2025-01-15T10:00:00Z"
};

const Configuracoes = () => {
  const navigate = useNavigate();
  
  const [empresa, setEmpresa] = useState<TypeEmpresa>({
    nome_fantasia: '',
    razao_social: '',
    descricao: '',
    cnpj: '',
    inscricao_estadual: '',
    inscricao_municipal: '',
    endereco: '',
    cep: '',
    cidade: '',
    estado: '',
    numero: '',
    complemento: '',
    ativo: true
  });
  
  const [loading, setLoading] = useState<boolean>(true);
  const [saving, setSaving] = useState<boolean>(false);
  const [erro, setErro] = useState<string | null>(null);
  const [sucesso, setSucesso] = useState<string | null>(null);
  const [usingMockData, setUsingMockData] = useState(false);
  const [empresaExiste, setEmpresaExiste] = useState(false);

  // Função para testar conexão com o backend
  const testarConexao = async () => {
    try {
      const response = await fetch('http://localhost:8080/api/empresa/status');
      return response.ok;
    } catch (error) {
      return false;
    }
  };

  // Buscar dados da empresa - APENAS UMA VEZ
  useEffect(() => {
    const buscarEmpresa = async () => {
      try {
        setLoading(true);
        setErro(null);
        
        // Verificar se o usuário é ADMIN
        const userStr = localStorage.getItem('boxpro_user');
        if (userStr) {
          const user = JSON.parse(userStr);
          if (user.tipoFuncionario !== 'ADMIN') {
            navigate('/');
            return;
          }
        } else {
          navigate('/login');
          return;
        }

        // Primeiro testa se o backend está rodando
        const backendOnline = await testarConexao();
        
        if (!backendOnline) {
          console.warn("Backend não está disponível, usando dados mock");
          setEmpresa(mockEmpresa);
          setEmpresaExiste(true);
          setUsingMockData(true);
          setErro("⚠️ Usando dados de demonstração - Backend não conectado");
          return;
        }

        // Se backend estiver online, busca os dados reais
        const token = localStorage.getItem('boxpro_token');
        const headers: HeadersInit = {
          'Content-Type': 'application/json'
        };
        
        if (token) {
          headers['Authorization'] = `Bearer ${token}`;
        }

        const response = await fetch('http://localhost:8080/api/empresa', { headers });
        
        if (response.status === 404) {
          // Empresa não existe, mantém formulário vazio para criação
          setEmpresaExiste(false);
          setUsingMockData(false);
          return;
        }
        
        if (!response.ok) {
          throw new Error(`Erro HTTP: ${response.status}`);
        }
        
        const empresaData = await response.json();
        
        // Garantir que todos os campos sejam strings em vez de null
        // E mapear corretamente camelCase (backend) para snake_case (frontend)
        const empresaLimpa = {
          ...empresaData,
          nome_fantasia: empresaData.nomeFantasia || empresaData.nome_fantasia || '',
          razao_social: empresaData.razaoSocial || empresaData.razao_social || '',
          descricao: empresaData.descricao || '',
          cnpj: empresaData.cnpj || '',
          inscricao_estadual: empresaData.inscricaoEstadual || empresaData.inscricao_estadual || '',
          inscricao_municipal: empresaData.inscricaoMunicipal || empresaData.inscricao_municipal || '',
          endereco: empresaData.endereco || '',
          cep: empresaData.cep || '',
          cidade: empresaData.cidade || '',
          estado: empresaData.estado || '',
          numero: empresaData.numero || '',
          complemento: empresaData.complemento || '',
          ativo: empresaData.ativo !== undefined ? empresaData.ativo : true,
          id: empresaData.id
        };
        
        setEmpresa(empresaLimpa);
        setEmpresaExiste(true);
        setUsingMockData(false);
        
      } catch (error) {
        console.error("Erro ao carregar empresa:", error);
        // Em caso de erro, usa dados mock
        setEmpresa(mockEmpresa);
        setEmpresaExiste(true);
        setUsingMockData(true);
        setErro("⚠️ Erro de conexão - Usando dados de demonstração");
      } finally {
        setLoading(false);
      }
    };

    buscarEmpresa();
  }, []); // Array vazio - executa APENAS uma vez

  const handleSalvar = async (e: React.FormEvent) => {
    e.preventDefault();
    
    try {
      setSaving(true);
      setErro(null);
      setSucesso(null);

      if (usingMockData) {
        // Modo mock - simula salvamento
        setTimeout(() => {
          setSucesso(empresaExiste ? "Empresa atualizada com sucesso!" : "Empresa criada com sucesso!");
          setEmpresaExiste(true);
          setSaving(false);
        }, 1000);
        return;
      }

      // Preparar dados para envio
      const dadosParaEnviar = {
        nomeFantasia: empresa.nome_fantasia,
        razaoSocial: empresa.razao_social,
        descricao: empresa.descricao || null,
        cnpj: empresa.cnpj || null,
        inscricaoEstadual: empresa.inscricao_estadual || null,
        inscricaoMunicipal: empresa.inscricao_municipal || null,
        endereco: empresa.endereco || null,
        cep: empresa.cep || null,
        cidade: empresa.cidade || null,
        estado: empresa.estado || null,
        numero: empresa.numero || null,
        complemento: empresa.complemento || null,
        ativo: empresa.ativo
      };

      const token = localStorage.getItem('boxpro_token');
      const headers: HeadersInit = {
        'Content-Type': 'application/json'
      };
      
      if (token) {
        headers['Authorization'] = `Bearer ${token}`;
      }

      let response;
      let url;
      let method;
      
      if (empresaExiste && empresa.id) {
        // Atualizar empresa existente
        url = `http://localhost:8080/api/empresa/${empresa.id}`;
        method = 'PUT';
      } else {
        // Criar nova empresa
        url = 'http://localhost:8080/api/empresa';
        method = 'POST';
      }

      response = await fetch(url, {
        method,
        headers,
        body: JSON.stringify(dadosParaEnviar),
      });

      if (response.ok) {
        const responseData = await response.json();
        const empresaSalva = responseData.empresa || responseData;
        
        // Garantir que todos os campos sejam strings em vez de null
        const empresaLimpa = {
          ...empresaSalva,
          nome_fantasia: empresaSalva.nomeFantasia || empresaSalva.nome_fantasia || '',
          razao_social: empresaSalva.razaoSocial || empresaSalva.razao_social || '',
          descricao: empresaSalva.descricao || '',
          cnpj: empresaSalva.cnpj || '',
          inscricao_estadual: empresaSalva.inscricaoEstadual || empresaSalva.inscricao_estadual || '',
          inscricao_municipal: empresaSalva.inscricaoMunicipal || empresaSalva.inscricao_municipal || '',
          endereco: empresaSalva.endereco || '',
          cep: empresaSalva.cep || '',
          cidade: empresaSalva.cidade || '',
          estado: empresaSalva.estado || '',
          numero: empresaSalva.numero || '',
          complemento: empresaSalva.complemento || '',
          ativo: empresaSalva.ativo !== undefined ? empresaSalva.ativo : true,
          id: empresaSalva.id
        };
        
        setEmpresa(empresaLimpa);
        setEmpresaExiste(true);
        setSucesso(empresaExiste ? "Empresa atualizada com sucesso!" : "Empresa criada com sucesso!");
      } else {
        const errorText = await response.text();
        let errorData;
        try {
          errorData = JSON.parse(errorText);
        } catch (parseError) {
          errorData = { error: errorText };
        }
        
        throw new Error(errorData.error || errorData.message || `Erro ${response.status}: ${errorText}`);
      }
    } catch (error) {
      setErro(`Erro ao salvar empresa: ${error.message}`);
    } finally {
      setSaving(false);
    }
  };

  const handleInputChange = (field: keyof TypeEmpresa, value: string | boolean) => {
    setEmpresa(prev => ({
      ...prev,
      [field]: value
    }));
  };

  const handleVoltarAdmin = () => {
    navigate('/admin');
  };

  const formatarCNPJ = (value: string) => {
    const numbers = value.replace(/\D/g, '');
    return numbers.replace(/(\d{2})(\d{3})(\d{3})(\d{4})(\d{2})/, '$1.$2.$3/$4-$5');
  };

  const formatarCEP = (value: string) => {
    const numbers = value.replace(/\D/g, '');
    return numbers.replace(/(\d{5})(\d{3})/, '$1-$2');
  };

  const estados = [
    'AC', 'AL', 'AP', 'AM', 'BA', 'CE', 'DF', 'ES', 'GO', 'MA',
    'MT', 'MS', 'MG', 'PA', 'PB', 'PR', 'PE', 'PI', 'RJ', 'RN',
    'RS', 'RO', 'RR', 'SC', 'SP', 'SE', 'TO'
  ];

  if (loading) {
    return (
      <div className="flex justify-center items-center min-h-screen">
        <div className="text-center">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary mx-auto mb-4"></div>
          <p>Carregando configurações...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-background p-6">
      <div className="max-w-4xl mx-auto">
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
            <Button
              variant="ghost"
              size="icon"
              onClick={handleVoltarAdmin}
              className="mr-2"
            >
              <ArrowLeft className="w-5 h-5" />
            </Button>
            <div className="w-12 h-12 bg-primary/10 rounded-lg flex items-center justify-center">
              <Settings className="w-6 h-6 text-primary" />
            </div>
            <div>
              <h1 className="text-3xl font-bold text-foreground">Configurações da Empresa</h1>
              <p className="text-muted-foreground">
                {empresaExiste ? 'Gerencie as informações da sua empresa' : 'Configure os dados da sua empresa'}
              </p>
            </div>
          </div>
        </div>

        {/* Alertas de feedback */}
        {erro && (
          <div className="mb-6 p-4 bg-red-50 border border-red-200 rounded-md flex items-center gap-3">
            <AlertCircle className="w-5 h-5 text-red-600" />
            <p className="text-red-800">{erro}</p>
            <Button 
              variant="ghost" 
              size="sm" 
              onClick={() => setErro(null)}
              className="ml-auto"
            >
              ×
            </Button>
          </div>
        )}

        {sucesso && (
          <div className="mb-6 p-4 bg-green-50 border border-green-200 rounded-md flex items-center gap-3">
            <div className="w-5 h-5 bg-green-600 rounded-full flex items-center justify-center">
              <div className="w-2 h-2 bg-white rounded-full"></div>
            </div>
            <p className="text-green-800">{sucesso}</p>
            <Button 
              variant="ghost" 
              size="sm" 
              onClick={() => setSucesso(null)}
              className="ml-auto"
            >
              ×
            </Button>
          </div>
        )}

        {/* Formulário Principal */}
        <form onSubmit={handleSalvar} className="space-y-6">
          {/* Informações Básicas */}
          <Card>
            <CardHeader>
              <CardTitle className="flex items-center gap-2">
                <Building2 className="w-5 h-5" />
                Informações Básicas
              </CardTitle>
            </CardHeader>
            <CardContent className="space-y-4">
              <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div>
                  <label className="block text-sm font-medium mb-1">Nome Fantasia *</label>
                  <input
                    type="text"
                    required
                    className="w-full p-3 border rounded-md focus:ring-2 focus:ring-primary focus:border-transparent"
                    value={empresa.nome_fantasia}
                    onChange={(e) => handleInputChange('nome_fantasia', e.target.value)}
                    placeholder="Nome comercial da empresa"
                  />
                </div>

                <div>
                  <label className="block text-sm font-medium mb-1">Razão Social *</label>
                  <input
                    type="text"
                    required
                    className="w-full p-3 border rounded-md focus:ring-2 focus:ring-primary focus:border-transparent"
                    value={empresa.razao_social}
                    onChange={(e) => handleInputChange('razao_social', e.target.value)}
                    placeholder="Razão social conforme CNPJ"
                  />
                </div>
              </div>

              <div>
                <label className="block text-sm font-medium mb-1">Descrição</label>
                <textarea
                  rows={3}
                  className="w-full p-3 border rounded-md focus:ring-2 focus:ring-primary focus:border-transparent"
                  value={empresa.descricao}
                  onChange={(e) => handleInputChange('descricao', e.target.value)}
                  placeholder="Breve descrição sobre a empresa"
                />
              </div>
            </CardContent>
          </Card>

          {/* Documentos */}
          <Card>
            <CardHeader>
              <CardTitle className="flex items-center gap-2">
                <FileText className="w-5 h-5" />
                Documentos
              </CardTitle>
            </CardHeader>
            <CardContent className="space-y-4">
              <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                <div>
                  <label className="block text-sm font-medium mb-1">CNPJ</label>
                  <input
                    type="text"
                    className="w-full p-3 border rounded-md focus:ring-2 focus:ring-primary focus:border-transparent"
                    value={empresa.cnpj || ''}
                    onChange={(e) => {
                      const formatted = formatarCNPJ(e.target.value);
                      if (formatted.length <= 18) {
                        handleInputChange('cnpj', formatted);
                      }
                    }}
                    placeholder="00.000.000/0000-00"
                    maxLength={18}
                  />
                </div>

                <div>
                  <label className="block text-sm font-medium mb-1">Inscrição Estadual</label>
                  <input
                    type="text"
                    className="w-full p-3 border rounded-md focus:ring-2 focus:ring-primary focus:border-transparent"
                    value={empresa.inscricao_estadual || ''}
                    onChange={(e) => handleInputChange('inscricao_estadual', e.target.value)}
                    placeholder="Inscrição estadual"
                    maxLength={20}
                  />
                </div>

                <div>
                  <label className="block text-sm font-medium mb-1">Inscrição Municipal</label>
                  <input
                    type="text"
                    className="w-full p-3 border rounded-md focus:ring-2 focus:ring-primary focus:border-transparent"
                    value={empresa.inscricao_municipal || ''}
                    onChange={(e) => handleInputChange('inscricao_municipal', e.target.value)}
                    placeholder="Inscrição municipal"
                    maxLength={20}
                  />
                </div>
              </div>
            </CardContent>
          </Card>

          {/* Endereço */}
          <Card>
            <CardHeader>
              <CardTitle className="flex items-center gap-2">
                <MapPin className="w-5 h-5" />
                Endereço
              </CardTitle>
            </CardHeader>
            <CardContent className="space-y-4">
              <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                <div>
                  <label className="block text-sm font-medium mb-1">CEP</label>
                  <input
                    type="text"
                    className="w-full p-3 border rounded-md focus:ring-2 focus:ring-primary focus:border-transparent"
                    value={empresa.cep || ''}
                    onChange={(e) => {
                      const formatted = formatarCEP(e.target.value);
                      if (formatted.length <= 9) {
                        handleInputChange('cep', formatted);
                      }
                    }}
                    placeholder="00000-000"
                    maxLength={9}
                  />
                </div>

                <div>
                  <label className="block text-sm font-medium mb-1">Cidade</label>
                  <input
                    type="text"
                    className="w-full p-3 border rounded-md focus:ring-2 focus:ring-primary focus:border-transparent"
                    value={empresa.cidade || ''}
                    onChange={(e) => handleInputChange('cidade', e.target.value)}
                    placeholder="Nome da cidade"
                    maxLength={100}
                  />
                </div>

                <div>
                  <label className="block text-sm font-medium mb-1">Estado</label>
                  <select
                    className="w-full p-3 border rounded-md focus:ring-2 focus:ring-primary focus:border-transparent"
                    value={empresa.estado || ''}
                    onChange={(e) => handleInputChange('estado', e.target.value)}
                  >
                    <option value="">Selecione o estado</option>
                    {estados.map(estado => (
                      <option key={estado} value={estado}>{estado}</option>
                    ))}
                  </select>
                </div>
              </div>

              <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
                <div className="md:col-span-2">
                  <label className="block text-sm font-medium mb-1">Endereço</label>
                  <input
                    type="text"
                    className="w-full p-3 border rounded-md focus:ring-2 focus:ring-primary focus:border-transparent"
                    value={empresa.endereco || ''}
                    onChange={(e) => handleInputChange('endereco', e.target.value)}
                    placeholder="Rua, avenida, etc."
                    maxLength={500}
                  />
                </div>

                <div>
                  <label className="block text-sm font-medium mb-1">Número</label>
                  <input
                    type="text"
                    className="w-full p-3 border rounded-md focus:ring-2 focus:ring-primary focus:border-transparent"
                    value={empresa.numero || ''}
                    onChange={(e) => handleInputChange('numero', e.target.value)}
                    placeholder="123"
                    maxLength={10}
                  />
                </div>

                <div>
                  <label className="block text-sm font-medium mb-1">Complemento</label>
                  <input
                    type="text"
                    className="w-full p-3 border rounded-md focus:ring-2 focus:ring-primary focus:border-transparent"
                    value={empresa.complemento || ''}
                    onChange={(e) => handleInputChange('complemento', e.target.value)}
                    placeholder="Sala, bloco, etc."
                    maxLength={100}
                  />
                </div>
              </div>
            </CardContent>
          </Card>

          {/* Seção de Configurações Avançadas */}
          {empresaExiste && (
            <Card>
              <CardHeader>
                <CardTitle className="flex items-center gap-2">
                  <Settings className="w-5 h-5" />
                  Configurações Avançadas
                </CardTitle>
              </CardHeader>
              <CardContent>
                <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                  {/* Botão Horários */}
                  <div className="p-4 border rounded-lg hover:bg-gray-50 transition-colors">
                    <div className="flex items-center gap-3 mb-3">
                      <div className="w-10 h-10 bg-blue-100 rounded-lg flex items-center justify-center">
                        <Clock className="w-5 h-5 text-blue-600" />
                      </div>
                      <div>
                        <h4 className="font-semibold text-gray-900">Horários de Funcionamento</h4>
                        <p className="text-sm text-gray-600">Configure os horários para cada dia da semana</p>
                      </div>
                    </div>
                    <Button 
                      type="button"
                      variant="outline" 
                      className="w-full"
                      onClick={() => navigate('/empresa-horarios')}
                    >
                      <Clock className="w-4 h-4 mr-2" />
                      Gerenciar Horários
                    </Button>
                  </div>

                  {/* Botão Contatos */}
                  <div className="p-4 border rounded-lg hover:bg-gray-50 transition-colors">
                    <div className="flex items-center gap-3 mb-3">
                      <div className="w-10 h-10 bg-green-100 rounded-lg flex items-center justify-center">
                        <Phone className="w-5 h-5 text-green-600" />
                      </div>
                      <div>
                        <h4 className="font-semibold text-gray-900">Contatos</h4>
                        <p className="text-sm text-gray-600">Gerencie telefones, emails e redes sociais</p>
                      </div>
                    </div>
                    <Button 
                      type="button"
                      variant="outline" 
                      className="w-full opacity-60"
                      disabled
                      onClick={() => {
                        // Futura navegação: navigate('/empresa-contatos')
                        alert('Funcionalidade em desenvolvimento. Em breve você poderá gerenciar os contatos da empresa.');
                      }}
                    >
                      <Phone className="w-4 h-4 mr-2" />
                      Gerenciar Contatos
                      <span className="ml-2 text-xs bg-yellow-100 text-yellow-800 px-2 py-1 rounded">
                        Em breve
                      </span>
                    </Button>
                  </div>
                </div>
              </CardContent>
            </Card>
          )}

          {/* Botões de Ação */}
          <div className="flex justify-end gap-4">
            <Button
              type="button"
              variant="outline"
              onClick={handleVoltarAdmin}
            >
              Cancelar
            </Button>
            <Button
              type="submit"
              disabled={saving}
              className="min-w-[140px]"
            >
              {saving ? (
                <div className="flex items-center gap-2">
                  <div className="animate-spin rounded-full h-4 w-4 border-b-2 border-white"></div>
                  Salvando...
                </div>
              ) : (
                <div className="flex items-center gap-2">
                  <Save className="w-4 h-4" />
                  {empresaExiste ? 'Atualizar' : 'Criar Empresa'}
                </div>
              )}
            </Button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default Configuracoes;