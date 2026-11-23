package br.unicap.modvillagers.padroes.observer;

import br.unicap.modvillagers.util.ValidacaoUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Observador concreto ({@link RegistroCivilObserver}) que mantém uma trilha de
 * auditoria em memória com todos os eventos civis ocorridos na sessão do servidor.
 */
public class AuditoriaHistoricoObserver implements RegistroCivilObserver {

    private static final int LIMITE_HISTORICO = 500;
    private final List<EventoRegistroCivil> eventosRegistrados;

    /**
     * Cria um novo observador de auditoria com histórico vazio.
     */
    public AuditoriaHistoricoObserver() {
        this.eventosRegistrados = new ArrayList<>();
    }

    @Override
    public synchronized void onEvento(EventoRegistroCivil evento) {
        if (evento == null) {
            return;
        }
        if (this.eventosRegistrados.size() >= LIMITE_HISTORICO) {
            this.eventosRegistrados.remove(0);
        }
        this.eventosRegistrados.add(evento);
    }

    /**
     * Retorna uma visualização imutável dos eventos auditados.
     *
     * @return lista imutável de eventos registrados
     */
    public synchronized List<EventoRegistroCivil> getEventosRegistrados() {
        return Collections.unmodifiableList(new ArrayList<>(eventosRegistrados));
    }

    /**
     * Filtra os eventos auditados por um tipo específico.
     *
     * @param tipo tipo de evento desejado
     * @return lista imutável filtrada
     */
    public synchronized List<EventoRegistroCivil> filtrarPorTipo(EventoRegistroCivil.TipoEvento tipo) {
        ValidacaoUtil.exigirNaoNulo(tipo, "TipoEvento");
        List<EventoRegistroCivil> filtrados = new ArrayList<>();
        for (EventoRegistroCivil evento : eventosRegistrados) {
            if (evento.getTipo() == tipo) {
                filtrados.add(evento);
            }
        }
        return Collections.unmodifiableList(filtrados);
    }

    /**
     * Retorna o total de eventos atualmente registrados na auditoria.
     *
     * @return quantidade de eventos auditados
     */
    public synchronized int getTotalEventos() {
        return eventosRegistrados.size();
    }
}
