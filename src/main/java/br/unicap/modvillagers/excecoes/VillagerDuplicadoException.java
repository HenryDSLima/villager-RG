package br.unicap.modvillagers.excecoes;

/**
 * Exceção lançada quando há tentativa de registrar um Villager com um CPF
 * ou identificador já cadastrado no Cartório de Registro Civil.
 */
public class VillagerDuplicadoException extends RegistroCivilException {

    private static final long serialVersionUID = 1L;

    /**
     * Cria a exceção informando o CPF que já possui registro no cartório.
     *
     * @param cpf CPF duplicado
     */
    public VillagerDuplicadoException(String cpf) {
        super("Já existe um Villager cadastrado no cartório com o CPF '" + cpf + "'.");
    }
}
