package br.com.bancosaque.dto;

import java.math.BigDecimal;

/**
 * DTO que representa a requisição de saque enviada pelo usuário.
 * Encapsula os dados de entrada antes de qualquer validação de negócio.
 */
public class SaqueRequestDTO {

    private final String idConta;
    private final BigDecimal valorSaque;

    public SaqueRequestDTO(String idConta, BigDecimal valorSaque) {
        this.idConta = idConta;
        this.valorSaque = valorSaque;
    }

    public String getIdConta() {
        return idConta;
    }

    public BigDecimal getValorSaque() {
        return valorSaque;
    }

    @Override
    public String toString() {
        return String.format("SaqueRequestDTO{idConta='%s', valorSaque=R$ %.2f}", idConta, valorSaque);
    }
}
