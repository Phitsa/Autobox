import React, { useEffect, useState } from 'react';
import { Button } from "@/components/ui/button";
import { CalendarClock, Phone, MessageCircle, Clock, MapPin } from "lucide-react";

// Tipos baseados no arquivo EmpresaContatos
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
}

interface Empresa {
    id: number;
    nomeFantasia: string;
    razaoSocial?: string;
    endereco?: string;
}

enum TipoContato {
    TELEFONE = 'telefone',
    CELULAR = 'celular',
    WHATSAPP = 'whatsapp',
    EMAIL = 'email',
    FAX = 'fax'
}

// Dados mock como fallback (mesmos do arquivo original)
const mockEmpresa: Empresa = {
    id: 1,
    nomeFantasia: "Auto Box",
    razaoSocial: "Auto Box Estética Automotiva Ltda",
    endereco: "Mossoró - RN"
};

const mockContatos: EmpresaContato[] = [
    {
        id: 1,
        empresaId: 1,
        tipoContato: TipoContato.TELEFONE,
        valor: '8433334444',
        valorFormatado: '(84) 3333-4444',
        descricao: 'Comercial',
        principal: true,
        ativo: true
    },
    {
        id: 2,
        empresaId: 1,
        tipoContato: TipoContato.WHATSAPP,
        valor: '84999887766',
        valorFormatado: '(84) 99988-7766',
        descricao: 'Agendamentos',
        principal: true,
        ativo: true
    }
];

