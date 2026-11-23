package br.unicap.modvillagers.padroes.observer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Observador concreto ({@link RegistroCivilObserver}) que registra os eventos civis
 * no logger SLF4J oficial do mod Fabric.
 */
public class LoggerModObserver implements RegistroCivilObserver {

    private static final Logger LOGGER = LoggerFactory.getLogger("villagerrg");

    /**
     * Cria o observador de logging integrado ao logger do Fabric.
     */
    public LoggerModObserver() {
    }

    @Override
    public void onEvento(EventoRegistroCivil evento) {
        if (evento != null) {
            LOGGER.info("[Villager-RG] {}", evento);
        }
    }
}
