package br.com.oficina.shared.exception;

/**
 * Tratador global de erros. Em uma API REST (Spring) seria anotado com @RestControllerAdvice;
 * aqui, em Java puro, centraliza a traducao de excecoes em uma resposta padronizada.
 */
public class GlobalExceptionHandler {

    /** Estrutura simples de resposta de erro (equivalente a um JSON de erro). */
    public record ErroResposta(int status, String erro, String mensagem) {
        @Override
        public String toString() {
            return "{ \"status\": " + status + ", \"erro\": \"" + erro
                + "\", \"mensagem\": \"" + mensagem + "\" }";
        }
    }

    public ErroResposta tratar(RuntimeException ex) {
        if (ex instanceof RecursoNaoEncontradoException) {
            return new ErroResposta(404, "NAO_ENCONTRADO", ex.getMessage());
        }
        if (ex instanceof RegraNegocioException) {
            return new ErroResposta(422, "REGRA_NEGOCIO", ex.getMessage());
        }
        return new ErroResposta(500, "ERRO_INTERNO", ex.getMessage());
    }
}
