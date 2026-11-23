package br.unicap.modvillagers.itens;

import br.unicap.modvillagers.VillagerRGMod;
import br.unicap.modvillagers.eventos.RegistroCivilEventBus;
import br.unicap.modvillagers.modelo.VillagerDocument;
import br.unicap.modvillagers.modelo.VillagerDocumentHolder;
import br.unicap.modvillagers.padroes.strategy.DocumentoFormatStrategy;
import br.unicap.modvillagers.padroes.strategy.FormatoCompactoStrategy;
import br.unicap.modvillagers.padroes.strategy.FormatoFichaCompletaStrategy;
import br.unicap.modvillagers.padroes.strategy.FormatoJsonStrategy;
import br.unicap.modvillagers.servico.VillagerTradutor;
import br.unicap.modvillagers.util.ValidacaoUtil;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Item customizado do Minecraft ("Prancheta de Recenseamento") que estende a classe nativa
 * {@link Item}, implementa {@link ItemRegistrable} e aplica o padrão <b>Strategy</b>
 * ({@link DocumentoFormatStrategy}) para exibir o Registro Civil de Villagers no chat do jogador.
 *
 * <p><b>Controles no jogo:</b></p>
 * <ul>
 *   <li><b>Clique direito em um Villager:</b> exibe o documento civil no chat conforme a estratégia ativa.</li>
 *   <li><b>Shift + Clique direito no ar:</b> alterna dinamicamente a estratégia de exibição
 *       (Ficha Completa -&gt; Compacto -&gt; JSON).</li>
 * </ul>
 */
public class InspectorItem extends Item implements ItemRegistrable {

    private static final List<DocumentoFormatStrategy> ESTRATEGIAS_DISPONIVEIS = List.of(
        new FormatoFichaCompletaStrategy(),
        new FormatoCompactoStrategy(),
        new FormatoJsonStrategy()
    );

    private final String registryId;
    private boolean registrado;
    private int indiceEstrategia;
    private DocumentoFormatStrategy formatoExibicao;

    /**
     * Cria a instância da Prancheta de Recenseamento com pilha máxima de 1 unidade
     * e estratégia inicial {@link FormatoFichaCompletaStrategy}.
     *
     * @param settings configurações nativas do {@link Item} no Minecraft
     */
    public InspectorItem(Settings settings) {
        super(settings);
        this.registryId = VillagerRGMod.MOD_ID + ":prancheta_recenseamento";
        this.registrado = false;
        this.indiceEstrategia = 0;
        this.formatoExibicao = ESTRATEGIAS_DISPONIVEIS.get(0);
    }

    @Override
    public String getRegistryId() {
        return registryId;
    }

    @Override
    public String getNomeExibicao() {
        return "Prancheta de Recenseamento";
    }

    @Override
    public void registrar() {
        this.registrado = true;
    }

    @Override
    public boolean isRegistrado() {
        return registrado;
    }

    /**
     * Retorna a estratégia {@link DocumentoFormatStrategy} atualmente ativa na prancheta.
     *
     * @return estratégia de formatação ativa
     */
    public DocumentoFormatStrategy getFormatoExibicao() {
        return formatoExibicao;
    }

    /**
     * Altera dinamicamente a estratégia de formatação utilizada nas inspeções (padrão Strategy).
     *
     * @param formatoExibicao nova estratégia desejada
     */
    public void setFormatoExibicao(DocumentoFormatStrategy formatoExibicao) {
        this.formatoExibicao = ValidacaoUtil.exigirNaoNulo(formatoExibicao, "DocumentoFormatStrategy");
        for (int i = 0; i < ESTRATEGIAS_DISPONIVEIS.size(); i++) {
            if (ESTRATEGIAS_DISPONIVEIS.get(i).getNomeFormato().equalsIgnoreCase(formatoExibicao.getNomeFormato())) {
                this.indiceEstrategia = i;
                break;
            }
        }
    }

    /**
     * Alterna ciclicamente para a próxima estratégia de formatação disponível.
     *
     * @return a nova estratégia ativada
     */
    public DocumentoFormatStrategy alternarFormato() {
        this.indiceEstrategia = (this.indiceEstrategia + 1) % ESTRATEGIAS_DISPONIVEIS.size();
        this.formatoExibicao = ESTRATEGIAS_DISPONIVEIS.get(this.indiceEstrategia);
        return this.formatoExibicao;
    }

    /**
     * Permite que o jogador alterne o formato de exibição com {@code Shift + Clique Direito} no ar.
     */
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        if (user.isSneaking()) {
            if (!world.isClient()) {
                DocumentoFormatStrategy novoFormato = alternarFormato();
                user.sendMessage(
                    Text.literal("[Prancheta] Modo de exibição alterado para: " + novoFormato.getNomeFormato())
                            .formatted(Formatting.GOLD),
                    true
                );
            }
            return TypedActionResult.success(stack, world.isClient());
        }
        return TypedActionResult.pass(stack);
    }

    /**
     * Sobrescreve o método nativo de interação com entidades vivas para inspecionar
     * o Registro Civil de {@link VillagerEntity}s.
     */
    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        if (user.getWorld().isClient()) {
            return ActionResult.SUCCESS;
        }

        if (!(entity instanceof VillagerEntity villager)) {
            String nomeEntidade = entity.getType().getName().getString();
            user.sendMessage(
                Text.literal("[Prancheta] A entidade '" + nomeEntidade + "' não possui Registro Civil.")
                        .formatted(Formatting.YELLOW),
                false
            );
            return ActionResult.SUCCESS;
        }

        VillagerDocumentHolder holder = (VillagerDocumentHolder) villager;
        VillagerDocument documento = holder.villagerrg$garantirDocumento();
        String profissaoAtual = VillagerTradutor.traduzirProfissao(villager.getVillagerData().getProfession());

        String textoFormatado = documento.formatar(formatoExibicao, profissaoAtual);
        for (String linha : textoFormatado.split("\n")) {
            Formatting cor = linha.startsWith("===") ? Formatting.GOLD : Formatting.AQUA;
            user.sendMessage(Text.literal(linha).formatted(cor), false);
        }

        RegistroCivilEventBus.getInstance().onCidadaoInspecionado(user.getName().getString(), documento);
        return ActionResult.SUCCESS;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.literal("Clique direito em um Villager para ver o RG/CPF.").formatted(Formatting.GRAY));
        tooltip.add(Text.literal("Shift + Clique direito para trocar o formato.").formatted(Formatting.DARK_AQUA));
        tooltip.add(Text.literal("Formato atual: " + formatoExibicao.getNomeFormato()).formatted(Formatting.GOLD));
    }
}
