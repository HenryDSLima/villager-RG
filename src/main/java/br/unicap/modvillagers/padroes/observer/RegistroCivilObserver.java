package br.unicap.modvillagers.padroes.observer;

/**
 * Interface do padrão de projeto comportamental <b>Observer (GoF)</b> para assinantes
 * de eventos publicados pelo {@code RegistroCivilEventBus} do mod.
 */
public interface RegistroCivilObserver {

    /**
     * Método invocado sempre que um novo {@link EventoRegistroCivil} é publicado.
     *
     * @param evento dados imutáveis do evento ocorrido
     */
    void onEvento(EventoRegistroCivil evento);
}
