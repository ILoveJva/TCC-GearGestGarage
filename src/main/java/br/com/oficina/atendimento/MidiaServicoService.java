package br.com.oficina.atendimento;

import br.com.oficina.shared.exception.RegraNegocioException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

/**
 * Regras das mídias (fotos/vídeos) de uma OS. O arquivo escolhido pelo usuário é
 * copiado para a pasta local {@code midias_os/<idServico>/} e o registro no banco
 * guarda o caminho relativo. Assim os vídeos não incham o banco de dados.
 */
public class MidiaServicoService {
    /** Pasta raiz (relativa ao diretório de execução) onde as mídias ficam guardadas. */
    public static final String PASTA_RAIZ = "midias_os";

    private static final long TAMANHO_MAXIMO_BYTES = 200L * 1024 * 1024; // 200 MB por arquivo

    private final MidiaServicoRepository repository;

    public MidiaServicoService(MidiaServicoRepository repository) { this.repository = repository; }

    public List<MidiaServicoEntity> listar(long idServico) {
        return repository.listarPorServico(idServico);
    }

    /**
     * Copia o arquivo para a pasta de mídias da OS e registra os metadados.
     * O tipo (FOTO/VIDEO) é inferido pela extensão quando não informado.
     */
    public MidiaServicoEntity adicionar(long idServico, File origem, String descricao) {
        if (origem == null || !origem.isFile())
            throw new RegraNegocioException("Selecione um arquivo válido.");
        if (origem.length() > TAMANHO_MAXIMO_BYTES)
            throw new RegraNegocioException("Arquivo muito grande (máx. 200 MB).");

        String tipo = inferirTipo(origem.getName());
        if (tipo == null)
            throw new RegraNegocioException("Formato não suportado. Use imagens (jpg, png, gif) ou vídeos (mp4, mov, avi, mkv, webm).");

        try {
            Path pastaOS = Paths.get(PASTA_RAIZ, String.valueOf(idServico));
            Files.createDirectories(pastaOS);

            String nomeSeguro = System.currentTimeMillis() + "_" + sanitizar(origem.getName());
            Path destino = pastaOS.resolve(nomeSeguro);
            Files.copy(origem.toPath(), destino, StandardCopyOption.REPLACE_EXISTING);

            MidiaServicoEntity m = new MidiaServicoEntity(null, idServico, tipo,
                origem.getName(), destino.toString().replace('\\', '/'),
                descricao != null ? descricao.trim() : "", LocalDate.now().toString());
            return repository.salvar(m);
        } catch (IOException e) {
            throw new RegraNegocioException("Falha ao salvar o arquivo: " + e.getMessage());
        }
    }

    /** Remove o registro e o arquivo em disco. */
    public void remover(long idMidia) {
        MidiaServicoEntity m = repository.buscarPorId(idMidia);
        if (m == null) return;
        try {
            Files.deleteIfExists(Paths.get(m.getCaminho()));
        } catch (IOException ignored) {
            // arquivo já ausente/bloqueado: remove só o registro
        }
        repository.remover(idMidia);
    }

    /** Extensão -> FOTO | VIDEO, ou null se não suportado. */
    private String inferirTipo(String nome) {
        String ext = extensao(nome);
        switch (ext) {
            case "jpg": case "jpeg": case "png": case "gif": case "bmp":
                return "FOTO";
            case "mp4": case "mov": case "avi": case "mkv": case "webm": case "wmv":
                return "VIDEO";
            default:
                return null;
        }
    }

    private String extensao(String nome) {
        int i = nome.lastIndexOf('.');
        return i < 0 ? "" : nome.substring(i + 1).toLowerCase(Locale.ROOT);
    }

    private String sanitizar(String nome) {
        return nome.replaceAll("[^a-zA-Z0-9._-]", "_");
    }
}
