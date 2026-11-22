package br.unicap.modvillagers.entidades;

import br.unicap.modvillagers.util.ValidacaoUtil;

import java.util.UUID;

/**
 * Classe base que representa uma entidade viva no mundo do jogo
 * (equivalente à classe nativa {@code LivingEntity} do Minecraft).
 */
public class LivingEntity {

    private final String id;
    private String tipoEntidade;
    private double vida;
    private String biomaAtual;
    private boolean ativa;

    public LivingEntity(String tipoEntidade, double vida, String biomaAtual) {
        this(UUID.randomUUID().toString().substring(0, 8), tipoEntidade, vida, biomaAtual);
    }

    public LivingEntity(String id, String tipoEntidade, double vida, String biomaAtual) {
        this.id = ValidacaoUtil.exigirTextoValido(id, "ID da entidade");
        setTipoEntidade(tipoEntidade);
        setVida(vida);
        setBiomaAtual(biomaAtual);
        this.ativa = true;
    }

    public String getId() {
        return id;
    }

    public String getTipoEntidade() {
        return tipoEntidade;
    }

    public void setTipoEntidade(String tipoEntidade) {
        this.tipoEntidade = ValidacaoUtil.exigirTextoValido(tipoEntidade, "Tipo da entidade");
    }

    public double getVida() {
        return vida;
    }

    public void setVida(double vida) {
        this.vida = ValidacaoUtil.exigirNaoNegativo(vida, "Vida da entidade");
    }

    public String getBiomaAtual() {
        return biomaAtual;
    }

    public void setBiomaAtual(String biomaAtual) {
        this.biomaAtual = ValidacaoUtil.exigirTextoValido(biomaAtual, "Bioma atual");
    }

    public boolean isAtiva() {
        return ativa;
    }

    public void setAtiva(boolean ativa) {
        this.ativa = ativa;
    }

    public String getDescricaoEntidade() {
        return tipoEntidade + " [ID=" + id + ", Bioma=" + biomaAtual + ", Vida=" + vida + "]";
    }

    @Override
    public String toString() {
        return getDescricaoEntidade();
    }
}
