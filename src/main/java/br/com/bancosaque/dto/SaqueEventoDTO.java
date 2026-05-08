package br.com.bancosaque.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;


public class SaqueEventoDTO {

    private String idConta;
    private String nomeTitular;
    private String email;
    private BigDecimal valorSacado;
    private BigDecimal saldoRestante;
    private LocalDateTime dataHora;

    // Construtor padrão necessário para desserialização pelo Gson
    public SaqueEventoDTO() {}

    public SaqueEventoDTO(String idConta, String nomeTitular, String email,
                           BigDecimal valorSacado, BigDecimal saldoRestante, LocalDateTime dataHora) {
        this.idConta = idConta;
        this.nomeTitular = nomeTitular;
        this.email = email;
        this.valorSacado = valorSacado;
        this.saldoRestante = saldoRestante;
        this.dataHora = dataHora;
    }

    public String getIdConta() { return idConta; }
    public void setIdConta(String idConta) { this.idConta = idConta; }

    public String getNomeTitular() { return nomeTitular; }
    public void setNomeTitular(String nomeTitular) { this.nomeTitular = nomeTitular; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public BigDecimal getValorSacado() { return valorSacado; }
    public void setValorSacado(BigDecimal valorSacado) { this.valorSacado = valorSacado; }

    public BigDecimal getSaldoRestante() { return saldoRestante; }
    public void setSaldoRestante(BigDecimal saldoRestante) { this.saldoRestante = saldoRestante; }

    public LocalDateTime getDataHora() { return dataHora; }
    public void setDataHora(LocalDateTime dataHora) { this.dataHora = dataHora; }

    @Override
    public String toString() {
        return String.format(
                "SaqueEventoDTO{idConta='%s', nomeTitular='%s', email='%s', valorSacado=R$ %.2f, saldoRestante=R$ %.2f, dataHora=%s}",
                idConta, nomeTitular, email, valorSacado, saldoRestante, dataHora
        );
    }
}