const HeroSection = () => {
    const [empresa, setEmpresa] = useState<Empresa | null>(null);
    const [contatos, setContatos] = useState<EmpresaContato[]>([]);
    const [loading, setLoading] = useState(true);
    const [usingMockData, setUsingMockData] = useState(false);

    // Horários de funcionamento (você pode tornar isso dinâmico também se necessário)
    const horariosFuncionamento = [
        { dias: 'Segunda - Sexta', horario: '8:00 - 19:00' },
        { dias: 'Sábado', horario: '8:00 - 17:00' },
        { dias: 'Domingo', horario: '9:00 - 14:00' }
    ];

    // Função para testar conexão com backend
    const testarConexao = async () => {
        try {
            const response = await fetch('http://localhost:8080/api/empresa-contatos/status', {
                method: 'GET',
                headers: { 'Content-Type': 'application/json' }
            });
            return response.ok;
        } catch (error) {
            return false;
        }
    };

    // Carregar dados da empresa e contatos
    useEffect(() => {
        const carregarDados = async () => {
            try {
                setLoading(true);

                const backendOnline = await testarConexao();

                if (!backendOnline) {
                    console.warn("Backend não disponível, usando dados mock");
                    setEmpresa(mockEmpresa);
                    setContatos(mockContatos);
                    setUsingMockData(true);
                    setLoading(false);
                    return;
                }

                const headers = { 'Content-Type': 'application/json', 'Accept': 'application/json' };

                // Buscar dados da empresa
                try {
                    const empresaResponse = await fetch('http://localhost:8080/api/empresa/1', {
                        method: 'GET',
                        headers
                    });

                    let empresaData;
                    if (empresaResponse.ok) {
                        empresaData = await empresaResponse.json();
                    } else {
                        empresaData = mockEmpresa;
                    }

                    const empresaInfo = {
                        id: empresaData.id || 1,
                        nomeFantasia: empresaData.nomeFantasia || empresaData.nome_fantasia || empresaData.razao_social || "Auto Box",
                        razaoSocial: empresaData.razaoSocial || empresaData.razao_social,
                        endereco: empresaData.endereco || "Mossoró - RN"
                    };
                    setEmpresa(empresaInfo);

                    // Buscar contatos
                    try {
                        const contatosResponse = await fetch(`http://localhost:8080/api/empresa-contatos/empresa/${empresaInfo.id}`, {
                            method: 'GET',
                            headers
                        });

                        let contatosData = [];
                        if (contatosResponse.ok) {
                            contatosData = await contatosResponse.json();
                        }

                        setContatos(contatosData || []);
                        setUsingMockData(false);

                    } catch (error) {
                        console.error("Erro ao buscar contatos:", error);
                        setContatos([]);
                    }

                } catch (error) {
                    console.error("Erro ao buscar empresa:", error);
                    setEmpresa(mockEmpresa);
                    setContatos(mockContatos);
                    setUsingMockData(true);
                }

            } catch (error) {
                console.error("Erro geral:", error);
                setEmpresa(mockEmpresa);
                setContatos(mockContatos);
                setUsingMockData(true);
            } finally {
                setLoading(false);
            }
        };

        carregarDados();
    }, []);

    // Função para gerar link do WhatsApp
    const gerarLinkWhatsApp = () => {
        const whatsappContato = contatos.find(c => 
            c.tipoContato === TipoContato.WHATSAPP && c.ativo
        );

        if (!whatsappContato) {
            // Fallback para outros tipos de telefone
            const telefoneContato = contatos.find(c => 
                (c.tipoContato === TipoContato.CELULAR || c.tipoContato === TipoContato.TELEFONE) && c.ativo
            );
            
            if (!telefoneContato) {
                alert('Número do WhatsApp não encontrado. Entre em contato pelos outros meios disponíveis.');
                return;
            }
            
            // Use o telefone como WhatsApp
            const numeroLimpo = telefoneContato.valor.replace(/\D/g, '');
            const numeroFormatado = numeroLimpo.startsWith('55') ? numeroLimpo : `55${numeroLimpo}`;
            const mensagem = encodeURIComponent(`Olá! Gostaria de agendar um serviço na ${empresa?.nomeFantasia || 'Auto Box'}.`);
            
            window.open(`https://wa.me/${numeroFormatado}?text=${mensagem}`, '_blank');
            return;
        }

        const numeroLimpo = whatsappContato.valor.replace(/\D/g, '');
        const numeroFormatado = numeroLimpo.startsWith('55') ? numeroLimpo : `55${numeroLimpo}`;
        const mensagem = encodeURIComponent(`Olá! Gostaria de agendar um serviço na ${empresa?.nomeFantasia || 'Auto Box'}.`);
        
        window.open(`https://wa.me/${numeroFormatado}?text=${mensagem}`, '_blank');
    };

    // Função para ligar
    const ligar = () => {
        const telefoneContato = contatos.find(c => 
            (c.tipoContato === TipoContato.TELEFONE || c.tipoContato === TipoContato.CELULAR) && c.ativo
        );

        if (!telefoneContato) {
            alert('Número de telefone não encontrado.');
            return;
        }

        window.open(`tel:${telefoneContato.valor}`);
    };

    if (loading) {
        return (
            <div className="relative min-h-screen bg-gradient-to-br from-blue-900 via-blue-800 to-purple-900 flex items-center justify-center">
                <div className="text-center text-white">
                    <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-white mx-auto mb-4"></div>
                    <p>Carregando informações...</p>
                </div>
            </div>
        );
    }

    return (
        <section id="inicio" className="relative bg-gradient-to-r from-blue-900 to-blue-700 pt-24 pb-16 md:pt-32 md:pb-24">
      <div className="absolute inset-0 bg-[url('https://images.unsplash.com/photo-1607860108855-64acf2078ed9?ixlib=rb-1.2.1&q=80&fm=jpg&crop=entropy&cs=tinysrgb&dl=oscar-sutton-Y1NBBydL2BU-unsplash.jpg')] bg-cover bg-center opacity-20"></div>
      
      <div className="container mx-auto px-6 relative z-10">
        <div className="flex flex-col md:flex-row items-center">
          <div className="md:w-1/2 text-center md:text-left mb-10 md:mb-0">
            <h1 className="text-4xl md:text-5xl font-bold text-white mb-6 leading-tight">
              Auto Box<br/>
              <span className="text-yellow-300">Estética Automotiva</span>
            </h1>
            <p className="text-white/90 text-lg mb-8 max-w-lg mx-auto md:mx-0">
              Seu carro merece o melhor tratamento! Agende seu horário agora.
            </p>
            <Button
              size="lg"
              onClick={gerarLinkWhatsApp}
              className="bg-carwash-orange hover:bg-orange-600 text-white"
            >
              <CalendarClock className="mr-2 h-5 w-5" />
              Agendar Agora
            </Button>

          </div>
          
          <div className="md:w-1/2">
            <div className="bg-white rounded-xl p-6 shadow-lg">
              <h3 className="text-2xl font-bold text-carwash-blue mb-4 text-center">Horário de Funcionamento</h3>
              <div className="space-y-3">
                <div className="flex justify-between border-b pb-2">
                  <span>Segunda - Sexta</span>
                  <span className="font-semibold">8:00 - 19:00</span>
                </div>
                <div className="flex justify-between border-b pb-2">
                  <span>Sábado</span>
                  <span className="font-semibold">8:00 - 17:00</span>
                </div>
                <div className="flex justify-between">
                  <span>Domingo</span>
                  <span className="font-semibold">9:00 - 14:00</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>
    );
};

export default HeroSection;
