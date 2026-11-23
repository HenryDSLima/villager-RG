package br.unicap.modvillagers.modelo;

import br.unicap.modvillagers.excecoes.CpfInvalidoException;
import br.unicap.modvillagers.excecoes.DocumentoInvalidoException;
import br.unicap.modvillagers.padroes.strategy.DocumentoFormatStrategy;
import br.unicap.modvillagers.padroes.strategy.FormatoFichaCompletaStrategy;
import br.unicap.modvillagers.util.ValidacaoUtil;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Classe de modelo que representa o Documento de Registro Civil de um Villager.
 *
 * <p>Aplica encapsulamento rigoroso: todos os atributos são {@code private} e os
 * dados de identidade (nome, CPF, RG, bioma de origem e data de geração) são
 * imutáveis após a criação. O CPF é validado matematicamente pelo Módulo 11 na
 * construção, lançando {@link CpfInvalidoException} caso seja inválido.</p>
 *
 * <p>Na Etapa 5, a responsabilidade de apresentação textual foi delegada ao padrão
 * <b>Strategy</b> ({@link DocumentoFormatStrategy}), mantendo esta classe focada
 * exclusivamente nos dados e regras do documento civil.</p>
 */
public class VillagerDocument {

    private static final DocumentoFormatStrategy ESTRATEGIA_PADRAO = new FormatoFichaCompletaStrategy();

    private final String nome;
    private final String cpf;
    private final String rg;
    private final String biomaOrigem;
    private final LocalDate dataGeracao;
    private final List<String> historicoProfissoes;

    /**
     * Construtor paramétrico principal conforme definido no Diagrama de Classes da Etapa 2.
     *
     * @param nome nome completo do Villager
     * @param cpf  CPF válido do Villager
     * @param rg   RG formatado do Villager
     * @throws CpfInvalidoException       se o CPF informado for inválido
     * @throws DocumentoInvalidoException se nome ou RG forem nulos ou vazios
     */
    public VillagerDocument(String nome, String cpf, String rg) {
        this(nome, cpf, rg, "Planície", LocalDate.now());
    }

    /**
     * Construtor sobrecarregado que permite informar também o bioma de origem e a data de geração.
     *
     * @param nome        nome completo do Villager
     * @param cpf         CPF válido do Villager
     * @param rg          RG formatado do Villager
     * @param biomaOrigem bioma em que o Villager foi gerado
     * @param dataGeracao data de emissão do documento civil
     * @throws CpfInvalidoException       se o CPF informado for inválido
     * @throws DocumentoInvalidoException se algum campo obrigatório for nulo ou vazio
     */
    public VillagerDocument(String nome, String cpf, String rg, String biomaOrigem, LocalDate dataGeracao) {
        this.nome = exigirCampoDocumento(nome, "Nome do Villager");
        if (cpf == null || cpf.isBlank() || !ValidacaoUtil.validarCpf(cpf)) {
            throw new CpfInvalidoException(cpf);
        }
        this.cpf = cpf.trim();
        this.rg = exigirCampoDocumento(rg, "RG do Villager");
        this.biomaOrigem = exigirCampoDocumento(biomaOrigem, "Bioma de origem");
        if (dataGeracao == null) {
            throw new DocumentoInvalidoException("Data de geração do documento não pode ser nula.");
        }
        this.dataGeracao = dataGeracao;
        this.historicoProfissoes = new ArrayList<>();
    }

    private static String exigirCampoDocumento(String valor, String nomeCampo) {
        if (valor == null || valor.isBlank()) {
            throw new DocumentoInvalidoException(nomeCampo + " não pode ser nulo ou vazio.");
        }
        return valor.trim();
    }

    /**
     * Retorna o nome completo registrado no documento civil.
     *
     * @return nome do Villager
     */
    public String getNome() {
        return nome;
    }

    /**
     * Retorna o CPF válido e formatado do Villager.
     *
     * @return CPF do Villager
     */
    public String getCpf() {
        return cpf;
    }

    /**
     * Retorna o RG formatado do Villager.
     *
     * @return RG do Villager
     */
    public String getRg() {
        return rg;
    }

    /**
     * Retorna o bioma de origem em que o Villager foi gerado.
     *
     * @return bioma de origem
     */
    public String getBiomaOrigem() {
        return biomaOrigem;
    }

