# ATVI - AutoBots

API para cadastro de clientes, documentos, telefones e endereços, desenvolvida com Java 17, Spring Boot 4.1.1, Spring Data JPA e banco H2. Baseada no projeto fornecido pelo professor Gerson Penha.

## Como executar

Requisitos: **JDK 17**, variável `JAVA_HOME` apontando para a pasta do JDK e internet na primeira execução. O Maven Wrapper está incluído; não é necessário instalar Maven ou um servidor de banco de dados.

1. Extraia o ZIP e entre na pasta **`AutoBots-ATVI/automanager`**, onde estão `pom.xml`, `mvnw` e `mvnw.cmd`.
2. No Windows, abra essa pasta no Explorador, digite `powershell` na barra de endereço e pressione Enter.
3. Confira o Java:

   ```powershell
   java -version
   javac -version
   ```

   Se `JAVA_HOME` ainda não estiver configurado, defina-o nessa janela. Substitua o caminho abaixo pela pasta do seu JDK:

   ```powershell
   $env:JAVA_HOME = "C:\Program Files\Java\jdk-17"
   Test-Path "$env:JAVA_HOME\bin\javac.exe"
   ```

   O último comando deve retornar `True`. Essa configuração vale apenas para a janela atual.

4. Execute os testes e compile:

   ```powershell
   .\mvnw.cmd clean verify
   ```

   Aguarde **`BUILD SUCCESS`**. A entrega foi verificada com **23 testes sem falhas**. A primeira execução pode demorar para baixar as dependências.

5. Inicie a aplicação:

   ```powershell
   .\mvnw.cmd spring-boot:run
   ```

   Aguarde a mensagem `Started AutomanagerApplication` e acesse **http://localhost:8080/clientes**. Se não houver clientes, a resposta será `[]`.

A porta padrão é **8080**. Mantenha o terminal aberto; para encerrar, pressione **Ctrl+C**. A aplicação é uma API: use as rotas abaixo para acessá-la; a raiz `/` não tem página inicial.

Para usar outra porta, por exemplo 8081:

```powershell
.\mvnw.cmd spring-boot:run "-Dspring-boot.run.arguments=--server.port=8081"
```

Nesse caso, use `http://localhost:8081` em todas as chamadas.

No **Linux/Ubuntu**, com JDK 17 e `JAVA_HOME` configurados, abra o terminal na pasta `automanager` e execute:

```sh
sh mvnw clean verify
sh mvnw spring-boot:run
```

Depois de compilar, também é possível iniciar pelo JAR, no Windows ou Linux:

```sh
java -jar target/automanager-0.0.1-SNAPSHOT.jar
```

Escolha uma forma de inicialização por vez. O H2 cria o banco na pasta **`automanager/data`** e mantém os dados entre execuções. Inicie sempre a partir da pasta `automanager`.

## Rotas

URL base: **`http://localhost:8080`**. Envie JSON com `Content-Type: application/json`.

| Método | Rota | Operação |
|---|---|---|
| GET | `/clientes` | Listar clientes |
| GET | `/clientes/{id}` | Consultar cliente |
| POST | `/clientes` | Cadastrar cliente e, opcionalmente, seus dados associados |
| PUT | `/clientes/{id}` | Atualizar cliente e dados associados informados |
| DELETE | `/clientes/{id}` | Excluir cliente e seus dados associados |
| GET | `/documentos` | Listar documentos |
| GET | `/documentos/{id}` | Consultar documento |
| POST | `/documentos?clienteId={clienteId}` | Cadastrar documento no cliente |
| PUT | `/documentos/{id}` | Atualizar documento |
| DELETE | `/documentos/{id}` | Excluir documento |
| GET | `/telefones` | Listar telefones |
| GET | `/telefones/{id}` | Consultar telefone |
| POST | `/telefones?clienteId={clienteId}` | Cadastrar telefone no cliente |
| PUT | `/telefones/{id}` | Atualizar telefone |
| DELETE | `/telefones/{id}` | Excluir telefone |
| GET | `/enderecos` | Listar endereços |
| GET | `/enderecos/{id}` | Consultar endereço |
| POST | `/enderecos?clienteId={clienteId}` | Cadastrar endereço no cliente |
| PUT | `/enderecos/{id}` | Atualizar endereço |
| DELETE | `/enderecos/{id}` | Excluir endereço |

Use os IDs retornados pela API. Nos cadastros de documento, telefone e endereço, `clienteId` é opcional: sem ele, o registro é criado sem vínculo com cliente. As rotas originais `/cliente/cadastro`, `/cliente/clientes`, `/cliente/cliente/{id}`, `/cliente/atualizar` e `/cliente/excluir` também continuam disponíveis; nas duas últimas, envie `id` no corpo.

## Exemplos de JSON

Use Postman, Insomnia ou outro cliente HTTP para enviar os exemplos abaixo.

### Cadastrar cliente — POST /clientes

É possível enviar apenas `nome` ou cadastrar os dados associados juntos. Os IDs são gerados pelo banco; não os informe no cadastro. `dataCadastro` é preenchida automaticamente quando omitida. Datas usam `yyyy-MM-dd`.

```json
{
  "nome": "Ana Silva",
  "nomeSocial": "Ana",
  "dataNascimento": "2000-05-15",
  "endereco": {
    "estado": "SP",
    "cidade": "Santos",
    "bairro": "Centro",
    "rua": "Rua das Flores",
    "numero": "100",
    "codigoPostal": "11000000",
    "informacoesAdicionais": "Apartamento 12"
  },
  "documentos": [{"tipo": "RG", "numero": "123456789"}],
  "telefones": [{"ddd": "13", "numero": "999999999"}]
}
```

### Atualizar cliente — PUT /clientes/{id}

```json
{"nome": "Ana Souza"}
```

As atualizações são **parciais**: campos omitidos ou nulos são preservados. Documentos e telefones enviados na atualização do cliente são adicionados quando não têm ID, ou atualizados quando o ID pertence ao cliente. Para removê-los, use suas rotas DELETE; listas vazias não removem registros.

### Documento — POST /documentos?clienteId={clienteId} ou PUT /documentos/{id}

```json
{"tipo": "RG", "numero": "987654321"}
```

### Telefone — POST /telefones?clienteId={clienteId} ou PUT /telefones/{id}

```json
{"ddd": "11", "numero": "988887777"}
```

### Endereço — POST /enderecos?clienteId={clienteId} ou PUT /enderecos/{id}

```json
{
  "estado": "SP",
  "cidade": "São Paulo",
  "bairro": "Centro",
  "rua": "Rua A",
  "numero": "20",
  "codigoPostal": "01001000",
  "informacoesAdicionais": "Casa"
}
```

Cada cliente pode ter **um endereço**. Se já existir, use PUT para atualizar. Excluir um documento, telefone ou endereço desfaz o vínculo e preserva o cliente; excluir o cliente remove todos os seus dados associados.

São obrigatórios: nome do cliente; tipo e número do documento; DDD e número do telefone; cidade, rua e número do endereço. O número do documento deve ser único.

## Respostas

- **200**: consulta ou atualização realizada.
- **201**: cadastro criado, com registro no corpo e URL no cabeçalho `Location`.
- **204**: exclusão realizada, sem corpo na resposta.
- **400**: dados inválidos ou IDs incompatíveis.
- **404**: registro ou cliente não encontrado.
- **409**: conflito, como documento duplicado ou segundo endereço para o mesmo cliente.
