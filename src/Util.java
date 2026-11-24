import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.Locale;

public class Util {

    public static final DateTimeFormatter DATA =
        DateTimeFormatter.ofPattern("dd/MM/uuuu").withResolverStyle(ResolverStyle.STRICT);
    public static final DateTimeFormatter DATA_HORA =
        DateTimeFormatter.ofPattern("dd/MM/uuuu HH:mm");

    private static final Locale PT_BR = Locale.forLanguageTag("pt-BR");
    private static final BigDecimal MAXIMO = new BigDecimal("9999999999.99");

    public static String moeda(BigDecimal v) {
        return NumberFormat.getCurrencyInstance(PT_BR).format(v);
    }

    /** Aceita "50", "50,5", "50.5", "1.234,56" e "R$ 1.234,56". Lança NumberFormatException se inválido. */
    public static BigDecimal parseValor(String txt) {
        String s = txt == null ? "" : txt.trim().replace("R$", "").replace(" ", "");
        if (s.isEmpty()) throw new NumberFormatException("vazio");

        if (s.contains(",")) {
            s = s.replace(".", "").replace(",", ".");
        } else if (s.matches("\\d{1,3}(\\.\\d{3})+")) {
            s = s.replace(".", ""); // 1.234 = mil duzentos e trinta e quatro
        }

        BigDecimal v = new BigDecimal(s).setScale(2, RoundingMode.HALF_UP);
        if (v.signum() <= 0) throw new NumberFormatException("O valor deve ser maior que zero");
        if (v.compareTo(MAXIMO) > 0) throw new NumberFormatException("Valor muito alto");
        return v;
    }
}
