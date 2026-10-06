package br.unicap.modvillagers.modelo;

import java.util.ArrayList;
import java.util.List;

public class VillagerDocument {
    private String nome;
    private String cpf;
    private String rg;
    private List<String> historicoProfissoes;

    public VillagerDocument(String nome, String cpf, String rg) {
        if (cpf == null || cpf.isEmpty()) {
            throw new IllegalArgumentException("CPF não pode ser vazio");
        }
        this.nome = nome;
        this.cpf = cpf;
        this.rg = rg;
        this.historicoProfissoes = new ArrayList<>();
    }

    public String getNome() {
        return nome;
    }

    public String getCpf() {
        return cpf;
    }

    public void adicionarProfissao(String profissao) {
        if (profissao != null) {
            this.historicoProfissoes.add(profissao);
        }
    }
    
    @Override
    public String toString() {
        return "VillagerDocument{" + "nome='" + nome + '\'' + ", cpf='" + cpf + '\'' + '}';
    }
}