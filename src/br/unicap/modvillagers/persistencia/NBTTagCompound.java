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

    /**
     * Cria um composto NBT vazio pronto para armazenar tags textuais e listas.
     */
    public NBTTagCompound() {
        this.stringTags = new LinkedHashMap<>();
        this.listTags = new LinkedHashMap<>();
    }

    /**
     * Armazena uma tag textual associada à chave informada.
     *
     * @param chave identificador da tag
     * @param valor valor textual a ser salvo
     */
    public void setString(String chave, String valor) {
        String chaveValida = ValidacaoUtil.exigirTextoValido(chave, "Chave NBT");
        ValidacaoUtil.exigirNaoNulo(valor, "Valor associado à chave '" + chaveValida + "'");
        this.stringTags.put(chaveValida, valor);
    }

    /**
     * Recupera o valor textual associado à chave informada, ou string vazia se ausente.
     *
     * @param chave identificador da tag
     * @return valor armazenado ou {@code ""} caso a chave não exista
     */
    public String getString(String chave) {
        String chaveValida = ValidacaoUtil.exigirTextoValido(chave, "Chave NBT");
        return this.stringTags.getOrDefault(chaveValida, "");
    }

    /**
     * Armazena uma cópia defensiva de uma lista de strings sob a chave informada.
     *
     * @param chave   identificador da tag de lista
     * @param valores lista de valores textuais
     */
    public void setStringList(String chave, List<String> valores) {
        String chaveValida = ValidacaoUtil.exigirTextoValido(chave, "Chave NBT");
        ValidacaoUtil.exigirNaoNulo(valores, "Lista associada à chave '" + chaveValida + "'");
        this.listTags.put(chaveValida, new ArrayList<>(valores));
    }

    /**
     * Recupera uma lista imutável de strings associada à chave informada.
     *
     * @param chave identificador da tag de lista
     * @return lista imutável armazenada ou lista vazia se a chave não existir
     */
    public List<String> getStringList(String chave) {
        String chaveValida = ValidacaoUtil.exigirTextoValido(chave, "Chave NBT");
        List<String> lista = this.listTags.get(chaveValida);
        if (lista == null) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(new ArrayList<>(lista));
    }

    /**
     * Verifica se existe alguma tag (textual ou lista) armazenada sob a chave informada.
     *
     * @param chave identificador da tag
     * @return {@code true} se a chave existir no composto
     */
    public boolean hasKey(String chave) {
        if (chave == null || chave.isBlank()) {
            return false;
        }
        String chaveValida = chave.trim();
        return this.stringTags.containsKey(chaveValida) || this.listTags.containsKey(chaveValida);
    }

    /**
     * Remove a tag correspondente à chave informada, caso exista.
     *
     * @param chave identificador da tag a remover
     */
    public void remove(String chave) {
        if (chave != null) {
            String chaveValida = chave.trim();
            this.stringTags.remove(chaveValida);
            this.listTags.remove(chaveValida);
        }
    }

    /**
     * Indica se o composto NBT não contém nenhuma tag armazenada.
     *
     * @return {@code true} se estiver vazio
     */
    public boolean isEmpty() {
        return this.stringTags.isEmpty() && this.listTags.isEmpty();
    }

    /**
     * Retorna o conjunto imutável ({@link Set}) com todas as chaves armazenadas neste composto.
     *
     * @return conjunto de chaves presentes
     */
    public Set<String> getKeys() {
        Set<String> chaves = new LinkedHashSet<>();
        chaves.addAll(this.stringTags.keySet());
        chaves.addAll(this.listTags.keySet());
        return Collections.unmodifiableSet(chaves);
    }

    /**
     * Retorna uma visão imutável do mapa de tags textuais armazenadas.
     *
     * @return mapa imutável de chaves e valores textuais
     */
    public Map<String, String> getStringTags() {
        return Collections.unmodifiableMap(this.stringTags);
    }

    /**
     * Retorna uma visão imutável do mapa de tags de listas armazenadas.
     *
     * @return mapa imutável de chaves e listas imutáveis
     */
    public Map<String, List<String>> getListTags() {
        Map<String, List<String>> copia = new LinkedHashMap<>();
        for (Map.Entry<String, List<String>> entrada : this.listTags.entrySet()) {
            copia.put(entrada.getKey(), Collections.unmodifiableList(new ArrayList<>(entrada.getValue())));
        }
        return Collections.unmodifiableMap(copia);
    }

    /**
     * Retorna a representação textual técnica de todas as tags deste composto.
     *
     * @return representação em string do {@code NBTTagCompound}
     */
    @Override
    public String toString() {
        return "NBTTagCompound{"
                + "stringTags=" + stringTags
                + ", listTags=" + listTags
                + '}';
    }
}
