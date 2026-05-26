package br.com.checklist;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ChecklistFrame extends JFrame {

    private final TarefaRepository repository = new TarefaRepository();

    private final JTabbedPane abas = new JTabbedPane();

    public ChecklistFrame() {
        configurarJanela();
        configurarComponentes();
        carregarCategorias();
    }

    private void configurarJanela() {
        setTitle("Checklist de Tarefas");
        setSize(760, 580);
        setMinimumSize(new Dimension(600, 460));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    private void configurarComponentes() {
        setLayout(new BorderLayout(10, 10));

        JPanel painelTopo = criarPainelTopo();
        JPanel painelAcoes = criarPainelAcoes();

        add(painelTopo, BorderLayout.NORTH);
        add(abas, BorderLayout.CENTER);
        add(painelAcoes, BorderLayout.SOUTH);
    }

    private JPanel criarPainelTopo() {
        JPanel painel = new JPanel(new BorderLayout());
        painel.setBorder(BorderFactory.createEmptyBorder(15, 15, 5, 15));

        JLabel titulo = new JLabel("Checklist de Tarefas", SwingConstants.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 24));

        painel.add(titulo, BorderLayout.CENTER);

        return painel;
    }

    private JPanel criarPainelAcoes() {
        JPanel painelPrincipal = new JPanel(new BorderLayout(10, 10));
        painelPrincipal.setBorder(BorderFactory.createEmptyBorder(5, 15, 15, 15));

        JPanel painelAbas = new JPanel();
        JPanel painelOrdemAbas = new JPanel();

        JButton botaoNovaAba = new JButton("Nova aba");
        JButton botaoRenomearAba = new JButton("Renomear aba");
        JButton botaoExcluirAba = new JButton("Excluir aba atual");

        JButton botaoAbaEsquerda = new JButton("Aba para esquerda");
        JButton botaoAbaDireita = new JButton("Aba para direita");

        botaoNovaAba.addActionListener(e -> criarNovaAba());
        botaoRenomearAba.addActionListener(e -> renomearAbaAtual());
        botaoExcluirAba.addActionListener(e -> excluirAbaAtual());

        botaoAbaEsquerda.addActionListener(e -> moverAbaParaEsquerda());
        botaoAbaDireita.addActionListener(e -> moverAbaParaDireita());

        painelAbas.add(botaoNovaAba);
        painelAbas.add(botaoRenomearAba);
        painelAbas.add(botaoExcluirAba);

        painelOrdemAbas.add(botaoAbaEsquerda);
        painelOrdemAbas.add(botaoAbaDireita);

        painelPrincipal.add(painelAbas, BorderLayout.NORTH);
        painelPrincipal.add(painelOrdemAbas, BorderLayout.SOUTH);

        return painelPrincipal;
    }

    private void carregarCategorias() {
        Map<String, List<Tarefa>> categorias = repository.carregar();

        for (Map.Entry<String, List<Tarefa>> categoria : categorias.entrySet()) {
            adicionarAba(categoria.getKey(), categoria.getValue());
        }
    }

    private void adicionarAba(String nomeCategoria, List<Tarefa> tarefas) {
        CategoriaChecklistPanel painel = new CategoriaChecklistPanel(
                tarefas,
                this::salvarCategorias
        );

        abas.addTab(nomeCategoria, painel);
    }

    private void criarNovaAba() {
        String nome = JOptionPane.showInputDialog(
                this,
                "Digite o nome da nova aba:",
                "Nova aba",
                JOptionPane.PLAIN_MESSAGE
        );

        if (nome == null) {
            return;
        }

        nome = nome.trim();

        if (nome.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "O nome da aba não pode estar vazio.",
                    "Atenção",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        if (existeAba(nome)) {
            JOptionPane.showMessageDialog(
                    this,
                    "Já existe uma aba com esse nome.",
                    "Atenção",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        adicionarAba(nome, List.of());
        abas.setSelectedIndex(abas.getTabCount() - 1);

        salvarCategorias();
    }

    private void renomearAbaAtual() {
        int index = abas.getSelectedIndex();

        if (index < 0) {
            return;
        }

        String nomeAtual = abas.getTitleAt(index);

        String novoNome = JOptionPane.showInputDialog(
                this,
                "Digite o novo nome da aba:",
                nomeAtual
        );

        if (novoNome == null) {
            return;
        }

        novoNome = novoNome.trim();

        if (novoNome.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "O nome da aba não pode estar vazio.",
                    "Atenção",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        if (!novoNome.equalsIgnoreCase(nomeAtual) && existeAba(novoNome)) {
            JOptionPane.showMessageDialog(
                    this,
                    "Já existe uma aba com esse nome.",
                    "Atenção",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        abas.setTitleAt(index, novoNome);
        salvarCategorias();
    }

    private void excluirAbaAtual() {
        int index = abas.getSelectedIndex();

        if (index < 0) {
            return;
        }

        if (abas.getTabCount() == 1) {
            JOptionPane.showMessageDialog(
                    this,
                    "Você precisa manter pelo menos uma aba.",
                    "Atenção",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        String nomeAba = abas.getTitleAt(index);

        int resposta = JOptionPane.showConfirmDialog(
                this,
                "Deseja excluir a aba \"" + nomeAba + "\"?",
                "Confirmar exclusão",
                JOptionPane.YES_NO_OPTION
        );

        if (resposta != JOptionPane.YES_OPTION) {
            return;
        }

        abas.removeTabAt(index);
        salvarCategorias();
    }

    private void moverAbaParaEsquerda() {
        int index = abas.getSelectedIndex();

        if (index <= 0) {
            return;
        }

        moverAba(index, index - 1);
    }

    private void moverAbaParaDireita() {
        int index = abas.getSelectedIndex();

        if (index < 0 || index >= abas.getTabCount() - 1) {
            return;
        }

        moverAba(index, index + 1);
    }

    private void moverAba(int origem, int destino) {
        String titulo = abas.getTitleAt(origem);
        java.awt.Component componente = abas.getComponentAt(origem);

        String tooltip = abas.getToolTipTextAt(origem);
        javax.swing.Icon icone = abas.getIconAt(origem);

        abas.removeTabAt(origem);
        abas.insertTab(titulo, icone, componente, tooltip, destino);
        abas.setSelectedIndex(destino);

        salvarCategorias();
    }

    private boolean existeAba(String nome) {
        for (int i = 0; i < abas.getTabCount(); i++) {
            if (abas.getTitleAt(i).equalsIgnoreCase(nome)) {
                return true;
            }
        }

        return false;
    }

    private void salvarCategorias() {
        Map<String, List<Tarefa>> categorias = new LinkedHashMap<>();

        for (int i = 0; i < abas.getTabCount(); i++) {
            String nomeCategoria = abas.getTitleAt(i);
            CategoriaChecklistPanel painel = (CategoriaChecklistPanel) abas.getComponentAt(i);

            categorias.put(nomeCategoria, painel.getTarefas());
        }

        repository.salvar(categorias);
    }
}