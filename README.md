# numble-banking-api
Numble Challenge - Banking API

### 테스트

- 서버는 `run.sh` 를 실행하면 됩니다.

> 간혹 pinpoint-hbase 이 정상 실행하기 전에 pinpoint-collector 가 실행되어 apm 이 정상적으로 실행되지 않는 경우가 존재합니다. 이런 경우 collector를 재실행 해주세요.

- 테스트는 `test.sh` 를 실행하면 됩니다.


### Development Environment

- Back-End : Spring-Boot, Spring-Security, JPA, MySQL, Testcontainers
- Front-End : Thymeleaf
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
