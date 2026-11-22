package br.unicap.modvillagers.persistencia;

import br.unicap.modvillagers.util.ValidacaoUtil;

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
        String chaveValida = ValidacaoUtil.exigirTextoValido(chave, "Chave NBT");
        ValidacaoUtil.exigirNaoNulo(valor, "Valor associado à chave '" + chaveValida + "'");
        this.stringTags.put(chaveValida, valor);
    }

    public String getString(String chave) {
        String chaveValida = ValidacaoUtil.exigirTextoValido(chave, "Chave NBT");
        return this.stringTags.getOrDefault(chaveValida, "");
    }

    public void setStringList(String chave, List<String> valores) {
        String chaveValida = ValidacaoUtil.exigirTextoValido(chave, "Chave NBT");
        ValidacaoUtil.exigirNaoNulo(valores, "Lista associada à chave '" + chaveValida + "'");
        this.listTags.put(chaveValida, new ArrayList<>(valores));
    }

    public List<String> getStringList(String chave) {
        String chaveValida = ValidacaoUtil.exigirTextoValido(chave, "Chave NBT");
        List<String> lista = this.listTags.get(chaveValida);
        if (lista == null) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(new ArrayList<>(lista));
    }

    public boolean hasKey(String chave) {
        if (chave == null || chave.isBlank()) {
            return false;
        }
        String chaveValida = chave.trim();
        return this.stringTags.containsKey(chaveValida) || this.listTags.containsKey(chaveValida);
    }

    public void remove(String chave) {
        if (chave != null) {
            String chaveValida = chave.trim();
            this.stringTags.remove(chaveValida);
            this.listTags.remove(chaveValida);
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

    public Map<String, String> getStringTags() {
        return Collections.unmodifiableMap(this.stringTags);
    }

    public Map<String, List<String>> getListTags() {
        Map<String, List<String>> copia = new LinkedHashMap<>();
        for (Map.Entry<String, List<String>> entrada : this.listTags.entrySet()) {
            copia.put(entrada.getKey(), Collections.unmodifiableList(new ArrayList<>(entrada.getValue())));
        }
        return Collections.unmodifiableMap(copia);
    }

    @Override
    public String toString() {
        return "NBTTagCompound{"
                + "stringTags=" + stringTags
                + ", listTags=" + listTags
                + '}';
    }
}
