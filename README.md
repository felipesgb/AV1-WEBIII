# AutoBots — Atividade prática ATVI

Implementação dos CRUDs de **Cliente, Documento, Endereço e Telefone**, conforme o enunciado e os diagramas UML da atividade. Projeto desenvolvido a partir de https://github.com/gerson-pn/atvi-autobots-microservico-spring, revisão `d5bdb729abee6e6d39b9e23aed202ebc66912ce9`.

## Como executar

Requisitos: **JDK 17**, variável `JAVA_HOME` apontando para o JDK e internet na primeira compilação para baixar as dependências. O Maven Wrapper está incluído; não é necessário instalar Maven separadamente.

No PowerShell, entre na pasta `automanager` e execute:

```powershell
.\mvnw.cmd clean verify
.\mvnw.cmd spring-boot:run
```

No Linux/Ubuntu/macOS, dentro da mesma pasta:

```sh
sh mvnw clean verify
sh mvnw spring-boot:run
```

O wrapper baixa o Maven compatível automaticamente. Use JDK 17 e configure `JAVA_HOME` para a instalação local do Java em cada sistema.

A API fica em `http://localhost:8080`. Para verificar no navegador, abra `http://localhost:8080/clientes` (inicialmente retorna `[]`). A atividade é um back-end: não há página gráfica na raiz.

Também é possível executar o pacote gerado:

```powershell
java -jar target/automanager-0.0.1-SNAPSHOT.jar
```

No Eclipse, use **File > Import > Maven > Existing Maven Projects**, selecione `automanager` e configure o JDK 17. O projeto mantém Lombok, como na base; se o editor não reconhecer os getters/setters, configure o suporte ao Lombok do Eclipse. A compilação pelo Maven já processa essas anotações.

## Operações disponíveis

Para cada recurso `clientes`, `documentos`, `enderecos` e `telefones`:

| Método | Rota | Resultado |
|---|---|---|
| POST | `/{recurso}` | Insere e retorna o registro, ID e cabeçalho Location (201) |
| GET | `/{recurso}` | Lista os registros (200) |
| GET | `/{recurso}/{id}` | Seleciona pelo ID (200 ou 404) |
| PUT | `/{recurso}/{id}` | Atualiza os campos informados (200 ou 404) |
| DELETE | `/{recurso}/{id}` | Exclui o registro (204 ou 404) |

As rotas originais continuam disponíveis: `GET /cliente/cliente/{id}`, `GET /cliente/clientes`, `POST /cliente/cadastro`, `PUT /cliente/atualizar` e `DELETE /cliente/excluir`. Nas duas últimas, informe `id` no corpo JSON. Os demais recursos também aceitam aliases equivalentes no singular.

### Cadastro completo de cliente

Envie este corpo em `POST /clientes` com `Content-Type: application/json`:

```json
{
  "nome": "Ana Silva",
  "nomeSocial": "Ana",
  "dataNascimento": "2000-05-15",
  "endereco": {
    "estado": "SP",
    "cidade": "São Paulo",
    "bairro": "Centro",
    "rua": "Rua das Flores",
    "numero": "100",
    "codigoPostal": "01001000",
    "informacoesAdicionais": "Apartamento 12"
  },
  "documentos": [{"tipo": "RG", "numero": "123456789"}],
  "telefones": [{"ddd": "11", "numero": "999999999"}]
}
```

Os IDs são gerados pelo banco. `dataCadastro` é preenchida automaticamente quando omitida. Datas usam `yyyy-MM-dd`, com fuso UTC.

### Dados independentes ou vinculados

Documento, endereço e telefone podem ser cadastrados isoladamente por suas rotas. Para criar um registro já associado a um cliente, use o parâmetro `clienteId`:

```http
POST /documentos?clienteId=1
Content-Type: application/json

{"tipo":"CPF","numero":"00000000001"}
```

O mesmo vale para `/enderecos?clienteId=1` e `/telefones?clienteId=1`. Use o ID real retornado no cadastro. Um segundo endereço para o mesmo cliente retorna 409; atualize ou exclua o endereço existente primeiro.

