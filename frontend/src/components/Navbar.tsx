import React, { useEffect, useState } from "react";
import { Button } from "@/components/ui/button";
import {
  CalendarClock,
  Phone,
  MessageCircle,
  Clock,
  MapPin,
  Menu,
  X,
  LogOut
} from "lucide-react";
import LoginRegisterModal from "./LoginRegisterModal";
import { useAuth } from "@/hooks/useAuth";
import { User } from "@/types/auth";

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
  TELEFONE = "telefone",
  CELULAR = "celular",
  WHATSAPP = "whatsapp",
  EMAIL = "email",
  FAX = "fax"
}

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
    valor: "8433334444",
    valorFormatado: "(84) 3333-4444",
    descricao: "Comercial",
    principal: true,
    ativo: true
  },
  {
    id: 2,
    empresaId: 1,
    tipoContato: TipoContato.WHATSAPP,
    valor: "84999887766",
    valorFormatado: "(84) 99988-7766",
    descricao: "Agendamentos",
    principal: true,
    ativo: true
  }
];

const Navbar = () => {
  const [empresa, setEmpresa] = useState<Empresa | null>(null);
  const [contatos, setContatos] = useState<EmpresaContato[]>([]);
  const [loading, setLoading] = useState(true);
  const [usingMockData, setUsingMockData] = useState(false);
  const [isMenuOpen, setIsMenuOpen] = useState(false);
  const [showLoginModal, setShowLoginModal] = useState(false);

  const { user, isAuthenticated, login, logout } = useAuth();

  const toggleMenu = () => setIsMenuOpen(!isMenuOpen);

  const handleLoginSuccess = (userData: User) => {
    login(userData);
    setShowLoginModal(false);
    window.location.href = "/admin";
  };

  const handleLogout = () => logout();

  const handleAdminAccess = () => {
    if (isAuthenticated) {
      window.location.href = "/admin";
    } else {
      setShowLoginModal(true);
    }
  };

  const horariosFuncionamento = [
    { dias: "Segunda - Sexta", horario: "8:00 - 19:00" },
    { dias: "Sábado", horario: "8:00 - 17:00" },
    { dias: "Domingo", horario: "9:00 - 14:00" }
  ];

  const testarConexao = async () => {
    try {
      const response = await fetch("http://localhost:8080/api/empresa-contatos/status");
      return response.ok;
    } catch {
      return false;
    }
  };

  useEffect(() => {
    const carregarDados = async () => {
      try {
        setLoading(true);
        const backendOnline = await testarConexao();

        if (!backendOnline) {
          setEmpresa(mockEmpresa);
          setContatos(mockContatos);
          setUsingMockData(true);
          return;
        }

        const headers = {
          "Content-Type": "application/json",
          Accept: "application/json"
        };

        const empresaResponse = await fetch("http://localhost:8080/api/empresa/1", {
          method: "GET",
          headers
        });

        let empresaData;
        if (empresaResponse.ok) {
          empresaData = await empresaResponse.json();
        } else {
          empresaData = mockEmpresa;
        }

        const empresaInfo: Empresa = {
          id: empresaData.id || 1,
          nomeFantasia:
            empresaData.nomeFantasia ||
            empresaData.nome_fantasia ||
            empresaData.razao_social ||
            "Auto Box",
          razaoSocial: empresaData.razaoSocial || empresaData.razao_social,
          endereco: empresaData.endereco || "Mossoró - RN"
        };
        setEmpresa(empresaInfo);

        const contatosResponse = await fetch(
          `http://localhost:8080/api/empresa-contatos/empresa/${empresaInfo.id}`,
          {
            method: "GET",
            headers
          }
        );

        if (contatosResponse.ok) {
          const contatosData = await contatosResponse.json();
          setContatos(contatosData || []);
          setUsingMockData(false);
        } else {
          setContatos([]);
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

  const gerarLinkWhatsApp = () => {
    const whatsappContato = contatos.find(
      (c) => c.tipoContato === TipoContato.WHATSAPP && c.ativo
    );

    const contato = whatsappContato || contatos.find(
      (c) => (c.tipoContato === TipoContato.CELULAR || c.tipoContato === TipoContato.TELEFONE) && c.ativo
    );

    if (!contato) {
      alert("Número do WhatsApp não encontrado.");
      return;
    }

    const numeroLimpo = contato.valor.replace(/\D/g, "");
    const numeroFormatado = numeroLimpo.startsWith("55")
      ? numeroLimpo
      : `55${numeroLimpo}`;
    const mensagem = encodeURIComponent(
      `Olá! Gostaria de agendar um serviço na ${empresa?.nomeFantasia || "Auto Box"}.`
    );

    window.open(`https://wa.me/${numeroFormatado}?text=${mensagem}`, "_blank");
  };

  const ligar = () => {
    const telefoneContato = contatos.find(
      (c) => (c.tipoContato === TipoContato.TELEFONE || c.tipoContato === TipoContato.CELULAR) && c.ativo
    );

    if (!telefoneContato) {
      alert("Número de telefone não encontrado.");
      return;
    }

    window.open(`tel:${telefoneContato.valor}`);
  };

  return (
    <>
      <nav className="bg-white shadow-md fixed w-full z-50">
        <div className="container mx-auto px-4">
          <div className="flex justify-between items-center py-4">
            <div className="flex items-center space-x-2">
              <span className="text-carwash-blue font-bold text-2xl">Auto</span>
              <span className="bg-carwash-blue text-white px-2 py-1 rounded-md font-bold">BOX</span>
            </div>

            <div className="hidden md:flex space-x-8">
              <a href="#inicio" className="text-gray-600 hover:text-carwash-blue transition-colors">Início</a>
              <a href="#servicos" className="text-gray-600 hover:text-carwash-blue transition-colors">Serviços</a>
              <a href="#como-funciona" className="text-gray-600 hover:text-carwash-blue transition-colors">Como Funciona</a>
              <a href="#sobre" className="text-gray-600 hover:text-carwash-blue transition-colors">Sobre</a>
              <a href="#contato" className="text-gray-600 hover:text-carwash-blue transition-colors">Contato</a>
            </div>

            <div className="hidden md:block">
              <Button onClick={handleAdminAccess} className="bg-carwash-blue hover:bg-blue-600 text-white mr-2">
                Menu Admin
              </Button>
              <Button onClick={gerarLinkWhatsApp} className="bg-carwash-orange hover:bg-orange-600 text-white">
                Agendar Agora
              </Button>
            </div>

            <div className="md:hidden">
              <button onClick={toggleMenu} className="text-carwash-blue">
                {isMenuOpen ? <X size={24} /> : <Menu size={24} />}
              </button>
            </div>
          </div>

          {isMenuOpen && (
            <div className="md:hidden py-4 bg-white">
              <div className="flex flex-col space-y-4 pb-4">
                <a href="#inicio" className="text-gray-600 hover:text-carwash-blue transition-colors">Início</a>
                <a href="#servicos" className="text-gray-600 hover:text-carwash-blue transition-colors">Serviços</a>
                <a href="#como-funciona" className="text-gray-600 hover:text-carwash-blue transition-colors">Como Funciona</a>
                <a href="#sobre" className="text-gray-600 hover:text-carwash-blue transition-colors">Sobre</a>
                <a href="#contato" className="text-gray-600 hover:text-carwash-blue transition-colors">Contato</a>
                <Button onClick={handleAdminAccess} className="bg-carwash-blue hover:bg-blue-600 text-white w-full">Menu Admin</Button>
                <Button onClick={gerarLinkWhatsApp} className="bg-carwash-orange hover:bg-orange-600 text-white w-full">Agendar Agora</Button>
              </div>
            </div>
          )}
        </div>
      </nav>

      <LoginRegisterModal
        isOpen={showLoginModal}
        onClose={() => setShowLoginModal(false)}
        onLoginSuccess={handleLoginSuccess}
      />
    </>
  );
};

export default Navbar;
