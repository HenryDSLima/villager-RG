package br.unicap.modvillagers.modelo;

import br.unicap.modvillagers.excecoes.CpfInvalidoException;
import br.unicap.modvillagers.excecoes.DocumentoInvalidoException;
import br.unicap.modvillagers.util.ValidacaoUtil;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
 */
public class VillagerDocument {

    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

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

    public String getNome() {
        return nome;
    }

    public String getCpf() {
        return cpf;
    }

    public String getRg() {
        return rg;
    }

    public String getBiomaOrigem() {
        return biomaOrigem;
    }

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
     * Formata os dados do documento civil para exibição amigável no chat do jogador.
     *
     * @return representação formatada do documento
     */
    public String formatarParaChat() {
        return formatarParaChat(null);
    }

    /**
     * Formata os dados do documento civil incluindo, opcionalmente, a profissão atual.
     *
     * @param profissaoAtual profissão atual do Villager (opcional)
     * @return representação formatada do documento
     */
    public String formatarParaChat(String profissaoAtual) {
        String historicoTexto = historicoProfissoes.isEmpty()
                ? "Nenhum registro"
                : String.join(" -> ", historicoProfissoes);

        StringBuilder sb = new StringBuilder()
                .append("===========================================\n")
                .append("       REGISTRO CIVIL DE VILLAGER\n")
                .append("===========================================\n")
                .append(" Nome: ").append(nome).append("\n")
                .append(" CPF: ").append(cpf).append("\n")
                .append(" RG: ").append(rg).append("\n")
                .append(" Bioma de Origem: ").append(biomaOrigem).append("\n")
                .append(" Data de Emissão: ").append(dataGeracao.format(FORMATO_DATA)).append("\n");

        if (profissaoAtual != null && !profissaoAtual.isBlank()) {
            sb.append(" Profissão Atual: ").append(profissaoAtual.trim()).append("\n");
        }

        sb.append(" Histórico de Profissões: ").append(historicoTexto).append("\n")
                .append("===========================================");
        return sb.toString();
    }

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

    @Override
    public int hashCode() {
        return Objects.hash(nome, cpf, rg, biomaOrigem, dataGeracao, historicoProfissoes);
    }
}
