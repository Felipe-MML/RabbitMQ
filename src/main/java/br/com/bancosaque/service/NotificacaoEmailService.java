package br.com.bancosaque.service;

import br.com.bancosaque.dto.SaqueEventoDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.format.DateTimeFormatter;

public class NotificacaoEmailService {

    private static final Logger log = LoggerFactory.getLogger(NotificacaoEmailService.class);
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy 'às' HH:mm:ss");


    public void enviarNotificacao(SaqueEventoDTO evento) {
        log.info("[E-mail] Simulando envio de e-mail para: {}", evento.getEmail());

        String corpo = construirCorpoEmail(evento);
        System.out.println(corpo);

        log.info("[E-mail] E-mail simulado enviado com sucesso para {}", evento.getEmail());
    }

    /**
     * Constrói o corpo do e-mail formatado para exibição no console.
     */
    private String construirCorpoEmail(SaqueEventoDTO evento) {
        String dataFormatada = evento.getDataHora() != null
                ? evento.getDataHora().format(FORMATTER)
                : "Data não disponível";

        return """
                
                ╔══════════════════════════════════════════════════════════════╗
                ║              NOTIFICAÇÃO DE SAQUE - BANCO JAVA MSG           ║
                ╠══════════════════════════════════════════════════════════════╣
                ║  Para:          %s                                           ║
                ║  Destinatário:  %s                                           ║
                ╠══════════════════════════════════════════════════════════════╣
                ║  Detalhes da Transação                                       ║
                ╠══════════════════════════════════════════════════════════════╣
                ║  Conta:         %s                                           ║
                ║  Valor Sacado:  R$ %,.2f                                     ║
                ║  Saldo Atual:   R$ %,.2f                                     ║
                ║  Data/Hora:     %s                                           ║
                ╠══════════════════════════════════════════════════════════════╣
                ║  Se você não reconhece esta transação, entre em contato      ║
                ║  imediatamente com nossa central: 0800-123-4567              ║
                ╚══════════════════════════════════════════════════════════════╝
                """.formatted(
                evento.getEmail(),
                evento.getNomeTitular(),
                evento.getIdConta(),
                evento.getValorSacado(),
                evento.getSaldoRestante(),
                dataFormatada
        );
    }
}
