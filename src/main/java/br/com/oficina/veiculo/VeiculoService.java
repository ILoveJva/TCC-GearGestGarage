package br.com.oficina.veiculo;

import br.com.oficina.shared.exception.RecursoNaoEncontradoException;
import br.com.oficina.shared.exception.RegraNegocioException;
import br.com.oficina.usuario.ClienteEntity;
import br.com.oficina.usuario.UsuarioRepository;
import br.com.oficina.veiculo.dto.VeiculoResponseDTO;
import java.util.ArrayList;
import java.util.List;

public class VeiculoService {
    private final VeiculoRepository repository;
    private final UsuarioRepository usuarioRepository;

    public VeiculoService(VeiculoRepository repository, UsuarioRepository usuarioRepository) {
        this.repository = repository;
        this.usuarioRepository = usuarioRepository;
    }

    public List<MontadoraEntity> listarMontadoras() { return repository.listarMontadoras(); }
    public List<ModeloEntity> listarModelos(long idMontadora) { return repository.listarModelosPorMontadora(idMontadora); }

    public MontadoraEntity cadastrarMontadora(String nome, String pais) {
        return repository.salvarMontadora(new MontadoraEntity(null, nome, pais));
    }
    public ModeloEntity cadastrarModelo(String nome, int ano, long idMontadora) {
        if (repository.buscarMontadora(idMontadora) == null)
            throw new RecursoNaoEncontradoException("Montadora " + idMontadora + " nao encontrada");
        return repository.salvarModelo(new ModeloEntity(null, nome, ano, idMontadora));
    }

    /** Cadastra veiculo validando que o modelo pertence a montadora escolhida. */
    public VeiculoEntity cadastrar(String tipo, String placa, long idMontadora, long idModelo, long idCliente) {
        ModeloEntity modelo = repository.buscarModelo(idModelo);
        if (modelo == null) throw new RecursoNaoEncontradoException("Modelo " + idModelo + " nao encontrado");
        if (modelo.getIdMontadora() != idMontadora)
            throw new RegraNegocioException("Modelo nao pertence a montadora " + idMontadora);
        return repository.salvar(new VeiculoEntity(null, placa, tipo, idCliente, idModelo));
    }

    public List<VeiculoResponseDTO> listar() {
        List<VeiculoResponseDTO> out = new ArrayList<>();
        for (VeiculoEntity v : repository.listarTodos()) out.add(paraDTO(v));
        return out;
    }

    public VeiculoResponseDTO buscarDTO(long id) {
        VeiculoEntity v = repository.buscarPorId(id);
        if (v == null) throw new RecursoNaoEncontradoException("Veiculo " + id + " nao encontrado");
        return paraDTO(v);
    }

    private VeiculoResponseDTO paraDTO(VeiculoEntity v) {
        ModeloEntity modelo = v.getIdModelo() != null ? repository.buscarModelo(v.getIdModelo()) : null;
        String nomeModelo = modelo != null ? modelo.getNome() : "?";
        int ano = modelo != null ? modelo.getAno() : 0;
        String nomeMontadora = "?";
        if (modelo != null) {
            MontadoraEntity m = repository.buscarMontadora(modelo.getIdMontadora());
            if (m != null) nomeMontadora = m.getNome();
        }
        String proprietario = "Nao Informado";
        if (v.getIdCliente() != null && v.getIdCliente() > 0) {
            ClienteEntity c = usuarioRepository.buscarCliente(v.getIdCliente());
            if (c != null) proprietario = c.getNome();
        }
        return new VeiculoResponseDTO(v.getIdVeiculo(), v.getTipoVeiculo(), v.getPlaca(),
            ano, nomeMontadora, nomeModelo, v.getIdCliente(), proprietario);
    }
}
