package br.unicap.modvillagers.padroes.strategy;

import br.unicap.modvillagers.entidades.Player;
import br.unicap.modvillagers.entidades.Villager;
import br.unicap.modvillagers.itens.InspectorItem;
import br.unicap.modvillagers.modelo.VillagerDocument;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Testes unitários para o padrão comportamental Strategy ({@link DocumentoFormatStrategy})
 * e sua integração dinâmica com {@link InspectorItem}.
 */
@DisplayName("Testes do Padrão Strategy (DocumentoFormatStrategy)")
class DocumentoFormatStrategyTest {

    @Test
    @DisplayName("Deve formatar o mesmo documento em Ficha Completa, Compacto e JSON")
    void deveFormatarDocumentoEmMultiplasEstrategias() {
        Villager villager = new Villager("Deserto", "Ferreiro de Armas");
        VillagerDocument doc = villager.getDocumentAttachment().getDocumento();

        String ficha = doc.formatar(new FormatoFichaCompletaStrategy(), villager.getProfissaoAtual());
        String compacto = doc.formatar(new FormatoCompactoStrategy(), villager.getProfissaoAtual());
        String json = doc.formatar(new FormatoJsonStrategy(), villager.getProfissaoAtual());

        assertTrue(ficha.contains("REGISTRO CIVIL DE VILLAGER"));
        assertTrue(ficha.contains(doc.getCpf()));

        assertTrue(compacto.startsWith("[RG] "));
        assertFalse(compacto.contains("\n"));
        assertTrue(compacto.contains(doc.getCpf()));

        assertTrue(json.startsWith("{") && json.endsWith("}"));
        assertTrue(json.contains("\"cpf\": \"" + doc.getCpf() + "\""));
        assertTrue(json.contains("\"profissaoAtual\": \"Ferreiro de Armas\""));
    }

    @Test
    @DisplayName("Deve alternar dinamicamente a estratégia de exibição no InspectorItem")
    void deveAlternarEstrategiaNoInspectorItem() {
        Player jogador = new Player("Henry", "Planície");
        Villager villager = new Villager("Planície", "Bibliotecário");
        InspectorItem prancheta = new InspectorItem();

        assertEquals("Ficha Completa", prancheta.getFormatoExibicao().getNomeFormato());
        assertTrue(prancheta.interactLivingEntity(jogador, villager));
        assertTrue(jogador.getHistoricoChat().get(0).contains("REGISTRO CIVIL DE VILLAGER"));

        prancheta.setFormatoExibicao(new FormatoCompactoStrategy());
        assertTrue(prancheta.interactLivingEntity(jogador, villager));
        assertTrue(jogador.getHistoricoChat().get(1).startsWith("[RG] "));

        prancheta.setFormatoExibicao(new FormatoJsonStrategy());
        assertTrue(prancheta.interactLivingEntity(jogador, villager));
        assertTrue(jogador.getHistoricoChat().get(2).contains("\"historicoProfissoes\""));
    }
}
