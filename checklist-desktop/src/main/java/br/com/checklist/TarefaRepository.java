package br.com.checklist;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class TarefaRepository {

    private static final Path ARQUIVO = Path.of(
            System.getProperty("user.home"),
            "checklist-tarefas.txt"
    );

    public Map<String, List<Tarefa>> carregar() {
        Map<String, List<Tarefa>> categorias = new LinkedHashMap<>();

        if (!Files.exists(ARQUIVO)) {
            criarCategoriasPadrao(categorias);
            return categorias;
        }

        String categoriaAtual = null;

        try (BufferedReader reader = Files.newBufferedReader(ARQUIVO)) {
            String linha;

            while ((linha = reader.readLine()) != null) {
                if (linha.isBlank()) {
                    continue;
                }

                if (linha.startsWith("[") && linha.endsWith("]")) {
                    categoriaAtual = linha.substring(1, linha.length() - 1);
                    categorias.putIfAbsent(categoriaAtual, new ArrayList<>());
                    continue;
                }

                if (categoriaAtual == null) {
                    continue;
                }

                Tarefa tarefa = converterLinhaParaTarefa(linha);

                if (tarefa != null) {
                    categorias.get(categoriaAtual).add(tarefa);
                }
            }

        } catch (IOException e) {
            throw new RuntimeException("Erro ao carregar tarefas", e);
        }

        if (categorias.isEmpty()) {
            criarCategoriasPadrao(categorias);
        }

        return categorias;
    }

    private Tarefa converterLinhaParaTarefa(String linha) {
        String[] partes = linha.split(";", 5);

        if (partes.length == 2) {
            StatusTarefa status = StatusTarefa.fromTexto(partes[0]);
            String descricao = partes[1];

            return new Tarefa(
                    descricao,
                    "",
                    "",
                    status,
                    TipoTarefa.DIARIA
            );
        }

        if (partes.length == 3) {
            StatusTarefa status = StatusTarefa.fromTexto(partes[0]);
            String descricao = partes[1];
            String observacao = partes[2];

            return new Tarefa(
                    descricao,
                    observacao,
                    "",
                    status,
                    TipoTarefa.DIARIA
            );
        }

        if (partes.length >= 5) {
            StatusTarefa status = StatusTarefa.fromTexto(partes[0]);
            TipoTarefa tipo = TipoTarefa.fromTexto(partes[1]);
            String dataReferencia = partes[2];
            String descricao = partes[3];
            String observacao = partes[4];

            return new Tarefa(
                    descricao,
                    observacao,
                    dataReferencia,
                    status,
                    tipo
            );
        }

        return null;
    }

    public void salvar(Map<String, List<Tarefa>> categorias) {
        try (BufferedWriter writer = Files.newBufferedWriter(ARQUIVO)) {
            for (Map.Entry<String, List<Tarefa>> categoria : categorias.entrySet()) {
                writer.write("[" + categoria.getKey() + "]");
                writer.newLine();

                for (Tarefa tarefa : categoria.getValue()) {
                    writer.write(
                            tarefa.getStatus().name()
                                    + ";"
                                    + tarefa.getTipo().name()
                                    + ";"
                                    + limparTexto(tarefa.getDataReferencia())
                                    + ";"
                                    + limparTexto(tarefa.getDescricao())
                                    + ";"
                                    + limparTexto(tarefa.getObservacao())
                    );
                    writer.newLine();
                }

                writer.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException("Erro ao salvar tarefas", e);
        }
    }

    private String limparTexto(String texto) {
        if (texto == null) {
            return "";
        }

        return texto
                .replace("\r", " ")
                .replace("\n", " ")
                .replace(";", ",");
    }

    private void criarCategoriasPadrao(Map<String, List<Tarefa>> categorias) {
        categorias.put("Doméstico", new ArrayList<>());
        categorias.put("Trabalho", new ArrayList<>());
        categorias.put("Jogo", new ArrayList<>());
    }
}