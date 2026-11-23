package br.unicap.modvillagers.padroes.strategy;

import br.unicap.modvillagers.modelo.VillagerDocument;
import br.unicap.modvillagers.util.ValidacaoUtil;

import java.util.List;

/**
 * Estratégia concreta ({@link DocumentoFormatStrategy}) que serializa o documento
 * civil em formato JSON estruturado, útil para depuração, logs e integração externa.
 */
public class FormatoJsonStrategy implements DocumentoFormatStrategy {

    /**
     * Cria uma instância da estratégia de formatação em JSON.
     */
    public FormatoJsonStrategy() {
    }

    /**
     * Formata o documento como um objeto JSON válido contendo todos os seus campos.
     *
     * @param documento      documento civil a ser formatado
     * @param profissaoAtual profissão atual do Villager (opcional)
     * @return string JSON representando o registro civil
     */
    @Override
    public String formatar(VillagerDocument documento, String profissaoAtual) {
        ValidacaoUtil.exigirNaoNulo(documento, "VillagerDocument");

        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append("  \"nome\": \"").append(escaparJson(documento.getNome())).append("\",\n");
        sb.append("  \"cpf\": \"").append(escaparJson(documento.getCpf())).append("\",\n");
        sb.append("  \"rg\": \"").append(escaparJson(documento.getRg())).append("\",\n");
        sb.append("  \"biomaOrigem\": \"").append(escaparJson(documento.getBiomaOrigem())).append("\",\n");
        sb.append("  \"dataEmissao\": \"").append(documento.getDataGeracao()).append("\",\n");
        if (profissaoAtual != null && !profissaoAtual.isBlank()) {
            sb.append("  \"profissaoAtual\": \"").append(escaparJson(profissaoAtual.trim())).append("\",\n");
        }
        sb.append("  \"historicoProfissoes\": [");
        List<String> historico = documento.getHistoricoProfissoes();
        for (int i = 0; i < historico.size(); i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append("\"").append(escaparJson(historico.get(i))).append("\"");
        }
        sb.append("]\n");
        sb.append("}");
        return sb.toString();
    }

    /**
     * Retorna o nome descritivo desta estratégia.
     *
     * @return {@code "JSON"}
     */
    @Override
    public String getNomeFormato() {
        return "JSON";
    }

    private static String escaparJson(String texto) {
        return texto
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "");
    }
}
