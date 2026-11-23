package br.unicap.modvillagers.padroes.strategy;

import br.unicap.modvillagers.modelo.VillagerDocument;
import br.unicap.modvillagers.util.ValidacaoUtil;

/**
 * Estratégia concreta ({@link DocumentoFormatStrategy}) que apresenta os dados
 * essenciais do Villager em uma única linha compacta, reduzindo poluição visual no chat.
 */
public class FormatoCompactoStrategy implements DocumentoFormatStrategy {

    /**
     * Cria uma instância da estratégia de formatação compacta.
     */
    public FormatoCompactoStrategy() {
    }

    /**
     * Formata o documento em uma linha resumida contendo nome, CPF, RG, bioma e profissão.
     *
     * @param documento      documento civil a ser formatado
     * @param profissaoAtual profissão atual do Villager (opcional)
     * @return linha resumida do registro civil
     */
    @Override
    public String formatar(VillagerDocument documento, String profissaoAtual) {
        ValidacaoUtil.exigirNaoNulo(documento, "VillagerDocument");

        StringBuilder sb = new StringBuilder()
                .append("[RG] ")
                .append(documento.getNome())
                .append(" | CPF: ").append(documento.getCpf())
                .append(" | RG: ").append(documento.getRg())
                .append(" | Bioma: ").append(documento.getBiomaOrigem());

        if (profissaoAtual != null && !profissaoAtual.isBlank()) {
            sb.append(" | Profissão: ").append(profissaoAtual.trim());
        }
        return sb.toString();
    }

    /**
     * Retorna o nome descritivo desta estratégia.
     *
     * @return {@code "Compacto"}
     */
    @Override
    public String getNomeFormato() {
        return "Compacto";
    }
}
