import React from "react";
import { useNavigate } from "react-router-dom";
import { Users } from "lucide-react";
import { Button } from "@/components/ui/button";

const CardClientes: React.FC = () => {
  const navigate = useNavigate();

  const handleAcessar = () => {
    navigate("/clientes");
  };

  return (
    <div className="bg-white rounded-lg shadow-md hover:shadow-lg transition-shadow cursor-pointer">
      <div className="p-6">
        <div className="flex items-center justify-between mb-4">
          <div className="w-12 h-12 bg-green-100 rounded-lg flex items-center justify-center">
            <Users className="w-6 h-6 text-green-600" />
          </div>
          <Button size="sm" variant="outline" onClick={handleAcessar}>
            Acessar
          </Button>
        </div>
        <h3 className="text-lg font-semibold text-gray-900 mb-2">Clientes</h3>
        <p className="text-gray-600 text-sm">
          Cadastre e gerencie informações dos clientes, histórico e preferências.
        </p>
      </div>
    </div>
  );
};

export default CardClientes;