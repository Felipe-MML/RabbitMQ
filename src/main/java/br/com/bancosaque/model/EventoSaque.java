package br.com.bancosaque.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class EventoSaque {

    private String numeroConta;
    private String titular;
    private String email;
    private BigDecimal valorSaque;
    private BigDecimal saldoRestante;
    private String dataHora;

    public EventoSaque() {}

    public EventoSaque(String numeroConta, String titular, String email,
                       BigDecimal valorSaque, BigDecimal saldoRestante) {
        this.numeroConta = numeroConta;
        this.titular = titular;
        this.email = email;
        this.valorSaque = valorSaque;
        this.saldoRestante = saldoRestante;
        this.dataHora = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
    }

    // Getters e Setters
    public String getNumeroConta() { return numeroConta; }
    public void setNumeroConta(String numeroConta) { this.numeroConta = numeroConta; }

    public String getTitular() { return titular; }
    public void setTitular(String titular) { this.titular = titular; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public BigDecimal getValorSaque() { return valorSaque; }
    public void setValorSaque(BigDecimal valorSaque) { this.valorSaque = valorSaque; }

    public BigDecimal getSaldoRestante() { return saldoRestante; }
    public void setSaldoRestante(BigDecimal saldoRestante) { this.saldoRestante = saldoRestante; }

    public String getDataHora() { return dataHora; }
    public void setDataHora(String dataHora) { this.dataHora = dataHora; }

    @Override
    public String toString() {
        return String.format("Saque de R$ %s realizado na conta %s (%s) em %s. Saldo atual: R$ %s",
                valorSaque, numeroConta, titular, dataHora, saldoRestante);
    }
}
