package com.boxpro.repository;

import com.boxpro.entity.Veiculo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Year;
import java.util.List;
import java.util.Optional;

@Repository
public interface VeiculoRepository extends JpaRepository<Veiculo, Integer> {
    
    // Buscar por placa
    Optional<Veiculo> findByPlaca(String placa);
    
    // Verificar se placa já existe
    boolean existsByPlaca(String placa);
    
    // Buscar veículos por usuário
    List<Veiculo> findByUsuarioId(Integer usuarioId);
    
    // Buscar por marca
    List<Veiculo> findByMarcaIgnoreCase(String marca);
    
    // Buscar por modelo (contém)
    List<Veiculo> findByModeloContainingIgnoreCase(String modelo);
    
    // Buscar por ano
    List<Veiculo> findByAno(Year ano);
    
    // Buscar por cor
    List<Veiculo> findByCorIgnoreCase(String cor);
    
    // Buscar veículos por usuário e modelo
    List<Veiculo> findByUsuarioIdAndModeloContainingIgnoreCase(Integer usuarioId, String modelo);
    
    // Contar veículos por usuário
    Long countByUsuarioId(Integer usuarioId);
    
    // Buscar marcas distintas
    @Query("SELECT DISTINCT v.marca FROM Veiculo v ORDER BY v.marca")
    List<String> findDistinctMarcas();
    
    // Buscar veículos com agendamentos
    @Query("SELECT DISTINCT v FROM Veiculo v JOIN FETCH v.agendamentos WHERE v.usuario.id = :usuarioId")
    List<Veiculo> findByUsuarioIdWithAgendamentos(@Param("usuarioId") Integer usuarioId);
}