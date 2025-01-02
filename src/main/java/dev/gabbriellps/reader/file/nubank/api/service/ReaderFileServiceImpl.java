package dev.gabbriellps.reader.file.nubank.api.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.gabbriellps.reader.file.nubank.api.dto.DadosArquivoDTO;
import dev.gabbriellps.reader.file.nubank.api.dto.response.ComprasResponseGeral;
import dev.gabbriellps.reader.file.nubank.api.dto.response.DadosCompraResponseDTO;
import dev.gabbriellps.reader.file.nubank.api.service.interfaces.ReaderFileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReaderFileServiceImpl implements ReaderFileService {

    private final ObjectMapper om;

    @Override
    public ComprasResponseGeral readFile(MultipartFile faturaFile) throws IOException {
        log.info("m=readFile -> Iniciando leitura arquivo...");
        File faturaConvertida = convertMultipartToFile(faturaFile);

        List<String> lines = readLines(faturaConvertida);

        log.info("m=readFile -> Iniciando conversão de linhas para DTOs...");
        List<DadosArquivoDTO> dadosArquivoDTO = lines.stream()
                .map(this::montaLinhaDTO)
                .filter(Objects::nonNull)
                .toList();
        log.info("m=readFile -> Linhas convertida em DTOs com sucesso!");

        log.info("m=readFile -> Iniciando agrupamento das compras pelos nomes");
        Map<String, List<DadosArquivoDTO>> dadosAgrupados = dadosArquivoDTO.stream()
                .collect(Collectors.groupingBy(DadosArquivoDTO::getNome));

        List<DadosCompraResponseDTO> response = new ArrayList<>();
        dadosAgrupados.forEach((nome, dado) -> response.add(new DadosCompraResponseDTO(nome, dado)));


        return ComprasResponseGeral.builder()
                .response(response)
                .vlrTotalGeral(response.stream()
                        .map(DadosCompraResponseDTO::getValorTotal)
                        .reduce(BigDecimal.ZERO, BigDecimal::add))
                .build();
    }

    private static String getNomeByTitulo(String titulo) {
        titulo = removeParcelaDoTitulo(titulo);

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

    private static String removeParcelaDoTitulo(String titulo) {
        int indexParcela = titulo.lastIndexOf("- Parcela") - 1;
        if(isIndexInvalido(indexParcela)){
            indexParcela = titulo.length();
        }
        return titulo.substring(0, indexParcela);
    }

    private static String removeAspasString(String titulo) {
        return titulo.replace("\"", Strings.EMPTY);
    }

    private static boolean isIndexInvalido(int indexName) {
        return indexName < 0;
    }

    private DadosArquivoDTO montaLinhaDTO(String line) {
        log.info("m=montaLinhaDTO -> Iniciando montagem de linha para DTO");
        List<String> dados = new ArrayList<>(Arrays.stream(line.split(",")).toList());

        String titleCompletoSemAspas = removeAspasString(dados.get(1));
        dados.set(1, titleCompletoSemAspas);
        String tituloComParcela = getTitulo(dados);

        if(tituloComParcela.equals("Pagamento recebido")) { // Não adiciona pagamento recebido para a lista
            return null;
        }

        BigDecimal valor = BigDecimal.valueOf(Double.parseDouble(removeAspasString(dados.get(2))))
                .setScale(2, RoundingMode.HALF_UP);

        DadosArquivoDTO dadosArquivoDTO = DadosArquivoDTO.builder()
                .data(LocalDate.parse(removeAspasString(dados.get(0)), DateTimeFormatter.ISO_LOCAL_DATE))
                .titulo(tituloComParcela)
                .valor(valor)
                .nome(getNomeByTitulo(titleCompletoSemAspas))
                .obs(Strings.EMPTY)
                .build();

        int indexQueryParams = titleCompletoSemAspas.indexOf("?");
        if(isIndexInvalido(indexQueryParams)){
            log.info("m=montaLinhaDTO -> Linha nao contem query param, objeto montado com sucesso!");
            return dadosArquivoDTO;
        }
        log.info("m=montaLinhaDTO -> Encontrado queryParams, montando campo observacao");
        String queryParams = titleCompletoSemAspas.substring(indexQueryParams);
        queryParams = removeParcelaDoTitulo(queryParams);

        String jsonString = convertQueryParamsToStringObject(queryParams);
        Map<String, Object> map = convertJsonStringParaHashMap(jsonString);

        log.info("m=montaLinhaDTO -> setando campo observacao");
        dadosArquivoDTO.setObs(String.valueOf(map.get("obs")));

        log.info("m=montaLinhaDTO -> Montagem de linha para DTO realizada com sucesso!");
        return dadosArquivoDTO;
    }

    private Map<String, Object> convertJsonStringParaHashMap(String test) {
        log.info("m=convertJsonStringParaHashMap, entrando metodo converte string objeto para hash map");
        try {
            Map<String, Object> map = om.readValue(test, new TypeReference<>() {});
            log.info("m=convertJsonStringParaHashMap, string objeto convertido para hash map com sucesso!");
            return map;
        } catch (JsonProcessingException e) {
            log.error("m=montaLinhaDTO, erro ao converter string objeto para hash map ", e);
            throw new RuntimeException(e);
        }
    }

    private static String convertQueryParamsToStringObject(String titulo) {
        log.info("m=convertQueryParamsToStringObject, entrando metodo converte query params para string objeto");
        String objectString = titulo.transform(s -> s.replace("?", "{\"")
                .replace("?", "{\"")
                .replace("=", "\":\"")
                .replace("&", "\",\"")) + "\"}";
        log.info("m=convertQueryParamsToStringObject, query params convertido para string objeto com sucesso!");
        return objectString;
    }

    private static String getTitulo(List<String> dados) {
        String titleCompleto = dados.get(1);
        String parcela = Strings.EMPTY;

        int indexParcela = titleCompleto.indexOf("- Parcela");
        if(!isIndexInvalido(indexParcela)){
            parcela = titleCompleto.substring(indexParcela);
        }

        int indexNome = titleCompleto.indexOf("-");
        if(isIndexInvalido(indexNome)) {
            indexNome = titleCompleto.length();
        }

        int indexQueryParam = titleCompleto.indexOf("?");
        if(!isIndexInvalido(indexQueryParam)) {
            indexNome = indexQueryParam - 1;
        }

        return titleCompleto.substring(0, indexNome) + parcela;
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
