package br.unicap.modvillagers.entidades;

import br.unicap.modvillagers.modelo.DocumentAttachment;
import br.unicap.modvillagers.persistencia.NBTTagCompound;

/**
 * Representa um NPC Villager no mundo do jogo.
 * Estende {@link LivingEntity} e utiliza composição para anexar um {@link DocumentAttachment}
 * contendo seu registro civil único e imutável.
 */
public class Villager extends LivingEntity {

    private String profissaoAtual;
    private final DocumentAttachment documentAttachment;

    /**
     * Cria um Villager desempregado no bioma informado, emitindo automaticamente
     * seu documento de registro civil por meio de {@link DocumentAttachment}.
     *
     * @param biomaAtual bioma em que o Villager surgiu
     */
    public Villager(String biomaAtual) {
        this(biomaAtual, "Desempregado");
    }

    /**
     * Cria um Villager no bioma e profissão inicial informados, emitindo automaticamente
     * seu documento civil e registrando a profissão inicial no histórico.
     *
     * @param biomaAtual       bioma em que o Villager surgiu
     * @param profissaoInicial profissão inicial exercida pelo Villager
     */
    public Villager(String biomaAtual, String profissaoInicial) {
        super("Villager", 20.0, biomaAtual);
        this.documentAttachment = new DocumentAttachment(biomaAtual);
        setProfissaoAtual(profissaoInicial);
    }

    public String getProfissaoAtual() {
        return profissaoAtual;
    }

    /**
     * Atualiza a profissão atual do Villager e registra automaticamente a mudança
     * no histórico de profissões do documento civil anexado.
     *
     * @param novaProfissao nova profissão assumida pelo Villager
     */
    public void setProfissaoAtual(String novaProfissao) {
        if (novaProfissao == null || novaProfissao.isBlank()) {
            throw new IllegalArgumentException("Profissão não pode ser nula ou vazia.");
        }
        this.profissaoAtual = novaProfissao.trim();
        if (documentAttachment.hasDocumento()) {
            documentAttachment.registrarProfissao(this.profissaoAtual);
        }
    }

    public DocumentAttachment getDocumentAttachment() {
        return documentAttachment;
    }

    /**
     * Salva os dados do registro civil deste Villager em uma tag NBT.
     *
     * @param nbt estrutura NBT de destino
     */
    public void salvarDados(NBTTagCompound nbt) {
        documentAttachment.writeToNBT(nbt);
    }

    /**
     * Carrega os dados do registro civil deste Villager a partir de uma tag NBT.
     *
     * @param nbt estrutura NBT de origem
     */
    public void carregarDados(NBTTagCompound nbt) {
        documentAttachment.readFromNBT(nbt);
        if (documentAttachment.hasDocumento()) {
            var historico = documentAttachment.getDocumento().getHistoricoProfissoes();
            if (!historico.isEmpty()) {
                this.profissaoAtual = historico.get(historico.size() - 1);
            }
        }
    }

    /**
     * Simula a remoção da entidade do mundo, destruindo também o documento civil
     * associado no componente {@link DocumentAttachment} (ciclo de vida da composição).
     */
    public void removerDoMundo() {
        setAtiva(false);
        documentAttachment.destruir();
    }

    @Override
    public String getDescricaoEntidade() {
        String nomeCivil = documentAttachment.hasDocumento()
                ? documentAttachment.getDocumento().getNome()
                : "Sem registro ativo";
        return "Villager '" + nomeCivil + "' [ID=" + getId()
                + ", Bioma=" + getBiomaAtual()
                + ", Profissão=" + profissaoAtual + "]";
    }
}
