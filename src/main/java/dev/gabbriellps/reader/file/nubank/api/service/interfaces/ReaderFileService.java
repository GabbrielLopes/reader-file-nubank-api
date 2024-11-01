package dev.gabbriellps.reader.file.nubank.api.service.interfaces;

import dev.gabbriellps.reader.file.nubank.api.dto.response.DadosCompraResponseDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ReaderFileService {

    List<DadosCompraResponseDTO> readFile(MultipartFile faturaFile) throws IOException;

}
