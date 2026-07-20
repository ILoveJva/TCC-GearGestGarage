package br.com.oficina.atendimento;

import br.com.oficina.shared.config.*;
import java.util.ArrayList;
import java.util.List;

public class TipoServicoRepository {
    private final Tabela t;

    public TipoServicoRepository(Conexao con) { this.t = con.tabela("tipo_servico"); }

    private TipoServicoEntity map(Registro r) {
        return new TipoServicoEntity(r.getLong("id_tipo_servico"), r.get("nome"));
    }

    public TipoServicoEntity salvar(String nome) {
        long id = t.inserir(new Registro().set("nome", nome));
        return new TipoServicoEntity(id, nome);
    }

    public List<TipoServicoEntity> listarTodos() {
        List<TipoServicoEntity> out = new ArrayList<>();
        for (Registro r : t.registros()) out.add(map(r));
        return out;
    }
}
