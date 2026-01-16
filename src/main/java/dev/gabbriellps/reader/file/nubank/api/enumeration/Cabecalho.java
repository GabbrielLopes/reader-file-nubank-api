package dev.gabbriellps.reader.file.nubank.api.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum Cabecalho {

    DATA(0),
    TITULO(1),
    VALOR(2);

    @Getter
    private final int index;

}
