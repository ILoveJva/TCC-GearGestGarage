package view;

import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.text.Normalizer;
import java.util.Locale;

/**
 * Utilitário compartilhado pelos campos de busca/filtro usados para facilitar
 * a localização de itens em combos com muitas opções (serviços, peças, etc.).
 */
public final class BuscaUtil {
    private BuscaUtil() {}

    /** Normaliza removendo acentuação e caixa, para comparação de busca "contém". */
    public static String normalizar(String texto) {
        if (texto == null) return "";
        String semAcento = Normalizer.normalize(texto, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
        return semAcento.toLowerCase(Locale.ROOT).trim();
    }

    public static boolean contem(String textoAlvo, String termoBusca) {
        String termo = normalizar(termoBusca);
        return termo.isEmpty() || normalizar(textoAlvo).contains(termo);
    }

    /** Executa {@code acao} a cada alteração do texto do campo (digitar, colar, apagar). */
    public static void aoDigitar(JTextField campo, Runnable acao) {
        campo.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { acao.run(); }
            @Override public void removeUpdate(DocumentEvent e) { acao.run(); }
            @Override public void changedUpdate(DocumentEvent e) { acao.run(); }
        });
    }
}
