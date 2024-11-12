# Med_Scheduling

**Med_Scheduling** é uma aplicação desenvolvida para ajudar pessoas que frequentemente esquecem de tomar seus medicamentos. Com uma integração com um bot do Telegram, o projeto oferece lembretes eficazes para garantir que você nunca esqueça de tomar sua medicação.

## Funcionalidades

- Lembretes automáticos de medicamentos via Telegram.
- Integração simples e rápida com um bot criado por você.

## Tecnologias Utilizadas

- **Java 17**
- **Spring Boot 3**
- **JUnit 5** com **Mockito** para testes unitários
- **MySQL** rodando em um container Docker

## Pré-requisitos

Para utilizar esta aplicação, você precisará:

1. **Criar um Bot no Telegram:**
    - Acesse o [BotFather](https://telegram.me/BotFather).
    - Crie um novo bot e anote o `name` e o `token` gerados.

2. **Configurar Variáveis de Ambiente:**
    - Adicione as seguintes variáveis ao seu ambiente:
      ```
      TELEGRAM_BOT_TOKEN={seu_token_aqui}
      TELEGRAM_BOT_NAME={nome_do_seu_bot}
      ```

## Como Executar o Projeto

### Usando Docker Compose

O projeto já inclui um arquivo `docker-compose.yml`, que facilita a execução do MySQL e da aplicação. Para iniciar o projeto, siga os passos abaixo:

1. Clone este repositório:
   ```bash
   git clone https://github.com/seu_usuario/Med_Scheduling.git
   cd Med_Scheduling
   
2. Inicie os serviços com Docker Compose:

```
docker-compose up 
```

3. Rode a aplicação depois de adicionar as variáveis de ambiente já mencionadas.

## Contribuições
Sinta-se à vontade para contribuir com melhorias ou relatar problemas. Qualquer dúvida ou sugestão, entre em contato!
