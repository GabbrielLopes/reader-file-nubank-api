package dev.gabbriellps.reader.file.nubank.api.service.interfaces;

import dev.gabbriellps.reader.file.nubank.api.dto.response.ComprasResponseGeral;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ReaderFileService {

    ComprasResponseGeral readFile(MultipartFile faturaFile) throws IOException;

}
