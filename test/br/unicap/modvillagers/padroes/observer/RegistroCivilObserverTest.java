package br.unicap.modvillagers.padroes.observer;

import br.unicap.modvillagers.entidades.Player;
import br.unicap.modvillagers.entidades.Villager;
import br.unicap.modvillagers.eventos.RegistroCivilEventBus;
import br.unicap.modvillagers.itens.InspectorItem;
import br.unicap.modvillagers.padroes.observer.EventoRegistroCivil.TipoEvento;
import br.unicap.modvillagers.servico.CartorioRegistroCivil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Testes unitários para o padrão comportamental Observer ({@link RegistroCivilObserver})
 * integrado ao {@link RegistroCivilEventBus}.
 */
@DisplayName("Testes do Padrão Observer (RegistroCivilEventBus)")
class RegistroCivilObserverTest {

    @Test
    @DisplayName("Deve notificar AuditoriaHistoricoObserver em todos os tipos de evento")
    void deveAuditarEventosDoBarramento() {
        RegistroCivilEventBus eventBus = new RegistroCivilEventBus(new CartorioRegistroCivil(), false);
        AuditoriaHistoricoObserver auditoria = new AuditoriaHistoricoObserver();
        eventBus.adicionarObservador(auditoria);

        InspectorItem prancheta = new InspectorItem();
        Player jogador = new Player("Henry", "Planície");

        eventBus.registrarItem(prancheta);
        Villager v1 = eventBus.emitirDocumentoNoSpawn("Planície", "Desempregado");
        eventBus.atualizarHistoricoEmprego(v1, "Fazendeiro");
        eventBus.dispararInteracaoEntidade(jogador, prancheta, v1);
        eventBus.removerVillager(v1.getDocumentAttachment().getDocumento().getCpf());

        assertEquals(5, auditoria.getTotalEventos());
        assertEquals(1, auditoria.filtrarPorTipo(TipoEvento.ITEM_REGISTRADO).size());
        assertEquals(1, auditoria.filtrarPorTipo(TipoEvento.VILLAGER_NASCIDO).size());
        assertEquals(1, auditoria.filtrarPorTipo(TipoEvento.PROFISSAO_ATUALIZADA).size());
        assertEquals(1, auditoria.filtrarPorTipo(TipoEvento.CIDADAO_INSPECIONADO).size());
        assertEquals(1, auditoria.filtrarPorTipo(TipoEvento.VILLAGER_REMOVIDO).size());
    }

    @Test
    @DisplayName("Deve filtrar notificações do NotificacaoJogadorObserver pelo bioma do jogador e suportar remoção")
    void deveFiltrarNotificacoesPorBiomaERemoverObservador() {
        RegistroCivilEventBus eventBus = new RegistroCivilEventBus(new CartorioRegistroCivil(), false);
        Player jogador = new Player("Henry", "Planície");
        NotificacaoJogadorObserver obsJogador = new NotificacaoJogadorObserver(jogador, true);
        eventBus.adicionarObservador(obsJogador);

        eventBus.emitirDocumentoNoSpawn("Planície", "Fazendeiro"); // deve notificar
        eventBus.emitirDocumentoNoSpawn("Deserto", "Armeiro");     // bioma diferente: não notifica
        assertEquals(1, jogador.getHistoricoChat().size());
        assertTrue(jogador.getHistoricoChat().get(0).contains("[Alerta Cartório]"));

        assertTrue(eventBus.removerObservador(obsJogador));
        eventBus.emitirDocumentoNoSpawn("Planície", "Bibliotecário"); // observador removido: não notifica
        assertEquals(1, jogador.getHistoricoChat().size());
    }
}
