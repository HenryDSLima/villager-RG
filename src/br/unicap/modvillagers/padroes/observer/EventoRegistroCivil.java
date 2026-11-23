package br.unicap.modvillagers.padroes.observer;

import br.unicap.modvillagers.entidades.Villager;
import br.unicap.modvillagers.util.ValidacaoUtil;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Representa um evento imutável disparado pelo {@code RegistroCivilEventBus} e
 * entregue aos observadores registrados ({@link RegistroCivilObserver}).
 */
public class EventoRegistroCivil {

    private static final DateTimeFormatter FORMATO_HORA = DateTimeFormatter.ofPattern("HH:mm:ss");

    /**
     * Tipos de eventos suportados pelo barramento do Sistema de Registro Civil.
     */
    public enum TipoEvento {
        /** Disparado quando um item registrável é cadastrado no barramento. */
        ITEM_REGISTRADO,
        /** Disparado quando um Villager nasce e recebe seu documento civil. */
        VILLAGER_NASCIDO,
        /** Disparado quando um Villager muda de profissão e atualiza seu histórico. */
        PROFISSAO_ATUALIZADA,
        /** Disparado quando um jogador inspeciona com sucesso um Villager. */
        CIDADAO_INSPECIONADO,
        /** Disparado quando um Villager é removido do cadastro ativo e do mundo. */
        VILLAGER_REMOVIDO
    }

    private final TipoEvento tipo;
    private final String mensagem;
    private final Villager villager;
    private final LocalDateTime timestamp;

    /**
     * Cria um novo evento de registro civil sem entidade Villager associada.
     *
     * @param tipo     categoria do evento ocorrido
     * @param mensagem descrição legível do evento
     */
    public EventoRegistroCivil(TipoEvento tipo, String mensagem) {
        this(tipo, mensagem, null);
    }

    /**
     * Cria um novo evento de registro civil associado a um {@link Villager}.
     *
     * @param tipo     categoria do evento ocorrido
     * @param mensagem descrição legível do evento
     * @param villager entidade Villager relacionada ao evento (opcional)
     */
    public EventoRegistroCivil(TipoEvento tipo, String mensagem, Villager villager) {
        this.tipo = ValidacaoUtil.exigirNaoNulo(tipo, "Tipo do evento");
        this.mensagem = ValidacaoUtil.exigirTextoValido(mensagem, "Mensagem do evento");
        this.villager = villager;
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
     * Retorna o {@link Villager} associado ao evento, quando aplicável.
     *
     * @return instância de {@link Villager} ou {@code null}
     */
    public Villager getVillager() {
        return villager;
    }

    /**
     * Retorna o instante exato em que o evento foi criado.
     *
     * @return data e hora do evento
     */
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    /**
     * Retorna uma representação textual padronizada do evento com horário e tipo.
     *
     * @return representação formatada do evento
     */
    @Override
    public String toString() {
        return "[" + timestamp.format(FORMATO_HORA) + " | " + tipo + "] " + mensagem;
    }
}
