package br.com.bancosaque.exception;

import java.math.BigDecimal;

public class SaldoInsuficienteException extends RuntimeException {

    private final BigDecimal saldoAtual;
    private final BigDecimal valorSolicitado;

    public SaldoInsuficienteException(BigDecimal saldoAtual, BigDecimal valorSolicitado) {
        super(String.format(
                "Saldo insuficiente. Saldo atual: R$ %.2f | Valor solicitado: R$ %.2f",
                saldoAtual, valorSolicitado
        ));
        this.saldoAtual = saldoAtual;
        this.valorSolicitado = valorSolicitado;
    }

    public BigDecimal getSaldoAtual() {
        return saldoAtual;
    }

    public BigDecimal getValorSolicitado() {
        return valorSolicitado;
    }
}
