package br.com.bancosaque.exception;

public class ContaNaoEncontradaException extends RuntimeException {

    private final String idConta;

    public ContaNaoEncontradaException(String idConta) {
        super(String.format("Conta não encontrada para o ID: '%s'", idConta));
        this.idConta = idConta;
    }

    public String getIdConta() {
        return idConta;
    }
}
