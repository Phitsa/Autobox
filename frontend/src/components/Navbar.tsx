
import { useState } from "react";
import { Button } from "@/components/ui/button";
import { Menu, X } from "lucide-react";

const Navbar = () => {
  const [isMenuOpen, setIsMenuOpen] = useState(false);

  const toggleMenu = () => {
    setIsMenuOpen(!isMenuOpen);
  };

  return (
    <nav className="bg-white shadow-md fixed w-full z-50">
      <div className="container mx-auto px-4">
        <div className="flex justify-between items-center py-4">
          <div className="flex items-center space-x-2">
            <span className="text-carwash-blue font-bold text-2xl">Auto</span>
            <span className="bg-carwash-blue text-white px-2 py-1 rounded-md font-bold">BOX</span>
            <span className="text-carwash-blue font-bold text-2xl">Car</span>
          </div>
          
          <div className="hidden md:flex space-x-8">
            <a href="#inicio" className="text-gray-600 hover:text-carwash-blue transition-colors">Início</a>
            <a href="#servicos" className="text-gray-600 hover:text-carwash-blue transition-colors">Serviços</a>
            <a href="#como-funciona" className="text-gray-600 hover:text-carwash-blue transition-colors">Como Funciona</a>
            <a href="#sobre" className="text-gray-600 hover:text-carwash-blue transition-colors">Sobre</a>
            <a href="#contato" className="text-gray-600 hover:text-carwash-blue transition-colors">Contato</a>
          </div>
          
          <div className="hidden md:block">
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
              <Button className="bg-carwash-orange hover:bg-orange-600 text-white w-full">
                Agendar Agora
              </Button>
            </div>
          </div>
        )}
      </div>
    </nav>
  );
};

export default Navbar;
