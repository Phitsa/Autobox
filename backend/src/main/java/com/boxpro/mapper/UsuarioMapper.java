package com.boxpro.mapper;

import com.boxpro.dto.request.UsuarioCreateRequest;
import com.boxpro.dto.request.UsuarioUpdateRequest;
import com.boxpro.dto.response.UsuarioResponse;
import com.boxpro.entity.Usuario;
import org.springframework.stereotype.Component;

@Component
public class UsuarioMapper {
    
    public Usuario toEntity(UsuarioCreateRequest request) {
        if (request == null) return null;
        
        Usuario usuario = new Usuario();
        usuario.setNome(request.getNome());
        usuario.setEmail(request.getEmail());
        usuario.setSenha(request.getSenha());
        usuario.setTelefone(request.getTelefone());
        usuario.setTipoUsuario(request.getTipoUsuario());
        
        return usuario;
    }
    
    public void updateEntity(Usuario usuario, UsuarioUpdateRequest request) {
        if (request == null) return;
        
        usuario.setNome(request.getNome());
        usuario.setEmail(request.getEmail());
        usuario.setTelefone(request.getTelefone());
        
        if (request.getTipoUsuario() != null) {
            usuario.setTipoUsuario(request.getTipoUsuario());
        }
    }
    
    public UsuarioResponse toResponse(Usuario usuario) {
        if (usuario == null) return null;
        
        return UsuarioResponse.builder()
                .id(usuario.getId())
                .nome(usuario.getNome())
                .email(usuario.getEmail())
                .telefone(usuario.getTelefone())
                .tipoUsuario(usuario.getTipoUsuario())
                .createdAt(usuario.getCreatedAt())
                .updatedAt(usuario.getUpdatedAt())
                .totalVeiculos(usuario.getVeiculos() != null ? usuario.getVeiculos().size() : 0)
                .totalAgendamentos(usuario.getAgendamentos() != null ? usuario.getAgendamentos().size() : 0)
                .build();
    }
}