package br.com.bancosaque.model;

import java.math.BigDecimal;

public class ContaCorrente {

    private final String numeroConta;
    private BigDecimal saldo;
    private final String titular;
    private final String email;

    public ContaCorrente(String numeroConta, String titular, String email, BigDecimal saldoInicial) {
        this.numeroConta = numeroConta;
        this.titular = titular;
        this.email = email;
        this.saldo = saldoInicial;
    }

    public boolean validarSaldo(BigDecimal valor) {
        return saldo.compareTo(valor) >= 0;
    }

    public void sacar(BigDecimal valor) {
        if (!validarSaldo(valor)) {
            throw new RuntimeException("Saldo insuficiente. Saldo atual: R$ " + saldo);
        }
        this.saldo = this.saldo.subtract(valor);
    }

    public String getNumeroConta() { return numeroConta; }
    public BigDecimal getSaldo() { return saldo; }
    public String getTitular() { return titular; }
    public String getEmail() { return email; }
}
