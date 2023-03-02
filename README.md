# numble-banking-api
Numble Challenge - Banking API

### API

https://this-is-spear.github.io/numble-banking-api/

### Development Environment

- Back-End : Spring-Boot, Spring-Security, JPA, MySQL, Testcontainers
- Fornt-End : Thymeleaf
- Cloud : AWS - RDS
- Infra : Docker
- Document : Rest Docs

### How to Build

1. execute docker docker
2. install mysql 8.0 (read application-local.yml)
3. gradle build
4. gradle bootRun
5. http://localhost:8080/docs/index.html 접속

### Entity Relation Diagram

```mermaid
erDiagram
	User {
		long id PK
		string email
		string anme
		string password
	}
	Account{
		long id PK
		long userId
		string number
		long balance
	}
	AccountHistory {
		long id PK
		long fromAccountNumber
		long toAccountNumber
		string type
		long amount	
		long money	
	}
	
	Friend{
		Long id PK
		Long fromUserId
		Long toUserId
	}
	
	FriendRequestHistory {
		Long id PK
		Long fromUserId
		Long toUserId
		Boolean approval
	}
	
	User ||--|| Friend : has
	Friend ||--|| FriendRequestHistory : contain
	User }|--|{ Account : own	
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
