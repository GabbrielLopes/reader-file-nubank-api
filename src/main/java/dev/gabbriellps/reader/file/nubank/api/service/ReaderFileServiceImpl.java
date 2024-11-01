package dev.gabbriellps.reader.file.nubank.api.service;

import dev.gabbriellps.reader.file.nubank.api.dto.DadosArquivoDTO;
import dev.gabbriellps.reader.file.nubank.api.dto.response.DadosCompraResponseDTO;
import dev.gabbriellps.reader.file.nubank.api.service.interfaces.ReaderFileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ReaderFileServiceImpl implements ReaderFileService {


    @Override
    public List<DadosCompraResponseDTO> readFile(MultipartFile faturaFile) throws IOException {
        log.info("m=readFile -> Iniciando leitura arquivo...");
        File faturaConvertida = convertMultipartToFile(faturaFile);

        List<String> lines = readLines(faturaConvertida);

        log.info("m=readFile -> Iniciando conversão de linhas para DTOs...");
        List<DadosArquivoDTO> dadosArquivoDTO = lines.stream()
                .map(ReaderFileServiceImpl::montaLinhaDTO)
                .filter(Objects::nonNull)
                .toList();
        log.info("m=readFile -> Linhas convertida em DTOs com sucesso!");

        log.info("m=readFile -> Iniciando agrupamento das compras pelos nomes");
        Map<String, List<DadosArquivoDTO>> dadosAgrupados = dadosArquivoDTO.stream()
                .collect(Collectors.groupingBy(DadosArquivoDTO::getNome));

        List<DadosCompraResponseDTO> response = new ArrayList<>();
        dadosAgrupados.forEach((nome, dado) -> response.add(new DadosCompraResponseDTO(nome, dado)));

        return response;
    }

    private static String getNomeByTitulo(String titulo) {
        int indexParcela = titulo.lastIndexOf("- Parcela") - 1;
        if(isIndexInvalido(indexParcela)){
            indexParcela = titulo.length();
        }
        titulo = titulo.substring(0, indexParcela);
        int indexInicioNome = titulo.lastIndexOf("-");
        if(isIndexInvalido(indexInicioNome)){ // Se não tiver hifen = sem nome
            return "sem nome";
        }
        indexInicioNome += 1;
        String nome = titulo.substring(indexInicioNome);

        int indexFinalNome = nome.indexOf(" "); // Se for item parcelado, fica o nome e a parcela ex: -> "nomeExemplo 2/3",
        if(isIndexInvalido(indexFinalNome)){ // Se nao tiver espaco após o nome, o item nao é parcelado entao o nome já vem certo
            return nome;
        }

        // Pegamos do começo até o espaço para pegar apenas o 1° nome após o hifen
        return nome.substring(0, indexFinalNome);
    }

    private static boolean isIndexInvalido(int indexName) {
        return indexName < 0;
    }

    private static DadosArquivoDTO montaLinhaDTO(String line) {
        log.info("m=montaLinhaDTO -> Iniciando montagem de linha para DTO");
        List<String> dados = Arrays.stream(line.split(",")).toList();

        String titulo = getTitulo(dados);

        if(titulo.equals("Pagamento recebido")) { // Não adiciona pagamento recebido para a lista
            return null;
        }

        DadosArquivoDTO dadosArquivoDTO = DadosArquivoDTO.builder()
                .data(LocalDate.parse(dados.get(0), DateTimeFormatter.ISO_LOCAL_DATE))
                .titulo(titulo)
                .valor(Double.parseDouble(dados.get(2)))
                .nome(getNomeByTitulo(titulo))
                .build();

        log.info("m=montaLinhaDTO -> Montagem de linha para DTO realizada com sucesso!");
        return dadosArquivoDTO;
    }

    private static String getTitulo(List<String> dados) {
        return dados.get(1);
    }

    private static List<String> readLines(File faturaConvertida) throws IOException {
        log.info("m=readLines -> Iniciando leitura do arquivo...");
        List<String> lines;
        try (BufferedReader br = new BufferedReader(new FileReader(faturaConvertida))) {
            lines = br.lines().collect(Collectors.toList());
        }

        if (lines.isEmpty()) {
            log.error("Erro, arquivo vazio");
            throw new IllegalArgumentException("Erro, arquivo vazio.");
        }

        log.info("m=readLines -> Leitura realizada com sucesso!");

        lines.remove(0); // Remove cabecalho do arquivo

        return lines;
    }

    private File convertMultipartToFile(MultipartFile faturaFile) {
        log.info("m=convertMultipartToFile -> Iniciando conversão de MultipartFile para File...");
        File file = new File(faturaFile.getOriginalFilename());
        try {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(faturaFile.getBytes());
            fos.close();
        } catch (IOException e) {
            file = null;
        }

        log.info("m=convertMultipartToFile -> MultipartFile convertido com sucesso para File!");
        return file;
    }


}
