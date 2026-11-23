package br.unicap.modvillagers.excecoes;

/**
 * Exceção lançada quando uma busca ou operação no Cartório de Registro Civil
 * não localiza nenhum Villager para o critério informado.
 */
public class VillagerNaoEncontradoException extends RegistroCivilException {

    private static final long serialVersionUID = 1L;

    public VillagerNaoEncontradoException(String cpf) {
        super("Nenhum Villager encontrado no cartório para o CPF '" + cpf + "'.");
    }
}
