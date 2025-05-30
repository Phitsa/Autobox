package com.boxpro.config;

import com.boxpro.entity.*;
import com.boxpro.entity.enums.StatusAgendamento;
import com.boxpro.entity.enums.TipoUsuario;
import com.boxpro.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Year;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataInitializer {

    @Bean
    CommandLineRunner init(
            UsuarioRepository usuarioRepository,
            CategoriaServicoRepository categoriaRepository,
            ServicoRepository servicoRepository,
            VeiculoRepository veiculoRepository,
            AgendamentoRepository agendamentoRepository,
            PasswordEncoder passwordEncoder) {
        
        return args -> {
            log.info("Iniciando carga de dados de teste...");
            
            // Verificar se já tem dados
            if (usuarioRepository.count() > 0) {
                log.info("Banco já possui dados. Pulando inicialização.");
                return;
            }
            
            // 1. Criar usuários
            log.info("Criando usuários...");
            
            // Admin
            Usuario admin = new Usuario();
            admin.setNome("Administrador");
            admin.setEmail("admin@boxpro.com");
            admin.setSenha(passwordEncoder.encode("admin123"));
            admin.setTelefone("(11) 99999-9999");
            admin.setTipoUsuario(TipoUsuario.ADMINISTRADOR);
            admin = usuarioRepository.save(admin);
            
            // Cliente 1
            Usuario cliente1 = new Usuario();
            cliente1.setNome("João Silva");
            cliente1.setEmail("joao@email.com");
            cliente1.setSenha(passwordEncoder.encode("senha123"));
            cliente1.setTelefone("(11) 98888-8888");
            cliente1.setTipoUsuario(TipoUsuario.CLIENTE);
            cliente1 = usuarioRepository.save(cliente1);
            
            // Cliente 2
            Usuario cliente2 = new Usuario();
            cliente2.setNome("Maria Santos");
            cliente2.setEmail("maria@email.com");
            cliente2.setSenha(passwordEncoder.encode("senha123"));
            cliente2.setTelefone("(11) 97777-7777");
            cliente2.setTipoUsuario(TipoUsuario.CLIENTE);
            cliente2 = usuarioRepository.save(cliente2);
            
            log.info("✓ Usuários criados");
            
            // 2. Criar categorias
            log.info("Criando categorias...");
            
            CategoriaServico catLavagem = new CategoriaServico();
            catLavagem.setNome("Lavagem");
            catLavagem.setDescricao("Serviços de lavagem");
            catLavagem = categoriaRepository.save(catLavagem);
            
            CategoriaServico catPolimento = new CategoriaServico();
            catPolimento.setNome("Polimento");
            catPolimento.setDescricao("Serviços de polimento e enceramento");
            catPolimento = categoriaRepository.save(catPolimento);
            
            CategoriaServico catDetalhamento = new CategoriaServico();
            catDetalhamento.setNome("Detalhamento");
            catDetalhamento.setDescricao("Serviços de detalhamento automotivo");
            catDetalhamento = categoriaRepository.save(catDetalhamento);
            
            log.info("✓ Categorias criadas");
            
            // 3. Criar serviços
            log.info("Criando serviços...");
            
            // Serviços de Lavagem
            Servico lavSimples = new Servico();
            lavSimples.setNome("Lavagem Simples");
            lavSimples.setDescricao("Lavagem externa básica");
            lavSimples.setPreco(new BigDecimal("30.00"));
            lavSimples.setDuracaoEstimada(30);
            lavSimples.setCategoria(catLavagem);
            lavSimples.setAtivo(true);
            servicoRepository.save(lavSimples);
            
            Servico lavCompleta = new Servico();
            lavCompleta.setNome("Lavagem Completa");
            lavCompleta.setDescricao("Lavagem externa e interna");
            lavCompleta.setPreco(new BigDecimal("50.00"));
            lavCompleta.setDuracaoEstimada(60);
            lavCompleta.setCategoria(catLavagem);
            lavCompleta.setAtivo(true);
            servicoRepository.save(lavCompleta);
            
            Servico lavDetalhada = new Servico();
            lavDetalhada.setNome("Lavagem Detalhada");
            lavDetalhada.setDescricao("Lavagem completa com detalhamento");
            lavDetalhada.setPreco(new BigDecimal("80.00"));
            lavDetalhada.setDuracaoEstimada(90);
            lavDetalhada.setCategoria(catLavagem);
            lavDetalhada.setAtivo(true);
            servicoRepository.save(lavDetalhada);
            
            // Serviços de Polimento
            Servico polimento = new Servico();
            polimento.setNome("Polimento");
            polimento.setDescricao("Polimento profissional");
            polimento.setPreco(new BigDecimal("120.00"));
            polimento.setDuracaoEstimada(120);
            polimento.setCategoria(catPolimento);
            polimento.setAtivo(true);
            servicoRepository.save(polimento);
            
            Servico enceramento = new Servico();
            enceramento.setNome("Enceramento");
            enceramento.setDescricao("Aplicação de cera protetora");
            enceramento.setPreco(new BigDecimal("60.00"));
            enceramento.setDuracaoEstimada(60);
            enceramento.setCategoria(catPolimento);
            enceramento.setAtivo(true);
            servicoRepository.save(enceramento);
            
            log.info("✓ Serviços criados");
            
            // 4. Criar veículos
            log.info("Criando veículos...");
            
            Veiculo veiculo1 = new Veiculo();
            veiculo1.setUsuario(cliente1);
            veiculo1.setMarca("Toyota");
            veiculo1.setModelo("Corolla");
            veiculo1.setAno(Year.of(2022));
            veiculo1.setPlaca("ABC1234");
            veiculo1.setCor("Prata");
            veiculo1 = veiculoRepository.save(veiculo1);
            
            Veiculo veiculo2 = new Veiculo();
            veiculo2.setUsuario(cliente1);
            veiculo2.setMarca("Honda");
            veiculo2.setModelo("Civic");
            veiculo2.setAno(Year.of(2021));
            veiculo2.setPlaca("XYZ5678");
            veiculo2.setCor("Preto");
            veiculoRepository.save(veiculo2);
            
            Veiculo veiculo3 = new Veiculo();
            veiculo3.setUsuario(cliente2);
            veiculo3.setMarca("Volkswagen");
            veiculo3.setModelo("Golf");
            veiculo3.setAno(Year.of(2023));
            veiculo3.setPlaca("DEF9012");
            veiculo3.setCor("Branco");
            veiculoRepository.save(veiculo3);
            
            log.info("✓ Veículos criados");
            
            // 5. Criar alguns agendamentos
            log.info("Criando agendamentos...");
            
            // Agendamento futuro
            Agendamento agendamento1 = new Agendamento();
            agendamento1.setUsuario(cliente1);
            agendamento1.setVeiculo(veiculo1);
            agendamento1.setServico(lavCompleta);
            agendamento1.setDataAgendamento(LocalDate.now().plusDays(1));
            agendamento1.setHoraInicio(LocalTime.of(10, 0));
            agendamento1.setHoraFim(LocalTime.of(11, 0));
            agendamento1.setStatus(StatusAgendamento.AGENDADO);
            agendamento1.setValorTotal(lavCompleta.getPreco());
            agendamento1.setObservacoes("Cliente preferencial");
            agendamentoRepository.save(agendamento1);
            
            // Agendamento para hoje
            Agendamento agendamento2 = new Agendamento();
            agendamento2.setUsuario(cliente2);
            agendamento2.setVeiculo(veiculo3);
            agendamento2.setServico(polimento);
            agendamento2.setDataAgendamento(LocalDate.now());
            agendamento2.setHoraInicio(LocalTime.of(14, 0));
            agendamento2.setHoraFim(LocalTime.of(16, 0));
            agendamento2.setStatus(StatusAgendamento.AGENDADO);
            agendamento2.setValorTotal(polimento.getPreco());
            agendamentoRepository.save(agendamento2);
            
            log.info("✓ Agendamentos criados");
            
            log.info("=== DADOS DE TESTE CRIADOS COM SUCESSO ===");
            log.info("Usuários para login:");
            log.info("  Admin: admin@boxpro.com / admin123");
            log.info("  Cliente 1: joao@email.com / senha123");
            log.info("  Cliente 2: maria@email.com / senha123");
            log.info("=========================================");
        };
    }
}