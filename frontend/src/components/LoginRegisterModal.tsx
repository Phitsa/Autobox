import React, { useState } from 'react';
import { X, User, Lock, Mail, Phone, CreditCard, Eye, EyeOff, Shield } from 'lucide-react';
import { LoginRegisterModalProps, LoginData, RegisterData, User as UserType } from '../types/auth';

const LoginRegisterModal: React.FC<LoginRegisterModalProps> = ({ isOpen, onClose, onLoginSuccess }) => {
  const [isLogin, setIsLogin] = useState(true);
  const [loading, setLoading] = useState(false);
  const [showPassword, setShowPassword] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  // Estados para Login
  const [loginData, setLoginData] = useState({
    email: '',
    senha: ''
  });

  // Estados para Registro
  const [registerData, setRegisterData] = useState({
    nome: '',
    email: '',
    senha: '',
    telefone: '',
    cpf: '',
    tipoUsuario: 'ADMIN'
  });

  // Função para fazer login
  const handleLogin = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');

    try {
      const response = await fetch('http://localhost:8080/api/auth/login', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(loginData)
      });

      const data = await response.json();

      if (response.ok) {
        // Salvar token no localStorage
        localStorage.setItem('boxpro_token', data.token);
        localStorage.setItem('boxpro_user', JSON.stringify({
          id: data.id,
          nome: data.nome,
          email: data.email,
          tipoUsuario: data.tipoUsuario
        }));

        setSuccess('Login realizado com sucesso!');
        
        // Chamar callback de sucesso
        if (onLoginSuccess) {
          onLoginSuccess(data);
        }

        // Fechar modal após 1 segundo
        setTimeout(() => {
          onClose();
          setSuccess('');
          // Reset form
          setLoginData({ email: '', senha: '' });
        }, 1000);

      } else {
        setError(data.message || 'Email ou senha inválidos');
      }
    } catch (err) {
      setError('Erro de conexão. Verifique se a API está rodando.');
    } finally {
      setLoading(false);
    }
  };

  // Função para registrar
  const handleRegister = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');

    // Validações básicas
    if (registerData.senha.length < 6) {
      setError('A senha deve ter pelo menos 6 caracteres');
      setLoading(false);
      return;
    }

    if (registerData.cpf.replace(/\D/g, '').length !== 11) {
      setError('CPF deve ter 11 dígitos');
      setLoading(false);
      return;
    }

    try {
      const response = await fetch('http://localhost:8080/api/auth/register', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          ...registerData,
          cpf: registerData.cpf.replace(/\D/g, ''), // Remove formatação
          telefone: registerData.telefone.replace(/\D/g, '') // Remove formatação
        })
      });

      const data = await response.json();

      if (response.ok) {
        // Salvar token no localStorage (auto-login após registro)
        localStorage.setItem('boxpro_token', data.token);
        localStorage.setItem('boxpro_user', JSON.stringify({
          id: data.id,
          nome: data.nome,
          email: data.email,
          tipoUsuario: data.tipoUsuario
        }));

        setSuccess('Usuário registrado com sucesso!');
        
        // Chamar callback de sucesso
        if (onLoginSuccess) {
          onLoginSuccess(data);
        }

        // Fechar modal após 1 segundo
        setTimeout(() => {
          onClose();
          setSuccess('');
          // Reset form
          setRegisterData({
            nome: '',
            email: '',
            senha: '',
            telefone: '',
            cpf: '',
            tipoUsuario: 'ADMIN'
          });
        }, 1000);

      } else {
        setError(data.message || 'Erro ao registrar usuário');
      }
    } catch (err) {
      setError('Erro de conexão. Verifique se a API está rodando.');
    } finally {
      setLoading(false);
    }
  };

  // Função para formatar CPF
  const formatCPF = (value) => {
    return value
      .replace(/\D/g, '')
      .replace(/(\d{3})(\d)/, '$1.$2')
      .replace(/(\d{3})(\d)/, '$1.$2')
      .replace(/(\d{3})(\d{1,2})/, '$1-$2')
      .replace(/(-\d{2})\d+?$/, '$1');
  };

  // Função para formatar telefone
  const formatPhone = (value) => {
    return value
      .replace(/\D/g, '')
      .replace(/(\d{2})(\d)/, '($1) $2')
      .replace(/(\d{4,5})(\d{4})/, '$1-$2')
      .replace(/(-\d{4})\d+?$/, '$1');
  };

  // Função para preencher dados de teste
  const fillTestData = (userType) => {
    const testUsers = {
      admin: { email: 'admin@boxpro.com', senha: '123456' },
      funcionario: { email: 'funcionario@boxpro.com', senha: '123456' },
      cliente: { email: 'cliente@boxpro.com', senha: '123456' }
    };
    
    setLoginData(testUsers[userType]);
  };

  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
      <div className="bg-white rounded-xl shadow-2xl w-full max-w-md max-h-[90vh] overflow-y-auto">
        {/* Header */}
        <div className="flex items-center justify-between p-6 border-b bg-gradient-to-r from-blue-600 to-blue-700 rounded-t-xl">
          <div className="flex items-center space-x-2">
            <div className="w-8 h-8 bg-white rounded-full flex items-center justify-center">
              <Shield className="w-5 h-5 text-blue-600" />
            </div>
            <h2 className="text-2xl font-bold text-white">
              {isLogin ? 'Login BoxPro' : 'Novo Usuário'}
            </h2>
          </div>
          <button 
            onClick={onClose}
            className="text-white hover:text-gray-200 transition-colors"
          >
            <X size={24} />
          </button>
        </div>

        {/* Tabs */}
        <div className="flex border-b">
          <button
            className={`flex-1 py-3 px-4 text-center font-medium transition-all duration-200 ${
              isLogin 
                ? 'bg-blue-50 text-blue-600 border-b-2 border-blue-600' 
                : 'text-gray-500 hover:text-gray-700 hover:bg-gray-50'
            }`}
            onClick={() => {
              setIsLogin(true);
              setError('');
              setSuccess('');
            }}
          >
            Login
          </button>
          <button
            className={`flex-1 py-3 px-4 text-center font-medium transition-all duration-200 ${
              !isLogin 
                ? 'bg-blue-50 text-blue-600 border-b-2 border-blue-600' 
                : 'text-gray-500 hover:text-gray-700 hover:bg-gray-50'
            }`}
            onClick={() => {
              setIsLogin(false);
              setError('');
              setSuccess('');
            }}
          >
            Registrar
          </button>
        </div>

        {/* Content */}
        <div className="p-6">
          {/* Mensagens */}
          {error && (
            <div className="mb-4 p-3 bg-red-50 border border-red-200 text-red-700 rounded-lg flex items-center">
              <div className="w-2 h-2 bg-red-500 rounded-full mr-2"></div>
              {error}
            </div>
          )}
          
          {success && (
            <div className="mb-4 p-3 bg-green-50 border border-green-200 text-green-700 rounded-lg flex items-center">
              <div className="w-2 h-2 bg-green-500 rounded-full mr-2"></div>
              {success}
            </div>
          )}

          {/* Login Form */}
          {isLogin ? (
            <div className="space-y-4">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Email
                </label>
                <div className="relative">
                  <Mail className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400" size={20} />
                  <input
                    type="email"
                    required
                    className="w-full pl-10 pr-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-all"
                    placeholder="admin@boxpro.com"
                    value={loginData.email}
                    onChange={(e) => setLoginData({...loginData, email: e.target.value})}
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
                    className="w-full pl-10 pr-12 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-all"
                    placeholder="Digite sua senha"
                    value={loginData.senha}
                    onChange={(e) => setLoginData({...loginData, senha: e.target.value})}
                  />
                  <button
                    type="button"
                    className="absolute right-3 top-1/2 transform -translate-y-1/2 text-gray-400 hover:text-gray-600 transition-colors"
                    onClick={() => setShowPassword(!showPassword)}
                  >
                    {showPassword ? <EyeOff size={20} /> : <Eye size={20} />}
                  </button>
                </div>
              </div>

              <button
                onClick={handleLogin}
                disabled={loading}
                className="w-full bg-gradient-to-r from-blue-600 to-blue-700 hover:from-blue-700 hover:to-blue-800 disabled:from-gray-400 disabled:to-gray-500 text-white font-semibold py-3 px-4 rounded-lg transition-all transform hover:scale-[1.02] disabled:hover:scale-100"
              >
                {loading ? (
                  <div className="flex items-center justify-center">
                    <div className="animate-spin rounded-full h-5 w-5 border-b-2 border-white mr-2"></div>
                    Entrando...
                  </div>
                ) : 'Entrar'}
              </button>

              {/* Usuários de teste */}
              <div className="mt-4 p-4 bg-gray-50 rounded-lg border">
                <p className="text-sm text-gray-600 font-medium mb-3 flex items-center">
                  <Shield className="w-4 h-4 mr-1" />
                  Usuários de teste:
                </p>
                <div className="space-y-2">
                  <button
                    type="button"
                    onClick={() => fillTestData('admin')}
                    className="w-full text-left text-xs bg-white p-2 rounded border hover:bg-blue-50 transition-colors"
                  >
                    <span className="font-medium text-blue-600">Admin:</span> admin@boxpro.com / 123456
                  </button>
                  <button
                    type="button"
                    onClick={() => fillTestData('funcionario')}
                    className="w-full text-left text-xs bg-white p-2 rounded border hover:bg-green-50 transition-colors"
                  >
                    <span className="font-medium text-green-600">Funcionário:</span> funcionario@boxpro.com / 123456
                  </button>
                  <button
                    type="button"
                    onClick={() => fillTestData('cliente')}
                    className="w-full text-left text-xs bg-white p-2 rounded border hover:bg-purple-50 transition-colors"
                  >
                    <span className="font-medium text-purple-600">Cliente:</span> cliente@boxpro.com / 123456
                  </button>
                </div>
              </div>
            </div>
          ) : (
            /* Register Form */
            <div className="space-y-4">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Nome Completo
                </label>
                <div className="relative">
                  <User className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400" size={20} />
                  <input
                    type="text"
                    required
                    className="w-full pl-10 pr-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-all"
                    placeholder="Digite seu nome completo"
                    value={registerData.nome}
                    onChange={(e) => setRegisterData({...registerData, nome: e.target.value})}
                  />
                </div>
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Email
                </label>
                <div className="relative">
                  <Mail className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400" size={20} />
                  <input
                    type="email"
                    required
                    className="w-full pl-10 pr-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-all"
                    placeholder="seu@email.com"
                    value={registerData.email}
                    onChange={(e) => setRegisterData({...registerData, email: e.target.value})}
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
                    minLength="6"
                    className="w-full pl-10 pr-12 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-all"
                    placeholder="Mínimo 6 caracteres"
                    value={registerData.senha}
                    onChange={(e) => setRegisterData({...registerData, senha: e.target.value})}
                  />
                  <button
                    type="button"
                    className="absolute right-3 top-1/2 transform -translate-y-1/2 text-gray-400 hover:text-gray-600 transition-colors"
                    onClick={() => setShowPassword(!showPassword)}
                  >
                    {showPassword ? <EyeOff size={20} /> : <Eye size={20} />}
                  </button>
                </div>
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Telefone
                </label>
                <div className="relative">
                  <Phone className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400" size={20} />
                  <input
                    type="tel"
                    required
                    className="w-full pl-10 pr-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-all"
                    placeholder="(11) 99999-9999"
                    value={registerData.telefone}
                    onChange={(e) => setRegisterData({...registerData, telefone: formatPhone(e.target.value)})}
                  />
                </div>
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  CPF
                </label>
                <div className="relative">
                  <CreditCard className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400" size={20} />
                  <input
                    type="text"
                    required
                    className="w-full pl-10 pr-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-all"
                    placeholder="000.000.000-00"
                    value={registerData.cpf}
                    onChange={(e) => setRegisterData({...registerData, cpf: formatCPF(e.target.value)})}
                  />
                </div>
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Tipo de Usuário
                </label>
                <div className="relative">
                  <Shield className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400" size={20} />
                  <select
                    value={registerData.tipoUsuario}
                    onChange={(e) => setRegisterData({...registerData, tipoUsuario: e.target.value})}
                    className="w-full pl-10 pr-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-all appearance-none bg-white"
                  >
                    <option value="ADMIN">Administrador</option>
                    <option value="FUNCIONARIO">Funcionário</option>
                    <option value="CLIENTE">Cliente</option>
                  </select>
                </div>
              </div>

              <button
                onClick={handleRegister}
                disabled={loading}
                className="w-full bg-gradient-to-r from-green-600 to-green-700 hover:from-green-700 hover:to-green-800 disabled:from-gray-400 disabled:to-gray-500 text-white font-semibold py-3 px-4 rounded-lg transition-all transform hover:scale-[1.02] disabled:hover:scale-100"
              >
                {loading ? (
                  <div className="flex items-center justify-center">
                    <div className="animate-spin rounded-full h-5 w-5 border-b-2 border-white mr-2"></div>
                    Registrando...
                  </div>
                ) : 'Registrar'}
              </button>

              <p className="text-xs text-gray-500 text-center mt-4">
                Ao registrar-se, você concorda com nossos termos de uso e política de privacidade.
              </p>
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default LoginRegisterModal;
