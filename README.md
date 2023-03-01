
## Executando a Aplicação

Para executar a aplicação, você precisa seguir os seguintes passos:

1. Clone o repositório em sua máquina
2. Abra um terminal na pasta raiz do projeto
3. Execute o comando `mvn clean install` para baixar as dependências e construir a aplicação
4. Execute o comando abaixo para rodar um container de Mysql para ser usado na aplicação:
```shell
docker run -it \
-e MYSQL_ROOT_PASSWORD=s3cr3t \
-e MYSQL_DATABASE=convention \
-p 3306:3306 \
--rm \
--name mysql \
mysql:latest
```
5. Execute o comando `java -jar target/convention-api.jar` para iniciar a aplicação

Após iniciar a aplicação, você poderá acessá-la em http://localhost:8080.

## Documentação da API

A documentação da API pode ser acessada em http://localhost:8080/v3/api-docs.yaml.

## Testando a Aplicação

Para testar a aplicação, você pode utilizar a interface do Swagger, que pode ser acessada em http://localhost:8080/swagger-ui.html.

## Exemplos de requisição
Abaixo alguns simples exemplos do fluxo de votação:
```http
### Register a user to vote
POST http://localhost:8080/api/v1/voter
Content-Type: application/json

{
  "id": "12345678901",
  "name": "John Doe",
  "email": "johndoe@gmail.com"
}

### Initiate a voting session
POST http://localhost:8080/api/v1/convention-session
Content-Type: application/json

{
  "convention": "Do you agree that Spring is awesome?",
  "durationInMinutes": 2
}

### Registering a vote
POST http://localhost:8080/api/v1/convention-session/6/vote
Content-Type: application/json

{
  "voterId": "12345678901",
  "vote": false
}

```

## Considerações Finais

Esta aplicação foi desenvolvida como parte de um processo de avaliação para uma vaga de desenvolvedor na empresa Sicred. Em caso de dúvidas ou sugestões, por favor entre em contato com a equipe responsável pelo processo seletivo.
