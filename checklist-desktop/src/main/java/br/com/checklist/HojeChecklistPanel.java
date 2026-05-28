package br.com.checklist;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HojeChecklistPanel extends JPanel {

    private static final DateTimeFormatter FORMATADOR_DATA = DateTimeFormatter
            .ofPattern("dd/MM/uuuu")
            .withResolverStyle(ResolverStyle.STRICT);

    private final HojeTableModel tableModel = new HojeTableModel();
    private final JTable tabelaHoje = new JTable(tableModel);

    private final JLabel titulo = new JLabel("", SwingConstants.CENTER);
    private final JLabel resumo = new JLabel("", SwingConstants.CENTER);

    public HojeChecklistPanel() {
        setLayout(new BorderLayout(10, 10));

        configurarTopo();
        configurarTabela();
    }

    private void configurarTopo() {
        titulo.setFont(new Font("Arial", Font.BOLD, 24));
        resumo.setFont(new Font("Arial", Font.PLAIN, 14));

        JPanel painelTopo = new JPanel(new BorderLayout(5, 5));
        painelTopo.setBorder(BorderFactory.createEmptyBorder(15, 15, 5, 15));
        painelTopo.add(titulo, BorderLayout.NORTH);
        painelTopo.add(resumo, BorderLayout.SOUTH);

        add(painelTopo, BorderLayout.NORTH);
    }

    private void configurarTabela() {
        tabelaHoje.setFont(new Font("Arial", Font.PLAIN, 15));
        tabelaHoje.setRowHeight(32);
        tabelaHoje.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabelaHoje.getTableHeader().setReorderingAllowed(false);

        tabelaHoje.getColumnModel().getColumn(0).setPreferredWidth(140);
        tabelaHoje.getColumnModel().getColumn(1).setPreferredWidth(120);
        tabelaHoje.getColumnModel().getColumn(2).setPreferredWidth(120);
        tabelaHoje.getColumnModel().getColumn(3).setPreferredWidth(120);
        tabelaHoje.getColumnModel().getColumn(4).setPreferredWidth(350);
        tabelaHoje.getColumnModel().getColumn(5).setPreferredWidth(350);

        HojeTableCellRenderer renderer = new HojeTableCellRenderer();

        tabelaHoje.setDefaultRenderer(Object.class, renderer);
        tabelaHoje.setDefaultRenderer(String.class, renderer);
        tabelaHoje.setDefaultRenderer(StatusTarefa.class, renderer);
        tabelaHoje.setDefaultRenderer(TipoTarefa.class, renderer);

        JScrollPane scrollPane = new JScrollPane(tabelaHoje);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 15, 15, 15));

        add(scrollPane, BorderLayout.CENTER);
    }

    public void atualizar(Map<String, List<Tarefa>> categorias) {
        LocalDate hoje = LocalDate.now();
        List<TarefaHojeView> tarefasHoje = new ArrayList<>();

        for (Map.Entry<String, List<Tarefa>> categoria : categorias.entrySet()) {
            String nomeCategoria = categoria.getKey();

            for (Tarefa tarefa : categoria.getValue()) {
                if (deveAparecerHoje(tarefa, hoje)) {
                    tarefasHoje.add(new TarefaHojeView(nomeCategoria, tarefa));
                }
            }
        }

        tableModel.atualizar(tarefasHoje);

        titulo.setText("HOJE - " + hoje.format(FORMATADOR_DATA));
        resumo.setText("Tarefas para hoje: " + tarefasHoje.size());
    }

    private boolean deveAparecerHoje(Tarefa tarefa, LocalDate hoje) {
        if (tarefa == null) {
            return false;
        }

        if (StatusTarefa.DONE.equals(tarefa.getStatus())) {
            return false;
        }

        TipoTarefa tipo = tarefa.getTipo();

        if (tipo == null) {
            return false;
        }

        return switch (tipo) {
            case DIARIA -> true;
            case SEMANAL -> tarefaSemanalEhHoje(tarefa, hoje);
            case MENSAL -> tarefaMensalEhHoje(tarefa, hoje);
            case PROGRAMADA -> tarefaProgramadaEhHojeOuAtrasada(tarefa, hoje);
        };
    }

    private boolean tarefaSemanalEhHoje(Tarefa tarefa, LocalDate hoje) {
        DiaSemana diaDaTarefa = DiaSemana.fromTexto(tarefa.getDataReferencia());
        DiaSemana diaDeHoje = DiaSemana.fromDayOfWeek(hoje.getDayOfWeek());

        return diaDaTarefa != null && diaDaTarefa.equals(diaDeHoje);
    }

    private boolean tarefaMensalEhHoje(Tarefa tarefa, LocalDate hoje) {
        String dataReferencia = tarefa.getDataReferencia();

        if (dataReferencia == null || dataReferencia.isBlank()) {
            return false;
        }

        try {
            int diaDaTarefa = Integer.parseInt(dataReferencia.trim());
            return diaDaTarefa == hoje.getDayOfMonth();
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean tarefaProgramadaEhHojeOuAtrasada(Tarefa tarefa, LocalDate hoje) {
        String dataReferencia = tarefa.getDataReferencia();

        if (dataReferencia == null || dataReferencia.isBlank()) {
            return false;
        }

        try {
            LocalDate dataDaTarefa = LocalDate.parse(dataReferencia.trim(), FORMATADOR_DATA);
            return !dataDaTarefa.isAfter(hoje);
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    private static class HojeTableModel extends AbstractTableModel {

        private final String[] colunas = {
                "Categoria",
                "Status",
                "Tipo",
                "Data",
                "Tarefa",
                "Observação"
        };

        private final List<TarefaHojeView> tarefas = new ArrayList<>();

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
            if (columnIndex == 1) {
                return StatusTarefa.class;
            }

            if (columnIndex == 2) {
                return TipoTarefa.class;
            }

            return String.class;
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return false;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            TarefaHojeView view = tarefas.get(rowIndex);
            Tarefa tarefa = view.tarefa();

            return switch (columnIndex) {
                case 0 -> view.categoria();
                case 1 -> tarefa.getStatus();
                case 2 -> tarefa.getTipo();
                case 3 -> tarefa.getDataReferencia();
                case 4 -> tarefa.getDescricao();
                case 5 -> tarefa.getObservacao();
                default -> "";
            };
        }

        public TarefaHojeView getView(int rowIndex) {
            return tarefas.get(rowIndex);
        }

        public void atualizar(List<TarefaHojeView> novasTarefas) {
            tarefas.clear();
            tarefas.addAll(novasTarefas);
            fireTableDataChanged();
        }
    }

    private record TarefaHojeView(String categoria, Tarefa tarefa) {
    }

    private class HojeTableCellRenderer extends DefaultTableCellRenderer {

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

            if (value instanceof StatusTarefa status) {
                setText(status.name());
            } else if (value instanceof TipoTarefa tipo) {
                setText(tipo.name());
            } else {
                setText(value == null ? "" : String.valueOf(value));
            }

            if (!isSelected) {
                int modelIndex = table.convertRowIndexToModel(row);
                StatusTarefa status = tableModel.getView(modelIndex).tarefa().getStatus();

                component.setBackground(status.getCor());
                component.setForeground(Color.BLACK);
            }

            if (isSelected) {
                component.setBackground(table.getSelectionBackground());
                component.setForeground(table.getSelectionForeground());
            }

            if (column == 1 || column == 2 || column == 3) {
                setHorizontalAlignment(SwingConstants.CENTER);
            } else {
                setHorizontalAlignment(SwingConstants.LEFT);
            }

            return component;
        }
    }
}