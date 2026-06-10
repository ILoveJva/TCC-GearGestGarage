package br.com.oficina.oficina;

import java.util.List;

public class OficinaController {
    private final OficinaService service;
    public OficinaController(OficinaService service) { this.service = service; }

    public OficinaEntity criar(String nome, String endereco, String telefone, String cnpj) {
        return service.cadastrar(nome, endereco, telefone, cnpj);
    }
    public OficinaEntity porId(long id) { return service.buscar(id); }
    public List<OficinaEntity> todas() { return service.listar(); }
}
