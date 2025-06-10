package com.boxpro.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.boxpro.entity.Usuario;
import com.boxpro.entity.Veiculo;

@Repository
public interface VeiculoRepository extends JpaRepository<Veiculo, Long>{
    List<Veiculo> findByClienteId(Long clienteId);
    Optional<Veiculo> findByPlaca(String placa);
}
