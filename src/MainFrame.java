import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.io.*;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class MainFrame extends JFrame {

    private static final Color ROSA       = new Color(180, 50, 100);
    private static final Color ROSA_CLARO = new Color(220, 100, 140);
    private static final Color FUNDO      = new Color(255, 245, 250);
    private static final Color VERDE      = new Color(40, 150, 90);
    private static final Color VERMELHO   = new Color(200, 60, 60);

    private final String usuario;
    private final FinanceiroDAO dao = new FinanceiroDAO();

    private final JTextField txtDe = new JTextField(8);
    private final JTextField txtAte = new JTextField(8);
    private final JComboBox<String> cmbTipo = new JComboBox<>(new String[]{"Todos", "Entradas", "Saídas"});
    private final JLabel lblEntradas = new JLabel("R$ 0,00");
    private final JLabel lblSaidas = new JLabel("R$ 0,00");
    private final JLabel lblSaldo = new JLabel("R$ 0,00");

    private DefaultTableModel modelo;
    private JTable tabela;
    private List<Lancamento> atual = new ArrayList<>();

    public MainFrame(String usuario) {
        this.usuario = usuario;

        setTitle("Ateliê — Controle Financeiro  |  usuário: " + usuario);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1000, 620);
        setMinimumSize(new Dimension(880, 520));
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(FUNDO);
        setContentPane(root);

        JPanel topo = new JPanel(new BorderLayout());
        topo.add(criarHeader(), BorderLayout.NORTH);

        JPanel meio = new JPanel(new BorderLayout());
        meio.setBackground(FUNDO);
        meio.add(criarFiltros(), BorderLayout.NORTH);
        meio.add(criarResumo(), BorderLayout.CENTER);
        topo.add(meio, BorderLayout.CENTER);

        root.add(topo, BorderLayout.NORTH);
        root.add(criarTabela(), BorderLayout.CENTER);
        root.add(criarBotoes(), BorderLayout.SOUTH);

        definirMesAtual();
        atualizar();
    }

    // ===== Construção da tela =====

    private JPanel criarHeader() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(ROSA);
        p.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));

        JLabel t = new JLabel("Ateliê de Maquiagem — Controle Financeiro");
        t.setFont(new Font("Segoe UI", Font.BOLD, 17));
        t.setForeground(Color.WHITE);

        JLabel u = new JLabel("Logado como: " + usuario);
        u.setForeground(new Color(255, 210, 230));

        p.add(t, BorderLayout.WEST);
        p.add(u, BorderLayout.EAST);
        return p;
    }

    private JPanel criarFiltros() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        p.setBackground(FUNDO);

        JButton btnFiltrar = botao("Filtrar", ROSA);
        JButton btnMes = botao("Este mês", ROSA_CLARO);

        p.add(new JLabel("De:"));
        p.add(txtDe);
        p.add(new JLabel("Até:"));
        p.add(txtAte);
        p.add(new JLabel("Tipo:"));
        p.add(cmbTipo);
        p.add(btnFiltrar);
        p.add(btnMes);

        btnFiltrar.addActionListener(e -> atualizar());
        btnMes.addActionListener(e -> { definirMesAtual(); atualizar(); });
        txtDe.addActionListener(e -> atualizar());
        txtAte.addActionListener(e -> atualizar());
        return p;
    }

    private JPanel criarResumo() {
        JPanel p = new JPanel(new GridLayout(1, 3, 12, 0));
        p.setBackground(FUNDO);
        p.setBorder(BorderFactory.createEmptyBorder(0, 10, 8, 10));
        p.add(card("Entradas", lblEntradas, VERDE));
        p.add(card("Saídas", lblSaidas, VERMELHO));
        p.add(card("Saldo do período", lblSaldo, ROSA));
        return p;
    }

    private JPanel card(String titulo, JLabel valor, Color cor) {
        JPanel c = new JPanel(new GridLayout(2, 1));
        c.setBackground(Color.WHITE);
        c.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 5, 0, 0, cor),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)));

        JLabel t = new JLabel(titulo);
        t.setForeground(Color.GRAY);
        valor.setFont(new Font("Segoe UI", Font.BOLD, 22));
        valor.setForeground(cor);

        c.add(t);
        c.add(valor);
        return c;
    }

    private JScrollPane criarTabela() {
        String[] colunas = {"ID", "Data", "Tipo", "Categoria", "Descrição", "Valor", "Usuário", "Registrado em"};
        modelo = new DefaultTableModel(colunas, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tabela = new JTable(modelo);
        tabela.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabela.setRowHeight(26);
        tabela.setGridColor(new Color(230, 200, 215));
        tabela.setSelectionBackground(new Color(255, 180, 210));
        tabela.setSelectionForeground(Color.BLACK);
        tabela.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tabela.getTableHeader().setBackground(ROSA_CLARO);
        tabela.getTableHeader().setForeground(Color.WHITE);

        tabela.getColumnModel().getColumn(0).setMaxWidth(60);
        tabela.getColumnModel().getColumn(1).setMaxWidth(90);
        tabela.getColumnModel().getColumn(2).setMaxWidth(80);
        tabela.getColumnModel().getColumn(3).setPreferredWidth(150);
        tabela.getColumnModel().getColumn(4).setPreferredWidth(200);
        tabela.getColumnModel().getColumn(5).setPreferredWidth(110);
        tabela.getColumnModel().getColumn(6).setPreferredWidth(80);
        tabela.getColumnModel().getColumn(7).setPreferredWidth(120);

        // Colore Tipo e Valor: verde para entrada, vermelho para saída
        DefaultTableCellRenderer cor = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean sel,
                                                           boolean foc, int r, int c) {
                super.getTableCellRendererComponent(t, v, sel, foc, r, c);
                boolean entrada = "Entrada".equals(t.getValueAt(r, 2));
                if (!sel) setForeground(entrada ? VERDE : VERMELHO);
                setFont(getFont().deriveFont(Font.BOLD));
                setHorizontalAlignment(c == 5 ? RIGHT : CENTER);
                return this;
            }
        };
        tabela.getColumnModel().getColumn(2).setCellRenderer(cor);
        tabela.getColumnModel().getColumn(5).setCellRenderer(cor);

        JScrollPane sp = new JScrollPane(tabela);
        sp.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        sp.getViewport().setBackground(Color.WHITE);
        return sp;
    }

    private JPanel criarBotoes() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 10));
        p.setBackground(FUNDO);

        JButton btnEntrada = botao("+ Nova entrada", VERDE);
        JButton btnSaida = botao("- Nova saída", VERMELHO);
        JButton btnExcluir = botao("Excluir", new Color(130, 130, 130));
        JButton btnCsv = botao("Exportar CSV", ROSA_CLARO);
        JButton btnUsuario = botao("Novo usuário", ROSA_CLARO);

        p.add(btnEntrada);
        p.add(btnSaida);
        p.add(btnExcluir);
        p.add(btnCsv);
        p.add(btnUsuario);

        btnEntrada.addActionListener(e -> novoLancamento(Lancamento.ENTRADA));
        btnSaida.addActionListener(e -> novoLancamento(Lancamento.SAIDA));
        btnExcluir.addActionListener(e -> excluir());
        btnCsv.addActionListener(e -> exportarCsv());
        btnUsuario.addActionListener(e -> NovoUsuarioDialog.mostrar(this));
        return p;
    }

    // ===== Ações =====

    private void definirMesAtual() {
        LocalDate hoje = LocalDate.now();
        txtDe.setText(hoje.withDayOfMonth(1).format(Util.DATA));
        txtAte.setText(hoje.withDayOfMonth(hoje.lengthOfMonth()).format(Util.DATA));
        cmbTipo.setSelectedIndex(0);
    }

    private void atualizar() {
        LocalDate de, ate;
        try {
            de = LocalDate.parse(txtDe.getText().trim(), Util.DATA);
            ate = LocalDate.parse(txtAte.getText().trim(), Util.DATA);
        } catch (DateTimeParseException e) {
            aviso("Datas inválidas. Use o formato dd/mm/aaaa.");
            return;
        }
        if (ate.isBefore(de)) {
            aviso("A data final não pode ser anterior à data inicial.");
            return;
        }

        String tipo = switch (cmbTipo.getSelectedIndex()) {
            case 1 -> Lancamento.ENTRADA;
            case 2 -> Lancamento.SAIDA;
            default -> null;
        };

        try {
            atual = dao.listar(de, ate, tipo);
            preencher();
        } catch (SQLException e) {
            erro(e);
        }
    }

    private void preencher() {
        modelo.setRowCount(0);
        BigDecimal entradas = BigDecimal.ZERO;
        BigDecimal saidas = BigDecimal.ZERO;

        for (Lancamento l : atual) {
            if (l.isEntrada()) entradas = entradas.add(l.getValor());
            else saidas = saidas.add(l.getValor());

            modelo.addRow(new Object[]{
                l.getId(),
                l.getData().format(Util.DATA),
                l.isEntrada() ? "Entrada" : "Saída",
                l.getCategoria(),
                l.getDescricao(),
                (l.isEntrada() ? "+ " : "- ") + Util.moeda(l.getValor()),
                l.getUsuario(),
                l.getCriadoEm().format(Util.DATA_HORA)
            });
        }

        BigDecimal saldo = entradas.subtract(saidas);
        lblEntradas.setText(Util.moeda(entradas));
        lblSaidas.setText(Util.moeda(saidas));
        lblSaldo.setText(Util.moeda(saldo));
        lblSaldo.setForeground(saldo.signum() >= 0 ? VERDE : VERMELHO);
    }

    private void novoLancamento(String tipo) {
        Lancamento l = new LancamentoDialog(this, tipo, usuario).mostrar();
        if (l == null) return;
        try {
            dao.inserir(l);
            atualizar();
        } catch (SQLException e) {
            erro(e);
        }
    }

    private void excluir() {
        int linha = tabela.getSelectedRow();
        if (linha < 0) { aviso("Selecione um lançamento na tabela."); return; }

        Long id = (Long) modelo.getValueAt(linha, 0);
        String desc = modelo.getValueAt(linha, 3) + " — " + modelo.getValueAt(linha, 5);
        int r = JOptionPane.showConfirmDialog(this,
            "Excluir o lançamento?\n" + desc,
            "Confirmar exclusão", JOptionPane.YES_NO_OPTION);
        if (r != JOptionPane.YES_OPTION) return;

        try {
            dao.excluir(id);
            atualizar();
        } catch (SQLException e) {
            erro(e);
        }
    }

    private void exportarCsv() {
        if (atual.isEmpty()) { aviso("Não há lançamentos para exportar."); return; }

        JFileChooser fc = new JFileChooser();
        fc.setSelectedFile(new File("financeiro_" + LocalDate.now() + ".csv"));
        if (fc.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;

        try (BufferedWriter w = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(fc.getSelectedFile()), StandardCharsets.UTF_8))) {
            w.write('\uFEFF'); // BOM para o Excel reconhecer acentos
            w.write("Data;Tipo;Categoria;Descrição;Valor;Usuário\r\n");
            for (Lancamento l : atual) {
                w.write(String.join(";",
                    l.getData().format(Util.DATA),
                    l.isEntrada() ? "Entrada" : "Saída",
                    csv(l.getCategoria()),
                    csv(l.getDescricao()),
                    l.getValor().toPlainString().replace('.', ','),
                    csv(l.getUsuario())));
                w.write("\r\n");
            }
            JOptionPane.showMessageDialog(this, "Arquivo exportado com sucesso!");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Erro ao salvar o arquivo:\n" + e.getMessage(),
                "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static String csv(String s) {
        if (s == null) return "";
        // evita que o Excel interprete o texto como fórmula
        if (!s.isEmpty() && "=+-@".indexOf(s.charAt(0)) >= 0) s = "'" + s;
        return "\"" + s.replace("\"", "\"\"") + "\"";
    }

    // ===== Helpers =====

    private JButton botao(String texto, Color cor) {
        JButton b = new JButton(texto);
        b.setBackground(cor);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setFont(new Font("Segoe UI", Font.BOLD, 12));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setBorder(BorderFactory.createEmptyBorder(7, 14, 7, 14));
        return b;
    }

    private void aviso(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Atenção", JOptionPane.WARNING_MESSAGE);
    }

    private void erro(SQLException e) {
        JOptionPane.showMessageDialog(this, "Erro no banco de dados:\n" + e.getMessage(),
            "Erro", JOptionPane.ERROR_MESSAGE);
    }
}