### Regras de atualização e remoção

- Para preservar o comportamento de atualização parcial do projeto-base, `PUT` altera somente campos não nulos. Campos omitidos ou nulos são preservados. Campos opcionais de texto podem ser esvaziados com `""`.
- Atualizar cliente aceita documentos e telefones: itens sem ID são novos; itens com ID atualizam somente filhos que já pertencem àquele cliente. IDs desconhecidos, de outro cliente ou repetidos no mesmo pedido são rejeitados.
- Listas vazias, omitidas ou nulas na atualização não removem filhos. Use o `DELETE` específico de documento ou telefone para removê-los.
- O endereço enviado na atualização altera o existente ou cria um quando não houver. Um ID de endereço incompatível é rejeitado. O CEP também é atualizado.
- Excluir documento, telefone ou endereço vinculado desfaz o vínculo e remove o registro, preservando o cliente.
- Excluir cliente remove também seu endereço, documentos e telefones por cascata. Registros independentes permanecem.
- Não informe IDs no cadastro, inclusive nos filhos. A atualização não permite trocar o ID nem transferir registros de um cliente para outro.

### Validação e respostas de erro

São obrigatórios: nome do cliente; tipo e número do documento; DDD e número do telefone; cidade, rua e número do endereço. Filhos enviados também são validados. As listas não aceitam itens nulos.

O número de documento é único, mantendo a regra do repositório original. Não há validação de dígitos verificadores de CPF/RG: o modelo admite documentos de tipos genéricos.

| Código | Situação |
|---|---|
| 400 | Dados obrigatórios ausentes/em branco, JSON ou parâmetros inválidos, IDs incompatíveis |
| 404 | Registro ou cliente informado não encontrado |
| 409 | Número de documento duplicado ou conflito de relacionamento |

Erros tratados retornam JSON com `status` e `mensagem`. As gravações são transacionais: uma falha não deixa um cadastro ou uma alteração parcialmente salvo.

## Organização e banco

- `entidades`: modelo JPA das quatro classes do diagrama.
- `repositorios`: acesso ao banco com Spring Data JPA.
- `modelo`: atualização dos campos e coleções.
- `servicos`: transações, validação e regras de relacionamento.
- `controles`: API HTTP e tratamento de erros.
- `src/test`: testes automatizados de integração e regressão.

Projeto atualizado para **Spring Boot 4.1.1**, mantendo **Java 17** e a estrutura do projeto-base. O Maven Wrapper utiliza Maven **3.9.16**. As versões de Spring Framework, Hibernate, Jackson, H2, Lombok e bibliotecas de teste são gerenciadas pelo Spring Boot para manter compatibilidade. As APIs de persistência e validação usam Jakarta; o processamento de JSON usa Jackson 3. Dependências redundantes de Spring Data JDBC foram removidas; a persistência usa JPA. A consulta individual usa `findById`, sem carregar todos os clientes. A classe `ClienteSelecionador` foi mantida como parte da base, mas a API não depende dela.

O banco padrão é **H2 em arquivo**, em `automanager/data` quando a execução parte de `automanager`. Os dados sobrevivem ao reinício. Não há inserção automática de exemplos, evitando duplicatas a cada inicialização. Execute a aplicação sempre a partir da mesma pasta para usar o mesmo banco.

Os testes usam banco H2 em memória separado, recriado a cada execução. O projeto é destinado à atividade acadêmica; autenticação e publicação em servidor não fazem parte do enunciado.

## Verificação

Execute `mvnw.cmd clean verify` dentro de `automanager`. Os relatórios detalhados ficam em `target/surefire-reports`.

A entrega atualizada passou por 23 testes automatizados no Windows com Java 17 e Maven 3.9.16. O JAR também foi verificado por chamadas HTTP e reinício com persistência dos dados. O wrapper inclui suporte a Windows e Linux/Ubuntu; não foi feita execução em Linux neste ambiente.
