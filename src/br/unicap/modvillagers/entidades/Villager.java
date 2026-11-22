package br.unicap.modvillagers.entidades;

import br.unicap.modvillagers.excecoes.PersistenciaNBTException;
import br.unicap.modvillagers.modelo.DocumentAttachment;
import br.unicap.modvillagers.persistencia.NBTTagCompound;
import br.unicap.modvillagers.util.ValidacaoUtil;

import java.util.List;

/**
 * Representa um NPC Villager no mundo do jogo.
 * Estende {@link LivingEntity} e utiliza composição para anexar um {@link DocumentAttachment}
 * contendo seu registro civil único e imutável.
 */
public class Villager extends LivingEntity {

    public static final String TAG_ENTITY_ID = "entity_id";
    public static final String TAG_PROFISSAO_ATUAL = "villager_profissao_atual";

    private String profissaoAtual;
    private final DocumentAttachment documentAttachment;

    public Villager(String biomaAtual) {
        this(biomaAtual, "Desempregado");
    }

    public Villager(String biomaAtual, String profissaoInicial) {
        super("Villager", 20.0, biomaAtual);
        this.documentAttachment = new DocumentAttachment(biomaAtual);
        setProfissaoAtual(profissaoInicial);
    }

    /**
     * Construtor utilizado na restauração de um Villager a partir de dados persistidos
     * ou com um {@link DocumentAttachment} previamente construído.
     *
     * @param id                 identificador único da entidade
     * @param biomaAtual         bioma atual do Villager
     * @param profissaoAtual     profissão atual do Villager
     * @param documentAttachment componente de documento civil associado
     */
    public Villager(String id, String biomaAtual, String profissaoAtual, DocumentAttachment documentAttachment) {
        super(id, "Villager", 20.0, biomaAtual);
        this.documentAttachment = ValidacaoUtil.exigirNaoNulo(documentAttachment, "DocumentAttachment");
        this.profissaoAtual = ValidacaoUtil.exigirTextoValido(profissaoAtual, "Profissão atual");
        if (this.documentAttachment.hasDocumento()
                && this.documentAttachment.getDocumento().getHistoricoProfissoes().isEmpty()) {
            this.documentAttachment.registrarProfissao(this.profissaoAtual);
        }
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
        this.profissaoAtual = ValidacaoUtil.exigirTextoValido(novaProfissao, "Profissão");
        if (documentAttachment.hasDocumento()) {
            documentAttachment.registrarProfissao(this.profissaoAtual);
        }
    }

    public DocumentAttachment getDocumentAttachment() {
        return documentAttachment;
    }

    /**
     * Salva os dados da entidade e do registro civil deste Villager em uma tag NBT.
     *
     * @param nbt estrutura NBT de destino
     * @throws PersistenciaNBTException se ocorrer erro na serialização NBT
     */
    public void salvarDados(NBTTagCompound nbt) throws PersistenciaNBTException {
        if (nbt == null) {
            throw new PersistenciaNBTException("NBTTagCompound de destino não pode ser nulo.");
        }
        nbt.setString(TAG_ENTITY_ID, getId());
        nbt.setString(TAG_PROFISSAO_ATUAL, profissaoAtual);
        documentAttachment.writeToNBT(nbt);
    }

    /**
     * Carrega os dados do registro civil deste Villager a partir de uma tag NBT.
     *
     * @param nbt estrutura NBT de origem
     * @throws PersistenciaNBTException se o NBT estiver incompleto ou inválido
     */
    public void carregarDados(NBTTagCompound nbt) throws PersistenciaNBTException {
        documentAttachment.readFromNBT(nbt);
        if (nbt.hasKey(TAG_PROFISSAO_ATUAL)) {
            this.profissaoAtual = nbt.getString(TAG_PROFISSAO_ATUAL);
        } else if (documentAttachment.hasDocumento()) {
            List<String> historico = documentAttachment.getDocumento().getHistoricoProfissoes();
            if (!historico.isEmpty()) {
                this.profissaoAtual = historico.get(historico.size() - 1);
            }
        }
        if (documentAttachment.hasDocumento()) {
            setBiomaAtual(documentAttachment.getDocumento().getBiomaOrigem());
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
