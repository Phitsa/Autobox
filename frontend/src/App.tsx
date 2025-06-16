// App.js
import { Toaster } from "@/components/ui/toaster";
import { Toaster as Sonner } from "@/components/ui/sonner";
import { TooltipProvider } from "@/components/ui/tooltip";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { BrowserRouter, Routes, Route, useLocation } from "react-router-dom";
import { AuthProvider } from "./contexts/AuthContext";
import ProtectedRoute from "./components/ProtectedRoute";
import UserHeader from "./components/UserHeader";
import Index from "./pages/Index";
import NotFound from "./pages/NotFound";
import AdminDashboard from "./pages/AdminDashboard";
import ClientesDashboard from "./pages/ClientesDashboard";
import Categorias from "./pages/Categorias";
import Servicos from "./pages/Servicos";
import Funcionarios from './pages/Funcionarios';
import Configuracoes from "./pages/Configuracoes";
import EmpresaHorarios from "./pages/EmpresaHorarios";
import Veiculos from "./pages/Veiculos";
import EmpresaContatos from "./pages/EmpresaContatos";
import Agendamentos from "./pages/Agendamentos";
import HistoricoAgendamentos from "./pages/HistoricoAgendamentos";

const queryClient = new QueryClient();

// Componente interno que tem acesso ao useLocation
const AppContent = () => {
  const location = useLocation();
  
  // Verificar se está na página inicial
  const isHomePage = location.pathname === '/';

  return (
    <div className="min-h-screen bg-gray-50">
      {/* Header condicional - aparece em todas as páginas exceto / */}
      {!isHomePage && <UserHeader />}
      
      <Routes>
        {/* Rota pública - Página inicial */}
        <Route path="/" element={<Index />} />

        {/* Rotas protegidas */}
        <Route
          path="/admin"
          element={
            <ProtectedRoute>
              <AdminDashboard />
            </ProtectedRoute>
          }
        />
        <Route
          path="/clientes"
          element={
            <ProtectedRoute>
              <ClientesDashboard />
            </ProtectedRoute>
          }
        />
        <Route
          path="/veiculos"
          element={
            <ProtectedRoute>
              <Veiculos />
            </ProtectedRoute>
          }
        />
        <Route
          path="/categorias"
          element={
            <ProtectedRoute>
              <Categorias />
            </ProtectedRoute>
          }
        />
        <Route
          path="/servicos"
          element={
            <ProtectedRoute>
              <Servicos />
            </ProtectedRoute>
          }
        />
        <Route
          path="/funcionarios"
          element={
            <ProtectedRoute>
              <Funcionarios />
            </ProtectedRoute>
          }
        />
        <Route
          path="/configuracoes"
          element={
            <ProtectedRoute>
              <Configuracoes />
            </ProtectedRoute>
          }
        />
        <Route
          path="/empresa-horarios"
          element={
            <ProtectedRoute>
              <EmpresaHorarios />
            </ProtectedRoute>
          }
        />
        <Route
          path="/empresa-contatos"
          element={
            <ProtectedRoute>
              <EmpresaContatos />
            </ProtectedRoute>
          }
        />
        <Route
          path="/agendamentos"
          element={
            <ProtectedRoute>
              <Agendamentos />
            </ProtectedRoute>
          }
        />
        <Route
          path="/historico-agendamentos"
          element={
            <ProtectedRoute>
              <HistoricoAgendamentos />
            </ProtectedRoute>
          }
        />
        {/* Rota 404 */}
        <Route path="*" element={<NotFound />} />
      </Routes>
    </div>
  );
};

const App = () => (
  <QueryClientProvider client={queryClient}>
    <TooltipProvider>
      <Toaster />
      <Sonner />
      <BrowserRouter>
        <AuthProvider>
          <AppContent />
        </AuthProvider>
      </BrowserRouter>
    </TooltipProvider>
  </QueryClientProvider>
);

export default App;