package dev.gabbriellps.reader.file.nubank.api.util;


import org.apache.logging.log4j.util.Strings;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public abstract class Util {


    public static String formatarDataBRL(LocalDateTime dateTime) {
        if (Objects.isNull(dateTime))
            return "";
        return dateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    public static String formatarDataBRL(LocalDate data){
        if(Objects.isNull(data)) return Strings.EMPTY;

        return data.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    public static String formatarDataHoraBRL(LocalDateTime dateTime) {
        if (Objects.isNull(dateTime))
            return "";
        return dateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
    }

    public static String formatarValorBRLString(BigDecimal valor) {
        if (Objects.isNull(valor)) {
            return "R$ 0,00";
        }
        String valorFormatado = valor.setScale(2, RoundingMode.HALF_UP).toString().replace(".", ",");
        return "R$ " + valorFormatado;
    }

    public static BigDecimal formatarValor(BigDecimal valor) {
        if (Objects.isNull(valor)) {
            return BigDecimal.ZERO;
        }
        return valor.setScale(2, RoundingMode.HALF_UP);
    }

}
