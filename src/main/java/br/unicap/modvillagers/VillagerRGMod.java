package br.unicap.modvillagers;

import br.unicap.modvillagers.comandos.VillagerRGCommands;
import br.unicap.modvillagers.eventos.RegistroCivilEventBus;
import br.unicap.modvillagers.itens.InspectorItem;
import br.unicap.modvillagers.itens.ModItems;
import br.unicap.modvillagers.modelo.VillagerDocument;
import br.unicap.modvillagers.modelo.VillagerDocumentHolder;
import br.unicap.modvillagers.servico.VillagerTradutor;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Ponto de entrada oficial ({@link ModInitializer}) do mod <b>Villager-RG</b> para
 * Minecraft 1.20.1 (Fabric).
 *
 * <p>Conecta o núcleo orientado a objetos (domínio, validações, Builder, Strategy e Observer)
 * aos registros e callbacks nativos da API Fabric.</p>
 */
public class VillagerRGMod implements ModInitializer {

    /** Identificador oficial do mod no Fabric Loader. */
    public static final String MOD_ID = "villagerrg";
    /** Logger SLF4J do mod. */
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("Inicializando Sistema de Registro Civil para Villagers (Villager-RG)...");

        ModItems.registerModItems();
        registrarEventosFabric();
        VillagerRGCommands.registerCommands();

        LOGGER.info("Villager-RG inicializado com sucesso!");
    }

    private void registrarEventosFabric() {
        // Garante que todo Villager carregado/gerado no mundo possua documento civil persistente
        ServerEntityEvents.ENTITY_LOAD.register((entity, world) -> {
            if (entity instanceof VillagerEntity villager) {
                VillagerDocumentHolder holder = (VillagerDocumentHolder) villager;
                VillagerDocument existente = holder.villagerrg$getDocumento();
                String profissao = VillagerTradutor.traduzirProfissao(villager.getVillagerData().getProfession());

                if (existente == null) {
                    holder.villagerrg$garantirDocumento();
                } else {
                    RegistroCivilEventBus.getInstance()
                            .registrarVillagerCarregado(villager.getUuid(), existente, profissao);
                }
            }
        });

        // Remove Villagers descarregados/mortos do censo ativo em memória
        ServerEntityEvents.ENTITY_UNLOAD.register((entity, world) -> {
            if (entity instanceof VillagerEntity villager) {
                RegistroCivilEventBus.getInstance().onVillagerDescarregado(villager.getUuid());
            }
        });

        // Intercepta o clique direito com a Prancheta antes que a tela de trocas do Villager abra
        UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            ItemStack stack = player.getStackInHand(hand);
            if (stack.getItem() instanceof InspectorItem inspector && entity instanceof LivingEntity living) {
                return inspector.useOnEntity(stack, player, living, hand);
            }
            return ActionResult.PASS;
        });
    }
}
