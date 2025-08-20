# README

## VERSÃO 
JDK 17 é necessário para a execução desta aplicação

## Padrões utilizados para o desenvolvimento

**API FIRST** - Contratos criados antes da implementação da lógica de negócio (Controle de Interfaces de controle e DTOs sempre documentados)
	
O Swagger deve ser desenvolvido utilizando o editor aqui >> https://editor.swagger.io/

**METHOD REFERENCE** - Utilizado para refatorar algumas referências de métodos.

**LAMBDA STREAM** - Utilizado para melhorar a leitura de algumas operações de loop, mapeamento e filtro.

## H2 CONSOLE - LOCAL
http://localhost:8080/h2-console

**Configurações de Conexão:**
- **JDBC URL:** `jdbc:h2:mem:worst-movie`
- **Username:** `desenv`
- **Password:** `dGVzdGUxMjM=`

## Como Executar

### Compilar e executar
```bash
./gradlew bootRun
```

### Executar testes
```bash
./gradlew test
```

### Limpar e compilar
```bash
./gradlew clean build
```

## Endpoint Principal
```
GET /producers
```
Retorna os produtores com maiores e menores intervalos entre prêmios consecutivos do Golden Raspberry Awards.

## Documentação da API
Swagger UI: http://localhost:8080/swagger-ui.html