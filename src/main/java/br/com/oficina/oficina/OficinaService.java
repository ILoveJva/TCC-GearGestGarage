package br.com.oficina.oficina;

import br.com.oficina.shared.exception.RecursoNaoEncontradoException;
import java.util.List;

public class OficinaService {
    private final OficinaRepository repository;
    public OficinaService(OficinaRepository repository) { this.repository = repository; }

    public OficinaEntity cadastrar(String nome, String endereco, String telefone, String cnpj) {
        return repository.salvar(new OficinaEntity(null, nome, endereco, telefone, cnpj));
    }
    public OficinaEntity buscar(long id) {
        OficinaEntity o = repository.buscarPorId(id);
        if (o == null) throw new RecursoNaoEncontradoException("Oficina " + id + " nao encontrada");
        return o;
    }
    public List<OficinaEntity> listar() { return repository.listarTodas(); }
}
