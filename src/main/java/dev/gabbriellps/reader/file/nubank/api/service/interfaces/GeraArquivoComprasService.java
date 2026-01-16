package dev.gabbriellps.reader.file.nubank.api.service.interfaces;

import dev.gabbriellps.reader.file.nubank.api.dto.response.ComprasResponseGeral;

public interface GeraArquivoComprasService {

    void gerarArquivoCompras(ComprasResponseGeral compras, String nomeArquivo);

}
