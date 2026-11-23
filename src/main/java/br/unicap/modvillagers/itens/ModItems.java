package br.unicap.modvillagers.itens;

import br.unicap.modvillagers.VillagerRGMod;
import br.unicap.modvillagers.eventos.RegistroCivilEventBus;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

/**
 * Responsável pelo registro oficial dos itens do mod no {@link Registries#ITEM}
 * e na aba criativa de Ferramentas ({@link ItemGroups#TOOLS}) do Minecraft.
 */
public final class ModItems {

    /** Instância oficial da Prancheta de Recenseamento ({@code villagerrg:prancheta_recenseamento}). */
    public static final InspectorItem PRANCHETA_RECENSEAMENTO = new InspectorItem(
        new FabricItemSettings().maxCount(1)
    );

    private ModItems() {
    }

    /**
     * Registra todos os itens do mod no Minecraft e os notifica no {@link RegistroCivilEventBus}.
     */
    public static void registerModItems() {
        Registry.register(
            Registries.ITEM,
            new Identifier(VillagerRGMod.MOD_ID, "prancheta_recenseamento"),
            PRANCHETA_RECENSEAMENTO
        );

        RegistroCivilEventBus.getInstance().registrarItem(PRANCHETA_RECENSEAMENTO);

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(entries ->
            entries.add(PRANCHETA_RECENSEAMENTO)
        );
    }
}
