# BIC / IBAN Toolkit

Spring Boot toolkit for validating **ISO 13616 IBAN** (national length + mod-97) and **ISO 9362 BIC** (BIC8/BIC11), plus a SEPA country flag for the IBAN country code.

Inspired by the problem space of libraries such as [jbanking](https://github.com/marcwrobel/jbanking) (Apache-2.0). This repository is an independent educational implementation under MIT.

## Architecture

```mermaid
flowchart LR
    Client --> API["BicIbanController"]
    API --> Svc["BicIbanService"]
    Svc --> Len["Country length table"]
    Svc --> IBAN["normalize + mod-97"]
    Svc --> BIC["BIC8 / BIC11 parse"]
    Svc --> SEPA["SEPA country set"]
```

## API

| Method | Path | Description |
|--------|------|-------------|
| `POST` | `/api/iban/validate` | Body `{ "value": "DE89..." }` |
| `POST` | `/api/bic/validate` | Body `{ "value": "DEUTDEFF" }` |
| `GET` | `/api/health` | Liveness |

### IBAN response (success)

```json
{
  "iban": "DE89370400440532013000",
  "valid": true,
  "country": "DE",
  "sepa": true,
  "message": "OK"
}
```

Invalid results return HTTP 400 with the same shape and a `message` explaining length or mod-97 failure.

## Diagrams

Architecture and UML diagrams are in [docs/architecture.md](docs/architecture.md) and [docs/uml.md](docs/uml.md). A standalone index is available at [docs/index.html](docs/index.html).

```mermaid
classDiagram
    direction TB
    class BicIbanController {
        <<controller>>
        -service: BicIbanService
        +validateIban(request) IbanResult
        +validateBic(request) BicResult
    }
    class BicIbanService {
        <<service>>
        +validateIban(value) IbanResult
        +validateBic(value) BicResult
    }
    class ValidateRequest {
        <<record>>
        +value: String
    }
    class IbanResult {
        <<record>>
        +iban: String
        +valid: boolean
        +country: String
        +sepa: boolean
        +message: String
    }
    class BicResult {
        <<record>>
        +bic: String
        +valid: boolean
        +bankCode: String
        +country: String
        +location: String
        +branch: String
        +message: String
    }
    BicIbanController --> BicIbanService
    BicIbanController ..> ValidateRequest
    BicIbanService ..> IbanResult
    BicIbanService ..> BicResult
```

## Quick start

```bash
./mvnw test
./mvnw spring-boot:run
```

HTTP: `http://localhost:8084`

```bash
curl -s -X POST http://localhost:8084/api/iban/validate \
  -H "Content-Type: application/json" \
  -d "{\"value\":\"DE89 3704 0044 0532 0130 00\"}"

curl -s -X POST http://localhost:8084/api/bic/validate \
  -H "Content-Type: application/json" \
  -d "{\"value\":\"DEUTDEFFXXX\"}"
```

## Notes

- Length checks cover an educational ISO 13616 subset across SEPA countries and selected Turkish neighbours. Unknown countries still run mod-97 only; consult the official ISO 13616 registry for authoritative national rules.
- Inputs are limited to 128 characters, reject control characters and require uppercase BIC/IBAN letters. BIC bank and country codes are letters; location and branch codes are alphanumeric.
- `GB` remains in the SEPA helper set for demo convenience; production SEPA reachability for UK accounts depends on scheme rules.

## License

[MIT](LICENSE)
