package br.unicap.modvillagers.padroes.strategy;

import br.unicap.modvillagers.modelo.VillagerDocument;

/**
 * Interface do padrão de projeto comportamental <b>Strategy (GoF)</b> para definir
 * algoritmos intercambiáveis de formatação e apresentação de um {@link VillagerDocument}.
 *
 * <p><b>Problema resolvido:</b> anteriormente, a apresentação textual do documento civil
 * ficava rigidamente acoplada ao modelo {@link VillagerDocument} e ao {@code InspectorItem},
 * exigindo modificações em classes existentes sempre que um novo formato fosse necessário.</p>
 *
 * <p><b>Benefício:</b> atende ao Princípio Aberto/Fechado (OCP) e permite alternar o
 * formato de exibição em tempo de execução (ex.: Ficha Completa, Compacto ou JSON).</p>
 */
public interface DocumentoFormatStrategy {

    /**
     * Formata os dados de um {@link VillagerDocument} conforme a estratégia concreta.
     *
     * @param documento      documento civil a ser formatado
     * @param profissaoAtual profissão atual do Villager (opcional, pode ser {@code null})
     * @return representação textual formatada do documento
     */
    String formatar(VillagerDocument documento, String profissaoAtual);

    /**
     * Retorna o nome descritivo da estratégia de formatação.
     *
     * @return nome legível do formato
     */
    String getNomeFormato();
}
