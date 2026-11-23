package br.unicap.modvillagers;

import br.unicap.modvillagers.modelo.VillagerDocument;
import br.unicap.modvillagers.padroes.builder.VillagerDocumentBuilder;
import br.unicap.modvillagers.padroes.observer.AuditoriaHistoricoObserver;
import br.unicap.modvillagers.padroes.observer.EventoRegistroCivil;
import br.unicap.modvillagers.padroes.observer.EventoRegistroCivil.TipoEvento;
import br.unicap.modvillagers.padroes.strategy.FormatoCompactoStrategy;
import br.unicap.modvillagers.padroes.strategy.FormatoFichaCompletaStrategy;
import br.unicap.modvillagers.padroes.strategy.FormatoJsonStrategy;
import br.unicap.modvillagers.servico.DocumentGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Testes unitários dos padrões GoF (Builder, Strategy e Observer) integrados ao mod Fabric.
 */
@DisplayName("Testes dos Padrões GoF no Mod Fabric")
class GoFPatternsTest {

    @Test
    @DisplayName("Deve construir via Builder e formatar nas 3 estratégias (Ficha, Compacto, JSON)")
    void deveAplicarBuilderEStrategy() {
        DocumentGenerator gerador = new DocumentGenerator(new Random(1234));
        VillagerDocument doc = new VillagerDocumentBuilder()
                .comNome("Astrid Pinheiro")
                .comCpf(gerador.gerarCpfValido())
                .comRg(gerador.gerarRg())
                .comBiomaOrigem("Taiga")
                .comProfissao("Cartógrafo")
                .build();

        assertTrue(doc.formatar(new FormatoFichaCompletaStrategy(), "Cartógrafo").contains("REGISTRO CIVIL DE VILLAGER"));
        assertTrue(doc.formatar(new FormatoCompactoStrategy(), "Cartógrafo").startsWith("[RG] Astrid Pinheiro"));
        assertTrue(doc.formatar(new FormatoJsonStrategy(), "Cartógrafo").contains("\"biomaOrigem\": \"Taiga\""));
    }

    @Test
    @DisplayName("Deve registrar e filtrar eventos no AuditoriaHistoricoObserver")
    void deveAuditarEventosObserver() {
        AuditoriaHistoricoObserver auditoria = new AuditoriaHistoricoObserver();
        auditoria.onEvento(new EventoRegistroCivil(TipoEvento.VILLAGER_NASCIDO, "Nascimento teste"));
        auditoria.onEvento(new EventoRegistroCivil(TipoEvento.CIDADAO_INSPECIONADO, "Inspeção teste"));

        assertEquals(2, auditoria.getTotalEventos());
        assertEquals(1, auditoria.filtrarPorTipo(TipoEvento.VILLAGER_NASCIDO).size());
        assertEquals(1, auditoria.filtrarPorTipo(TipoEvento.CIDADAO_INSPECIONADO).size());
    }
}
