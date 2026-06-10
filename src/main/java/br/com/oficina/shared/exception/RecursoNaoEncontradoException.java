package br.com.oficina.shared.exception;

/** Lancada quando um recurso (entidade) nao e encontrado no banco. */
public class RecursoNaoEncontradoException extends RuntimeException {
    public RecursoNaoEncontradoException(String mensagem) {
        super(mensagem);
    }
}
