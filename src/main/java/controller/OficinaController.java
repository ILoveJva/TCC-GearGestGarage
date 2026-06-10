package controller;

import br.com.oficina.shared.config.*;
import br.com.oficina.oficina.OficinaEntity;
import br.com.oficina.usuario.ClienteEntity;
import br.com.oficina.veiculo.*;
import br.com.oficina.veiculo.dto.VeiculoResponseDTO;
import br.com.oficina.atendimento.dto.ServicoResponseDTO;

import model.Cliente;
import model.ItemServico;
import model.Modelo;
import model.Montadora;
import model.OrdemDeServico;
import model.Veiculo;

import java.util.ArrayList;
import java.util.List;

/**
 * Fachada que conecta a camada View (model.* / controller.*) ao backend
 * modular br.com.oficina (banco MySQL GearGestGarage).
 */
public class OficinaController {

    public static OficinaController instancia;

    private final OficinaController_backendBridge bridge;
    private model.Oficina oficinaLogada;

    public OficinaController() {
        Conexao con = DatabaseConfig.inicializar(DatabaseConfig.DIRETORIO_PADRAO);
        Seed.popular(con);
        this.bridge = new OficinaController_backendBridge(con);
        instancia = this;

        List<model.Oficina> ofs = listarTodas();
        if (!ofs.isEmpty()) this.oficinaLogada = ofs.get(0);
    }

    // ============== OFICINA ==============
    public List<model.Oficina> listarTodas() {
        List<model.Oficina> out = new ArrayList<>();
        for (OficinaEntity o : bridge.oficinaController.todas()) {
            out.add(new model.Oficina(o.getIdOficina(), o.getNome(), o.getCnpj(),
                "Manutencao automotiva geral", "oficina@geargest.com", "123456"));
        }
        return out;
    }
    public model.Oficina getOficina() {
        if (oficinaLogada != null) return oficinaLogada;
        List<model.Oficina> ofs = listarTodas();
        return ofs.isEmpty() ? new model.Oficina(0,"Oficina","-","-","-","-") : ofs.get(0);
    }
    public void setOficinaLogada(model.Oficina o) { this.oficinaLogada = o; }
    public model.Oficina getOficinaLogada() { return oficinaLogada; }

    public boolean autenticar(String email, String senha) {
        return bridge.usuarioController.login(email, senha) != null;
    }

    // ============== CLIENTES ==============
    public ArrayList<Cliente> listarClientes() {
        ArrayList<Cliente> out = new ArrayList<>();
        for (var dto : bridge.clienteController.todos()) {
            Cliente c = new Cliente(dto.idCliente(), dto.nome(), dto.cpf(), "", dto.email());
            anexarVeiculos(c);
            out.add(c);
        }
        return out;
    }
    public ArrayList<Cliente> getTodosClientes() { return listarClientes(); }

    public void salvarCliente(String nome, String cpf, String celular, String email) {
        // o schema GGG02 nao tem CPF em usuario; cpf e ignorado na persistencia
        bridge.clienteController.cadastrar(nome, email, "1234", celular, getOficina().getIdOficina());
    }

    // ============== VEICULOS ==============
    public List<Veiculo> listarVeiculos() {
        List<Veiculo> out = new ArrayList<>();
        for (VeiculoResponseDTO dto : bridge.veiculoController.todos()) out.add(montarVeiculo(dto));
        return out;
    }
    public void salvarVeiculo(Modelo modelo, String tipo, int ano, String placa) {
        salvarVeiculo(modelo, tipo, ano, placa, 0L);
    }
    public void salvarVeiculo(Modelo modelo, String tipo, int ano, String placa, long idCliente) {
        long idMontadora = modelo.getMontadora() != null ? modelo.getMontadora().getIdMontadora() : 0L;
        bridge.veiculoController.cadastrar(tipo, placa, idMontadora, modelo.getIdModelo(), idCliente);
    }
    private Veiculo montarVeiculo(VeiculoResponseDTO dto) {
        Montadora mont = new Montadora(0, dto.montadora(), "");
        Modelo mod = new Modelo(0, dto.modelo(), dto.ano(), mont);
        mont.addModelo(mod);
        return new Veiculo(dto.idVeiculo(), dto.tipo(), "", dto.placa(), "", mod);
    }
    private void anexarVeiculos(Cliente c) {
        for (VeiculoResponseDTO dto : bridge.veiculoController.todos())
            if (dto.idCliente() != null && dto.idCliente() == c.getIdUsuario())
                c.addVeiculo(montarVeiculo(dto));
    }

    // ============== SERVICOS (O.S.) ==============
    public List<OrdemDeServico> listarOS() {
        List<OrdemDeServico> out = new ArrayList<>();
        for (ServicoResponseDTO dto : bridge.servicoController.todas()) {
            OrdemDeServico os = new OrdemDeServico(dto.idServico(), dto.titulo(),
                mapStatusOS(dto.status()), null);
            for (var it : dto.itens())
                os.getItensServico().add(new ItemServico(it.id(), mapStatusItem(it.status()), null, it.descricao()));
            out.add(os);
        }
        return out;
    }
    private OrdemDeServico.Status mapStatusOS(String s) {
        if (s == null) return OrdemDeServico.Status.ABERTA;
        return switch (s) {
            case "CONCLUIDA" -> OrdemDeServico.Status.CONCLUIDA;
            case "EM_ANDAMENTO" -> OrdemDeServico.Status.EM_ANDAMENTO;
            default -> OrdemDeServico.Status.ABERTA;
        };
    }
    private ItemServico.Status mapStatusItem(String s) {
        if (s == null) return ItemServico.Status.PENDENTE;
        return "CONCLUIDO".equals(s) ? ItemServico.Status.CONCLUIDO : ItemServico.Status.PENDENTE;
    }

    // catalogo p/ DB_Montadora
    public List<Montadora> montadorasComModelos() {
        List<Montadora> out = new ArrayList<>();
        for (MontadoraEntity me : bridge.veiculoController.montadoras()) {
            Montadora m = new Montadora(me.getIdMontadora(), me.getNome(), me.getPaisOrigem());
            for (ModeloEntity mod : bridge.veiculoController.modelos(me.getIdMontadora()))
                m.addModelo(new Modelo(mod.getIdModelo(), mod.getNome(), mod.getAno(), m));
            out.add(m);
        }
        return out;
    }
}
