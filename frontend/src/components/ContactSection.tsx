import React, { useEffect, useState } from 'react';
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Textarea } from "@/components/ui/textarea";
import { useToast } from "@/components/ui/use-toast";
import { Phone, Mail, MapPin, Clock, AlertCircle, Loader2, MessageCircle, Smartphone, Printer } from 'lucide-react';

// Tipos para os dados da empresa
interface TypeEmpresa {
  id?: number;
  nome_fantasia: string;
  razao_social: string;
  descricao?: string;
  cnpj?: string;
  endereco?: string;
  cep?: string;
  cidade?: string;
  estado?: string;
  numero?: string;
  complemento?: string;
}

interface TypeContato {
  id?: number;
  tipo: string;
  valor: string;
  descricao?: string;
  ativo: boolean;
}

interface TypeHorario {
  id?: number;
  dia_semana: string;
  horario_abertura?: string;
  horario_fechamento?: string;
  ativo: boolean;
}

// Tipos mais específicos do segundo código
interface EmpresaContato {
    id?: number;
    empresaId?: number;
    tipoContato: TipoContato;
    nomeTipoContato?: string;
    valor: string;
    valorFormatado?: string;
    descricao?: string;
    principal?: boolean;
    ativo: boolean;
    createdAt?: string;
    updatedAt?: string;
}

enum TipoContatoEnum {
    TELEFONE = 'telefone',
    CELULAR = 'celular',
    WHATSAPP = 'whatsapp',
    EMAIL = 'email',
    FAX = 'fax'
}

