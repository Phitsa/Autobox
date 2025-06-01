package com.boxpro.config;

import com.boxpro.entity.Usuario;
import com.boxpro.entity.enums.TipoUsuario;
import com.boxpro.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

@Component
public class DataLoader implements CommandLineRunner {

    private static final Logger log = Logger.getLogger(DataLoader.class.getName());

    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        createDefaultUsers();
    }

    private void createDefaultUsers() {
        // Criar usuário admin padrão
        if (!usuarioRepository.existsByEmail("admin@boxpro.com")) {
            Usuario admin = new Usuario();
            admin.setNome("Administrador");
            admin.setEmail("admin@boxpro.com");
            admin.setSenha(passwordEncoder.encode("123456"));
            admin.setTelefone("(11) 99999-9999");
            admin.setCpf("000.000.000-00");
            admin.setTipoUsuario(TipoUsuario.ADMIN);
            admin.setAtivo(true);
            
            usuarioRepository.save(admin);
            log.info("Usuário admin padrão criado: admin@boxpro.com / 123456");
        }

        // Criar usuário funcionário padrão
        if (!usuarioRepository.existsByEmail("funcionario@boxpro.com")) {
            Usuario funcionario = new Usuario();
            funcionario.setNome("João Funcionário");
            funcionario.setEmail("funcionario@boxpro.com");
            funcionario.setSenha(passwordEncoder.encode("123456"));
            funcionario.setTelefone("(11) 88888-8888");
            funcionario.setCpf("111.111.111-11");
            funcionario.setTipoUsuario(TipoUsuario.FUNCIONARIO);
            funcionario.setAtivo(true);
            
            usuarioRepository.save(funcionario);
            log.info("Usuário funcionário padrão criado: funcionario@boxpro.com / 123456");
        }

        // Criar usuário cliente padrão
        if (!usuarioRepository.existsByEmail("cliente@boxpro.com")) {
            Usuario cliente = new Usuario();
            cliente.setNome("Maria Cliente");
            cliente.setEmail("cliente@boxpro.com");
            cliente.setSenha(passwordEncoder.encode("123456"));
            cliente.setTelefone("(11) 77777-7777");
            cliente.setCpf("222.222.222-22");
            cliente.setTipoUsuario(TipoUsuario.CLIENTE);
            cliente.setAtivo(true);
            
            usuarioRepository.save(cliente);
            log.info("Usuário cliente padrão criado: cliente@boxpro.com / 123456");
        }
    }
}