import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.*;
import java.util.HexFormat;

public class UsuarioDAO {

    public boolean existeAlgum() throws SQLException {
        try (Connection conn = Database.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM usuarios")) {
            rs.next();
            return rs.getInt(1) > 0;
        }
    }

    /** Retorna false se o nome já existe. */
    public boolean criar(String nome, String senha) throws SQLException {
        nome = nome.trim().toLowerCase();
        byte[] bytes = new byte[16];
        new SecureRandom().nextBytes(bytes);
        String salt = HexFormat.of().formatHex(bytes);

        String sql = "INSERT INTO usuarios (nome, salt, senha_hash) VALUES (?, ?, ?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nome);
            ps.setString(2, salt);
            ps.setString(3, hash(salt, senha));
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            if (jaExiste(nome)) return false;
            throw e;
        }
    }

    public boolean autenticar(String nome, String senha) throws SQLException {
        String sql = "SELECT salt, senha_hash FROM usuarios WHERE nome = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nome.trim().toLowerCase());
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return false;
                return hash(rs.getString("salt"), senha).equals(rs.getString("senha_hash"));
            }
        }
    }

    private boolean jaExiste(String nome) throws SQLException {
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT 1 FROM usuarios WHERE nome = ?")) {
            ps.setString(1, nome);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    private static String hash(String salt, String senha) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] h = md.digest((salt + senha).getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(h);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}
