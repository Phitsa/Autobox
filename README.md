# BoxPro - Sistema de Gerenciamento de Agendamentos

## 📋 Sobre o Projeto

O **BoxPro** é um sistema web desenvolvido como projeto acadêmico para gerenciamento de agendamentos e serviços automotivos. O sistema foi projetado para auxiliar na precificação e gerenciamento de serviços, tornando o processo mais organizado, completo e livre de erros, além de colaborar para um fluxo de caixa mais eficiente.

### 👥 Equipe de Desenvolvimento

- **Adriel Torres Alves de Alencar**
- **Anthony Felipe Fonseca de França** (Cliente)
- **Aldenor Bezerra de Almeida**
- **Pedro Gabriel da Silva Nolasco** (Desenvolvedor)

## 🎯 Objetivo

Desenvolver uma plataforma que permita:
- Agendamento online de serviços automotivos
- Gerenciamento eficiente de horários e disponibilidade
- Acompanhamento do status dos serviços
- Cadastro e gestão de veículos dos clientes
- Controle administrativo completo dos agendamentos

## 🚀 Tecnologias Utilizadas

### Backend
- **Spring Boot** - Framework Java para desenvolvimento da API REST
- **Maven** - Gerenciador de dependências
- **MySQL** - Banco de dados relacional
- **Docker** - Containerização da aplicação

### Frontend
- **React** - Biblioteca JavaScript para construção da interface
- **Vite** - Build tool e dev server
- **Node.js** - Runtime JavaScript

### DevOps
- **Docker Compose** - Orquestração de containers
- **Git** - Controle de versão

## 📁 Estrutura do Projeto

```
gs-spring-boot-docker/
├── backend/                 # API Spring Boot
│   ├── dockerfile          # Configuração Docker do backend
│   ├── pom.xml            # Dependências Maven
│   ├── src/               # Código-fonte Java
│   └── ...
├── frontend/               # Aplicação React
│   ├── src/               # Código-fonte React
│   ├── public/            # Assets públicos
│   ├── package.json       # Dependências Node.js
│   └── ...
├── docker-compose.yml      # Orquestração dos serviços
├── .gitignore             # Arquivos ignorados pelo Git
└── README.md              # Este arquivo
```

## ⚙️ Funcionalidades Principais

### Para Clientes
- ✅ Login seguro com e-mail e senha
- 📅 Agendamento de serviços com seleção de data e horário
- 🔄 Cancelamento e remarcação de agendamentos
- 🚗 Cadastro de veículos (modelo, ano, placa)
- 📊 Acompanhamento do status do serviço
- 📋 Visualização de lista de serviços disponíveis

### Para Administradores
- 👥 Gerenciamento completo de agendamentos
- 🛠️ Cadastro, edição e remoção de serviços
- 💰 Definição de preços e descrições
- 📏 Controle de regras de cancelamento
- 📈 Visualização de todos os agendamentos

## 🔧 Pré-requisitos

- Docker e Docker Compose instalados
- Node.js 18+ (para desenvolvimento local do frontend)
- Java 17+ e Maven (para desenvolvimento local do backend)
- MySQL 8.0+ (se não usar Docker)

## 🏃‍♂️ Como Executar

### Usando Docker Compose (Recomendado)

1. Clone o repositório:
```bash
git clone [URL_DO_REPOSITORIO]
cd gs-spring-boot-docker
```

2. Inicie os containers:
```bash
docker compose up -d
```

3. Acesse a aplicação:
- Frontend: http://localhost:5173
- Backend API: http://localhost:8080
- MySQL: localhost:3306

### Desenvolvimento Local

#### Backend
```bash
cd backend
./mvnw spring-boot:run
```

#### Frontend
```bash
cd frontend
npm install
npm run dev
```

## 📊 Requisitos Não Funcionais

- **Performance**: Tempo de resposta < 3 segundos
- **Disponibilidade**: Mínimo de 95% de uptime
- **Escalabilidade**: Suporte a 1000 usuários simultâneos
- **Segurança**: Senhas criptografadas com bcrypt
- **Compatibilidade**: Chrome, Firefox, Edge, iOS e Android

## 🗃️ Banco de Dados

O sistema utiliza MySQL com as seguintes entidades principais:
- Usuários
- Agendamentos
- Serviços
- Veículos

## 🔒 Segurança

- Autenticação via JWT
- Senhas armazenadas com hash bcrypt
- Validação de dados no backend
- Proteção contra SQL Injection

## 📝 Status do Projeto

🚧 **Em Desenvolvimento** - Projeto acadêmico em andamento

## 📄 Licença

Este é um projeto acadêmico desenvolvido para fins educacionais.

---

**Observação**: Este projeto foi desenvolvido como trabalho acadêmico e está baseado na análise do sistema atual "Cera" utilizado pela Autobox.