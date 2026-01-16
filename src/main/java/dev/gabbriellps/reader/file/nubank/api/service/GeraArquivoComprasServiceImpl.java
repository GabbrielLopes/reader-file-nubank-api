package dev.gabbriellps.reader.file.nubank.api.service;

import dev.gabbriellps.reader.file.nubank.api.dto.response.ComprasResponseGeral;
import dev.gabbriellps.reader.file.nubank.api.dto.response.DadosCompraResponseDTO;
import dev.gabbriellps.reader.file.nubank.api.service.interfaces.GeraArquivoComprasService;
import dev.gabbriellps.reader.file.nubank.api.util.Util;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
public class GeraArquivoComprasServiceImpl implements GeraArquivoComprasService {

    @Value("${diretorio.arquivo.compras}")
    private String caminho;

    @Override
    public void gerarArquivoCompras(ComprasResponseGeral comprasResponse, String nomeArquivo) {
        log.info("Iniciando o processo de geração do arquivo de compras.");

        LocalDate data = extraiDataPeloNomeArquivo(nomeArquivo);

        nomeArquivo = "Nubank_Fatura_" + data.getMonthValue() + "_" + data.getYear() + ".txt";

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

    private static LocalDate extraiDataPeloNomeArquivo(String nomeArquivo) {
        if (StringUtils.isBlank(nomeArquivo) || !nomeArquivo.contains("_") || !nomeArquivo.contains(".")) {
            throw new IllegalArgumentException("Nome do arquivo inválido: " + nomeArquivo);
        }

        try {
            String data = nomeArquivo.substring(nomeArquivo.lastIndexOf("_") + 1, nomeArquivo.lastIndexOf("."));
            return LocalDate.parse(data, DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (Exception e) {
            throw new IllegalArgumentException("Formato de data inválido no nome do arquivo: " + nomeArquivo, e);
        }
    }

    private void addHifens(BufferedWriter writer) throws IOException {
        writer.write(StringUtils.repeat("-", 50) + System.lineSeparator());
    }

}
