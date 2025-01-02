package dev.gabbriellps.reader.file.nubank.api.dto.response;

import dev.gabbriellps.reader.file.nubank.api.dto.DadosArquivoDTO;
import dev.gabbriellps.reader.file.nubank.api.util.Util;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DadosCompraResponseDTO {

    private String nome;
    private List<DadosArquivoDTO> compras;


    public BigDecimal getValorTotal() {
        return compras.stream()
                .map(DadosArquivoDTO::getValor)
                .map(vlr -> vlr.setScale(2, RoundingMode.HALF_UP))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

//    public String getComprasConcat() {
//        return compras.stream().map(dados -> String.format("Data: %s - Titulo: %s - Valor: %s",
//                        Util.formatarDataBRL(dados.getData()), dados.getTitulo(), dados.getValor()))
//                .collect(Collectors.joining("|"));
//    }

}
