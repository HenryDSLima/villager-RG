package br.unicap.modvillagers.modelo;

import br.unicap.modvillagers.excecoes.DocumentoInvalidoException;
import br.unicap.modvillagers.excecoes.PersistenciaNBTException;
import br.unicap.modvillagers.excecoes.RegistroCivilException;
import br.unicap.modvillagers.persistencia.NBTTagCompound;
import br.unicap.modvillagers.servico.DocumentGenerator;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * Componente anexado a cada entidade {@code Villager} responsável por manter
 * e persistir o seu {@link VillagerDocument}.
 *
 * <p>Relacionamentos conforme o Diagrama de Classes da Etapa 2:</p>
 * <ul>
 *   <li><b>Composição (1:1) com {@link VillagerDocument}:</b> {@code DocumentAttachment}
 *       atua como o todo e gerencia o ciclo de vida do documento civil associado.</li>
 *   <li><b>Dependência de {@link DocumentGenerator}:</b> utiliza temporariamente os
 *       serviços do gerador na emissão do documento sem manter referência de instância.</li>
 * </ul>
 */
public class DocumentAttachment {

    public static final String TAG_NOME = "villager_nome";
    public static final String TAG_CPF = "villager_cpf";
    public static final String TAG_RG = "villager_rg";
    public static final String TAG_BIOMA = "villager_bioma";
    public static final String TAG_DATA_GERACAO = "villager_data_geracao";
    public static final String TAG_HISTORICO_PROFISSOES = "villager_historico_profissoes";

    private VillagerDocument documento;

    public DocumentAttachment() {
        this("Planície");
    }

    public DocumentAttachment(String biomaOrigem) {
        DocumentGenerator gerador = new DocumentGenerator();
        String nome = gerador.gerarNome(biomaOrigem);
        String cpf = gerador.gerarCpfValido();
        String rg = gerador.gerarRg();
        this.documento = new VillagerDocument(nome, cpf, rg, biomaOrigem, LocalDate.now());
    }

    public DocumentAttachment(VillagerDocument documento) {
        if (documento == null) {
            throw new DocumentoInvalidoException("O documento anexado ao DocumentAttachment não pode ser nulo.");
        }
        this.documento = documento;
    }

    public VillagerDocument getDocumento() {
        return documento;
    }

    public boolean hasDocumento() {
        return documento != null;
    }

    /**
     * Registra uma nova profissão no histórico do documento civil anexado.
     *
     * @param profissao profissão adquirida pelo Villager
     */
    public void registrarProfissao(String profissao) {
        if (documento == null) {
            throw new DocumentoInvalidoException("Não há documento civil ativo neste componente para registrar profissão.");
        }
        documento.adicionarProfissao(profissao);
    }

    /**
     * Serializa os dados do {@link VillagerDocument} para uma estrutura {@link NBTTagCompound}.
     *
     * @param nbt composto NBT de destino
     * @throws PersistenciaNBTException se o composto NBT for nulo ou não houver documento ativo
     */
    public void writeToNBT(NBTTagCompound nbt) throws PersistenciaNBTException {
        if (nbt == null) {
            throw new PersistenciaNBTException("Não é possível serializar para um NBTTagCompound nulo.");
        }
        if (documento == null) {
            throw new PersistenciaNBTException("Não há documento civil ativo no componente para serialização em NBT.");
        }

        nbt.setString(TAG_NOME, documento.getNome());
        nbt.setString(TAG_CPF, documento.getCpf());
        nbt.setString(TAG_RG, documento.getRg());
        nbt.setString(TAG_BIOMA, documento.getBiomaOrigem());
        nbt.setString(TAG_DATA_GERACAO, documento.getDataGeracao().toString());
        nbt.setStringList(TAG_HISTORICO_PROFISSOES, documento.getHistoricoProfissoes());
    }

    /**
     * Reconstrói o {@link VillagerDocument} a partir dos dados armazenados em um {@link NBTTagCompound}.
     *
     * @param nbt composto NBT de origem
     * @throws PersistenciaNBTException se o composto NBT estiver incompleto, corrompido ou contiver dados inválidos
     */
    public void readFromNBT(NBTTagCompound nbt) throws PersistenciaNBTException {
        if (nbt == null) {
            throw new PersistenciaNBTException("Não é possível ler dados de um NBTTagCompound nulo.");
        }
        if (!nbt.hasKey(TAG_NOME) || !nbt.hasKey(TAG_CPF) || !nbt.hasKey(TAG_RG)) {
            throw new PersistenciaNBTException(
                "Dados NBT incompletos: as tags obrigatórias '" + TAG_NOME + "', '"
                + TAG_CPF + "' e '" + TAG_RG + "' devem estar presentes."
            );
        }

        String nome = nbt.getString(TAG_NOME);
        String cpf = nbt.getString(TAG_CPF);
        String rg = nbt.getString(TAG_RG);
        String bioma = nbt.hasKey(TAG_BIOMA) ? nbt.getString(TAG_BIOMA) : "Planície";

        LocalDate dataGeracao = LocalDate.now();
        if (nbt.hasKey(TAG_DATA_GERACAO)) {
            try {
                dataGeracao = LocalDate.parse(nbt.getString(TAG_DATA_GERACAO));
            } catch (DateTimeParseException e) {
                throw new PersistenciaNBTException(
                    "Formato inválido para a data de geração no NBT: '" + nbt.getString(TAG_DATA_GERACAO) + "'.", e
                );
            }
        }

        try {
            VillagerDocument restaurado = new VillagerDocument(nome, cpf, rg, bioma, dataGeracao);
            List<String> profissoes = nbt.getStringList(TAG_HISTORICO_PROFISSOES);
            for (String profissao : profissoes) {
                restaurado.adicionarProfissao(profissao);
            }
            this.documento = restaurado;
        } catch (RegistroCivilException e) {
            throw new PersistenciaNBTException("Falha ao reconstruir VillagerDocument a partir do NBT: " + e.getMessage(), e);
        }
    }

    /**
     * Libera a referência ao documento civil quando o Villager associado é removido do mundo,
     * refletindo a relação forte de composição (1:1).
     */
    public void destruir() {
        this.documento = null;
    }

    @Override
    public String toString() {
        return "DocumentAttachment{documento=" + documento + '}';
    }
}
