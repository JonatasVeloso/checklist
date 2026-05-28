package br.com.checklist;

import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DropMode;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.TransferHandler;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableRowSorter;
import java.awt.BorderLayout;
import java.awt.Color;
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
    private final javax.swing.JCheckBox filtroNaoFinalizadas = new javax.swing.JCheckBox("Mostrar apenas não finalizadas");
    private final JComboBox<String> filtroTipo = new JComboBox<>();

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

        filtroNaoFinalizadas.addActionListener(e -> aplicarFiltro());

        filtroTipo.addItem("TODAS");
        for (TipoTarefa tipo : TipoTarefa.values()) {
            filtroTipo.addItem(tipo.name());
        }
        filtroTipo.addActionListener(e -> aplicarFiltro());

        JPanel painelInput = new JPanel(new BorderLayout(10, 10));
        painelInput.add(campoTarefa, BorderLayout.CENTER);
        painelInput.add(botaoAdicionar, BorderLayout.EAST);

        JPanel painelFiltros = new JPanel(new BorderLayout(10, 10));
        painelFiltros.add(filtroNaoFinalizadas, BorderLayout.WEST);

        JPanel painelTipo = new JPanel(new BorderLayout(5, 5));
        painelTipo.add(new JLabel("Tipo:"), BorderLayout.WEST);
        painelTipo.add(filtroTipo, BorderLayout.CENTER);

        painelFiltros.add(painelTipo, BorderLayout.EAST);

        painel.add(painelInput, BorderLayout.CENTER);
        painel.add(painelFiltros, BorderLayout.SOUTH);

        return painel;
    }

    private JScrollPane criarPainelLista() {
        tabelaTarefas.setModel(tableModel);
        tabelaTarefas.setRowSorter(sorter);

        for (int i = 0; i < tableModel.getColumnCount(); i++) {
            sorter.setSortable(i, false);
        }

        tabelaTarefas.setFont(new Font("Arial", Font.PLAIN, 16));
        tabelaTarefas.setRowHeight(32);
        tabelaTarefas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        tabelaTarefas.getTableHeader().setReorderingAllowed(false);

        configurarColunaStatus();
        configurarColunaTipo();

        tabelaTarefas.getColumnModel().getColumn(2).setPreferredWidth(130);
        tabelaTarefas.getColumnModel().getColumn(3).setPreferredWidth(350);
        tabelaTarefas.getColumnModel().getColumn(4).setPreferredWidth(350);

        tabelaTarefas.setDefaultRenderer(StatusTarefa.class, new StatusTableCellRenderer());
        tabelaTarefas.setDefaultRenderer(TipoTarefa.class, new TipoTableCellRenderer());
        tabelaTarefas.setDefaultRenderer(String.class, new TextoTableCellRenderer());

        tabelaTarefas.setDragEnabled(true);
        tabelaTarefas.setDropMode(DropMode.INSERT_ROWS);
        tabelaTarefas.setTransferHandler(new TarefaTableTransferHandler());

        JScrollPane scrollPane = new JScrollPane(tabelaTarefas);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        return scrollPane;
    }

    private void configurarColunaStatus() {
        TableColumn colunaStatus = tabelaTarefas.getColumnModel().getColumn(0);
        colunaStatus.setMinWidth(120);
        colunaStatus.setMaxWidth(150);
        colunaStatus.setPreferredWidth(130);

        JComboBox<StatusTarefa> comboStatus = new JComboBox<>(StatusTarefa.values());
        comboStatus.setRenderer(new StatusComboBoxRenderer());

        colunaStatus.setCellEditor(new DefaultCellEditor(comboStatus));
    }

    private void configurarColunaTipo() {
        TableColumn colunaTipo = tabelaTarefas.getColumnModel().getColumn(1);
        colunaTipo.setMinWidth(120);
        colunaTipo.setMaxWidth(150);
        colunaTipo.setPreferredWidth(130);

        JComboBox<TipoTarefa> comboTipo = new JComboBox<>(TipoTarefa.values());
        colunaTipo.setCellEditor(new DefaultCellEditor(comboTipo));
    }

    private JPanel criarPainelBotoes() {
        JPanel painel = new JPanel(new GridLayout(1, 3, 10, 10));
        painel.setBorder(BorderFactory.createEmptyBorder(5, 15, 15, 15));

        JButton botaoMarcarDoneTodas = new JButton("Marcar todas DONE");
        JButton botaoLimparDone = new JButton("Limpar DONE");
        JButton botaoExcluirSelecionada = new JButton("Excluir selecionada");

        botaoMarcarDoneTodas.addActionListener(e -> marcarTodasComoDone());
        botaoLimparDone.addActionListener(e -> limparDone());
        botaoExcluirSelecionada.addActionListener(e -> excluirSelecionada());

        painel.add(botaoMarcarDoneTodas);
        painel.add(botaoLimparDone);
        painel.add(botaoExcluirSelecionada);

        return painel;
    }

    private void aplicarFiltro() {
        sorter.setRowFilter(new RowFilter<>() {
            @Override
            public boolean include(Entry<? extends TarefasTableModel, ? extends Integer> entry) {
                StatusTarefa status = (StatusTarefa) entry.getValue(0);
                TipoTarefa tipo = (TipoTarefa) entry.getValue(1);

                boolean mostrarPorStatus = true;
                boolean mostrarPorTipo = true;

                if (filtroNaoFinalizadas.isSelected()) {
                    mostrarPorStatus = !StatusTarefa.DONE.equals(status);
                }

                String tipoSelecionado = String.valueOf(filtroTipo.getSelectedItem());

                if (!"TODAS".equals(tipoSelecionado)) {
                    mostrarPorTipo = tipo.name().equals(tipoSelecionado);
                }

                return mostrarPorStatus && mostrarPorTipo;
            }
        });
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

        tableModel.adicionar(new Tarefa(
                descricao,
                "",
                "",
                StatusTarefa.PENDING,
                TipoTarefa.DIARIA
        ));

        campoTarefa.setText("");
        campoTarefa.requestFocus();

        aplicarFiltro();
        notificarAlteracao();
    }

    private void marcarTodasComoDone() {
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            tableModel.getTarefa(i).setStatus(StatusTarefa.DONE);
        }

        tableModel.fireTableDataChanged();
        aplicarFiltro();
        notificarAlteracao();
    }

    private void limparDone() {
        for (int i = tableModel.getRowCount() - 1; i >= 0; i--) {
            if (StatusTarefa.DONE.equals(tableModel.getTarefa(i).getStatus())) {
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
        aplicarFiltro();
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

        private final String[] colunas = {"Status", "Tipo", "Data", "Tarefa", "Observação"};
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
                return StatusTarefa.class;
            }

            if (columnIndex == 1) {
                return TipoTarefa.class;
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

            return switch (columnIndex) {
                case 0 -> tarefa.getStatus();
                case 1 -> tarefa.getTipo();
                case 2 -> tarefa.getDataReferencia();
                case 3 -> tarefa.getDescricao();
                case 4 -> tarefa.getObservacao();
                default -> "";
            };
        }

        @Override
        public void setValueAt(Object valor, int rowIndex, int columnIndex) {
            Tarefa tarefa = tarefas.get(rowIndex);

            if (columnIndex == 0) {
                atualizarStatus(tarefa, valor);
            } else if (columnIndex == 1) {
                atualizarTipo(tarefa, valor);
            } else if (columnIndex == 2) {
                atualizarData(tarefa, valor);
            } else if (columnIndex == 3) {
                atualizarDescricao(tarefa, valor);
            } else if (columnIndex == 4) {
                atualizarObservacao(tarefa, valor);
            }

            fireTableRowsUpdated(rowIndex, rowIndex);
            aplicarFiltro();
            notificarAlteracao();
        }

        private void atualizarStatus(Tarefa tarefa, Object valor) {
            if (valor instanceof StatusTarefa status) {
                tarefa.setStatus(status);
            } else {
                tarefa.setStatus(StatusTarefa.fromTexto(String.valueOf(valor)));
            }
        }

        private void atualizarTipo(Tarefa tarefa, Object valor) {
            TipoTarefa novoTipo;

            if (valor instanceof TipoTarefa tipo) {
                novoTipo = tipo;
            } else {
                novoTipo = TipoTarefa.fromTexto(String.valueOf(valor));
            }

            tarefa.setTipo(novoTipo);

            String dataAjustada = DataTarefaValidator.ajustarAoTrocarTipo(
                    novoTipo,
                    tarefa.getDataReferencia()
            );

            tarefa.setDataReferencia(dataAjustada);
        }

        private void atualizarData(Tarefa tarefa, Object valor) {
            String dataDigitada = String.valueOf(valor).trim();

            DataTarefaValidator.ResultadoValidacao resultado = DataTarefaValidator.normalizar(
                    tarefa.getTipo(),
                    dataDigitada
            );

            if (!resultado.valida()) {
                JOptionPane.showMessageDialog(
                        CategoriaChecklistPanel.this,
                        resultado.mensagemErro(),
                        "Data inválida",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            tarefa.setDataReferencia(resultado.valorNormalizado());
        }

        private void atualizarDescricao(Tarefa tarefa, Object valor) {
            String novaDescricao = String.valueOf(valor).trim();

            if (!novaDescricao.isEmpty()) {
                tarefa.setDescricao(novaDescricao);
            }
        }

        private void atualizarObservacao(Tarefa tarefa, Object valor) {
            tarefa.setObservacao(String.valueOf(valor).trim());
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

    private static class StatusTableCellRenderer extends DefaultTableCellRenderer {

        @Override
        protected void setValue(Object value) {
            if (value instanceof StatusTarefa status) {
                setText(status.name());
            } else {
                setText("");
            }
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
            Component component = super.getTableCellRendererComponent(
                    table,
                    value,
                    isSelected,
                    hasFocus,
                    row,
                    column
            );

            if (!isSelected && value instanceof StatusTarefa status) {
                component.setBackground(status.getCor());
                component.setForeground(Color.BLACK);
            }

            if (isSelected) {
                component.setBackground(table.getSelectionBackground());
                component.setForeground(table.getSelectionForeground());
            }

            setHorizontalAlignment(JLabel.CENTER);

            return component;
        }
    }

    private static class TipoTableCellRenderer extends DefaultTableCellRenderer {

        @Override
        protected void setValue(Object value) {
            if (value instanceof TipoTarefa tipo) {
                setText(tipo.name());
            } else {
                setText("");
            }
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
            Component component = super.getTableCellRendererComponent(
                    table,
                    value,
                    isSelected,
                    hasFocus,
                    row,
                    column
            );

            if (!isSelected) {
                component.setBackground(table.getBackground());
                component.setForeground(Color.BLACK);
            }

            if (isSelected) {
                component.setBackground(table.getSelectionBackground());
                component.setForeground(table.getSelectionForeground());
            }

            setHorizontalAlignment(JLabel.CENTER);

            return component;
        }
    }

    private class TextoTableCellRenderer extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(
                JTable table,
                Object value,
                boolean isSelected,
                boolean hasFocus,
                int row,
                int column
        ) {
            Component component = super.getTableCellRendererComponent(
                    table,
                    value,
                    isSelected,
                    hasFocus,
                    row,
                    column
            );

            if (!isSelected) {
                int modelIndex = table.convertRowIndexToModel(row);
                StatusTarefa status = tableModel.getTarefa(modelIndex).getStatus();

                component.setBackground(status.getCor());
                component.setForeground(Color.BLACK);
            }

            if (isSelected) {
                component.setBackground(table.getSelectionBackground());
                component.setForeground(table.getSelectionForeground());
            }

            return component;
        }
    }

    private static class StatusComboBoxRenderer extends DefaultListCellRenderer {

        @Override
        public Component getListCellRendererComponent(
                JList<?> list,
                Object value,
                int index,
                boolean isSelected,
                boolean cellHasFocus
        ) {
            Component component = super.getListCellRendererComponent(
                    list,
                    value,
                    index,
                    isSelected,
                    cellHasFocus
            );

            if (value instanceof StatusTarefa status) {
                setText(status.name());

                if (!isSelected) {
                    component.setBackground(status.getCor());
                    component.setForeground(Color.BLACK);
                }
            }

            return component;
        }
    }
}