package br.com.oficina.shared.exception;

/** Lancada quando uma regra de negocio e violada (ex: gerar OS de orcamento nao aprovado). */
public class RegraNegocioException extends RuntimeException {
    public RegraNegocioException(String mensagem) {
        super(mensagem);
    }
}
