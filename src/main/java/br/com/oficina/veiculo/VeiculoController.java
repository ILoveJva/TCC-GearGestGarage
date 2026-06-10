package br.com.oficina.veiculo;

import br.com.oficina.veiculo.dto.VeiculoResponseDTO;
import java.util.List;

public class VeiculoController {
    private final VeiculoService service;
    public VeiculoController(VeiculoService service) { this.service = service; }

    public List<MontadoraEntity> montadoras() { return service.listarMontadoras(); }
    public List<ModeloEntity> modelos(long idMontadora) { return service.listarModelos(idMontadora); }

    public VeiculoEntity cadastrar(String tipo, String placa, long idMontadora, long idModelo, long idCliente) {
        return service.cadastrar(tipo, placa, idMontadora, idModelo, idCliente);
    }
    public VeiculoResponseDTO porId(long id) { return service.buscarDTO(id); }
    public List<VeiculoResponseDTO> todos() { return service.listar(); }
}
