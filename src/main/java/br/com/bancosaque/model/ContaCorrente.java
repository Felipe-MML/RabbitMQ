package br.com.bancosaque.model;

import java.math.BigDecimal;

public class ContaCorrente {

    private final String id;
    private final String nomeTitular;
    private final String email;
    private BigDecimal saldo;

    public ContaCorrente(String id, String nomeTitular, String email, BigDecimal saldo) {
        if (id == null || id.isBlank()) throw new IllegalArgumentException("ID da conta não pode ser nulo ou vazio.");
        if (nomeTitular == null || nomeTitular.isBlank()) throw new IllegalArgumentException("Nome do titular não pode ser nulo ou vazio.");
        if (email == null || email.isBlank()) throw new IllegalArgumentException("E-mail não pode ser nulo ou vazio.");
        if (saldo == null || saldo.compareTo(BigDecimal.ZERO) < 0) throw new IllegalArgumentException("Saldo inicial não pode ser negativo.");

        this.id = id;
        this.nomeTitular = nomeTitular;
        this.email = email;
        this.saldo = saldo;
    }

    public void debitar(BigDecimal valor) {
        if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Valor de débito deve ser maior que zero.");
        }
        if (valor.compareTo(this.saldo) > 0) {
            throw new IllegalStateException("Saldo insuficiente para realizar o débito.");
        }
        this.saldo = this.saldo.subtract(valor);
    }

    public String getId() {
        return id;
    }

    public String getNomeTitular() {
        return nomeTitular;
    }

    public String getEmail() {
        return email;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    @Override
    public String toString() {
        return String.format("ContaCorrente{id='%s', titular='%s', email='%s', saldo=R$ %.2f}",
                id, nomeTitular, email, saldo);
    }
}