const ContactSection = () => {
  const { toast } = useToast();
  
  const [empresa, setEmpresa] = useState<TypeEmpresa>({
    nome_fantasia: 'Auto Box Car',
    razao_social: 'Auto Box Car Ltda',
    descricao: 'Lavagem e detalhamento automotivo',
    endereco: 'Av. Principal',
    numero: '1234',
    cidade: 'São Paulo',
    estado: 'SP',
    cep: '01234-567'
  });

  const [contatos, setContatos] = useState<TypeContato[]>([
    { tipo: 'TELEFONE', valor: '(11) 5555-1234', ativo: true },
    { tipo: 'CELULAR', valor: '(11) 98765-4321', ativo: true },
    { tipo: 'EMAIL', valor: 'contato@autoboxcar.com.br', ativo: true },
    { tipo: 'EMAIL', valor: 'agendamento@autoboxcar.com.br', descricao: 'Agendamentos', ativo: true }
  ]);

  // Contatos específicos do segundo código (mais detalhados)
  const [contatosDetalhados, setContatosDetalhados] = useState<EmpresaContato[]>([]);

  const [horarios, setHorarios] = useState<TypeHorario[]>([
    { dia_semana: 'SEGUNDA', horario_abertura: '08:00', horario_fechamento: '19:00', ativo: true },
    { dia_semana: 'TERCA', horario_abertura: '08:00', horario_fechamento: '19:00', ativo: true },
    { dia_semana: 'QUARTA', horario_abertura: '08:00', horario_fechamento: '19:00', ativo: true },
    { dia_semana: 'QUINTA', horario_abertura: '08:00', horario_fechamento: '19:00', ativo: true },
    { dia_semana: 'SEXTA', horario_abertura: '08:00', horario_fechamento: '19:00', ativo: true },
    { dia_semana: 'SABADO', horario_abertura: '08:00', horario_fechamento: '17:00', ativo: true },
    { dia_semana: 'DOMINGO', horario_abertura: '09:00', horario_fechamento: '14:00', ativo: true }
  ]);

  const [loading, setLoading] = useState(true);
  const [usingMockData, setUsingMockData] = useState(false);
  
  // Estado do formulário
  const [formData, setFormData] = useState({
    name: '',
    email: '',
    phone: '',
    subject: '',
    message: ''
  });
  const [sendingMessage, setSendingMessage] = useState(false);
  const [messageSent, setMessageSent] = useState(false);

  // Função para testar conexão com o backend
  const testarConexao = async () => {
    try {
      const response = await fetch('http://localhost:8080/api/empresa/status');
      return response.ok;
    } catch (error) {
      // Tentar endpoint alternativo
      try {
        const response2 = await fetch('http://localhost:8080/api/empresa-contatos/status');
        return response2.ok;
      } catch (error2) {
        return false;
      }
    }
  };

  // Buscar dados da empresa
  const buscarDadosEmpresa = async () => {
    try {
      const backendOnline = await testarConexao();
      
      if (!backendOnline) {
        console.warn("Backend não está disponível, usando dados mock");
        setUsingMockData(true);
        return;
      }

      const headers: HeadersInit = {
        'Content-Type': 'application/json'
      };

      const token = localStorage.getItem('boxpro_token');
      if (token) {
        headers['Authorization'] = `Bearer ${token}`;
      }

      // Buscar dados da empresa
      try {
        const empresaResponse = await fetch('http://localhost:8080/api/empresa', { headers });
        if (empresaResponse.ok) {
          const empresaData = await empresaResponse.json();
          setEmpresa({
            ...empresaData,
            nome_fantasia: empresaData.nomeFantasia || empresaData.nome_fantasia || 'Auto Box Car',
            razao_social: empresaData.razaoSocial || empresaData.razao_social || 'Auto Box Car Ltda',
            descricao: empresaData.descricao || 'Lavagem e detalhamento automotivo',
            endereco: empresaData.endereco || 'Av. Principal',
            numero: empresaData.numero || '1234',
            cidade: empresaData.cidade || 'São Paulo',
            estado: empresaData.estado || 'SP',
            cep: empresaData.cep || '01234-567'
          });
        }
      } catch (error) {
        console.warn("Erro ao buscar dados da empresa:", error);
      }

      // Buscar contatos da empresa (primeiro formato)
      try {
        const contatosResponse = await fetch('http://localhost:8080/api/empresa/contatos', { headers });
        if (contatosResponse.ok) {
          const contatosData = await contatosResponse.json();
          if (contatosData && contatosData.length > 0) {
            setContatos(contatosData);
          }
        }
      } catch (error) {
        console.warn("Erro ao buscar contatos formato 1:", error);
      }

      // Buscar contatos detalhados (segundo formato)
      try {
        const empresaId = empresa.id || 1;
        const contatosDetalhadosResponse = await fetch(`http://localhost:8080/api/empresa-contatos/empresa/${empresaId}`, { headers });
        if (contatosDetalhadosResponse.ok) {
          const contatosDetalhadosData = await contatosDetalhadosResponse.json();
          if (contatosDetalhadosData && contatosDetalhadosData.length > 0) {
            setContatosDetalhados(contatosDetalhadosData);
          }
        }
      } catch (error) {
        console.warn("Erro ao buscar contatos formato 2:", error);
      }

      // Buscar horários da empresa
      try {
        const horariosResponse = await fetch('http://localhost:8080/api/empresa/horarios', { headers });
        if (horariosResponse.ok) {
          const horariosData = await horariosResponse.json();
          if (horariosData && horariosData.length > 0) {
            setHorarios(horariosData);
          }
        }
      } catch (error) {
        console.warn("Erro ao buscar horários:", error);
      }

      setUsingMockData(false);
    } catch (error) {
      console.error("Erro ao carregar dados da empresa:", error);
      setUsingMockData(true);
    }
  };

  useEffect(() => {
    const carregarDados = async () => {
      setLoading(true);
      await buscarDadosEmpresa();
      setLoading(false);
    };

    carregarDados();
  }, []);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    
    if (!formData.name || !formData.email || !formData.message) {
      toast({
        title: "Campos obrigatórios",
        description: "Por favor, preencha todos os campos obrigatórios.",
        variant: "destructive",
      });
      return;
    }

    setSendingMessage(true);

    try {
      // Simular envio (aqui você pode integrar com seu backend de mensagens)
      await new Promise(resolve => setTimeout(resolve, 2000));
      
      toast({
        title: "Mensagem enviada!",
        description: "Entraremos em contato em breve.",
        duration: 3000,
      });

      setMessageSent(true);
      setFormData({
        name: '',
        email: '',
        phone: '',
        subject: '',
        message: ''
      });

      setTimeout(() => setMessageSent(false), 5000);
    } catch (error) {
      console.error('Erro ao enviar mensagem:', error);
      toast({
        title: "Erro ao enviar",
        description: "Ocorreu um erro ao enviar sua mensagem. Tente novamente.",
        variant: "destructive",
      });
    } finally {
      setSendingMessage(false);
    }
  };

  const handleInputChange = (field: string, value: string) => {
    setFormData(prev => ({
      ...prev,
      [field]: value
    }));
  };

  // Função para formatar endereço completo
  const formatarEnderecoCompleto = () => {
    const partes = [];
    
    if (empresa.endereco) partes.push(empresa.endereco);
    if (empresa.numero) partes.push(empresa.numero);
    
    const enderecoLinha1 = partes.join(', ');
    const enderecoLinha2 = `${empresa.cidade || 'São Paulo'} - ${empresa.estado || 'SP'}`;
    const cep = empresa.cep ? `CEP: ${empresa.cep}` : 'CEP: 01234-567';
    
    return { enderecoLinha1, enderecoLinha2, cep };
  };

  // Função para obter telefones (usando dados detalhados se disponível)
  const obterTelefones = () => {
    if (contatosDetalhados.length > 0) {
      return contatosDetalhados
        .filter(contato => ['telefone', 'celular'].includes(contato.tipoContato.toLowerCase()) && contato.ativo)
        .sort((a, b) => {
          if (a.principal && !b.principal) return -1;
          if (!a.principal && b.principal) return 1;
          return 0;
        });
    }
    
    // Fallback para formato original
    return contatos
      .filter(contato => ['TELEFONE', 'CELULAR'].includes(contato.tipo) && contato.ativo)
      .map(contato => ({ 
        valor: contato.valor, 
        valorFormatado: contato.valor,
        descricao: contato.descricao,
        tipoContato: contato.tipo.toLowerCase()
      }));
  };

  // Função para obter WhatsApp
  const obterWhatsApp = () => {
    if (contatosDetalhados.length > 0) {
      return contatosDetalhados
        .filter(contato => contato.tipoContato.toLowerCase() === 'whatsapp' && contato.ativo)
        .sort((a, b) => {
          if (a.principal && !b.principal) return -1;
          if (!a.principal && b.principal) return 1;
          return 0;
        });
    }
    
    return contatos
      .filter(contato => contato.tipo === 'WHATSAPP' && contato.ativo)
      .map(contato => ({ 
        valor: contato.valor, 
        valorFormatado: contato.valor,
        descricao: contato.descricao,
        tipoContato: 'whatsapp'
      }));
  };

  // Função para obter emails
  const obterEmails = () => {
    if (contatosDetalhados.length > 0) {
      return contatosDetalhados
        .filter(contato => contato.tipoContato.toLowerCase() === 'email' && contato.ativo)
        .sort((a, b) => {
          if (a.principal && !b.principal) return -1;
          if (!a.principal && b.principal) return 1;
          return 0;
        });
    }
    
    return contatos
      .filter(contato => contato.tipo === 'EMAIL' && contato.ativo)
      .map(contato => ({ 
        valor: contato.valor, 
        valorFormatado: contato.valor,
        descricao: contato.descricao,
        tipoContato: 'email'
      }));
  };

  // Função para formatar horários de funcionamento
  const formatarHorarios = () => {
    const diasSemana = {
      SEGUNDA: 'Segunda',
      TERCA: 'Terça', 
      QUARTA: 'Quarta',
      QUINTA: 'Quinta',
      SEXTA: 'Sexta',
      SABADO: 'Sábado',
      DOMINGO: 'Domingo'
    };

    const horariosAtivos = horarios.filter(h => h.ativo);
    
    if (horariosAtivos.length === 0) {
      return [
        'Segunda - Sexta: 8:00 - 19:00',
        'Sábado: 8:00 - 17:00',
        'Domingo: 9:00 - 14:00'
      ];
    }

    // Agrupar horários similares
    const grupos = {};
    horariosAtivos.forEach(horario => {
      const chave = `${horario.horario_abertura}-${horario.horario_fechamento}`;
      if (!grupos[chave]) {
        grupos[chave] = [];
      }
      grupos[chave].push(diasSemana[horario.dia_semana] || horario.dia_semana);
    });

    return Object.entries(grupos).map(([horario, dias]) => {
      const [abertura, fechamento] = horario.split('-');
      if (dias.length === 1) {
        return `${dias[0]}: ${abertura} - ${fechamento}`;
      } else if (dias.length <= 3) {
        return `${dias.join(', ')}: ${abertura} - ${fechamento}`;
      } else {
        return `${dias[0]} - ${dias[dias.length - 1]}: ${abertura} - ${fechamento}`;
      }
    });
  };

  const { enderecoLinha1, enderecoLinha2, cep } = formatarEnderecoCompleto();
  const telefones = obterTelefones();
  const whatsapp = obterWhatsApp();
  const emails = obterEmails();
  const horariosFormatados = formatarHorarios();

  if (loading) {
    return (
      <section id="contato" className="py-16">
        <div className="container mx-auto px-4">
          <div className="flex justify-center items-center h-64">
            <div className="text-center">
              <Loader2 className="animate-spin h-12 w-12 text-blue-600 mx-auto mb-4" />
              <p className="text-gray-600">Carregando informações de contato...</p>
            </div>
          </div>
        </div>
      </section>
    );
  }

  return (
    <section id="contato" className="py-16">
      <div className="container mx-auto px-4">
        {/* Alerta sobre modo mock */}
        {usingMockData && (
          <div className="mb-6 p-4 bg-yellow-50 border border-yellow-200 rounded-md text-center">
            <p className="text-yellow-800 text-sm">
              ⚠️ Exibindo dados de demonstração - Backend não conectado
            </p>
          </div>
        )}

        <div className="text-center mb-12">
          <h2 className="text-3xl md:text-4xl font-bold mb-4 bg-gradient-to-r from-blue-600 to-indigo-600 bg-clip-text text-transparent">
            Fale Conosco
          </h2>
          <p className="text-gray-600 max-w-2xl mx-auto">
            Tem alguma dúvida ou sugestão? Entre em contato conosco! Estamos sempre à disposição para melhor atendê-lo.
          </p>
        </div>
        
        <div className="grid grid-cols-1 lg:grid-cols-2 gap-10">
          <div className="bg-gray-50 p-8 rounded-lg shadow-md">
            <h3 className="text-2xl font-bold text-blue-600 mb-6">Informações de Contato</h3>
            
            <div className="space-y-6">
              <div>
                <h4 className="font-semibold text-lg mb-2">Endereço</h4>
                <p className="text-gray-600">
                  {enderecoLinha1}<br />
                  {enderecoLinha2}<br />
                  {cep}
                </p>
              </div>
              
              {telefones.length > 0 && (
                <div>
                  <h4 className="font-semibold text-lg mb-2">Telefone</h4>
                  <div className="text-gray-600">
                    {telefones.map((telefone, index) => (
                      <p key={index} className="mb-1">
                        <span className="font-medium">{telefone.valorFormatado || telefone.valor}</span>
                        {telefone.descricao && (
                          <span className="text-sm text-gray-500 ml-2">({telefone.descricao})</span>
                        )}
                      </p>
                    ))}
                  </div>
                </div>
              )}

              {whatsapp.length > 0 && (
                <div>
                  <h4 className="font-semibold text-lg mb-2">WhatsApp</h4>
                  <div className="text-gray-600">
                    {whatsapp.map((wpp, index) => (
                      <p key={index} className="mb-1">
                        <span className="font-medium">{wpp.valorFormatado || wpp.valor}</span>
                        {wpp.descricao && (
                          <span className="text-sm text-gray-500 ml-2">({wpp.descricao})</span>
                        )}
                      </p>
                    ))}
                  </div>
                </div>
              )}
              
              {emails.length > 0 && (
                <div>
                  <h4 className="font-semibold text-lg mb-2">Email</h4>
                  <div className="text-gray-600">
                    {emails.map((email, index) => (
                      <p key={index} className="mb-1">
                        <span className="font-medium">{email.valor}</span>
                        {email.descricao && (
                          <span className="text-sm text-gray-500 ml-2">({email.descricao})</span>
                        )}
                      </p>
                    ))}
                  </div>
                </div>
              )}
              
              <div>
                <h4 className="font-semibold text-lg mb-2">Horário de Atendimento</h4>
                <div className="text-gray-600">
                  {horariosFormatados.map((horario, index) => (
                    <p key={index}>{horario}</p>
                  ))}
                </div>
              </div>
            </div>
            
            <div className="mt-8">
              <iframe 
                src="https://www.google.com/maps/embed?pb=!1m18!1m12!1m3!1d3657.1976900160257!2d-46.6528357!3d-23.5636808!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0x94ce59c8da0aa315%3A0xd59f9431f2c9776a!2sAv.%20Paulista%2C%20S%C3%A3o%20Paulo%20-%20SP!5e0!3m2!1spt-BR!2sbr!4v1684972266373!5m2!1spt-BR!2sbr" 
                className="w-full h-64 rounded-lg border-0" 
                allowFullScreen 
                loading="lazy" 
                title={`Localização ${empresa.nome_fantasia}`}
              ></iframe>
            </div>
          </div>
          
          <div className="bg-gray-50 p-8 rounded-lg shadow-md">
            {messageSent && (
              <div className="mb-6 p-4 bg-green-50 border border-green-200 rounded-md flex items-center gap-3">
                <div className="w-5 h-5 bg-green-500 rounded-full flex items-center justify-center">
                  <svg className="w-3 h-3 text-white" fill="currentColor" viewBox="0 0 20 20">
                    <path fillRule="evenodd" d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z" clipRule="evenodd" />
                  </svg>
                </div>
                <p className="text-green-800 font-medium">Mensagem enviada com sucesso!</p>
              </div>
            )}

            <div className="space-y-6">
              <div>
                <label htmlFor="name" className="block text-sm font-medium text-gray-700 mb-1">Nome *</label>
                <Input 
                  id="name" 
                  placeholder="Seu nome completo" 
                  required 
                  value={formData.name}
                  onChange={(e) => handleInputChange('name', e.target.value)}
                />
              </div>
              
              <div>
                <label htmlFor="email" className="block text-sm font-medium text-gray-700 mb-1">Email *</label>
                <Input 
                  id="email" 
                  type="email" 
                  placeholder="seu.email@exemplo.com" 
                  required 
                  value={formData.email}
                  onChange={(e) => handleInputChange('email', e.target.value)}
                />
              </div>
              
              <div>
                <label htmlFor="phone" className="block text-sm font-medium text-gray-700 mb-1">Telefone</label>
                <Input 
                  id="phone" 
                  placeholder="(00) 00000-0000" 
                  value={formData.phone}
                  onChange={(e) => handleInputChange('phone', e.target.value)}
                />
              
      </div>
          <div>
            <label htmlFor="subject" className="block text-sm font-medium text-gray-700 mb-1">Assunto</label>
            <Input 
              id="subject" 
              placeholder="Assunto da mensagem" 
              value={formData.subject}
              onChange={(e) => handleInputChange('subject', e.target.value)}
            />
          </div>
              
              <div>
                <label htmlFor="message" className="block text-sm font-medium text-gray-700 mb-1">Mensagem *</label>
                <Textarea 
                  id="message" 
                  rows={4} 
                  placeholder="Sua mensagem..." 
                  required 
                  value={formData.message}
                  onChange={(e) => handleInputChange('message', e.target.value)}
                />
              </div>
              
              <Button 
                onClick={handleSubmit} 
                disabled={sendingMessage} 
                className="w-full bg-blue-600 hover:bg-blue-700 text-white"
              >
                {sendingMessage ? (
                  <>
                    <Loader2 className="animate-spin h-5 w-5 mr-2" />
                    Enviando...
                  </>
                ) : (
                  'Enviar Mensagem'
                )}
              </Button>
            </div>
          </div>
        </div>
      </div>
    </section>
  );
};
export default ContactSection;
