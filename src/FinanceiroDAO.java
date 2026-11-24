import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class FinanceiroDAO {

    public void inserir(Lancamento l) throws SQLException {
        String sql = "INSERT INTO lancamentos (tipo, categoria, descricao, valor, data_lanc, usuario) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, l.getTipo());
            ps.setString(2, l.getCategoria());
            ps.setString(3, l.getDescricao());
            ps.setBigDecimal(4, l.getValor());
            ps.setDate(5, Date.valueOf(l.getData()));
            ps.setString(6, l.getUsuario());
            ps.executeUpdate();
        }
    }

    public void excluir(long id) throws SQLException {
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM lancamentos WHERE id = ?")) {
            ps.setLong(1, id);
            ps.executeUpdate();
        }
    }

    /** Lista lançamentos no período (inclusive). Se tipo for null, traz entradas e saídas. */
    public List<Lancamento> listar(LocalDate de, LocalDate ate, String tipo) throws SQLException {
        StringBuilder sql = new StringBuilder(
            "SELECT id, tipo, categoria, descricao, valor, data_lanc, usuario, criado_em " +
            "FROM lancamentos WHERE data_lanc BETWEEN ? AND ?");
        if (tipo != null) sql.append(" AND tipo = ?");
        sql.append(" ORDER BY data_lanc DESC, id DESC");

        List<Lancamento> lista = new ArrayList<>();
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            ps.setDate(1, Date.valueOf(de));
            ps.setDate(2, Date.valueOf(ate));
            if (tipo != null) ps.setString(3, tipo);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new Lancamento(
                        rs.getLong("id"),
                        rs.getString("tipo"),
                        rs.getString("categoria"),
                        rs.getString("descricao"),
                        rs.getBigDecimal("valor"),
                        rs.getDate("data_lanc").toLocalDate(),
                        rs.getString("usuario"),
                        rs.getTimestamp("criado_em").toLocalDateTime()));
                }
            }
        }
        return lista;
    }
}
