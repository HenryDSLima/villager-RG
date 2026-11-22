package br.unicap.modvillagers.excecoes;

/**
 * Exceção lançada quando há tentativa de registrar um Villager com um CPF
 * ou identificador já cadastrado no Cartório de Registro Civil.
 */
public class VillagerDuplicadoException extends RegistroCivilException {

    public VillagerDuplicadoException(String cpf) {
        super("Já existe um Villager cadastrado no cartório com o CPF '" + cpf + "'.");
    }
}
