package br.com.bancosaque.exception;

public class MensageriaException extends RuntimeException {

    public MensageriaException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }

    public MensageriaException(String mensagem) {
        super(mensagem);
    }
}
