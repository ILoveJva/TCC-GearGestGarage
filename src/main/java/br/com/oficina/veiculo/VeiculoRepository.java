package br.com.oficina.veiculo;

import br.com.oficina.shared.config.*;
import java.util.ArrayList;
import java.util.List;

/** Acesso a veiculo, modelo, montadora e detalhes_veiculo. */
public class VeiculoRepository {
    private final Tabela tVeiculo;
    private final Tabela tModelo;
    private final Tabela tMontadora;
    private final Tabela tDetalhes;

    public VeiculoRepository(Conexao con) {
        this.tVeiculo = con.tabela("veiculo");
        this.tModelo = con.tabela("modelo");
        this.tMontadora = con.tabela("montadora");
        this.tDetalhes = con.tabela("detalhes_veiculo");
    }

    // ---- veiculo ----
    private VeiculoEntity mapVeiculo(Registro r) {
        return new VeiculoEntity(r.getLong("id_veiculo"), r.get("placa"),
            r.get("tipo_veiculo"), r.getLong("id_cliente"), r.getLong("id_modelo"));
    }

    public VeiculoEntity salvar(VeiculoEntity v) {
        long id = tVeiculo.inserir(new Registro()
            .set("placa", v.getPlaca()).set("tipo_veiculo", v.getTipoVeiculo())
            .set("id_cliente", v.getIdCliente()).set("id_modelo", v.getIdModelo()));
        v.setIdVeiculo(id);
        return v;
    }

    public VeiculoEntity buscarPorId(long id) {
        Registro r = tVeiculo.buscarPorId(id);
        return r == null ? null : mapVeiculo(r);
    }

    public List<VeiculoEntity> listarTodos() {
        List<VeiculoEntity> out = new ArrayList<>();
        for (Registro r : tVeiculo.registros()) out.add(mapVeiculo(r));
        return out;
    }

    public List<VeiculoEntity> listarPorCliente(long idCliente) {
        List<VeiculoEntity> out = new ArrayList<>();
        for (Registro r : tVeiculo.filtrar(v -> v.getLong("id_cliente") == idCliente))
            out.add(mapVeiculo(r));
        return out;
    }

    // ---- detalhes ----
    public DetalhesVeiculoEntity salvarDetalhes(DetalhesVeiculoEntity d) {
        long id = tDetalhes.inserir(new Registro()
            .set("id_veiculo", d.getIdVeiculo()).set("motor", d.getMotor())
            .set("cambio", d.getCambio()).set("direcao", d.getDirecao())
            .set("sistema_freios", d.getSistemaFreios()).set("cor", d.getCor()).set("vin", d.getVin()));
        d.setIdDetalhes(id);
        return d;
    }

    public DetalhesVeiculoEntity buscarDetalhesPorVeiculo(long idVeiculo) {
        List<Registro> l = tDetalhes.filtrar(d -> d.getLong("id_veiculo") == idVeiculo);
        if (l.isEmpty()) return null;
        Registro r = l.get(0);
        DetalhesVeiculoEntity d = new DetalhesVeiculoEntity();
        d.setIdDetalhes(r.getLong("id_detalhes")); d.setIdVeiculo(r.getLong("id_veiculo"));
        d.setMotor(r.get("motor")); d.setCambio(r.get("cambio")); d.setDirecao(r.get("direcao"));
        d.setSistemaFreios(r.get("sistema_freios")); d.setCor(r.get("cor")); d.setVin(r.get("vin"));
        return d;
    }

    // ---- montadora ----
    public MontadoraEntity salvarMontadora(MontadoraEntity m) {
        long id = tMontadora.inserir(new Registro().set("nome", m.getNome()).set("pais_origem", m.getPaisOrigem()));
        m.setIdMontadora(id);
        return m;
    }
    public MontadoraEntity buscarMontadora(long id) {
        Registro r = tMontadora.buscarPorId(id);
        return r == null ? null : new MontadoraEntity(r.getLong("id_montadora"), r.get("nome"), r.get("pais_origem"));
    }
    public List<MontadoraEntity> listarMontadoras() {
        List<MontadoraEntity> out = new ArrayList<>();
        for (Registro r : tMontadora.registros())
            out.add(new MontadoraEntity(r.getLong("id_montadora"), r.get("nome"), r.get("pais_origem")));
        return out;
    }

    // ---- modelo ----
    public ModeloEntity salvarModelo(ModeloEntity m) {
        long id = tModelo.inserir(new Registro()
            .set("nome", m.getNome()).set("ano", m.getAno()).set("id_montadora", m.getIdMontadora()));
        m.setIdModelo(id);
        return m;
    }
    public ModeloEntity buscarModelo(long id) {
        Registro r = tModelo.buscarPorId(id);
        return r == null ? null : new ModeloEntity(r.getLong("id_modelo"), r.get("nome"),
            r.getInt("ano"), r.getLong("id_montadora"));
    }
    public List<ModeloEntity> listarModelosPorMontadora(long idMontadora) {
        List<ModeloEntity> out = new ArrayList<>();
        for (Registro r : tModelo.filtrar(m -> m.getLong("id_montadora") == idMontadora))
            out.add(new ModeloEntity(r.getLong("id_modelo"), r.get("nome"), r.getInt("ano"), r.getLong("id_montadora")));
        return out;
    }
}
