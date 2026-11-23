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

    /**
     * Cria uma nova entidade viva gerando automaticamente um identificador curto.
     *
     * @param tipoEntidade nome do tipo da entidade (ex.: Ovelha, Jogador, Villager)
     * @param vida         pontos de vida iniciais (não negativo)
     * @param biomaAtual   bioma em que a entidade se encontra
     */
    public LivingEntity(String tipoEntidade, double vida, String biomaAtual) {
        this(UUID.randomUUID().toString().substring(0, 8), tipoEntidade, vida, biomaAtual);
    }

    /**
     * Cria uma nova entidade viva com identificador explícito.
     *
     * @param id           identificador único da entidade
     * @param tipoEntidade nome do tipo da entidade
     * @param vida         pontos de vida iniciais (não negativo)
     * @param biomaAtual   bioma em que a entidade se encontra
     */
    public LivingEntity(String id, String tipoEntidade, double vida, String biomaAtual) {
        this.id = ValidacaoUtil.exigirTextoValido(id, "ID da entidade");
        setTipoEntidade(tipoEntidade);
        setVida(vida);
        setBiomaAtual(biomaAtual);
        this.ativa = true;
    }

    /**
     * Retorna o identificador único imutável desta entidade.
     *
     * @return identificador da entidade
     */
    public String getId() {
        return id;
    }

    /**
     * Retorna o tipo da entidade (ex.: {@code "Villager"}, {@code "Jogador"}, {@code "Ovelha"}).
     *
     * @return tipo da entidade
     */
    public String getTipoEntidade() {
        return tipoEntidade;
    }

    /**
     * Define o tipo descritivo da entidade.
     *
     * @param tipoEntidade novo tipo da entidade
     */
    public void setTipoEntidade(String tipoEntidade) {
        this.tipoEntidade = ValidacaoUtil.exigirTextoValido(tipoEntidade, "Tipo da entidade");
    }

    /**
     * Retorna os pontos de vida atuais da entidade.
     *
     * @return quantidade de vida
     */
    public double getVida() {
        return vida;
    }

    /**
     * Atualiza os pontos de vida da entidade (valor não negativo).
     *
     * @param vida novos pontos de vida
     */
    public void setVida(double vida) {
        this.vida = ValidacaoUtil.exigirNaoNegativo(vida, "Vida da entidade");
    }

    /**
     * Retorna o bioma atual onde a entidade está localizada.
     *
     * @return nome do bioma atual
     */
    public String getBiomaAtual() {
        return biomaAtual;
    }

    /**
     * Atualiza o bioma atual onde a entidade está localizada.
     *
     * @param biomaAtual novo bioma
     */
    public void setBiomaAtual(String biomaAtual) {
        this.biomaAtual = ValidacaoUtil.exigirTextoValido(biomaAtual, "Bioma atual");
    }

    /**
     * Indica se a entidade continua ativa no mundo do jogo.
     *
     * @return {@code true} se estiver ativa; {@code false} se removida
     */
    public boolean isAtiva() {
        return ativa;
    }

    /**
     * Define o estado de atividade da entidade no mundo.
     *
     * @param ativa {@code true} para ativa, {@code false} para inativa
     */
    public void setAtiva(boolean ativa) {
        this.ativa = ativa;
    }

    /**
     * Retorna uma descrição textual padronizada da entidade (sobrescrita pelas subclasses).
     *
     * @return descrição resumida da entidade
     */
    public String getDescricaoEntidade() {
        return tipoEntidade + " [ID=" + id + ", Bioma=" + biomaAtual + ", Vida=" + vida + "]";
    }

    /**
     * Retorna a representação textual da entidade delegando para {@link #getDescricaoEntidade()}.
     *
     * @return descrição textual da entidade
     */
    @Override
    public String toString() {
        return getDescricaoEntidade();
    }
}
