package br.unicap.modvillagers.entidades;

import br.unicap.modvillagers.util.ValidacaoUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Representa um jogador no mundo do jogo (equivalente à classe nativa {@code Player} do Minecraft).
 * Estende {@link LivingEntity} e mantém um histórico das mensagens recebidas no chat.
 */
public class Player extends LivingEntity {

    private String nomeJogador;
    private final List<String> historicoChat;

    /**
     * Cria um jogador no bioma padrão ({@code "Planície"}).
     *
     * @param nomeJogador nome de exibição do jogador
     */
    public Player(String nomeJogador) {
        this(nomeJogador, "Planície");
    }

    /**
     * Cria um jogador no bioma informado.
     *
     * @param nomeJogador nome de exibição do jogador
     * @param biomaAtual  bioma em que o jogador se encontra
     */
    public Player(String nomeJogador, String biomaAtual) {
        super("Jogador", 20.0, biomaAtual);
        setNomeJogador(nomeJogador);
        this.historicoChat = new ArrayList<>();
    }

    /**
     * Retorna o nome do jogador.
     *
     * @return nome de exibição do jogador
     */
    public String getNomeJogador() {
        return nomeJogador;
    }

    /**
     * Atualiza o nome do jogador.
     *
     * @param nomeJogador novo nome de exibição
     */
    public void setNomeJogador(String nomeJogador) {
        this.nomeJogador = ValidacaoUtil.exigirTextoValido(nomeJogador, "Nome do jogador");
    }

    /**
     * Retorna uma visualização imutável de todas as mensagens recebidas no chat deste jogador.
     *
     * @return lista não modificável de mensagens do chat
     */
    public List<String> getHistoricoChat() {
        return Collections.unmodifiableList(historicoChat);
    }

    /**
     * Envia e exibe uma mensagem formatada no chat do jogador, registrando-a no histórico.
     *
     * @param mensagem conteúdo textual a ser exibido
     */
    public void enviarMensagemChat(String mensagem) {
        if (mensagem == null || mensagem.isBlank()) {
            return;
        }
        this.historicoChat.add(mensagem);
        System.out.println("[Chat -> " + nomeJogador + "]");
        System.out.println(mensagem);
    }

    /**
     * Retorna uma descrição textual específica para a entidade {@code Player}.
     *
     * @return descrição resumida do jogador
     */
    @Override
    public String getDescricaoEntidade() {
        return "Player '" + nomeJogador + "' [ID=" + getId() + ", Bioma=" + getBiomaAtual() + "]";
    }
}
