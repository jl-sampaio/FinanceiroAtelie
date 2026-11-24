import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class LancamentoDialog extends JDialog {

    private static final String[] CAT_ENTRADA = {
        "Serviço de maquiagem", "Maquiagem de noiva", "Curso / Workshop",
        "Venda de produtos", "Outros"
    };
    private static final String[] CAT_SAIDA = {
        "Compra de produtos", "Materiais", "Aluguel", "Contas (luz/água/internet)",
        "Marketing", "Impostos", "Outros"
    };

    private final String tipo;
    private final String usuario;
    private final JComboBox<String> cmbCategoria;
    private final JTextField txtDescricao = new JTextField(22);
    private final JTextField txtValor = new JTextField(10);
    private final JTextField txtData = new JTextField(LocalDate.now().format(Util.DATA), 10);
    private Lancamento resultado;

    public LancamentoDialog(Frame dono, String tipo, String usuario) {
        super(dono, tipo.equals(Lancamento.ENTRADA) ? "Nova entrada" : "Nova saída", true);
        this.tipo = tipo;
        this.usuario = usuario;

        cmbCategoria = new JComboBox<>(tipo.equals(Lancamento.ENTRADA) ? CAT_ENTRADA : CAT_SAIDA);
        cmbCategoria.setEditable(true);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(14, 16, 6, 16));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(5, 5, 5, 5);
        g.anchor = GridBagConstraints.WEST;
        g.fill = GridBagConstraints.HORIZONTAL;

        linha(form, g, 0, "Categoria:", cmbCategoria);
        linha(form, g, 1, "Descrição:", txtDescricao);
        linha(form, g, 2, "Valor (R$):", txtValor);
        linha(form, g, 3, "Data (dd/mm/aaaa):", txtData);

        JButton btnOk = new JButton("Salvar");
        JButton btnCancelar = new JButton("Cancelar");
        btnOk.addActionListener(e -> salvar());
        btnCancelar.addActionListener(e -> dispose());

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        botoes.add(btnCancelar);
        botoes.add(btnOk);

        setLayout(new BorderLayout());
        add(form, BorderLayout.CENTER);
        add(botoes, BorderLayout.SOUTH);
        getRootPane().setDefaultButton(btnOk);
        pack();
        setLocationRelativeTo(dono);
    }

    private void linha(JPanel p, GridBagConstraints g, int y, String rotulo, JComponent campo) {
        g.gridy = y; g.gridx = 0; g.weightx = 0;
        p.add(new JLabel(rotulo), g);
        g.gridx = 1; g.weightx = 1;
        p.add(campo, g);
    }

    private void salvar() {
        String categoria = String.valueOf(cmbCategoria.getEditor().getItem()).trim();
        if (categoria.isEmpty()) { aviso("Informe a categoria."); return; }

        BigDecimal valor;
        try {
            valor = Util.parseValor(txtValor.getText());
        } catch (NumberFormatException e) {
            aviso("Valor inválido. Exemplos: 50 / 50,90 / 1.250,00");
            return;
        }

        LocalDate data;
        try {
            data = LocalDate.parse(txtData.getText().trim(), Util.DATA);
        } catch (DateTimeParseException e) {
            aviso("Data inválida. Use o formato dd/mm/aaaa.");
            return;
        }

        resultado = new Lancamento(tipo, categoria, txtDescricao.getText().trim(), valor, data, usuario);
        dispose();
    }

    private void aviso(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Atenção", JOptionPane.WARNING_MESSAGE);
    }

    /** Mostra o diálogo e devolve o lançamento preenchido, ou null se cancelado. */
    public Lancamento mostrar() {
        setVisible(true);
        return resultado;
    }
}
