package dev.gabbriellps.reader.file.nubank.api.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
    @JsonIgnore
    private String nome;


}
