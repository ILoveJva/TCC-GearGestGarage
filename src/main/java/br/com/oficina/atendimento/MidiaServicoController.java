package br.com.oficina.atendimento;

import java.io.File;
import java.util.List;

public class MidiaServicoController {
    private final MidiaServicoService service;
    public MidiaServicoController(MidiaServicoService service) { this.service = service; }

    public List<MidiaServicoEntity> listar(long idServico) { return service.listar(idServico); }
    public MidiaServicoEntity adicionar(long idServico, File arquivo, String descricao) {
        return service.adicionar(idServico, arquivo, descricao);
    }
    public void remover(long idMidia) { service.remover(idMidia); }
}
