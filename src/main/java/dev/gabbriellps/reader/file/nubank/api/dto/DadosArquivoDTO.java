package dev.gabbriellps.reader.file.nubank.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DadosArquivoDTO {

    private LocalDate data;
//    private String categoria;
    private String titulo;
    private Double valor;
    private String nome;


}
