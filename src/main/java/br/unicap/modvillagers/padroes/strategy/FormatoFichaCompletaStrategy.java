package br.unicap.modvillagers.padroes.strategy;

import br.unicap.modvillagers.modelo.VillagerDocument;
import br.unicap.modvillagers.util.ValidacaoUtil;

import java.time.format.DateTimeFormatter;

/**
 * Estratégia concreta ({@link DocumentoFormatStrategy}) que apresenta o documento
 * civil em formato de ficha completa emoldurada, ideal para visualização detalhada
 * no chat do jogador e em relatórios de censo.
 */
public class FormatoFichaCompletaStrategy implements DocumentoFormatStrategy {

    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    /**
     * Cria uma instância da estratégia de formatação em ficha completa.
     */
    public FormatoFichaCompletaStrategy() {
    }

    /**
     * Formata o documento como uma ficha civil completa com bordas e histórico detalhado.
     *
     * @param documento      documento civil a ser formatado
     * @param profissaoAtual profissão atual do Villager (opcional)
     * @return texto formatado em múltiplas linhas
     */
    @Override
    public String formatar(VillagerDocument documento, String profissaoAtual) {
        ValidacaoUtil.exigirNaoNulo(documento, "VillagerDocument");

        String historicoTexto = documento.getHistoricoProfissoes().isEmpty()
                ? "Nenhum registro"
                : String.join(" -> ", documento.getHistoricoProfissoes());

        StringBuilder sb = new StringBuilder()
                .append("===========================================\n")
                .append("       REGISTRO CIVIL DE VILLAGER\n")
                .append("===========================================\n")
                .append(" Nome: ").append(documento.getNome()).append("\n")
                .append(" CPF: ").append(documento.getCpf()).append("\n")
                .append(" RG: ").append(documento.getRg()).append("\n")
                .append(" Bioma de Origem: ").append(documento.getBiomaOrigem()).append("\n")
                .append(" Data de Emissão: ").append(documento.getDataGeracao().format(FORMATO_DATA)).append("\n");

        if (profissaoAtual != null && !profissaoAtual.isBlank()) {
            sb.append(" Profissão Atual: ").append(profissaoAtual.trim()).append("\n");
        }

        sb.append(" Histórico de Profissões: ").append(historicoTexto).append("\n")
                .append("===========================================");
        return sb.toString();
    }

    /**
     * Retorna o nome descritivo desta estratégia.
     *
     * @return {@code "Ficha Completa"}
     */
    @Override
    public String getNomeFormato() {
        return "Ficha Completa";
    }
}
