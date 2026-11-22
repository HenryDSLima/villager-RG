package br.unicap.modvillagers.persistencia;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Representa uma estrutura de dados NBT (Named Binary Tag) simplificada,
 * simulando o formato nativo de persistência do Minecraft para serialização
 * e desserialização dos documentos civis dos Villagers.
 */
public class NBTTagCompound {

    private final Map<String, String> stringTags;
    private final Map<String, List<String>> listTags;

    public NBTTagCompound() {
        this.stringTags = new LinkedHashMap<>();
        this.listTags = new LinkedHashMap<>();
    }

    public void setString(String chave, String valor) {
        validarChave(chave);
        if (valor == null) {
            throw new IllegalArgumentException("Valor associado à chave '" + chave + "' não pode ser nulo.");
        }
        this.stringTags.put(chave, valor);
    }

    public String getString(String chave) {
        validarChave(chave);
        return this.stringTags.getOrDefault(chave, "");
    }

    public void setStringList(String chave, List<String> valores) {
        validarChave(chave);
        if (valores == null) {
            throw new IllegalArgumentException("Lista associada à chave '" + chave + "' não pode ser nula.");
        }
        this.listTags.put(chave, new ArrayList<>(valores));
    }

    public List<String> getStringList(String chave) {
        validarChave(chave);
        List<String> lista = this.listTags.get(chave);
        if (lista == null) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(new ArrayList<>(lista));
    }

    public boolean hasKey(String chave) {
        if (chave == null || chave.isBlank()) {
            return false;
        }
        return this.stringTags.containsKey(chave) || this.listTags.containsKey(chave);
    }

    public void remove(String chave) {
        if (chave != null) {
            this.stringTags.remove(chave);
            this.listTags.remove(chave);
        }
    }

    public boolean isEmpty() {
        return this.stringTags.isEmpty() && this.listTags.isEmpty();
    }

    public Set<String> getKeys() {
        Set<String> chaves = new LinkedHashSet<>();
        chaves.addAll(this.stringTags.keySet());
        chaves.addAll(this.listTags.keySet());
        return Collections.unmodifiableSet(chaves);
    }

    private void validarChave(String chave) {
        if (chave == null || chave.isBlank()) {
            throw new IllegalArgumentException("Chave NBT não pode ser nula ou vazia.");
        }
    }

    @Override
    public String toString() {
        return "NBTTagCompound{"
                + "stringTags=" + stringTags
                + ", listTags=" + listTags
                + '}';
    }
}
