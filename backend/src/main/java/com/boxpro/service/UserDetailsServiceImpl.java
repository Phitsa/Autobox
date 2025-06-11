package com.boxpro.service;

import com.boxpro.entity.Funcionario;
import com.boxpro.repository.FuncionarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private FuncionarioRepository funcionarioRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Funcionario funcionario = funcionarioRepository.findByEmailAndAtivo(username, true)
                .orElseThrow(() -> new UsernameNotFoundException("Funcionário não encontrado: " + username));

        if (funcionario.getBloqueado()) {
            throw new UsernameNotFoundException("Funcionário bloqueado: " + username);
        }

        return funcionario;
    }
}