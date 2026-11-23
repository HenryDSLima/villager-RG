package br.unicap.modvillagers.itens;

import br.unicap.modvillagers.entidades.LivingEntity;
import br.unicap.modvillagers.entidades.Player;
import br.unicap.modvillagers.entidades.Villager;
import br.unicap.modvillagers.modelo.DocumentAttachment;
import br.unicap.modvillagers.modelo.VillagerDocument;
import br.unicap.modvillagers.padroes.strategy.DocumentoFormatStrategy;
import br.unicap.modvillagers.padroes.strategy.FormatoFichaCompletaStrategy;
import br.unicap.modvillagers.util.ValidacaoUtil;

/**
 * Item customizado ("Prancheta de Recenseamento") utilizado pelo jogador para
 * inspecionar o Registro Civil de Villagers.
 *
 * <p>Aplica herança ao estender {@link Item}, polimorfismo ao sobrescrever
 * {@link #interactLivingEntity(Player, LivingEntity)}, interface via {@link ItemRegistrable}
 * e o padrão <b>Strategy</b> ({@link DocumentoFormatStrategy}) para alternar o formato
 * de exibição no chat em tempo de execução.</p>
 */
public class InspectorItem extends Item implements ItemRegistrable {

    private String registryId;
    private boolean registrado;
    private DocumentoFormatStrategy formatoExibicao;

    /**
     * Cria a Prancheta de Recenseamento padrão utilizando a estratégia {@link FormatoFichaCompletaStrategy}.
     */
    public InspectorItem() {
        this(
            "villagerrg:prancheta_recenseamento",
            "Prancheta de Recenseamento",
            "Permite inspecionar os documentos civis (Nome, CPF, RG e Histórico) de Villagers.",
            new FormatoFichaCompletaStrategy()
        );
    }

    /**
     * Cria um item de inspeção customizado utilizando a estratégia padrão de ficha completa.
     *
     * @param registryId identificador único de registro do item
     * @param nomeItem   nome de exibição do item
     * @param descricao  descrição funcional do item
     */
    public InspectorItem(String registryId, String nomeItem, String descricao) {
        this(registryId, nomeItem, descricao, new FormatoFichaCompletaStrategy());
    }

    /**
     * Cria um item de inspeção customizado com estratégia de formatação configurável.
     *
     * @param registryId      identificador único de registro do item
     * @param nomeItem        nome de exibição do item
     * @param descricao       descrição funcional do item
     * @param formatoExibicao estratégia {@link DocumentoFormatStrategy} utilizada na exibição no chat
     */
    public InspectorItem(
            String registryId,
            String nomeItem,
            String descricao,
            DocumentoFormatStrategy formatoExibicao) {
        super(nomeItem, descricao, 1);
        setRegistryId(registryId);
        setFormatoExibicao(formatoExibicao);
        this.registrado = false;
    }

    /**
     * Retorna o identificador único de registro deste item.
     *
     * @return registry ID do item
     */
    @Override
    public String getRegistryId() {
        return registryId;
    }

    /**
     * Atualiza o identificador único de registro deste item.
     *
     * @param registryId novo identificador de registro
     */
    public void setRegistryId(String registryId) {
        this.registryId = ValidacaoUtil.exigirTextoValido(registryId, "Registry ID do item");
    }

    /**
     * Retorna a estratégia atual de formatação do documento exibido no chat.
     *
     * @return estratégia {@link DocumentoFormatStrategy} ativa
     */
    public DocumentoFormatStrategy getFormatoExibicao() {
        return formatoExibicao;
    }

    /**
     * Altera dinamicamente a estratégia de formatação utilizada nas inspeções (padrão Strategy).
     *
     * @param formatoExibicao nova estratégia de formatação
     */
    public void setFormatoExibicao(DocumentoFormatStrategy formatoExibicao) {
        this.formatoExibicao = ValidacaoUtil.exigirNaoNulo(formatoExibicao, "Estratégia de formatação");
    }

    /**
     * Retorna o nome legível do item para exibição ao jogador.
     *
     * @return nome de exibição do item
     */
    @Override
    public String getNomeExibicao() {
        return getNomeItem();
    }

    /**
     * Marca o item como registrado no sistema de eventos.
     */
    @Override
    public void registrar() {
        this.registrado = true;
    }

    /**
     * Indica se o item já foi registrado no sistema de eventos.
     *
     * @return {@code true} se estiver registrado
     */
    @Override
    public boolean isRegistrado() {
        return registrado;
    }

    /**
     * Sobrescreve o comportamento padrão de {@link Item} para ler o documento civil do
     * {@link Villager} e exibi-lo no chat do jogador usando a {@link DocumentoFormatStrategy} configurada.
     *
     * @param jogador jogador que realiza a inspeção
     * @param alvo    entidade viva inspecionada
     * @return {@code true} se o alvo for um Villager com documento ativo; {@code false} caso contrário
     */
    @Override
    public boolean interactLivingEntity(Player jogador, LivingEntity alvo) {
        if (jogador == null || alvo == null) {
            return false;
        }

        if (!(alvo instanceof Villager villager)) {
            jogador.enviarMensagemChat(
                "[" + getNomeItem() + "] A entidade '" + alvo.getTipoEntidade()
                + "' não possui Registro Civil."
            );
            return false;
        }

        DocumentAttachment attachment = villager.getDocumentAttachment();
        if (!villager.isAtiva() || attachment == null || !attachment.hasDocumento()) {
            jogador.enviarMensagemChat(
                "[" + getNomeItem() + "] Este Villager não possui um documento civil ativo."
            );
            return false;
        }

        VillagerDocument documento = attachment.getDocumento();
        String saidaFormatada = documento.formatar(formatoExibicao, villager.getProfissaoAtual());
        jogador.enviarMensagemChat(saidaFormatada);
        return true;
    }

    /**
     * Retorna a representação textual do item de inspeção, incluindo o formato ativo.
     *
     * @return string descritiva do {@code InspectorItem}
     */
    @Override
    public String toString() {
        return "InspectorItem{"
                + "registryId='" + registryId + '\''
                + ", nome='" + getNomeItem() + '\''
                + ", formato='" + formatoExibicao.getNomeFormato() + '\''
                + ", registrado=" + registrado
                + '}';
    }
}
