package controller;

import br.com.oficina.shared.config.*;
import br.com.oficina.oficina.OficinaEntity;
import br.com.oficina.usuario.ClienteEntity;
import br.com.oficina.usuario.FuncionarioEntity;
import br.com.oficina.usuario.UsuarioEntity;
import br.com.oficina.veiculo.*;
import br.com.oficina.veiculo.dto.VeiculoResponseDTO;
import br.com.oficina.atendimento.CatalogoServicoEntity;
import br.com.oficina.atendimento.TipoServicoEntity;
import br.com.oficina.estoque.PecaEntity;
import br.com.oficina.atendimento.dto.OrcamentoResponseDTO;
import br.com.oficina.atendimento.dto.ServicoResponseDTO;

import model.Cliente;
import model.ItemServico;
import model.Modelo;
import model.Montadora;
import model.OrdemDeServico;
import model.Veiculo;

import java.util.ArrayList;
import java.util.List;

public class OficinaController {

    public static OficinaController instancia;

    private final OficinaController_backendBridge bridge;
    private model.Oficina oficinaLogada;
    private UsuarioEntity usuarioLogado;

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
            model.Oficina of = new model.Oficina(o.getIdOficina(), o.getNome(), o.getCnpj(),
                "Manutenção Automotiva Geral", "oficina@geargest.com", "");
            of.setEndereco(o.getEndereco());
            of.setTelefone(o.getTelefone());
            out.add(of);
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
        UsuarioEntity u = bridge.usuarioController.login(email, senha);
        if (u != null) {
            this.usuarioLogado = u;
            this.oficinaLogada = buscarOficinaPorId(u.getIdOficina());
        }
        return u != null;
    }

    private model.Oficina buscarOficinaPorId(Long id) {
        if (id == null) return oficinaLogada;
        for (model.Oficina o : listarTodas())
            if (o.getIdOficina() == id) return o;
        return oficinaLogada;
    }

    // ============== ESCOPO POR OFICINA ==============
    // O login define a oficina ativa; todas as listagens de clientes, veículos,
    // funcionários, orçamentos e ordens de serviço são restritas a ela. O catálogo
    // (montadoras, modelos, peças, serviços) permanece global/compartilhado.

    /** Oficina do usuário logado (fallback: oficina atual/primeira). */
    private long idOficinaAtual() {
        if (usuarioLogado != null && usuarioLogado.getIdOficina() != null)
            return usuarioLogado.getIdOficina();
        return getOficina().getIdOficina();
    }

    /** id_usuario de todos os usuários (clientes e funcionários) da oficina ativa. */
    private java.util.Set<Long> idsUsuariosDaOficina() {
        long idOf = idOficinaAtual();
        java.util.Set<Long> ids = new java.util.HashSet<>();
        for (UsuarioEntity u : bridge.usuarioController.todos())
            if (u.getIdOficina() != null && u.getIdOficina() == idOf && u.getIdUsuario() != null)
                ids.add(u.getIdUsuario());
        return ids;
    }

    /** id_cliente (PK) de todos os clientes da oficina ativa. */
    private java.util.Set<Long> idsClientesDaOficina() {
        java.util.Set<Long> usuarios = idsUsuariosDaOficina();
        java.util.Set<Long> ids = new java.util.HashSet<>();
        for (ClienteEntity c : bridge.clienteController.entidades())
            if (c.getIdUsuario() != null && usuarios.contains(c.getIdUsuario()) && c.getIdCliente() != null)
                ids.add(c.getIdCliente());
        return ids;
    }

    /** Reconfirma a senha do usuário atualmente logado, usada para proteger ações sensíveis. */
    public boolean confirmarSenha(String senha) {
        if (usuarioLogado == null) return false;
        return bridge.usuarioController.login(usuarioLogado.getEmail(), senha) != null;
    }

    // ============== CLIENTES ==============
    public ArrayList<Cliente> listarClientes() {
        java.util.Set<Long> usuarios = idsUsuariosDaOficina();
        ArrayList<Cliente> out = new ArrayList<>();
        for (ClienteEntity ce : bridge.clienteController.entidades()) {
            if (ce.getIdUsuario() == null || !usuarios.contains(ce.getIdUsuario())) continue;
            Cliente c = new Cliente(ce.getIdCliente(), ce.getNome(),
                ce.getCpf() == null ? "" : ce.getCpf(), ce.getTelefone(), ce.getEmail());
            anexarVeiculos(c);
            out.add(c);
        }
        return out;
    }
    public ArrayList<Cliente> getTodosClientes() { return listarClientes(); }

    public void salvarCliente(String nome, String cpf, String celular, String email) {
        bridge.clienteController.cadastrar(nome, cpf, email, "1234", celular, getOficina().getIdOficina());
    }

    public void atualizarCliente(long idCliente, String nome, String cpf, String email, String telefone) {
        bridge.clienteController.atualizar(idCliente, nome, cpf, email, telefone);
    }

    public void excluirCliente(long idCliente) {
        bridge.clienteController.remover(idCliente);
    }

    // ============== VEICULOS ==============
    public List<Veiculo> listarVeiculos() {
        java.util.Set<Long> clientes = idsClientesDaOficina();
        List<Veiculo> out = new ArrayList<>();
        for (VeiculoResponseDTO dto : bridge.veiculoController.todos())
            if (dto.idCliente() != null && clientes.contains(dto.idCliente()))
                out.add(montarVeiculo(dto));
        return out;
    }

    public void salvarVeiculo(Modelo modelo, String placa, long idCliente) {
        long idMontadora = modelo.getMontadora() != null ? modelo.getMontadora().getIdMontadora() : 0L;
        bridge.veiculoController.cadastrar(placa, idMontadora, modelo.getIdModelo(), idCliente);
    }

    public DetalhesVeiculoEntity getDetalhesVeiculo(long idVeiculo) {
        return bridge.veiculoController.buscarDetalhes(idVeiculo);
    }
    public void salvarDetalhesVeiculo(long idVeiculo, String motor, String cambio,
                                      String direcao, String freios, String cor, String vin) {
        bridge.veiculoController.salvarDetalhes(idVeiculo, motor, cambio, direcao, freios, cor, vin);
    }

    public void abrirOS(String titulo, String tipoServico, String tipoManutencao,
                        String dataServico, int km, long idVeiculo) {
        bridge.servicoController.abrir(titulo, tipoServico, tipoManutencao, dataServico, km,
            idVeiculo, getOficina().getIdOficina(), 0L);
    }

    // ============== CATÁLOGO DE SERVIÇOS ==============
    public List<CatalogoServicoEntity> listarCatalogoServicos() {
        return bridge.catalogoServicoController.todos();
    }
    public List<CatalogoServicoEntity> listarItensDoOrcamento(long idOrcamento) {
        return bridge.catalogoServicoController.listarPorOrcamento(idOrcamento);
    }

    public void salvarItemServico(String nome, String descricao, double valor,
                                  String tipo, String sistema,
                                  Integer validadeKm, Integer validadeMeses,
                                  List<Long> idPecasAssociadas) {
        CatalogoServicoEntity cat = bridge.catalogoServicoController.cadastrar(
            nome, descricao, valor, tipo, sistema, validadeKm, validadeMeses);
        if (idPecasAssociadas != null)
            for (Long idPeca : idPecasAssociadas)
                bridge.catalogoServicoController.vincularPecaAoCatalogo(cat.getIdCatalogoServico(), idPeca);
    }

    public List<Long> listarIdsPecasDoCatalogoItem(long idCatalogoServico) {
        return bridge.catalogoServicoController.listarIdsPecasDoCatalogo(idCatalogoServico);
    }

    public List<PecaEntity> listarPecasDoCatalogoItem(long idCatalogoServico) {
        List<PecaEntity> out = new ArrayList<>();
        for (Long idPeca : bridge.catalogoServicoController.listarIdsPecasDoCatalogo(idCatalogoServico)) {
            PecaEntity p = bridge.pecaController.entidade(idPeca);
            if (p != null) out.add(p);
        }
        return out;
    }

    public int contarCatalogoServicos() {
        return bridge.catalogoServicoController.todos().size();
    }

    public CatalogoServicoEntity buscarItemCatalogo(long id) {
        return bridge.catalogoServicoController.buscarPorId(id);
    }

    public void atualizarItemServico(long id, String nome, String descricao, double valor,
                                     String tipo, String sistema,
                                     Integer validadeKm, Integer validadeMeses) {
        CatalogoServicoEntity e = new CatalogoServicoEntity(id, nome, descricao, valor,
            tipo, sistema, validadeKm, validadeMeses);
        bridge.catalogoServicoController.atualizar(e);
    }

    /**
     * Adiciona uma peça ao item de catálogo E propaga para todos os orçamentos
     * que já contêm esse item (evita duplicatas).
     */
    public void adicionarPecaAItemCatalogo(long idCatalogo, long idPeca) {
        bridge.catalogoServicoController.vincularPecaAoCatalogo(idCatalogo, idPeca);
        for (long idOrcamento : bridge.catalogoServicoController.listarIdOrcamentosComItem(idCatalogo)) {
            List<Long> jaExistentes = bridge.orcamentoPecaRepository.listarIdsPecaPorOrcamento(idOrcamento);
            if (!jaExistentes.contains(idPeca))
                bridge.orcamentoPecaRepository.vincular(idOrcamento, idPeca, "", "", 0.0);
        }
    }

    /** Remove apenas o link entre o item de catálogo e a peça (não altera orçamentos existentes). */
    public void removerPecaDeItemCatalogo(long idCatalogoServicoPeca) {
        bridge.catalogoServicoController.removerLinkPeca(idCatalogoServicoPeca);
    }

    /** {id_catalogo_servico_peca → id_peca} — para exibição e remoção individual. */
    public java.util.Map<Long, Long> listarLinksPecasDoItemCatalogo(long idCatalogo) {
        return bridge.catalogoServicoController.listarLinksPecasDoCatalogo(idCatalogo);
    }

    public int contarOrcamentosComItemCatalogo(long idCatalogo) {
        return bridge.catalogoServicoController.listarIdOrcamentosComItem(idCatalogo).size();
    }

    // ============== ORÇAMENTOS ==============
    /**
     * valoresItens/valoresPecas são paralelos a itens/idPecas (mesmo índice) e permitem
     * sobrescrever o valor padrão de catálogo/peça no momento do orçamento (ex.: promoções).
     */
    public void criarOrcamento(double valor, String responsavel, String reclamacao,
                               long idVeiculo, long idCliente, Long idFuncionario,
                               List<CatalogoServicoEntity> itens, List<Double> valoresItens,
                               List<Long> idPecas, List<Double> valoresPecas,
                               List<String> nomesTecnicosPecas, List<String> fabricantesPecas) {
        String hoje = java.time.LocalDate.now().toString();
        OrcamentoResponseDTO dto = bridge.orcamentoController.criar(valor, responsavel, reclamacao, hoje,
            null, idVeiculo, idCliente, idFuncionario);
        long idOrcamento = dto.idOrcamento() != null ? dto.idOrcamento() : 0L;
        if (idOrcamento > 0) {
            if (itens != null)
                for (int i = 0; i < itens.size(); i++) {
                    CatalogoServicoEntity item = itens.get(i);
                    double valorCobrado = valoresItens != null && i < valoresItens.size()
                        ? valoresItens.get(i) : item.getValor();
                    bridge.catalogoServicoController.vincularAOrcamento(
                        idOrcamento, item.getIdCatalogoServico(), valorCobrado);
                }
            if (idPecas != null)
                for (int i = 0; i < idPecas.size(); i++) {
                    double valorCobrado = valoresPecas != null && i < valoresPecas.size()
                        ? valoresPecas.get(i) : 0.0;
                    String nomeTecnico = nomesTecnicosPecas != null && i < nomesTecnicosPecas.size()
                        ? nomesTecnicosPecas.get(i) : "";
                    String fabricante = fabricantesPecas != null && i < fabricantesPecas.size()
                        ? fabricantesPecas.get(i) : "";
                    bridge.orcamentoPecaRepository.vincular(idOrcamento, idPecas.get(i), nomeTecnico, fabricante, valorCobrado);
                }
        }
    }
    public void aprovarOrcamento(long idOrcamento) { bridge.orcamentoController.aprovar(idOrcamento); }
    public void reprovarOrcamento(long idOrcamento) { bridge.orcamentoController.reprovar(idOrcamento); }

    public br.com.oficina.atendimento.dto.OrcamentoResponseDTO buscarOrcamento(long idOrcamento) {
        return bridge.orcamentoController.porId(idOrcamento);
    }

    public java.util.LinkedHashMap<CatalogoServicoEntity, Double> listarItensOrcamentoComValor(long idOrcamento) {
        java.util.Map<Long, Double> valores = bridge.catalogoServicoController.listarValoresItensPorOrcamento(idOrcamento);
        java.util.LinkedHashMap<CatalogoServicoEntity, Double> out = new java.util.LinkedHashMap<>();
        for (CatalogoServicoEntity e : bridge.catalogoServicoController.listarPorOrcamento(idOrcamento))
            out.put(e, valores.getOrDefault(e.getIdCatalogoServico(), e.getValor()));
        return out;
    }

    /** Cada Object[] tem: {PecaEntity, Double valor, String nomeTecnico, String fabricante}. */
    public List<Object[]> listarPecasOrcamentoComValor(long idOrcamento) {
        java.util.Map<Long, Double> valores = bridge.orcamentoPecaRepository.listarValoresPorOrcamento(idOrcamento);
        java.util.Map<Long, String> nomesTecnicos = bridge.orcamentoPecaRepository.listarNomesTecnicosPorOrcamento(idOrcamento);
        java.util.Map<Long, String> fabricantes = bridge.orcamentoPecaRepository.listarFabricantesPorOrcamento(idOrcamento);
        List<Object[]> out = new ArrayList<>();
        for (Long idPeca : bridge.orcamentoPecaRepository.listarIdsPecaPorOrcamento(idOrcamento)) {
            br.com.oficina.estoque.PecaEntity p = bridge.pecaController.entidade(idPeca);
            if (p != null) out.add(new Object[]{p, valores.getOrDefault(idPeca, 0.0),
                nomesTecnicos.getOrDefault(idPeca, ""), fabricantes.getOrDefault(idPeca, "")});
        }
        return out;
    }

    public void atualizarItensOrcamento(long idOrcamento,
                                         List<CatalogoServicoEntity> itens, List<Double> valoresItens,
                                         List<Long> idPecas, List<Double> valoresPecas,
                                         List<String> nomesTecnicosPecas, List<String> fabricantesPecas) {
        bridge.catalogoServicoController.removerItensDoOrcamento(idOrcamento);
        bridge.orcamentoPecaRepository.removerPecasDoOrcamento(idOrcamento);
        if (itens != null)
            for (int i = 0; i < itens.size(); i++) {
                double v = valoresItens != null && i < valoresItens.size() ? valoresItens.get(i) : itens.get(i).getValor();
                bridge.catalogoServicoController.vincularAOrcamento(idOrcamento, itens.get(i).getIdCatalogoServico(), v);
            }
        if (idPecas != null)
            for (int i = 0; i < idPecas.size(); i++) {
                double v = valoresPecas != null && i < valoresPecas.size() ? valoresPecas.get(i) : 0.0;
                String nt = nomesTecnicosPecas != null && i < nomesTecnicosPecas.size() ? nomesTecnicosPecas.get(i) : "";
                String fab = fabricantesPecas != null && i < fabricantesPecas.size() ? fabricantesPecas.get(i) : "";
                bridge.orcamentoPecaRepository.vincular(idOrcamento, idPecas.get(i), nt, fab, v);
            }
        double total = 0;
        if (valoresItens != null) for (double v : valoresItens) total += v;
        if (valoresPecas != null) for (double v : valoresPecas) total += v;
        bridge.orcamentoController.atualizarValor(idOrcamento, total);
    }

    /** Lista apenas orçamentos externos (tipo=ENTRADA). */
    public List<model.Orcamento> listarOrcamentos() {
        java.util.Map<Long, String> placas = new java.util.HashMap<>();
        for (Veiculo v : listarVeiculos()) placas.put(v.getIdVeiculo(), v.getPlaca());
        java.util.Map<Long, String> nomes = new java.util.HashMap<>();
        for (Cliente c : listarClientes()) nomes.put(c.getIdUsuario(), c.getNome());
        for (FuncionarioEntity f : listarFuncionarios())
            if (f.getIdUsuario() != null) nomes.put(f.getIdUsuario(), f.getNome() + " (Func.)");

        java.util.Set<Long> clientes = idsClientesDaOficina();
        List<model.Orcamento> out = new ArrayList<>();
        for (OrcamentoResponseDTO dto : bridge.orcamentoController.entrada()) {
            if (dto.idCliente() == null || !clientes.contains(dto.idCliente())) continue;
            long idVeic = dto.idVeiculo() != null ? dto.idVeiculo() : 0L;
            long idCli  = dto.idCliente() != null ? dto.idCliente() : 0L;
            out.add(new model.Orcamento(
                dto.idOrcamento() != null ? dto.idOrcamento() : 0L,
                dto.codigo(), dto.tipo(),
                dto.valor(), dto.responsavel(), dto.reclamacao(), dto.dataCriacao(), dto.status(),
                idVeic, idCli,
                placas.getOrDefault(idVeic, "—"), nomes.getOrDefault(idCli, "—")));
        }
        return out;
    }

    /** Abre uma O.S. vinculada a um orçamento aprovado. */
    public void abrirOSDeOrcamento(String titulo, String tipoServico, String tipoManutencao,
                                   String dataServico, int km, long idVeiculo, long idOrcamento) {
        bridge.servicoController.abrir(titulo, tipoServico, tipoManutencao, dataServico, km,
            idVeiculo, getOficina().getIdOficina(), idOrcamento);
    }

    // ============== SERVIÇOS (ETAPAS) ==============
    public ServicoResponseDTO buscarServico(long idServico) {
        return bridge.servicoController.porId(idServico);
    }

    /** Registra a única etapa da OS: descrição geral + checklist [{desc, tempoGasto, "true"/"false"}]. */
    public void registrarEtapaOS(long idOS, String descricao, java.util.List<String[]> checklist) {
        bridge.servicoController.registrarEtapaOS(idOS, descricao, checklist);
    }

    /** Finaliza a OS (EM_ANDAMENTO -> CONCLUIDA), registrando o comentário do mecânico sobre o estado do veículo na saída. */
    public void finalizarOS(long idOS, String observacaoSaida, Long idFuncionario) {
        bridge.servicoController.finalizar(idOS, observacaoSaida, idFuncionario);
    }

    /** Cria um orçamento interno (REVISAO) vinculado a este serviço. */
    public model.Orcamento criarOrcamentoRevisao(long idServico, double valor, String responsavel,
                                                  String reclamacao, long idVeiculo, long idCliente,
                                                  Long idFuncionario) {
        OrcamentoResponseDTO dto = bridge.orcamentoController.criarRevisao(
            idServico, valor, responsavel, reclamacao, idVeiculo, idCliente, idFuncionario);
        java.util.Map<Long, String> placas = new java.util.HashMap<>();
        for (Veiculo v : listarVeiculos()) placas.put(v.getIdVeiculo(), v.getPlaca());
        java.util.Map<Long, String> nomes = new java.util.HashMap<>();
        for (Cliente c : listarClientes()) nomes.put(c.getIdUsuario(), c.getNome());
        long idV = dto.idVeiculo() != null ? dto.idVeiculo() : 0L;
        long idC = dto.idCliente() != null ? dto.idCliente() : 0L;
        return new model.Orcamento(dto.idOrcamento(), dto.codigo(), dto.tipo(),
            dto.valor(), dto.responsavel(), dto.reclamacao(), dto.dataCriacao(), dto.status(),
            idV, idC, placas.getOrDefault(idV, "—"), nomes.getOrDefault(idC, "—"));
    }

    // ============== FUNCIONÁRIOS ==============
    public void salvarFuncionario(String nome, String cpf, String endereco,
                                  String email, String telefone, String cargo) {
        bridge.funcionarioController.cadastrar(nome, cargo, endereco, cpf, email,
            "funcionario123", telefone, getOficina().getIdOficina());
    }

    public List<FuncionarioEntity> listarFuncionarios() {
        java.util.Set<Long> usuarios = idsUsuariosDaOficina();
        List<FuncionarioEntity> out = new ArrayList<>();
        for (FuncionarioEntity f : bridge.funcionarioController.todos())
            if (f.getIdUsuario() != null && usuarios.contains(f.getIdUsuario())) out.add(f);
        return out;
    }

    public void atualizarFuncionario(long idFuncionario, String nome, String cargo, String endereco,
                                     String cpf, String email, String telefone) {
        bridge.funcionarioController.atualizar(idFuncionario, nome, cargo, endereco, cpf, email, telefone);
    }

    public void excluirFuncionario(long idFuncionario) {
        bridge.funcionarioController.remover(idFuncionario);
    }

    // ============== TIPOS DE SERVIÇO ==============
    public void salvarTipoServico(String nome) {
        bridge.tipoServicoController.cadastrar(nome);
    }

    public List<TipoServicoEntity> listarTiposServico() {
        return bridge.tipoServicoController.todos();
    }

    // ============== ORÇAMENTOS APROVADOS SEM OS ==============
    /** Orçamentos ENTRADA+APROVADOS sem OS vinculada. */
    public List<model.Orcamento> listarOrcamentosAprovadosSemOS() {
        java.util.Set<Long> orcamentosComOS = new java.util.HashSet<>();
        for (ServicoResponseDTO dto : bridge.servicoController.todas()) {
            if (dto.idOrcamento() != null && dto.idOrcamento() > 0)
                orcamentosComOS.add(dto.idOrcamento());
        }
        java.util.Map<Long, String> placas = new java.util.HashMap<>();
        for (Veiculo v : listarVeiculos()) placas.put(v.getIdVeiculo(), v.getPlaca());
        java.util.Map<Long, String> nomes = new java.util.HashMap<>();
        for (Cliente c : listarClientes()) nomes.put(c.getIdUsuario(), c.getNome());

        java.util.Set<Long> clientes = idsClientesDaOficina();
        List<model.Orcamento> out = new ArrayList<>();
        for (OrcamentoResponseDTO dto : bridge.orcamentoController.entrada()) {
            if (!"APROVADO".equalsIgnoreCase(dto.status())) continue;
            if (dto.idCliente() == null || !clientes.contains(dto.idCliente())) continue;
            long id = dto.idOrcamento() != null ? dto.idOrcamento() : 0L;
            if (orcamentosComOS.contains(id)) continue;
            long idVeic = dto.idVeiculo() != null ? dto.idVeiculo() : 0L;
            long idCli  = dto.idCliente() != null ? dto.idCliente() : 0L;
            out.add(new model.Orcamento(id, dto.codigo(), dto.tipo(),
                dto.valor(), dto.responsavel(), dto.reclamacao(), dto.dataCriacao(), dto.status(),
                idVeic, idCli,
                placas.getOrDefault(idVeic, "—"), nomes.getOrDefault(idCli, "—")));
        }
        return out;
    }

    public void salvarMontadora(String nome, String pais) {
        bridge.veiculoController.cadastrarMontadora(nome, pais);
    }

    // ============== PEÇAS ==============
    /** Cadastra a peça e, se idCatalogoServico > 0, já a vincula a esse item do catálogo. Retorna o id da peça criada. */
    public long salvarPeca(String nomePopular, String vidaUtilTempo, String vidaUtilKm, String sistema, long idCatalogoServico) {
        PecaEntity peca = bridge.pecaController.cadastrar(nomePopular, vidaUtilTempo, vidaUtilKm, sistema);
        if (idCatalogoServico > 0)
            adicionarPecaAItemCatalogo(idCatalogoServico, peca.getIdPeca());
        return peca.getIdPeca();
    }
    public void atualizarPeca(long id, String nome, String vidaTempo, String vidaKm, String sistema) {
        bridge.pecaController.atualizar(id, nome, vidaTempo, vidaKm, sistema);
    }
    public void salvarModelo(String nome, int ano, String tipo, long idMontadora) {
        bridge.veiculoController.cadastrarModelo(nome, ano, tipo, idMontadora);
    }

    /** Retorna o idCliente do proprietário do veículo, ou 0 se não encontrado. */
    public long getIdClientePorVeiculo(long idVeiculo) {
        for (VeiculoResponseDTO dto : bridge.veiculoController.todos())
            if (dto.idVeiculo() != null && dto.idVeiculo() == idVeiculo)
                return dto.idCliente() != null ? dto.idCliente() : 0L;
        return 0L;
    }

    private Veiculo montarVeiculo(VeiculoResponseDTO dto) {
        Montadora mont = new Montadora(0, dto.montadora(), "");
        Modelo mod = new Modelo(0, dto.modelo(), dto.ano(), dto.tipo(), mont);
        mont.addModelo(mod);
        return new Veiculo(dto.idVeiculo(), "", dto.placa(), "", mod);
    }
    private void anexarVeiculos(Cliente c) {
        for (VeiculoResponseDTO dto : bridge.veiculoController.todos())
            if (dto.idCliente() != null && dto.idCliente() == c.getIdUsuario())
                c.addVeiculo(montarVeiculo(dto));
    }

    // ============== SERVICOS (O.S.) ==============
    public List<OrdemDeServico> listarOS() {
        java.util.Map<Long, Veiculo> veicMap = new java.util.HashMap<>();
        for (Veiculo v : listarVeiculos()) veicMap.put(v.getIdVeiculo(), v);

        long idOf = idOficinaAtual();
        List<OrdemDeServico> out = new ArrayList<>();
        for (ServicoResponseDTO dto : bridge.servicoController.todas()) {
            if (dto.idOficina() == null || dto.idOficina() != idOf) continue;
            Veiculo v = dto.idVeiculo() != null ? veicMap.get(dto.idVeiculo()) : null;
            OrdemDeServico os = new OrdemDeServico(dto.idServico(), dto.titulo(),
                mapStatusOS(dto.status()), v);
            os.setTipoServico(OrdemDeServico.TipoServicoOS.fromName(dto.tipoServico()));
            os.setTipoManutencao(OrdemDeServico.TipoManutencao.fromName(dto.tipoManutencao()));
            for (var it : dto.itens())
                os.getItensServico().add(new ItemServico(it.id(), mapStatusItem(it.status()), null, it.descricao()));
            out.add(os);
        }
        return out;
    }

    public List<PecaEntity> listarTodasPecas() {
        return bridge.pecaController.listarTodasEntidades();
    }

    public List<ServicoResponseDTO> listarTodosServicos() {
        long idOf = idOficinaAtual();
        List<ServicoResponseDTO> out = new ArrayList<>();
        for (ServicoResponseDTO dto : bridge.servicoController.todas())
            if (dto.idOficina() != null && dto.idOficina() == idOf) out.add(dto);
        return out;
    }

    public java.util.Map<Long, String> mapaReclamacaoPorIdOrcamento() {
        java.util.Map<Long, String> out = new java.util.HashMap<>();
        for (br.com.oficina.atendimento.dto.OrcamentoResponseDTO dto : bridge.orcamentoController.todos())
            if (dto.idOrcamento() != null && dto.reclamacao() != null && !dto.reclamacao().isBlank())
                out.put(dto.idOrcamento(), dto.reclamacao());
        return out;
    }

    public List<ServicoResponseDTO> listarServicosPorVeiculo(long idVeiculo) {
        List<ServicoResponseDTO> out = new ArrayList<>();
        for (ServicoResponseDTO dto : bridge.servicoController.todas())
            if (dto.idVeiculo() != null && dto.idVeiculo() == idVeiculo)
                out.add(dto);
        return out;
    }

    public List<Veiculo> listarVeiculosPorProprietario(long idUsuario) {
        List<Veiculo> out = new ArrayList<>();
        for (VeiculoResponseDTO dto : bridge.veiculoController.todos())
            if (dto.idCliente() != null && dto.idCliente() == idUsuario)
                out.add(montarVeiculo(dto));
        return out;
    }

    /** Peças vinculadas a um orçamento, com o valor cobrado por cada uma. */
    public List<String> listarPecasDoOrcamento(long idOrcamento) {
        java.util.Map<Long, Double> valores = bridge.orcamentoPecaRepository.listarValoresPorOrcamento(idOrcamento);
        List<String> out = new ArrayList<>();
        for (Long idPeca : bridge.orcamentoPecaRepository.listarIdsPecaPorOrcamento(idOrcamento)) {
            PecaEntity p = bridge.pecaController.entidade(idPeca);
            if (p == null) continue;
            double valor = valores.getOrDefault(idPeca, 0.0);
            out.add(String.format("%s - R$ %.2f", p.getNomeExibicao(), valor));
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

    // ============== ESTATÍSTICAS ==============
    public int contarClientes()    { return listarClientes().size(); }
    public int contarVeiculos()    { return listarVeiculos().size(); }
    public int contarOS()          { return listarOS().size(); }
    public int contarMontadoras()  { return montadorasComModelos().size(); }
    public int contarModelos() {
        return montadorasComModelos().stream().mapToInt(m -> m.listarModelos().size()).sum();
    }
    public int contarOSPorStatus(OrdemDeServico.Status status) {
        return (int) listarOS().stream().filter(os -> os.getStatus() == status).count();
    }
    public List<OrdemDeServico> listarOSPorStatus(OrdemDeServico.Status status) {
        return listarOS().stream().filter(os -> os.getStatus() == status).collect(java.util.stream.Collectors.toList());
    }

    public List<Montadora> montadorasComModelos() {
        List<Montadora> out = new ArrayList<>();
        for (MontadoraEntity me : bridge.veiculoController.montadoras()) {
            Montadora m = new Montadora(me.getIdMontadora(), me.getNome(), me.getPaisOrigem());
            for (ModeloEntity mod : bridge.veiculoController.modelos(me.getIdMontadora()))
                m.addModelo(new Modelo(mod.getIdModelo(), mod.getNome(), mod.getAno(), mod.getTipo(), m));
            out.add(m);
        }
        return out;
    }

    public Cliente cliente_id(long id) {
        return listarClientes().stream()
                .filter(c -> c.getIdUsuario() == id)
                .findFirst()
                .orElse(null);
    }
}
