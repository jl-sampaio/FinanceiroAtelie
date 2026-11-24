import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class Lancamento {
    public static final String ENTRADA = "ENTRADA";
    public static final String SAIDA = "SAIDA";

    private final long id;
    private final String tipo;
    private final String categoria;
    private final String descricao;
    private final BigDecimal valor;
    private final LocalDate data;
    private final String usuario;
    private final LocalDateTime criadoEm;

    public Lancamento(long id, String tipo, String categoria, String descricao,
                      BigDecimal valor, LocalDate data, String usuario, LocalDateTime criadoEm) {
        this.id = id;
        this.tipo = tipo;
        this.categoria = categoria;
        this.descricao = descricao;
        this.valor = valor;
        this.data = data;
        this.usuario = usuario;
        this.criadoEm = criadoEm;
    }

    // Construtor para lançamentos novos (id e data de registro são gerados pelo banco)
    public Lancamento(String tipo, String categoria, String descricao,
                      BigDecimal valor, LocalDate data, String usuario) {
        this(0, tipo, categoria, descricao, valor, data, usuario, null);
    }

    public long getId()                { return id; }
    public String getTipo()            { return tipo; }
    public String getCategoria()       { return categoria; }
    public String getDescricao()       { return descricao; }
    public BigDecimal getValor()       { return valor; }
    public LocalDate getData()         { return data; }
    public String getUsuario()         { return usuario; }
    public LocalDateTime getCriadoEm() { return criadoEm; }
    public boolean isEntrada()         { return ENTRADA.equals(tipo); }
}
