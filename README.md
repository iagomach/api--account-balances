# API Account Balances

Esta é uma API para gerenciamento de saldos de contas. Ela permite consultar e atualizar os saldos de contas de
usuários.

## Pré-requisitos

Antes de executar o projeto, certifique-se de ter o seguinte instalado:

- **Java 21**: Certifique-se de ter o Java 21 instalado em sua máquina. Você pode verificar a versão do Java com o
  comando `java -version`.

- **json-server 0.17.4**: O json-server é usado para simular endpoints de API para testes. Instale-o globalmente com o
  seguinte comando:

    ```bash
    npm install -g json-server@0.17.4
    ```

## Configuração do Mock Server

1. Navegue até o diretório `/json-server-mocks`, onde estão localizadas as configurações de mock.

2. Abra dois terminais nesse diretório.

3. Execute o seguinte comando em um terminal para iniciar o json-server na porta 3000:

    ```bash
    json-server -H <SEU_IP_LOCAL> -p 3000 cadastro.json --routes routes.json
    ```

4. Execute o seguinte comando em outro terminal para iniciar o json-server na porta 8000:

    ```bash
    json-server -H <SEU_IP_LOCAL> -p 8000 cadastro.json --routes routes.json
    ```

Substitua `<SEU_IP_LOCAL>` pelo seu endereço IP local (por exemplo, 192.168.0.1).

## Executando o Projeto

1. No arquivo `application.yaml`, substitua todas as ocorrências de "localhost" nas configurações dos clientes pelo seu
   endereço IP local.

2. Execute o projeto normalmente.
3. Chame a API com comando baixo:
    ```bash
   curl --location 'http://localhost:5000/accounts/v1/transfer' \
   --header 'Content-Type: application/json' \
   --header 'Authorization: Basic SWFnbyBQYXJpIE1hY2hhZG86aXRhdQ==' \
   --data '{
   "targetAccountFullName": "Milton Nascimento",
   "transactionType": "TRANSFERENCIA_MESMA_INSTITUICAO",
   "amount": 1000.00
   }'
    ```
   Obs: Fique à vontade para trocar os valores do payload de solicitação.

Agora você está pronto para usar a API Account Balances! 🚀