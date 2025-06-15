package com.boxpro.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.boxpro.dto.request.VeiculoRequestDTO;
import com.boxpro.dto.response.VeiculoResponseDTO;
import com.boxpro.service.VeiculoService;

import io.swagger.v3.oas.annotations.parameters.RequestBody;

@RestController
@RequestMapping("/api/veiculos")
public class VeiculoController {
    
    @Autowired
    private VeiculoService veiculoService;

    @PostMapping("/adicionar")
    public ResponseEntity<VeiculoResponseDTO> adicionar(@RequestBody VeiculoRequestDTO dto) {
        System.out.println("DTO completo no controller: " + dto);
        System.out.println("ClienteId recebido no Controller: " + dto.getClienteId());
        VeiculoResponseDTO veiculoSalvo = veiculoService.adicionarVeiculo(dto);
        return ResponseEntity.ok(veiculoSalvo);
    }

    // Listar veículos por cliente
    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<VeiculoResponseDTO>> listarPorCliente(@PathVariable Long clienteId) {
        List<VeiculoResponseDTO> veiculos = veiculoService.listarVeiculosPorCliente(clienteId);
        return ResponseEntity.ok(veiculos);
    }

    // Listar todos os veículos
    @GetMapping("/todos")
    public ResponseEntity<List<VeiculoResponseDTO>> listarTodos() {
        List<VeiculoResponseDTO> veiculos = veiculoService.listarTodosVeiculos();
        return ResponseEntity.ok(veiculos);
    }

    // Remover veículo
    @DeleteMapping("/remover/{id}")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        veiculoService.removerVeiculo(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/pagina")
    public ResponseEntity<Page<VeiculoResponseDTO>> listarVeiculosPaginados(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        Page<VeiculoResponseDTO> veiculosPage = veiculoService.listarVeiculosPaginados(page, size);
        return ResponseEntity.ok(veiculosPage);
    }

    @GetMapping("/cliente/{clienteId}/pagina")
    public ResponseEntity<Page<VeiculoResponseDTO>> listarVeiculosPorClientePaginados(
            @PathVariable Long clienteId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        Page<VeiculoResponseDTO> veiculosPage = veiculoService.listarVeiculosPorClientePaginados(clienteId, page, size);
        return ResponseEntity.ok(veiculosPage);
    }



    // Verificar se a placa existe
    @GetMapping("/placa/existe/{placa}")
    public ResponseEntity<Boolean> placaExiste(@PathVariable String placa) {
        boolean existe = veiculoService.placaExiste(placa);
        return ResponseEntity.ok(existe);
    }
    
    // Buscar veículo por placa
    @GetMapping("/placa/{placa}")
    public ResponseEntity<VeiculoResponseDTO> buscarPorPlaca(@PathVariable String placa) {
        VeiculoResponseDTO veiculo = veiculoService.buscarVeiculoPorPlaca(placa);
        return ResponseEntity.ok(veiculo);
    } 
}
