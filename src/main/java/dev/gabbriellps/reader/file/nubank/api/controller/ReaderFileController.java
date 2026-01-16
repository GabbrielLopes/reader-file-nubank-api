package dev.gabbriellps.reader.file.nubank.api.controller;

import dev.gabbriellps.reader.file.nubank.api.dto.response.ComprasResponseGeral;
import dev.gabbriellps.reader.file.nubank.api.service.interfaces.GeraArquivoComprasService;
import dev.gabbriellps.reader.file.nubank.api.service.interfaces.ReaderFileService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/read")
public class ReaderFileController {

    private final ReaderFileService readerFileService;
    private final GeraArquivoComprasService geraArquivoComprasService;


    public ReaderFileController(ReaderFileService readerFileService,
                                GeraArquivoComprasService geraArquivoComprasService) {
        this.readerFileService = readerFileService;
        this.geraArquivoComprasService = geraArquivoComprasService;
    }


    @PostMapping(value = "/file")
    public ResponseEntity<ComprasResponseGeral> readFile(@RequestPart("fatura") MultipartFile faturaFile)
            throws IOException {
        ComprasResponseGeral compras = readerFileService.readFile(faturaFile);
        geraArquivoComprasService.gerarArquivoCompras(compras, faturaFile.getOriginalFilename());

        return ResponseEntity.status(HttpStatus.OK).body(compras);
    }

}
