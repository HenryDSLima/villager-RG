package br.unicap.modvillagers.excecoes;

/**
 * Exceção lançada quando uma busca ou operação no Cartório de Registro Civil
 * não localiza nenhum Villager para o critério informado.
 */
public class VillagerNaoEncontradoException extends RegistroCivilException {

    private static final long serialVersionUID = 1L;

    /**
     * Cria a exceção informando o CPF pesquisado que não foi encontrado.
     *
     * @param cpf CPF consultado no cartório
     */
    public VillagerNaoEncontradoException(String cpf) {
        super("Nenhum Villager encontrado no cartório para o CPF '" + cpf + "'.");
    }
}
