package dev.gabbriellps.reader.file.nubank.api.util;


import org.apache.logging.log4j.util.Strings;

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

}
