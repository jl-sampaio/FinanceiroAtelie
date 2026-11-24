import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class NovoUsuarioDialog {

    /** Pede nome e senha e cria o usuário. Retorna true se criou. */
    public static boolean mostrar(Component pai) {
        JTextField txtNome = new JTextField(16);
        JPasswordField txtSenha = new JPasswordField(16);
        JPasswordField txtConfirma = new JPasswordField(16);

        JPanel p = new JPanel(new GridLayout(0, 1, 4, 4));
        p.add(new JLabel("Nome do usuário:"));
        p.add(txtNome);
        p.add(new JLabel("Senha (mínimo 4 caracteres):"));
        p.add(txtSenha);
        p.add(new JLabel("Repita a senha:"));
        p.add(txtConfirma);

        while (true) {
            int r = JOptionPane.showConfirmDialog(pai, p, "Novo usuário",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (r != JOptionPane.OK_OPTION) return false;

            String nome = txtNome.getText().trim();
            String senha = new String(txtSenha.getPassword());
            String confirma = new String(txtConfirma.getPassword());

            if (nome.isEmpty()) { erro(pai, "Informe o nome do usuário."); continue; }
            if (senha.length() < 4) { erro(pai, "A senha deve ter pelo menos 4 caracteres."); continue; }
            if (!senha.equals(confirma)) { erro(pai, "As senhas não conferem."); continue; }

            try {
                if (new UsuarioDAO().criar(nome, senha)) {
                    JOptionPane.showMessageDialog(pai, "Usuário \"" + nome.toLowerCase() + "\" criado!");
                    return true;
                }
                erro(pai, "Já existe um usuário com esse nome.");
            } catch (SQLException e) {
                erro(pai, "Erro no banco de dados:\n" + e.getMessage());
                return false;
            }
        }
    }

    private static void erro(Component pai, String msg) {
        JOptionPane.showMessageDialog(pai, msg, "Atenção", JOptionPane.WARNING_MESSAGE);
    }
}