    /**
     * Retorna a data de emissão do documento civil.
     *
     * @return data de geração do registro
     */
    public LocalDate getDataGeracao() {
        return dataGeracao;
    }

    /**
     * Retorna uma visualização imutável do histórico cronológico de profissões ({@link List}).
     *
     * @return lista não modificável de profissões exercidas
     */
    public List<String> getHistoricoProfissoes() {
        return Collections.unmodifiableList(historicoProfissoes);
    }

    /**
     * Retorna o conjunto imutável ({@link Set}) de profissões distintas já exercidas
     * pelo Villager, preservando a ordem da primeira ocorrência.
     *
     * @return conjunto não modificável de profissões únicas
     */
    public Set<String> getProfissoesUnicas() {
        return Collections.unmodifiableSet(new LinkedHashSet<>(historicoProfissoes));
    }

    /**
     * Adiciona uma nova profissão ao histórico de empregos do Villager.
     * Ignora valores nulos, em branco ou repetições imediatamente consecutivas.
     *
     * @param profissao nome da profissão exercida
     */
    public void adicionarProfissao(String profissao) {
        if (profissao == null || profissao.isBlank()) {
            return;
        }
        String profissaoNormalizada = profissao.trim();
        if (historicoProfissoes.isEmpty()
                || !historicoProfissoes.get(historicoProfissoes.size() - 1).equalsIgnoreCase(profissaoNormalizada)) {
            this.historicoProfissoes.add(profissaoNormalizada);
        }
    }

    /**
     * Formata os dados do documento civil utilizando uma estratégia {@link DocumentoFormatStrategy} específica.
     *
     * @param estrategia     estratégia de formatação desejada
     * @param profissaoAtual profissão atual do Villager (opcional)
     * @return representação textual produzida pela estratégia
     */
    public String formatar(DocumentoFormatStrategy estrategia, String profissaoAtual) {
        DocumentoFormatStrategy strategy = ValidacaoUtil.exigirNaoNulo(estrategia, "DocumentoFormatStrategy");
        return strategy.formatar(this, profissaoAtual);
    }

    /**
     * Formata os dados do documento civil para exibição no chat usando a estratégia padrão (Ficha Completa).
     *
     * @return representação formatada do documento
     */
    public String formatarParaChat() {
        return formatarParaChat(null);
    }

    /**
     * Formata os dados do documento civil incluindo, opcionalmente, a profissão atual,
     * utilizando a estratégia padrão (Ficha Completa).
     *
     * @param profissaoAtual profissão atual do Villager (opcional)
     * @return representação formatada do documento
     */
    public String formatarParaChat(String profissaoAtual) {
        return ESTRATEGIA_PADRAO.formatar(this, profissaoAtual);
    }

    /**
     * Retorna a representação textual técnica deste documento civil.
     *
     * @return representação em string dos campos do documento
     */
    @Override
    public String toString() {
        return "VillagerDocument{"
                + "nome='" + nome + '\''
                + ", cpf='" + cpf + '\''
                + ", rg='" + rg + '\''
                + ", biomaOrigem='" + biomaOrigem + '\''
                + ", dataGeracao=" + dataGeracao
                + ", historicoProfissoes=" + historicoProfissoes
                + '}';
    }

    /**
     * Compara este documento civil com outro objeto com base em todos os seus campos.
     *
     * @param o objeto a ser comparado
     * @return {@code true} se os documentos possuírem exatamente os mesmos dados
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof VillagerDocument that)) {
            return false;
        }
        return Objects.equals(nome, that.nome)
                && Objects.equals(cpf, that.cpf)
                && Objects.equals(rg, that.rg)
                && Objects.equals(biomaOrigem, that.biomaOrigem)
                && Objects.equals(dataGeracao, that.dataGeracao)
                && Objects.equals(historicoProfissoes, that.historicoProfissoes);
    }

    /**
     * Calcula o código hash consistente com {@link #equals(Object)}.
     *
     * @return código hash do documento
     */
    @Override
    public int hashCode() {
        return Objects.hash(nome, cpf, rg, biomaOrigem, dataGeracao, historicoProfissoes);
    }
}
