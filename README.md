# reader-file-nubank-api

## Descrição
O projeto **reader-file-nubank-api** é uma aplicação desenvolvida em Java com Spring Boot, que processa arquivos de faturas do Nubank, gerando informações detalhadas sobre as compras realizadas.

## Tecnologias Utilizadas
- **Java 17**
- **Spring Boot**
- **Maven**
- **Lombok**

## Configuração
### Requisitos
- **Java 17** ou superior
- **Maven 3.8+**
- **IntelliJ IDEA** (opcional)

### Configuração do `application.properties`
No arquivo `src/main/resources/application.properties`, configure o diretório onde os arquivos de compras serão gerados:
```ini
spring.application.name=reader-file-nubank-api
diretorio.arquivo.compras=C:\\Users\\teste\\Documents\\Faturas_Nubank\\

```

Certifique-se de que o diretório especificado em diretorio.arquivo.compras existe no sistema. 

## Funcionalidades

- **Processamento de Arquivos**: Leitura e processamento de arquivos de faturas do Nubank, extraindo informações relevantes.
- **Geração de Arquivos**: Criação de arquivos contendo informações detalhadas das compras realizadas.
traídas de parâmetros de consulta (query params) presentes no título.o de informações como título, valor, data e observações.

## Observação
 - Este projeto é de uso pessoal e está em constante melhoria. Novas funcionalidades e ajustes podem ser adicionados ao longo do tempo.