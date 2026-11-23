package br.unicap.modvillagers.comandos;

import br.unicap.modvillagers.eventos.RegistroCivilEventBus;
import br.unicap.modvillagers.itens.ModItems;
import br.unicap.modvillagers.padroes.observer.EventoRegistroCivil;
import br.unicap.modvillagers.padroes.strategy.DocumentoFormatStrategy;
import br.unicap.modvillagers.padroes.strategy.FormatoCompactoStrategy;
import br.unicap.modvillagers.padroes.strategy.FormatoFichaCompletaStrategy;
import br.unicap.modvillagers.padroes.strategy.FormatoJsonStrategy;
import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;
import java.util.Map;

/**
 * Registra comandos no jogo sob {@code /villagerrg} para consultar o censo civil,
 * inspecionar eventos da auditoria ({@code Observer}) e alternar o formato da prancheta ({@code Strategy}).
 */
public final class VillagerRGCommands {

    private VillagerRGCommands() {
    }

    /**
     * Registra o callback de comandos no servidor Fabric.
     */
    public static void registerCommands() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
            registrarArvoreComandos(dispatcher)
        );
    }

    private static void registrarArvoreComandos(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("villagerrg")
            .then(CommandManager.literal("censo").executes(ctx -> executarCenso(ctx.getSource())))
            .then(CommandManager.literal("auditoria").executes(ctx -> executarAuditoria(ctx.getSource())))
            .then(CommandManager.literal("formato")
                .then(CommandManager.literal("ficha").executes(ctx ->
                    alterarFormato(ctx.getSource(), new FormatoFichaCompletaStrategy())))
                .then(CommandManager.literal("compacto").executes(ctx ->
                    alterarFormato(ctx.getSource(), new FormatoCompactoStrategy())))
                .then(CommandManager.literal("json").executes(ctx ->
                    alterarFormato(ctx.getSource(), new FormatoJsonStrategy())))
            )
        );
    }

    private static int executarCenso(ServerCommandSource source) {
        RegistroCivilEventBus bus = RegistroCivilEventBus.getInstance();
        source.sendFeedback(() -> Text.literal("====== CENSO CIVIL VILLAGER-RG ======").formatted(Formatting.GOLD), false);
        source.sendFeedback(() -> Text.literal("Villagers carregados: " + bus.getTotalCarregados()).formatted(Formatting.AQUA), false);
        source.sendFeedback(() -> Text.literal("CPFs únicos emitidos na sessão: " + bus.getTotalCpfsEmitidos()).formatted(Formatting.AQUA), false);
        source.sendFeedback(() -> Text.literal("Biomas ativos: " + bus.listarBiomasAtivos()).formatted(Formatting.GREEN), false);

        Map<String, Integer> porProfissao = bus.contarPorProfissao();
        if (!porProfissao.isEmpty()) {
            source.sendFeedback(() -> Text.literal("Distribuição por profissão:").formatted(Formatting.YELLOW), false);
            for (Map.Entry<String, Integer> entry : porProfissao.entrySet()) {
                source.sendFeedback(() -> Text.literal(" - " + entry.getKey() + ": " + entry.getValue())
                        .formatted(Formatting.WHITE), false);
            }
        }
        return 1;
    }

    private static int executarAuditoria(ServerCommandSource source) {
        List<EventoRegistroCivil> eventos = RegistroCivilEventBus.getInstance()
                .getAuditoriaObserver()
                .getEventosRegistrados();
        int total = eventos.size();
        int inicio = Math.max(0, total - 8);

        source.sendFeedback(() -> Text.literal("====== AUDITORIA VILLAGER-RG (" + total + " eventos) ======")
                .formatted(Formatting.GOLD), false);
        for (int i = inicio; i < total; i++) {
            EventoRegistroCivil evento = eventos.get(i);
            source.sendFeedback(() -> Text.literal(" " + evento).formatted(Formatting.GRAY), false);
        }
        return 1;
    }

    private static int alterarFormato(ServerCommandSource source, DocumentoFormatStrategy estrategia) {
        ModItems.PRANCHETA_RECENSEAMENTO.setFormatoExibicao(estrategia);
        source.sendFeedback(
            () -> Text.literal("[Villager-RG] Estratégia de exibição alterada para: " + estrategia.getNomeFormato())
                    .formatted(Formatting.GOLD),
            false
        );
        return 1;
    }
}
