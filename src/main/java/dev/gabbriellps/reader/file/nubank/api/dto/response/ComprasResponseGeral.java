package dev.gabbriellps.reader.file.nubank.api.dto.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ComprasResponseGeral {

    private List<DadosCompraResponseDTO> response;
    private BigDecimal vlrTotalGeral;

}
