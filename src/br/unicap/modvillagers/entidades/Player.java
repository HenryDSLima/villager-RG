package br.unicap.modvillagers.entidades;

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

    public Player(String nomeJogador) {
        this(nomeJogador, "Planície");
    }

    public Player(String nomeJogador, String biomaAtual) {
        super("Jogador", 20.0, biomaAtual);
        setNomeJogador(nomeJogador);
        this.historicoChat = new ArrayList<>();
    }

    public String getNomeJogador() {
        return nomeJogador;
    }

    public void setNomeJogador(String nomeJogador) {
        if (nomeJogador == null || nomeJogador.isBlank()) {
            throw new IllegalArgumentException("Nome do jogador não pode ser nulo ou vazio.");
        }
        this.nomeJogador = nomeJogador.trim();
    }

    public List<String> getHistoricoChat() {
        return Collections.unmodifiableList(historicoChat);
    }

    /**
     * Envia e exibe uma mensagem formatada no chat do jogador.
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

    @Override
    public String getDescricaoEntidade() {
        return "Player '" + nomeJogador + "' [ID=" + getId() + ", Bioma=" + getBiomaAtual() + "]";
    }
}
