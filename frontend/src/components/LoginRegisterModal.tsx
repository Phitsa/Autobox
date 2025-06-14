// components/LoginModal.js (versão com redirecionamento)
import React, { useState } from 'react';
import { X, Lock, Mail, Eye, EyeOff, Shield } from 'lucide-react';
import { useAuth } from '../contexts/AuthContext';
import { useNavigate } from 'react-router-dom';

const LoginModal = ({ isOpen, onClose }) => {
  const [loading, setLoading] = useState(false);
  const [showPassword, setShowPassword] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const { login } = useAuth();
  const navigate = useNavigate(); // ← Adicionar navegação

  // Estados para Login
  const [loginData, setLoginData] = useState({
    email: '',
    senha: ''
  });

  // Função para fazer login
  const handleLogin = async () => {
    if (!loginData.email || !loginData.senha) return;
    
    setLoading(true);
    setError('');

    try {
      console.log('🔄 Tentando fazer login com:', { email: loginData.email });
      
      const response = await fetch('http://localhost:8080/auth/login', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(loginData)
      });

      console.log('📡 Response status:', response.status);
      
      const data = await response.json();
      console.log('📊 Response data:', data);

      if (response.ok) {
        // Salvar token no localStorage
        localStorage.setItem('boxpro_token', data.token);
        localStorage.setItem('boxpro_user', JSON.stringify({
          id: data.id,
          nome: data.nome,
          email: data.email,
          tipoFuncionario: data.tipoFuncionario
        }));

        // Atualizar contexto de autenticação
        login({
          id: data.id,
          nome: data.nome,
          email: data.email,
          tipoFuncionario: data.tipoFuncionario
        });

        setSuccess('Login realizado com sucesso! Redirecionando...');
        console.log('✅ Login bem-sucedido para:', data.email);
        
        // Fechar modal e redirecionar após 800ms
        setTimeout(() => {
          onClose();
          setSuccess('');
          // Reset form
          setLoginData({ email: '', senha: '' });
          
          // ⭐ REDIRECIONAMENTO PARA ADMIN
          navigate('/admin');
          console.log('🔀 Redirecionando para /admin');
          
        }, 800);

      } else {
        console.log('❌ Erro de login:', data.message);
        setError(data.message || 'Email ou senha inválidos');
      }
    } catch (err) {
      console.error('🚨 Erro de conexão:', err);
      setError('Erro de conexão. Verifique se a API está rodando em http://localhost:8080');
    } finally {
      setLoading(false);
    }
  };

  // Função para preencher dados de teste
  const fillTestData = (userType) => {
    const testUsers = {
      admin: { email: 'admin@boxpro.com', senha: '123456' },
      funcionario: { email: 'funcionario@boxpro.com', senha: '123456' }
    };
    
    setLoginData(testUsers[userType]);
  };

  // Limpar formulário ao fechar
  const handleClose = () => {
    setLoginData({ email: '', senha: '' });
    setError('');
    setSuccess('');
    setShowPassword(false);
    onClose();
  };

  // Função para lidar com Enter
  const handleKeyPress = (e) => {
    if (e.key === 'Enter' && loginData.email && loginData.senha && !loading) {
      handleLogin();
    }
  };

  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
      <div className="bg-white rounded-xl shadow-2xl w-full max-w-md">
        {/* Header */}
        <div className="flex items-center justify-between p-6 border-b bg-gradient-to-r from-blue-600 to-blue-700 rounded-t-xl">
          <div className="flex items-center space-x-3">
            <div className="w-10 h-10 bg-white rounded-full flex items-center justify-center">
              <Shield className="w-6 h-6 text-blue-600" />
            </div>
            <div>
              <h2 className="text-2xl font-bold text-white">AutoBox</h2>
              <p className="text-blue-100 text-sm">Sistema de Gestão</p>
            </div>
          </div>
          <button 
            onClick={handleClose}
            className="text-white hover:text-gray-200 transition-colors p-1 rounded-lg hover:bg-white/10"
          >
            <X size={24} />
          </button>
        </div>

        {/* Content */}
        <div className="p-6">
          {/* Mensagens */}
          {error && (
            <div className="mb-4 p-3 bg-red-50 border border-red-200 text-red-700 rounded-lg flex items-center">
              <div className="w-2 h-2 bg-red-500 rounded-full mr-2 flex-shrink-0"></div>
              {error}
            </div>
          )}
          
          {success && (
            <div className="mb-4 p-3 bg-green-50 border border-green-200 text-green-700 rounded-lg flex items-center">
              <div className="w-2 h-2 bg-green-500 rounded-full mr-2 flex-shrink-0"></div>
              {success}
            </div>
          )}

          {/* Login Form */}
          <div className="space-y-5">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Email
              </label>
              <div className="relative">
                <Mail className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400" size={20} />
                <input
                  type="email"
                  required
                  className="w-full pl-10 pr-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-all outline-none"
                  placeholder="Digite seu email"
                  value={loginData.email}
                  onChange={(e) => setLoginData({...loginData, email: e.target.value})}
                  onKeyPress={handleKeyPress}
                />
              </div>
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Senha
              </label>
              <div className="relative">
                <Lock className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400" size={20} />
                <input
                  type={showPassword ? "text" : "password"}
                  required
                  className="w-full pl-10 pr-12 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-all outline-none"
                  placeholder="Digite sua senha"
                  value={loginData.senha}
                  onChange={(e) => setLoginData({...loginData, senha: e.target.value})}
                  onKeyPress={handleKeyPress}
                />
                <button
                  type="button"
                  className="absolute right-3 top-1/2 transform -translate-y-1/2 text-gray-400 hover:text-gray-600 transition-colors p-1 rounded"
                  onClick={() => setShowPassword(!showPassword)}
                >
                  {showPassword ? <EyeOff size={20} /> : <Eye size={20} />}
                </button>
              </div>
            </div>

            <button
              type="button"
              onClick={handleLogin}
              disabled={loading || !loginData.email || !loginData.senha}
              className="w-full bg-gradient-to-r from-blue-600 to-blue-700 hover:from-blue-700 hover:to-blue-800 disabled:from-gray-400 disabled:to-gray-500 text-white font-semibold py-3 px-4 rounded-lg transition-all transform hover:scale-[1.01] disabled:hover:scale-100 disabled:cursor-not-allowed"
            >
              {loading ? (
                <div className="flex items-center justify-center">
                  <div className="animate-spin rounded-full h-5 w-5 border-b-2 border-white mr-2"></div>
                  Entrando...
                </div>
              ) : 'Entrar no Sistema'}
            </button>
          </div>

          {/* Usuários de teste */}
          <div className="mt-6 p-4 bg-gray-50 rounded-lg border">
            <p className="text-sm text-gray-600 font-medium mb-3 flex items-center">
              <Shield className="w-4 h-4 mr-2" />
              Usuários para teste:
            </p>
            <div className="space-y-2">
              <button
                type="button"
                onClick={() => fillTestData('admin')}
                className="w-full text-left text-xs bg-white p-3 rounded border hover:bg-blue-50 hover:border-blue-200 transition-colors group"
              >
                <div className="flex items-center justify-between">
                  <div>
                    <span className="font-medium text-blue-600 group-hover:text-blue-700">Administrador</span>
                    <div className="text-gray-500 mt-1">admin@boxpro.com</div>
                  </div>
                  <div className="text-gray-400 text-xs">Clique para preencher</div>
                </div>
              </button>
              <button
                type="button"
                onClick={() => fillTestData('funcionario')}
                className="w-full text-left text-xs bg-white p-3 rounded border hover:bg-blue-50 hover:border-blue-200 transition-colors group"
              >
                <div className="flex items-center justify-between">
                  <div>
                    <span className="font-medium text-blue-600 group-hover:text-blue-700">Funcionario</span>
                    <div className="text-gray-500 mt-1">funcionario@boxpro.com</div>
                  </div>
                  <div className="text-gray-400 text-xs">Clique para preencher</div>
                </div>
              </button>
            </div>
          </div>
          
          {/* Rodapé */}
          <div className="mt-6 text-center">
            <p className="text-xs text-gray-500">
              AutoBox v1.0 - Sistema de Gestão Automotiva
            </p>
          </div>
        </div>
      </div>
    </div>
  );
};

export default LoginModal;