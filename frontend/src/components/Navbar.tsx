import { useState } from "react";
import { Button } from "@/components/ui/button";
import { Menu, X, LogOut } from "lucide-react";
import LoginRegisterModal from "./LoginRegisterModal";
import { useAuth } from "@/hooks/useAuth";
import { User } from "@/types/auth";

const Navbar = () => {
  const [isMenuOpen, setIsMenuOpen] = useState(false);
  const [showLoginModal, setShowLoginModal] = useState(false);
  const { user, isAuthenticated, login, logout } = useAuth();

  const toggleMenu = () => {
    setIsMenuOpen(!isMenuOpen);
  };

  const handleLoginSuccess = (userData: User) => {
    login(userData);
    setShowLoginModal(false);
    // Redirecionar para o dashboard admin
    window.location.href = '/admin';
  };

  const handleLogout = () => {
    logout();
  };

  const handleAdminAccess = () => {
    if (isAuthenticated) {
      // Se já está logado, vai direto para o admin
      window.location.href = '/admin';
    } else {
      // Se não está logado, abre o modal
      setShowLoginModal(true);
    }
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
              <Button 
                onClick={handleAdminAccess}
                className="bg-carwash-blue hover:bg-blue-600 text-white mr-2"
              >
                Menu Admin
              </Button>
              
              <Button className="bg-carwash-orange hover:bg-orange-600 text-white">
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
                
                <Button 
                  onClick={handleAdminAccess}
                  className="bg-carwash-blue hover:bg-blue-600 text-white w-full"
                >
                  Menu Admin
                </Button>
                
                <Button className="bg-carwash-orange hover:bg-orange-600 text-white w-full">
                  Agendar Agora
                </Button>
              </div>
            </div>
          )}
        </div>
      </nav>

      {/* Modal de Login/Registro */}
      <LoginRegisterModal 
        isOpen={showLoginModal}
        onClose={() => setShowLoginModal(false)}
        onLoginSuccess={handleLoginSuccess}
      />
    </>
  );
};

export default Navbar;