import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class LoginFrame extends JFrame {

    private final JTextField txtUsuario = new JTextField(16);
    private final JPasswordField txtSenha = new JPasswordField(16);
    private final UsuarioDAO dao = new UsuarioDAO();

    public LoginFrame() {
        setTitle("Ateliê de Maquiagem — Financeiro");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel fundo = new JPanel(new GridBagLayout());
        fundo.setBackground(new Color(255, 240, 245));
        setContentPane(fundo);

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(6, 10, 6, 10);
        g.fill = GridBagConstraints.HORIZONTAL;

        JLabel titulo = new JLabel("Controle Financeiro", SwingConstants.CENTER);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titulo.setForeground(new Color(180, 50, 100));
        g.gridx = 0; g.gridy = 0; g.gridwidth = 2;
        fundo.add(titulo, g);

        boolean primeiroAcesso = false;
        try {
            primeiroAcesso = !dao.existeAlgum();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro no banco de dados:\n" + e.getMessage(),
                "Erro", JOptionPane.ERROR_MESSAGE);
        }

        if (primeiroAcesso) {
            JLabel aviso = new JLabel("<html><center>Primeiro acesso!<br>Crie o usuário administrador para começar.</center></html>",
                SwingConstants.CENTER);
            aviso.setForeground(Color.DARK_GRAY);
            g.gridy = 1;
            fundo.add(aviso, g);

            JButton btnCriar = botao("Criar usuário", new Color(200, 60, 100));
            g.gridy = 2;
            g.insets = new Insets(20, 10, 6, 10);
            fundo.add(btnCriar, g);
            btnCriar.addActionListener(e -> {
                if (NovoUsuarioDialog.mostrar(this)) {
                    dispose();
                    new LoginFrame().setVisible(true);
                }
            });
            return;
        }

        g.gridy = 1; g.gridwidth = 1; g.gridx = 0;
        fundo.add(new JLabel("Usuário:"), g);
        g.gridx = 1;
        fundo.add(txtUsuario, g);

        g.gridy = 2; g.gridx = 0;
        fundo.add(new JLabel("Senha:"), g);
        g.gridx = 1;
        fundo.add(txtSenha, g);

        JButton btnEntrar = botao("Entrar", new Color(200, 60, 100));
        g.gridy = 3; g.gridx = 0; g.gridwidth = 2;
        g.insets = new Insets(16, 10, 6, 10);
        fundo.add(btnEntrar, g);

        btnEntrar.addActionListener(e -> entrar());
        txtSenha.addActionListener(e -> entrar());
    }

    private void entrar() {
        String nome = txtUsuario.getText().trim();
        String senha = new String(txtSenha.getPassword());
        try {
            if (dao.autenticar(nome, senha)) {
                dispose();
                new MainFrame(nome.toLowerCase()).setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Usuário ou senha incorretos.",
                    "Acesso negado", JOptionPane.WARNING_MESSAGE);
                txtSenha.setText("");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro no banco de dados:\n" + e.getMessage(),
                "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JButton botao(String texto, Color cor) {
        JButton b = new JButton(texto);
        b.setBackground(cor);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setFont(new Font("Segoe UI", Font.BOLD, 13));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        return b;
    }
}
