package br.unicap.modvillagers.padroes.observer;

/**
 * Interface do padrão de projeto comportamental <b>Observer (GoF)</b> para assinantes
 * de eventos publicados pelo {@code RegistroCivilEventBus}.
 *
 * <p><b>Problema resolvido:</b> anteriormente, o barramento de eventos possuía saída
 * acoplada diretamente ao console ({@code System.out.println}), impedindo que múltiplos
 * interessados (logs, auditoria, notificações a jogadores) reagissem de forma independente.</p>
 *
 * <p><b>Benefício:</b> desacopla o produtor de eventos ({@code RegistroCivilEventBus}) dos
 * seus consumidores, permitindo adicionar ou remover comportamentos reativos em tempo de execução.</p>
 */
public interface RegistroCivilObserver {

    /**
     * Método de callback invocado sempre que um novo {@link EventoRegistroCivil} é publicado.
     *
     * @param evento dados imutáveis do evento ocorrido
     */
    void onEvento(EventoRegistroCivil evento);
}
