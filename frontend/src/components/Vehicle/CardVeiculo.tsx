import React from 'react';
import { Button } from '@/components/ui/button';
import { useNavigate } from 'react-router-dom';
import { Car } from "lucide-react"; // ou o caminho do ícone

const Veiculos = () => {
  const navigate = useNavigate();

const handleAcessar = () => {
    navigate('/veiculos');
  };

  return (
    <div className="bg-white rounded-lg shadow-md hover:shadow-lg transition-shadow cursor-pointer">
      <div className="p-6">
        <div className="flex items-center justify-between mb-4">
          <div className="w-12 h-12 bg-orange-100 rounded-lg flex items-center justify-center">
            <Car className="w-6 h-6 text-orange-600" />
          </div>
          <Button size="sm" variant="outline" onClick={handleAcessar}>
            Acessar
          </Button>
        </div>
        <h3 className="text-lg font-semibold text-gray-900 mb-2">Veiculos</h3>
        <p className="text-gray-600 text-sm">
          Cadastre e gerencie a frota de veículos da sua empresa.
        </p>
      </div>
    </div>
  );
};

export default Veiculos;