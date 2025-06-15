package com.boxpro.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.boxpro.entity.Usuario;
import com.boxpro.entity.Veiculo;

@Repository
public interface VeiculoRepository extends JpaRepository<Veiculo, Long>{

    Page<Veiculo> findAll(Pageable pageable);
    Page<Veiculo> findByClienteId(Long clienteId, Pageable pageable);
    
    List<Veiculo> findByClienteId(Long clienteId);
    Optional<Veiculo> findByPlaca(String placa);
}
