package br.unicap.modvillagers.padroes.builder;

import br.unicap.modvillagers.excecoes.CpfInvalidoException;
import br.unicap.modvillagers.excecoes.DocumentoInvalidoException;
import br.unicap.modvillagers.modelo.VillagerDocument;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementação do padrão de projeto criacional <b>Builder (GoF)</b> para a
 * construção passo a passo e fluente de instâncias imutáveis de {@link VillagerDocument}.
 *
 * <p><b>Problema resolvido:</b> {@link VillagerDocument} possui atributos obrigatórios
 * (nome, CPF e RG) e opcionais (bioma de origem, data de emissão e histórico inicial
 * de profissões). O uso de construtores telescópicos dificultava a legibilidade na
 * reconstrução via NBT e na emissão pelo cartório.</p>
 *
 * <p><b>Benefício:</b> separa o processo de montagem da representação final imutável,
 * mantendo a integridade das validações centralizadas no momento da chamada a {@link #build()}.</p>
 */
public class VillagerDocumentBuilder {

    private String nome;
    private String cpf;
    private String rg;
    private String biomaOrigem = "Planície";
    private LocalDate dataGeracao = LocalDate.now();
    private final List<String> historicoProfissoes = new ArrayList<>();

    /**
     * Cria um novo builder com valores padrão para bioma ({@code "Planície"})
     * e data de emissão ({@link LocalDate#now()}).
     */
    public VillagerDocumentBuilder() {
    }

    /**
     * Define o nome completo do Villager.
     *
     * @param nome nome completo do cidadão
     * @return esta própria instância do builder para encadeamento fluente
     */
    public VillagerDocumentBuilder comNome(String nome) {
        this.nome = nome;
        return this;
    }

    /**
     * Define o CPF do Villager (validado matematicamente em {@link #build()}).
     *
     * @param cpf CPF formatado ou numérico válido
     * @return esta própria instância do builder para encadeamento fluente
     */
    public VillagerDocumentBuilder comCpf(String cpf) {
        this.cpf = cpf;
        return this;
    }

    /**
     * Define o RG do Villager.
     *
     * @param rg número de RG formatado
     * @return esta própria instância do builder para encadeamento fluente
     */
    public VillagerDocumentBuilder comRg(String rg) {
        this.rg = rg;
        return this;
    }

    /**
     * Define o bioma de origem do Villager.
     *
     * @param biomaOrigem bioma em que o Villager foi gerado
     * @return esta própria instância do builder para encadeamento fluente
     */
    public VillagerDocumentBuilder comBiomaOrigem(String biomaOrigem) {
        this.biomaOrigem = biomaOrigem;
        return this;
    }

    /**
     * Define a data de emissão do documento civil.
     *
     * @param dataGeracao data de emissão do registro
     * @return esta própria instância do builder para encadeamento fluente
     */
    public VillagerDocumentBuilder comDataGeracao(LocalDate dataGeracao) {
        this.dataGeracao = dataGeracao;
        return this;
    }

    /**
     * Adiciona uma profissão ao histórico inicial a ser registrado no documento.
     *
     * @param profissao nome da profissão exercida
     * @return esta própria instância do builder para encadeamento fluente
     */
    public VillagerDocumentBuilder comProfissao(String profissao) {
        if (profissao != null && !profissao.isBlank()) {
            this.historicoProfissoes.add(profissao.trim());
        }
        return this;
    }

    /**
     * Adiciona uma lista de profissões ao histórico inicial do documento.
     *
     * @param profissoes lista de profissões a incluir
     * @return esta própria instância do builder para encadeamento fluente
     */
    public VillagerDocumentBuilder comHistoricoProfissoes(List<String> profissoes) {
        if (profissoes != null) {
            for (String profissao : profissoes) {
                comProfissao(profissao);
            }
        }
        return this;
    }

    /**
     * Constrói e valida a instância final de {@link VillagerDocument}.
     *
     * @return documento civil válido e devidamente populado
     * @throws CpfInvalidoException       se o CPF informado for inválido
     * @throws DocumentoInvalidoException se algum campo obrigatório estiver ausente ou inválido
     */
    public VillagerDocument build() {
        VillagerDocument documento = new VillagerDocument(nome, cpf, rg, biomaOrigem, dataGeracao);
        for (String profissao : historicoProfissoes) {
            documento.adicionarProfissao(profissao);
        }
        return documento;
    }
}
