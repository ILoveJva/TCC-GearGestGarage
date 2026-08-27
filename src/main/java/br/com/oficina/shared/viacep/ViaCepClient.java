package br.com.oficina.shared.viacep;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Cliente minimo para a API publica ViaCEP (https://viacep.com.br). */
public class ViaCepClient {

    public record Endereco(String cep, String logradouro, String complemento,
                            String bairro, String localidade, String uf) {
        /** Endereço pronto para exibição: "Logradouro, Bairro, Cidade - UF". */
        public String formatado() {
            StringBuilder sb = new StringBuilder();
            if (!logradouro.isBlank()) sb.append(logradouro);
            if (!bairro.isBlank()) sb.append(sb.isEmpty() ? "" : ", ").append(bairro);
            if (!localidade.isBlank()) sb.append(sb.isEmpty() ? "" : ", ").append(localidade);
            if (!uf.isBlank()) sb.append(sb.isEmpty() ? "" : " - ").append(uf);
            return sb.toString();
        }
    }

    private static final HttpClient CLIENT = HttpClient.newBuilder()
        .connectTimeout(Duration.ofSeconds(5))
        .build();

    /** Consulta o CEP na ViaCEP. Retorna null se o CEP for invalido ou nao encontrado. */
    public static Endereco buscar(String cepBruto) throws Exception {
        String cep = cepBruto == null ? "" : cepBruto.replaceAll("[^0-9]", "");
        if (cep.length() != 8) return null;

        HttpRequest req = HttpRequest.newBuilder()
            .uri(URI.create("https://viacep.com.br/ws/" + cep + "/json/"))
            .timeout(Duration.ofSeconds(5))
            .GET()
            .build();
        HttpResponse<String> resp = CLIENT.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        String body = resp.body();
        if (body == null || resp.statusCode() != 200 || body.contains("\"erro\"")) return null;

        return new Endereco(campo(body, "cep"), campo(body, "logradouro"), campo(body, "complemento"),
            campo(body, "bairro"), campo(body, "localidade"), campo(body, "uf"));
    }

    private static String campo(String json, String nome) {
        Matcher m = Pattern.compile("\"" + nome + "\"\\s*:\\s*\"([^\"]*)\"").matcher(json);
        return m.find() ? m.group(1) : "";
    }
}
