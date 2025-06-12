package com.boxpro.config;

import com.boxpro.entity.Funcionario;
import com.boxpro.repository.FuncionarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

import com.boxpro.entity.enums.TipoFuncionario;

@Component
public class DataLoader implements CommandLineRunner {

    private static final Logger log = Logger.getLogger(DataLoader.class.getName());

    @Autowired
    private FuncionarioRepository funcionarioRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        createDefaultFuncionarios();
    }

    private void createDefaultFuncionarios() {
        // Criar funcionário admin padrão
        if (!funcionarioRepository.existsByEmail("admin@boxpro.com")) {
            Funcionario admin = new Funcionario();
            admin.setNome("Administrador");
            admin.setEmail("admin@boxpro.com");
            admin.setSenha(passwordEncoder.encode("123456"));
            admin.setTelefone("(11) 99999-9999");
            admin.setCpf("000.000.000-00");
            admin.setTipoFuncionario(TipoFuncionario.ADMIN);
            admin.setAtivo(true);
            
            funcionarioRepository.save(admin);
            log.info("Funcionário admin padrão criado: admin@boxpro.com / 123456");
        }

        // Criar funcionário padrão
        if (!funcionarioRepository.existsByEmail("funcionario@boxpro.com")) {
            Funcionario funcionario = new Funcionario();
            funcionario.setNome("João Funcionário");
            funcionario.setEmail("funcionario@boxpro.com");
            funcionario.setSenha(passwordEncoder.encode("123456"));
            funcionario.setTelefone("(11) 88888-8888");
            funcionario.setCpf("111.111.111-11");
            funcionario.setTipoFuncionario(TipoFuncionario.FUNCIONARIO);
            funcionario.setAtivo(true);
            
            funcionarioRepository.save(funcionario);
            log.info("Funcionário padrão criado: funcionario@boxpro.com / 123456");
        }
    }
}
