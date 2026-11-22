package br.unicap.modvillagers.itens;

import br.unicap.modvillagers.entidades.LivingEntity;
import br.unicap.modvillagers.entidades.Player;
import br.unicap.modvillagers.entidades.Villager;
import br.unicap.modvillagers.modelo.DocumentAttachment;
import br.unicap.modvillagers.modelo.VillagerDocument;
import br.unicap.modvillagers.util.ValidacaoUtil;

/**
 * Item customizado ("Prancheta de Recenseamento") utilizado pelo jogador para
 * inspecionar o Registro Civil de Villagers.
 *
 * <p>Aplica herança ao estender {@link Item}, polimorfismo ao sobrescrever
 * {@link #interactLivingEntity(Player, LivingEntity)} e implementação de interface
 * ao cumprir o contrato de {@link ItemRegistrable}.</p>
 */
public class InspectorItem extends Item implements ItemRegistrable {

    private String registryId;
    private boolean registrado;

    public InspectorItem() {
        this(
            "villagerrg:prancheta_recenseamento",
            "Prancheta de Recenseamento",
            "Permite inspecionar os documentos civis (Nome, CPF, RG e Histórico) de Villagers."
        );
    }

    public InspectorItem(String registryId, String nomeItem, String descricao) {
        super(nomeItem, descricao, 1);
        setRegistryId(registryId);
        this.registrado = false;
    }

    @Override
    public String getRegistryId() {
        return registryId;
    }

    public void setRegistryId(String registryId) {
        this.registryId = ValidacaoUtil.exigirTextoValido(registryId, "Registry ID do item");
    }

    @Override
    public String getNomeExibicao() {
        return getNomeItem();
    }

    @Override
    public void registrar() {
        this.registrado = true;
    }

    @Override
    public boolean isRegistrado() {
        return registrado;
    }

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
        jogador.enviarMensagemChat(documento.formatarParaChat(villager.getProfissaoAtual()));
        return true;
    }

    @Override
    public String toString() {
        return "InspectorItem{"
                + "registryId='" + registryId + '\''
                + ", nome='" + getNomeItem() + '\''
                + ", registrado=" + registrado
                + '}';
    }
}
