package br.unicap.modvillagers.entidades;

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
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("ID da entidade não pode ser nulo ou vazio.");
        }
        this.id = id.trim();
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
        if (tipoEntidade == null || tipoEntidade.isBlank()) {
            throw new IllegalArgumentException("Tipo da entidade não pode ser nulo ou vazio.");
        }
        this.tipoEntidade = tipoEntidade.trim();
    }

    public double getVida() {
        return vida;
    }

    public void setVida(double vida) {
        if (vida < 0) {
            throw new IllegalArgumentException("Vida da entidade não pode ser negativa.");
        }
        this.vida = vida;
    }

    public String getBiomaAtual() {
        return biomaAtual;
    }

    public void setBiomaAtual(String biomaAtual) {
        if (biomaAtual == null || biomaAtual.isBlank()) {
            throw new IllegalArgumentException("Bioma atual não pode ser nulo ou vazio.");
        }
        this.biomaAtual = biomaAtual.trim();
    }

    public boolean isAtiva() {
        return ativa;
    }

    public void setAtiva(boolean ativa) {
        this.ativa = ativa;
    }

    /**
     * Retorna uma descrição textual da entidade (sobrescrita pelas subclasses).
     *
     * @return descrição da entidade
     */
    public String getDescricaoEntidade() {
        return tipoEntidade + " [ID=" + id + ", Bioma=" + biomaAtual + ", Vida=" + vida + "]";
    }

    @Override
    public String toString() {
        return getDescricaoEntidade();
    }
}
