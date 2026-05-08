package br.com.bancosaque.exception;

public class ValorSaqueInvalidoException extends RuntimeException {

    public ValorSaqueInvalidoException(String mensagem) {
        super(mensagem);
    }

    public ValorSaqueInvalidoException() {
        super("O valor do saque deve ser maior que zero.");
    }
}
