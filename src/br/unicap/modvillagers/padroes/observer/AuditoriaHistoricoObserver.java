package br.unicap.modvillagers.padroes.observer;

import br.unicap.modvillagers.util.ValidacaoUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Observador concreto ({@link RegistroCivilObserver}) que mantém um histórico
 * imutável de auditoria com todos os eventos disparados no sistema de registro civil.
 */
public class AuditoriaHistoricoObserver implements RegistroCivilObserver {

    private final List<EventoRegistroCivil> eventosRegistrados;

    /**
     * Cria uma nova instância do observador de auditoria com histórico vazio.
     */
    public AuditoriaHistoricoObserver() {
        this.eventosRegistrados = new ArrayList<>();
    }

    /**
     * Armazena o evento recebido na trilha cronológica de auditoria.
     *
     * @param evento evento publicado pelo barramento
     */
    @Override
    public void onEvento(EventoRegistroCivil evento) {
        if (evento != null) {
            this.eventosRegistrados.add(evento);
        }
    }

    /**
     * Retorna uma visualização imutável de todos os eventos auditados.
     *
     * @return lista não modificável de eventos registrados
     */
    public List<EventoRegistroCivil> getEventosRegistrados() {
        return Collections.unmodifiableList(eventosRegistrados);
    }

    /**
     * Filtra os eventos auditados por um tipo específico.
     *
     * @param tipo categoria desejada de {@link EventoRegistroCivil.TipoEvento}
     * @return lista imutável contendo apenas os eventos do tipo informado
     */
    public List<EventoRegistroCivil> filtrarPorTipo(EventoRegistroCivil.TipoEvento tipo) {
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
     * Retorna a quantidade total de eventos registrados na auditoria.
     *
     * @return total de eventos auditados
     */
    public int getTotalEventos() {
        return eventosRegistrados.size();
    }
}
