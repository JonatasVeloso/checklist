package br.com.checklist;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.DropMode;
import javax.swing.RowFilter;
import javax.swing.TransferHandler;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableRowSorter;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.util.ArrayList;
import java.util.List;

public class CategoriaChecklistPanel extends JPanel {

    private final Runnable onAlteracao;

    private final TarefasTableModel tableModel = new TarefasTableModel();
    private final JTable tabelaTarefas = new JTable(tableModel);
    private final TableRowSorter<TarefasTableModel> sorter = new TableRowSorter<>(tableModel);

    private final JTextField campoTarefa = new JTextField();
    private final JCheckBox filtroNaoConcluidas = new JCheckBox("Mostrar apenas não concluídas");

    public CategoriaChecklistPanel(List<Tarefa> tarefas, Runnable onAlteracao) {
        this.onAlteracao = onAlteracao;

        setLayout(new BorderLayout(10, 10));

        configurarComponentes();

        for (Tarefa tarefa : tarefas) {
            tableModel.adicionar(tarefa);
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

        filtroNaoConcluidas.addActionListener(e -> aplicarFiltro());

        JPanel painelInput = new JPanel(new BorderLayout(10, 10));
        painelInput.add(campoTarefa, BorderLayout.CENTER);
        painelInput.add(botaoAdicionar, BorderLayout.EAST);

        painel.add(painelInput, BorderLayout.CENTER);
        painel.add(filtroNaoConcluidas, BorderLayout.SOUTH);

        return painel;
    }

    private JScrollPane criarPainelLista() {
        tabelaTarefas.setModel(tableModel);
        tabelaTarefas.setRowSorter(sorter);

        tabelaTarefas.setFont(new Font("Arial", Font.PLAIN, 16));
        tabelaTarefas.setRowHeight(32);
        tabelaTarefas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        tabelaTarefas.getTableHeader().setReorderingAllowed(false);

        tabelaTarefas.getColumnModel().getColumn(0).setMinWidth(90);
        tabelaTarefas.getColumnModel().getColumn(0).setMaxWidth(110);
        tabelaTarefas.getColumnModel().getColumn(0).setPreferredWidth(100);

        tabelaTarefas.getColumnModel().getColumn(1).setPreferredWidth(500);

        tabelaTarefas.setDefaultRenderer(Boolean.class, new CheckBoxCentralizadoRenderer());

        tabelaTarefas.setDragEnabled(true);
        tabelaTarefas.setDropMode(DropMode.INSERT_ROWS);
        tabelaTarefas.setTransferHandler(new TarefaTableTransferHandler());

        JScrollPane scrollPane = new JScrollPane(tabelaTarefas);
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

    private void aplicarFiltro() {
        if (filtroNaoConcluidas.isSelected()) {
            sorter.setRowFilter(new RowFilter<>() {
                @Override
                public boolean include(Entry<? extends TarefasTableModel, ? extends Integer> entry) {
                    Boolean concluida = (Boolean) entry.getValue(0);
                    return !Boolean.TRUE.equals(concluida);
                }
            });
        } else {
            sorter.setRowFilter(null);
        }
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

        tableModel.adicionar(new Tarefa(descricao, false));

        campoTarefa.setText("");
        campoTarefa.requestFocus();

        notificarAlteracao();
    }

    private void concluirTodas() {
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            tableModel.getTarefa(i).setConcluida(true);
        }

        tableModel.fireTableDataChanged();
        aplicarFiltro();
        notificarAlteracao();
    }

    private void limparConcluidas() {
        for (int i = tableModel.getRowCount() - 1; i >= 0; i--) {
            if (tableModel.getTarefa(i).isConcluida()) {
                tableModel.remover(i);
            }
        }

        aplicarFiltro();
        notificarAlteracao();
    }

    private void excluirSelecionada() {
        int viewIndex = tabelaTarefas.getSelectedRow();

        if (viewIndex < 0) {
            JOptionPane.showMessageDialog(
                    this,
                    "Selecione uma tarefa para excluir.",
                    "Atenção",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        int modelIndex = tabelaTarefas.convertRowIndexToModel(viewIndex);

        tableModel.remover(modelIndex);
        notificarAlteracao();
    }

    private void moverTarefa(int origemView, int destinoView) {
        if (origemView < 0 || origemView >= tabelaTarefas.getRowCount()) {
            return;
        }

        if (destinoView < 0 || destinoView > tabelaTarefas.getRowCount()) {
            return;
        }

        int origemModel = tabelaTarefas.convertRowIndexToModel(origemView);

        int destinoModel;

        if (destinoView >= tabelaTarefas.getRowCount()) {
            destinoModel = tableModel.getRowCount();
        } else {
            destinoModel = tabelaTarefas.convertRowIndexToModel(destinoView);
        }

        if (origemModel == destinoModel || origemModel + 1 == destinoModel) {
            return;
        }

        tableModel.mover(origemModel, destinoModel);

        tableModel.fireTableDataChanged();
        aplicarFiltro();
        notificarAlteracao();
    }

    public List<Tarefa> getTarefas() {
        return tableModel.getTarefas();
    }

    private void notificarAlteracao() {
        if (onAlteracao != null) {
            onAlteracao.run();
        }
    }

    private class TarefasTableModel extends AbstractTableModel {

        private final String[] colunas = {"Concluída", "Tarefa"};
        private final List<Tarefa> tarefas = new ArrayList<>();

        @Override
        public int getRowCount() {
            return tarefas.size();
        }

        @Override
        public int getColumnCount() {
            return colunas.length;
        }

        @Override
        public String getColumnName(int column) {
            return colunas[column];
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            if (columnIndex == 0) {
                return Boolean.class;
            }

            return String.class;
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return true;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            Tarefa tarefa = tarefas.get(rowIndex);

            if (columnIndex == 0) {
                return tarefa.isConcluida();
            }

            return tarefa.getDescricao();
        }

        @Override
        public void setValueAt(Object valor, int rowIndex, int columnIndex) {
            Tarefa tarefa = tarefas.get(rowIndex);

            if (columnIndex == 0) {
                tarefa.setConcluida(Boolean.TRUE.equals(valor));
            } else if (columnIndex == 1) {
                String novaDescricao = String.valueOf(valor).trim();

                if (!novaDescricao.isEmpty()) {
                    tarefa.setDescricao(novaDescricao);
                }
            }

            fireTableRowsUpdated(rowIndex, rowIndex);
            aplicarFiltro();
            notificarAlteracao();
        }

        public void adicionar(Tarefa tarefa) {
            tarefas.add(tarefa);

            int index = tarefas.size() - 1;
            fireTableRowsInserted(index, index);
        }

        public void remover(int index) {
            tarefas.remove(index);
            fireTableRowsDeleted(index, index);
        }

        public void mover(int origem, int destino) {
            Tarefa tarefa = tarefas.remove(origem);

            if (destino > origem) {
                destino--;
            }

            tarefas.add(destino, tarefa);
            fireTableDataChanged();
        }

        public Tarefa getTarefa(int index) {
            return tarefas.get(index);
        }

        public List<Tarefa> getTarefas() {
            return new ArrayList<>(tarefas);
        }
    }

    private class TarefaTableTransferHandler extends TransferHandler {

        private final DataFlavor rowFlavor = new DataFlavor(Integer.class, "Integer Row Index");

        private int origemView = -1;

        @Override
        protected Transferable createTransferable(JComponent c) {
            origemView = tabelaTarefas.getSelectedRow();

            if (origemView < 0) {
                return null;
            }

            return new Transferable() {
                @Override
                public DataFlavor[] getTransferDataFlavors() {
                    return new DataFlavor[]{rowFlavor};
                }

                @Override
                public boolean isDataFlavorSupported(DataFlavor flavor) {
                    return rowFlavor.equals(flavor);
                }

                @Override
                public Object getTransferData(DataFlavor flavor) {
                    return origemView;
                }
            };
        }

        @Override
        public boolean canImport(TransferSupport support) {
            return support.isDrop() && support.isDataFlavorSupported(rowFlavor);
        }

        @Override
        public boolean importData(TransferSupport support) {
            if (!canImport(support)) {
                return false;
            }

            JTable.DropLocation dropLocation = (JTable.DropLocation) support.getDropLocation();
            int destinoView = dropLocation.getRow();

            moverTarefa(origemView, destinoView);

            origemView = -1;

            return true;
        }

        @Override
        public int getSourceActions(JComponent c) {
            return MOVE;
        }
    }

    private static class CheckBoxCentralizadoRenderer extends JCheckBox implements TableCellRenderer {

        public CheckBoxCentralizadoRenderer() {
            setHorizontalAlignment(CENTER);
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(
                JTable table,
                Object value,
                boolean isSelected,
                boolean hasFocus,
                int row,
                int column
        ) {
            setSelected(Boolean.TRUE.equals(value));

            if (isSelected) {
                setBackground(table.getSelectionBackground());
                setForeground(table.getSelectionForeground());
            } else {
                setBackground(table.getBackground());
                setForeground(table.getForeground());
            }

            return this;
        }
    }
}