package dev.gabbriellps.reader.file.nubank.api.controller;

import dev.gabbriellps.reader.file.nubank.api.dto.response.DadosCompraResponseDTO;
import dev.gabbriellps.reader.file.nubank.api.service.interfaces.ReaderFileService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/read")
public class ReaderFileController {

    private final ReaderFileService readerFileService;


    public ReaderFileController(ReaderFileService readerFileService) {
        this.readerFileService = readerFileService;
    }


    @PostMapping(value = "/file")
    public ResponseEntity<List<DadosCompraResponseDTO>> readFile(@RequestPart("fatura") MultipartFile faturaFile)
            throws IOException {
        return ResponseEntity.status(HttpStatus.OK).body(readerFileService.readFile(faturaFile));
    }

}
