package br.com.oficina.oficina;

import br.com.oficina.shared.config.*;
import java.util.ArrayList;
import java.util.List;

public class OficinaRepository {
    private final Tabela tabela;
    public OficinaRepository(Conexao con) { this.tabela = con.tabela("oficina"); }

    private OficinaEntity map(Registro r) {
        return new OficinaEntity(r.getLong("id_oficina"), r.get("nome"),
            r.get("endereco"), r.get("telefone"), r.get("cnpj"));
    }
    public OficinaEntity salvar(OficinaEntity o) {
        long id = tabela.inserir(new Registro()
            .set("nome", o.getNome()).set("endereco", o.getEndereco())
            .set("telefone", o.getTelefone()).set("cnpj", o.getCnpj()));
        o.setIdOficina(id);
        return o;
    }
    public OficinaEntity buscarPorId(long id) {
        Registro r = tabela.buscarPorId(id);
        return r == null ? null : map(r);
    }
    public List<OficinaEntity> listarTodas() {
        List<OficinaEntity> out = new ArrayList<>();
        for (Registro r : tabela.registros()) out.add(map(r));
        return out;
    }
}
