package dev.gabbriellps.reader.file.nubank.api.service;

import dev.gabbriellps.reader.file.nubank.api.dto.response.ComprasResponseGeral;
import dev.gabbriellps.reader.file.nubank.api.dto.response.DadosCompraResponseDTO;
import dev.gabbriellps.reader.file.nubank.api.service.interfaces.GeraArquivoComprasService;
import dev.gabbriellps.reader.file.nubank.api.util.Util;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;

@Service
@Slf4j
public class GeraArquivoComprasServiceImpl implements GeraArquivoComprasService {

    private static final String caminho = "C:\\Users\\gabriel\\Documents\\14_Faturas_Nubank\\";


    @Override
    public void gerarArquivoCompras(ComprasResponseGeral comprasResponse) {
        log.info("Iniciando o processo de geração do arquivo de compras.");
        // Implementação da lógica para gerar o arquivo de compras
        // ...

        String nomeArquivo = "Compras_" + LocalDateTime.now().getMonthValue() + "_" + LocalDateTime.now().getYear() + ".txt";

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(caminho + nomeArquivo, true))) {

            for (DadosCompraResponseDTO compras : comprasResponse.getResponse()) {
                addHifens(writer);

                writer.write(compras.getNome() + System.lineSeparator());

                compras.getCompras().forEach(compra -> {
                    try {
                        writer.write("Titulo: " + compra.getTitulo() + System.lineSeparator());
                        writer.write("Data: " + Util.formatarDataBRL(compra.getData()) +
                                ", Valor: " + Util.formatarValorBRLString(compra.getValor()) + System.lineSeparator());
                        writer.write("Descrição: " + compra.getObs() + System.lineSeparator() + System.lineSeparator());
                    } catch (IOException e) {
                        log.error("Erro ao escrever no arquivo: {}", e.getMessage());
                    }
                });

                writer.write("Sub Total: " + Util.formatarValorBRLString(compras.getValorTotal()) + System.lineSeparator());
            }

            addHifens(writer);
            writer.write("Valor Total: " + Util.formatarValorBRLString(comprasResponse.getVlrTotalGeral()));
        } catch (IOException e) {
            System.err.println("Erro ao gerar o arquivo: " + e.getMessage());
        }

        log.info("Arquivo de compras gerado com sucesso.");
    }

    private void addHifens(BufferedWriter writer) throws IOException {
        writer.write(StringUtils.repeat("-", 50) + System.lineSeparator());
    }

}
