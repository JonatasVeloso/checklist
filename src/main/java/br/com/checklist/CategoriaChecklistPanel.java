package br.com.checklist;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;

public class CategoriaChecklistPanel extends JPanel {

    private final Runnable onAlteracao;

    private final DefaultListModel<Tarefa> listModel = new DefaultListModel<>();
    private final JList<Tarefa> listaTarefas = new JList<>(listModel);

    private final JTextField campoTarefa = new JTextField();

    public CategoriaChecklistPanel(List<Tarefa> tarefas, Runnable onAlteracao) {
        this.onAlteracao = onAlteracao;

        setLayout(new BorderLayout(10, 10));

        configurarComponentes();

        for (Tarefa tarefa : tarefas) {
            listModel.addElement(tarefa);
        }
    }

    private void configurarComponentes() {
        JPanel painelTopo = criarPainelTopo();
        JScrollPane painelLista = criarPainelLista();
        JPanel painelBotoes = criarPainelBotoes();

        add(painelTopo, BorderLayout.NORTH);
        add(painelLista, BorderLayout.CENTER);
        add(painelBotoes, BorderLayout.SOUTH);
    }

    private JPanel criarPainelTopo() {
        JPanel painel = new JPanel(new BorderLayout(10, 10));
        painel.setBorder(BorderFactory.createEmptyBorder(15, 15, 5, 15));

        JButton botaoAdicionar = new JButton("Adicionar");
        botaoAdicionar.addActionListener(e -> adicionarTarefa());

        campoTarefa.addActionListener(e -> adicionarTarefa());

        painel.add(campoTarefa, BorderLayout.CENTER);
        painel.add(botaoAdicionar, BorderLayout.EAST);

        return painel;
    }

    private JScrollPane criarPainelLista() {
        listaTarefas.setCellRenderer(new TarefaCellRenderer());

        listaTarefas.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int index = listaTarefas.locationToIndex(e.getPoint());

                if (index >= 0) {
                    Tarefa tarefa = listModel.getElementAt(index);
                    tarefa.setConcluida(!tarefa.isConcluida());

                    listaTarefas.repaint();
                    notificarAlteracao();
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(listaTarefas);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        return scrollPane;
    }

    private JPanel criarPainelBotoes() {
        JPanel painel = new JPanel(new GridLayout(1, 3, 10, 10));
        painel.setBorder(BorderFactory.createEmptyBorder(5, 15, 15, 15));

        JButton botaoConcluirTodas = new JButton("Concluir todas");
        JButton botaoLimparConcluidas = new JButton("Limpar concluídas");
        JButton botaoExcluirSelecionada = new JButton("Excluir selecionada");

        botaoConcluirTodas.addActionListener(e -> concluirTodas());
        botaoLimparConcluidas.addActionListener(e -> limparConcluidas());
        botaoExcluirSelecionada.addActionListener(e -> excluirSelecionada());

        painel.add(botaoConcluirTodas);
        painel.add(botaoLimparConcluidas);
        painel.add(botaoExcluirSelecionada);

        return painel;
    }

    private void adicionarTarefa() {
        String descricao = campoTarefa.getText().trim();

        if (descricao.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Digite uma descrição para a tarefa.",
                    "Atenção",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        Tarefa tarefa = new Tarefa(descricao, false);
        listModel.addElement(tarefa);

        campoTarefa.setText("");
        campoTarefa.requestFocus();

        notificarAlteracao();
    }

    private void concluirTodas() {
        for (int i = 0; i < listModel.size(); i++) {
            listModel.getElementAt(i).setConcluida(true);
        }

        listaTarefas.repaint();
        notificarAlteracao();
    }

    private void limparConcluidas() {
        for (int i = listModel.size() - 1; i >= 0; i--) {
            if (listModel.getElementAt(i).isConcluida()) {
                listModel.remove(i);
            }
        }

        notificarAlteracao();
    }

    private void excluirSelecionada() {
        int index = listaTarefas.getSelectedIndex();

        if (index < 0) {
            JOptionPane.showMessageDialog(
                    this,
                    "Selecione uma tarefa para excluir.",
                    "Atenção",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        listModel.remove(index);
        notificarAlteracao();
    }

    public List<Tarefa> getTarefas() {
        List<Tarefa> tarefas = new ArrayList<>();

        for (int i = 0; i < listModel.size(); i++) {
            tarefas.add(listModel.getElementAt(i));
        }

        return tarefas;
    }

    private void notificarAlteracao() {
        if (onAlteracao != null) {
            onAlteracao.run();
        }
    }

    private static class TarefaCellRenderer extends JCheckBox implements ListCellRenderer<Tarefa> {

        public TarefaCellRenderer() {
            setOpaque(true);
            setFont(new Font("Arial", Font.PLAIN, 16));
            setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        }

        @Override
        public Component getListCellRendererComponent(
                JList<? extends Tarefa> list,
                Tarefa tarefa,
                int index,
                boolean isSelected,
                boolean cellHasFocus
        ) {
            setText(tarefa.getDescricao());
            setSelected(tarefa.isConcluida());

            if (isSelected) {
                setBackground(list.getSelectionBackground());
                setForeground(list.getSelectionForeground());
            } else {
                setBackground(list.getBackground());
                setForeground(list.getForeground());
            }

            return this;
        }
    }
}