package br.unicap.modvillagers.modelo;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Classe de modelo que representa o Documento de Registro Civil de um Villager.
 *
 * <p>Aplica encapsulamento rigoroso: todos os atributos são {@code private} e os
 * dados de identidade (nome, CPF, RG, bioma de origem e data de geração) são
 * imutáveis após a criação, expondo apenas métodos getters para leitura. O histórico
 * de profissões é protegido contra modificações externas diretas, sendo alterado
 * exclusivamente por meio do método {@link #adicionarProfissao(String)}.</p>
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
     * @param cpf  CPF formatado do Villager
     * @param rg   RG formatado do Villager
     */
    public VillagerDocument(String nome, String cpf, String rg) {
        this(nome, cpf, rg, "Planície", LocalDate.now());
    }

    /**
     * Construtor sobrecarregado que permite informar também o bioma de origem e a data de geração.
     * Utilizado na criação contextualizada por bioma e na restauração a partir de dados NBT.
     *
     * @param nome        nome completo do Villager
     * @param cpf         CPF formatado do Villager
     * @param rg          RG formatado do Villager
     * @param biomaOrigem bioma em que o Villager foi gerado
     * @param dataGeracao data de emissão do documento civil
     */
    public VillagerDocument(String nome, String cpf, String rg, String biomaOrigem, LocalDate dataGeracao) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("Nome não pode ser nulo ou vazio.");
        }
        if (cpf == null || cpf.isBlank()) {
            throw new IllegalArgumentException("CPF não pode ser nulo ou vazio.");
        }
        if (rg == null || rg.isBlank()) {
            throw new IllegalArgumentException("RG não pode ser nulo ou vazio.");
        }
        if (biomaOrigem == null || biomaOrigem.isBlank()) {
            throw new IllegalArgumentException("Bioma de origem não pode ser nulo ou vazio.");
        }
        if (dataGeracao == null) {
            throw new IllegalArgumentException("Data de geração não pode ser nula.");
        }

        this.nome = nome.trim();
        this.cpf = cpf.trim();
        this.rg = rg.trim();
        this.biomaOrigem = biomaOrigem.trim();
        this.dataGeracao = dataGeracao;
        this.historicoProfissoes = new ArrayList<>();
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
     * Retorna uma visualização imutável do histórico de profissões,
     * preservando o encapsulamento da lista interna.
     *
     * @return lista não modificável de profissões exercidas
     */
    public List<String> getHistoricoProfissoes() {
        return Collections.unmodifiableList(historicoProfissoes);
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
        String historicoTexto = historicoProfissoes.isEmpty()
                ? "Nenhum registro"
                : String.join(" -> ", historicoProfissoes);

        return new StringBuilder()
                .append("===========================================\n")
                .append("       REGISTRO CIVIL DE VILLAGER\n")
                .append("===========================================\n")
                .append(" Nome: ").append(nome).append("\n")
                .append(" CPF: ").append(cpf).append("\n")
                .append(" RG: ").append(rg).append("\n")
                .append(" Bioma de Origem: ").append(biomaOrigem).append("\n")
                .append(" Data de Emissão: ").append(dataGeracao.format(FORMATO_DATA)).append("\n")
                .append(" Histórico de Profissões: ").append(historicoTexto).append("\n")
                .append("===========================================")
                .toString();
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
