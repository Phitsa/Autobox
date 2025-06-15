package com.boxpro.mapper;

import com.boxpro.dto.request.VeiculoRequestDTO;
import com.boxpro.dto.response.VeiculoResponseDTO;
import com.boxpro.entity.Usuario;
import com.boxpro.entity.Veiculo;

public class VeiculoMapper {

    public static Veiculo toEntity(VeiculoRequestDTO dto, Usuario cliente) {
        Veiculo veiculo = new Veiculo();
        veiculo.setMarca(dto.getMarca());
        veiculo.setModelo(dto.getModelo());
        veiculo.setAno(dto.getAno());
        veiculo.setPlaca(dto.getPlaca());
        veiculo.setCor(dto.getCor());
        veiculo.setCliente(cliente);
        return veiculo;
    }

    public static VeiculoResponseDTO toDTO(Veiculo veiculo) {
        return new VeiculoResponseDTO(
            veiculo.getId(),
            veiculo.getMarca(),
            veiculo.getModelo(),
            veiculo.getAno(),
            veiculo.getPlaca(),
            veiculo.getCor(),
            veiculo.getCliente().getNome()
        );
    }
}
