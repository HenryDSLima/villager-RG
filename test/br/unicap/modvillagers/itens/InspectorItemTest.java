package br.unicap.modvillagers.itens;

import br.unicap.modvillagers.entidades.LivingEntity;
import br.unicap.modvillagers.entidades.Player;
import br.unicap.modvillagers.entidades.Villager;
import br.unicap.modvillagers.eventos.RegistroCivilEventBus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Testes unitários para {@link Item}, {@link InspectorItem} e {@link ItemRegistrable},
 * validando herança, contrato de interface e polimorfismo na interação com entidades.
 */
@DisplayName("Testes de InspectorItem, Herança e Polimorfismo")
class InspectorItemTest {

    @Test
    @DisplayName("Deve cumprir o contrato da interface ItemRegistrable ao ser registrado no EventBus")
    void deveRegistrarViaInterfaceItemRegistrable() {
        RegistroCivilEventBus eventBus = new RegistroCivilEventBus();
        InspectorItem prancheta = new InspectorItem();

        assertFalse(prancheta.isRegistrado());
        eventBus.registrarItem(prancheta);
        assertTrue(prancheta.isRegistrado());
        assertEquals("villagerrg:prancheta_recenseamento", prancheta.getRegistryId());
        assertEquals(prancheta, eventBus.buscarItemRegistrado(prancheta.getRegistryId()));
    }

    @Test
    @DisplayName("Deve aplicar polimorfismo entre Item base e InspectorItem ao interagir com Villager")
    void deveAplicarPolimorfismoNaInteracao() {
        Player jogador = new Player("Henry", "Planície");
        Villager villager = new Villager("Planície", "Bibliotecário");

        Item itemBase = new Item("Graveto", "Graveto comum", 64);
        Item itemPolimorfico = new InspectorItem();

        assertFalse(itemBase.interactLivingEntity(jogador, villager));
        assertTrue(itemPolimorfico.interactLivingEntity(jogador, villager));
        assertEquals(1, jogador.getHistoricoChat().size());
        assertTrue(jogador.getHistoricoChat().get(0).contains(villager.getDocumentAttachment().getDocumento().getCpf()));
    }

    @Test
    @DisplayName("Deve retornar false ao inspecionar entidade não-Villager ou Villager removido")
    void deveRetornarFalseParaAlvoSemRegistroCivil() {
        Player jogador = new Player("Henry", "Planície");
        InspectorItem prancheta = new InspectorItem();

        LivingEntity ovelha = new LivingEntity("Ovelha", 8.0, "Planície");
        assertFalse(prancheta.interactLivingEntity(jogador, ovelha));

        Villager villagerRemovido = new Villager("Neve", "Clérigo");
        villagerRemovido.removerDoMundo();
        assertFalse(prancheta.interactLivingEntity(jogador, villagerRemovido));
    }
}
