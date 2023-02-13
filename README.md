# numble-banking-api
Numble Challenge - Banking API

### Development Environment

- Back-End : Spring-Boot, Spring-Security, JPA, MySQL
- Cloud : AWS - RDS
- Infra : Docker
- Document : Rest Docs

### How to Build

1. execute docker docker
2. gradle build
3. http://localhost:8080/docs/index.html 접속

### Entity Relation Diagram

```mermaid
erDiagram
	User {
		long userId
		string id
		string password
	}
	Account{
		long id
		long userId
		string number
		long balance
	}
	AccountHistory {
		long id
		long accountId
		string type
		long money	
	}

	User ||--|{ Account : own	
	Account ||--o{ AccountHistory : write
```

### Sequence Diagram

```mermaid
sequenceDiagram
	autonumber
	actor User
	User ->> Controller : transfer money
	Controller ->> ApplicationService : transfer money
	ApplicationService ->>+ ConcurrencyManager : getLock
	Note right of ConcurrencyManager: Using Named Lock
	ApplicationService ->> Service : transfer money
	Service ->> Repository : transfer money
    opt Lock Connectioned Timeout
      ConcurrencyManager->>User: 503 Service Unavailable
	end
	ConcurrencyManager ->>- ApplicationService : releaseLock
    opt Success Complete
	ApplicationService -->> NumbleAlarmService : sendAlram
	Controller ->> User : 200 OK
	end
```
