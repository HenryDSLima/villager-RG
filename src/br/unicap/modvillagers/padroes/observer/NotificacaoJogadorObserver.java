package br.unicap.modvillagers.padroes.observer;

import br.unicap.modvillagers.entidades.Player;
import br.unicap.modvillagers.entidades.Villager;
import br.unicap.modvillagers.util.ValidacaoUtil;

/**
 * Observador concreto ({@link RegistroCivilObserver}) que envia alertas no chat de um
 * {@link Player} sempre que nascimentos ou mudanças de profissão ocorrem no seu bioma
 * (ou em todo o mundo, conforme configuração).
 */
public class NotificacaoJogadorObserver implements RegistroCivilObserver {

    private final Player jogador;
    private final boolean apenasMesmoBioma;

    /**
     * Cria um observador que notifica o jogador sobre eventos civis ocorridos em seu bioma atual.
     *
     * @param jogador jogador que receberá as notificações no chat
     */
    public NotificacaoJogadorObserver(Player jogador) {
        this(jogador, true);
    }

    /**
     * Cria um observador de notificações para um jogador com filtro opcional por bioma.
     *
     * @param jogador          jogador destinatário dos avisos
     * @param apenasMesmoBioma se {@code true}, filtra avisos para o bioma atual do jogador
     */
    public NotificacaoJogadorObserver(Player jogador, boolean apenasMesmoBioma) {
        this.jogador = ValidacaoUtil.exigirNaoNulo(jogador, "Player");
        this.apenasMesmoBioma = apenasMesmoBioma;
    }

    /**
     * Retorna o jogador inscrito para receber notificações.
     *
     * @return instância de {@link Player}
     */
    public Player getJogador() {
        return jogador;
    }

    /**
     * Indica se as notificações estão restritas ao bioma atual do jogador.
     *
     * @return {@code true} se o filtro por bioma estiver ativo
     */
    public boolean isApenasMesmoBioma() {
        return apenasMesmoBioma;
    }

    /**
     * Envia uma notificação ao chat do jogador quando o evento representar o nascimento
     * ou a mudança de profissão de um {@link Villager} compatível com o filtro configurado.
     *
     * @param evento evento publicado pelo barramento
     */
    @Override
    public void onEvento(EventoRegistroCivil evento) {
        if (evento == null || evento.getVillager() == null) {
            return;
        }
        EventoRegistroCivil.TipoEvento tipo = evento.getTipo();
        if (tipo != EventoRegistroCivil.TipoEvento.VILLAGER_NASCIDO
                && tipo != EventoRegistroCivil.TipoEvento.PROFISSAO_ATUALIZADA) {
            return;
        }

        Villager villager = evento.getVillager();
        if (apenasMesmoBioma && !villager.getBiomaAtual().equalsIgnoreCase(jogador.getBiomaAtual())) {
            return;
        }

        jogador.enviarMensagemChat("[Alerta Cartório] " + evento.getMensagem());
    }
}
