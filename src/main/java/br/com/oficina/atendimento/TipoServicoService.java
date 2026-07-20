package br.com.oficina.atendimento;

import java.util.List;

public class TipoServicoService {
    private final TipoServicoRepository repository;

    public TipoServicoService(TipoServicoRepository repository) { this.repository = repository; }

    public TipoServicoEntity cadastrar(String nome) { return repository.salvar(nome); }
    public List<TipoServicoEntity> listar() { return repository.listarTodos(); }
}
