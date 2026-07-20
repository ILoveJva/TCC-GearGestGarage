package br.com.oficina.atendimento;

import java.util.List;

public class TipoServicoController {
    private final TipoServicoService service;

    public TipoServicoController(TipoServicoService service) { this.service = service; }

    public TipoServicoEntity cadastrar(String nome) { return service.cadastrar(nome); }
    public List<TipoServicoEntity> todos() { return service.listar(); }
}
