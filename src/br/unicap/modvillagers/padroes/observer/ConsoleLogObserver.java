package br.unicap.modvillagers.padroes.observer;

/**
 * Observador concreto ({@link RegistroCivilObserver}) responsável por exibir
 * no console os eventos publicados pelo {@code RegistroCivilEventBus}.
 */
public class ConsoleLogObserver implements RegistroCivilObserver {

    /**
     * Cria uma nova instância do observador de log em console.
     */
    public ConsoleLogObserver() {
    }

    /**
     * Imprime no console padrão a descrição formatada do evento recebido.
     *
     * @param evento evento publicado pelo barramento
     */
    @Override
    public void onEvento(EventoRegistroCivil evento) {
        if (evento != null) {
            System.out.println("[EventBus] " + evento);
        }
    }
}
