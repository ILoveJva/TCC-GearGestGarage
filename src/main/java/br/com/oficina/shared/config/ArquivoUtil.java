package br.com.oficina.shared.config;

import br.com.oficina.shared.exception.RegraNegocioException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * Copia um arquivo de imagem escolhido pelo usuário para uma pasta local
 * (fora do banco), retornando o caminho relativo salvo. Mesmo padrão usado
 * por {@link br.com.oficina.atendimento.MidiaServicoService} para mídias de OS.
 */
public final class ArquivoUtil {

    private static final long TAMANHO_MAXIMO_BYTES = 20L * 1024 * 1024; // 20 MB

    private ArquivoUtil() {}

    /** Copia {@code origem} para {@code <pastaRaiz>/<idEntidade>/} e devolve o caminho relativo salvo. */
    public static String copiarParaPasta(String pastaRaiz, long idEntidade, File origem) {
        if (origem == null || !origem.isFile())
            throw new RegraNegocioException("Selecione um arquivo de imagem válido.");
        if (origem.length() > TAMANHO_MAXIMO_BYTES)
            throw new RegraNegocioException("Imagem muito grande (máx. 20 MB).");
        if (!isImagem(origem.getName()))
            throw new RegraNegocioException("Formato não suportado. Use jpg, jpeg, png, gif ou bmp.");

        try {
            Path pasta = Paths.get(pastaRaiz, String.valueOf(idEntidade));
            Files.createDirectories(pasta);

            String nomeSeguro = System.currentTimeMillis() + "_" + sanitizar(origem.getName());
            Path destino = pasta.resolve(nomeSeguro);
            Files.copy(origem.toPath(), destino, StandardCopyOption.REPLACE_EXISTING);

            return destino.toString().replace('\\', '/');
        } catch (IOException e) {
            throw new RegraNegocioException("Falha ao salvar a imagem: " + e.getMessage());
        }
    }

    private static boolean isImagem(String nome) {
        String ext = extensao(nome);
        return ext.equals("jpg") || ext.equals("jpeg") || ext.equals("png")
            || ext.equals("gif") || ext.equals("bmp");
    }

    private static String extensao(String nome) {
        int i = nome.lastIndexOf('.');
        return i < 0 ? "" : nome.substring(i + 1).toLowerCase(java.util.Locale.ROOT);
    }

    private static String sanitizar(String nome) {
        return nome.replaceAll("[^a-zA-Z0-9._-]", "_");
    }
}
