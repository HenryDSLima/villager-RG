package br.unicap.modvillagers.padroes.observer;

import br.unicap.modvillagers.modelo.VillagerDocument;
import br.unicap.modvillagers.util.ValidacaoUtil;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Representa um evento imutável disparado pelo {@code RegistroCivilEventBus} do mod
 * e entregue aos observadores registrados ({@link RegistroCivilObserver}).
 */
public class EventoRegistroCivil {

    private static final DateTimeFormatter FORMATO_HORA = DateTimeFormatter.ofPattern("HH:mm:ss");

    /**
     * Tipos de eventos suportados pelo barramento do mod Villager-RG.
     */
    public enum TipoEvento {
        /** Disparado quando um item registrável é cadastrado no Minecraft. */
        ITEM_REGISTRADO,
        /** Disparado quando um Villager recebe seu documento civil no mundo. */
        VILLAGER_NASCIDO,
        /** Disparado quando um Villager muda de profissão e atualiza seu histórico. */
        PROFISSAO_ATUALIZADA,
        /** Disparado quando um jogador inspeciona um Villager com a Prancheta. */
        CIDADAO_INSPECIONADO,
        /** Disparado quando um Villager é descarregado ou removido do mundo. */
        VILLAGER_REMOVIDO
    }

    private final TipoEvento tipo;
    private final String mensagem;
    private final VillagerDocument documento;
    private final LocalDateTime timestamp;

    /**
     * Cria um evento de registro civil sem documento associado.
     *
     * @param tipo     categoria do evento
     * @param mensagem descrição legível do evento
     */
    public EventoRegistroCivil(TipoEvento tipo, String mensagem) {
        this(tipo, mensagem, null);
    }

    /**
     * Cria um evento de registro civil associado a um {@link VillagerDocument}.
     *
     * @param tipo      categoria do evento
     * @param mensagem  descrição legível do evento
     * @param documento documento civil relacionado ao evento (opcional)
     */
    public EventoRegistroCivil(TipoEvento tipo, String mensagem, VillagerDocument documento) {
        this.tipo = ValidacaoUtil.exigirNaoNulo(tipo, "Tipo do evento");
        this.mensagem = ValidacaoUtil.exigirTextoValido(mensagem, "Mensagem do evento");
        this.documento = documento;
        this.timestamp = LocalDateTime.now();
    }

    /**
     * Retorna o tipo do evento ocorrido.
     *
     * @return constante de {@link TipoEvento}
     */
    public TipoEvento getTipo() {
        return tipo;
    }

    /**
     * Retorna a mensagem descritiva do evento.
     *
     * @return descrição do evento
     */
    public String getMensagem() {
        return mensagem;
    }

    /**
     * Retorna o {@link VillagerDocument} associado ao evento, quando aplicável.
     *
     * @return documento civil ou {@code null}
     */
    public VillagerDocument getDocumento() {
        return documento;
    }

    /**
     * Retorna o instante em que o evento foi criado.
     *
     * @return data e hora do evento
     */
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return "[" + timestamp.format(FORMATO_HORA) + " | " + tipo + "] " + mensagem;
    }
}
