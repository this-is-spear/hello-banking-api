import http from 'k6/http';
import {check, sleep} from 'k6';
import encoding from 'k6/encoding';

const users = [];
const BASE_URL = __ENV.BASE_URL || 'http://localhost';

function setup() {
    // 사용자 등록 및 정보 저장한다. 이 떄 이름과 이메일은 임의 값을 부여한다.
    for (let i = 0; i < 10; i++) {
        let user = {
            email: `${generateUUID().slice(1, 5)}@test.com`,
            name: `User ${generateUUID().slice(1, 5)}`,
            password: `password${i}`,
        };

        // 사용자 등록
        let auth = `${user.email}:${user.password}`;
        let registerResponse = http.post(`${BASE_URL}/members/register`, JSON.stringify(user),
            {headers: {'Content-Type': 'application/json'}});
        check(registerResponse, {
            'registerResponse status is 200': (r) => r.status === 200,
        });

        let meResponse = http.get(`${BASE_URL}/members/me`, {
            headers: {
                'Content-Type': 'application/json',
                Authorization: `Basic ${encoding.b64encode(auth)}`,
            }
        },);
        check(meResponse, {
            'meResponse status is 200': (r) => r.status === 200,
        });
        // 정보 저장
        user.id = meResponse.json().id;

        // 자신의 계좌 등록
        let accountResponse = http.post(`${BASE_URL}/accounts`, null, {
            headers: {
                'Content-Type': 'application/json',
                Authorization: `Basic ${encoding.b64encode(auth)}`,
                'Idempotency-Key': generateUUID(),
            }
        });

        check(accountResponse, {
            'accountResponse status is 200': (r) => r.status === 200,
        });
        user.account = accountResponse.json().number;

        users.push(user);
    }
}

export default function () {
    if (__ITER === 0) {
        setup();
    }

    // 두 명의 사용자를 찾는다.
    let randomUsers = getRandomUsers(users, 2);
    let fromUser = randomUsers[0];
    let toUser = randomUsers[1];

    let fromUserAuth = `${fromUser.email}:${fromUser.password}`;
    let toUserAuth = `${toUser.email}:${toUser.password}`;

    let fromUserMeResponse = http.get(`${BASE_URL}/members/me`, {
        headers: {
            'Content-Type': 'application/json',
            Authorization: `Basic ${encoding.b64encode(fromUserAuth)}`,
        }
    },);

    check(fromUserMeResponse, {
        'fromUserMeResponse status is 200': (r) => r.status === 200,
    });

    let toUserMeResponse = http.get(`${BASE_URL}/members/me`, {
        headers: {
            'Content-Type': 'application/json',
            Authorization: `Basic ${encoding.b64encode(toUserAuth)}`,
        }
    },);

    check(toUserMeResponse, {
        'toUserMeResponse status is 200': (r) => r.status === 200,
    });

    // 두 명의 사용자가 친구 신청되어 있는지 확인한다. 응답 값은 friendResponses 배열에서 userId를 추출해 확인해야 한다.
    let friendResponses = getFriends(fromUserAuth).map(request => request.userId).filter(userId => userId === toUser.id);

    // 친구 신청이 되어 있지 않다면 진행
    if (friendResponses.length === 0) {
        // A가 B에게 친구 신청
        let friendRequestResponse = http.post(`${BASE_URL}/members/friends/${toUser.id}`, null, {
            headers: {
                Authorization: `Basic ${encoding.b64encode(fromUserAuth)}`,
            }
        });

        check(friendRequestResponse, {
            'friendResponses status is 200': (r) => r.status === 200,
        });

        // B는 친구 신청을 확인
        let requests = getFriendRequest(toUserAuth).map(request => {
            return {
                requestId: request.requestId,
                userId: request.fromUserId
            }
        });

        let requestId;

        if (requests.length === 0) {
            return;
        }

        requests.forEach(request => {
            if (request.userId === fromUser.id) {
                requestId = request.requestId;
            }
        });

        if (requestId === undefined) {
            return;
        }

        // B는 A의 친구 신청을 수락하거나 거절. 수락하는 확률을 더 높여야 함.
        let acceptRequest = Math.random() > 0.2;
        if (acceptRequest) {
            let approveRequestResponse = http.post(`${BASE_URL}/members/friends/${requestId}/approval`, null, {
                headers: {
                    Authorization: `Basic ${encoding.b64encode(toUserAuth)}`,
                }
            });
            check(approveRequestResponse, {
                'approveRequestResponse status is 200': (r) => r.status === 200,
            });
        } else {
            let rejectRequestResponse = http.post(`${BASE_URL}/members/friends/${requestId}/rejected`, null, {
                headers: {
                    Authorization: `Basic ${encoding.b64encode(toUserAuth)}`,
                }
            });

            check(rejectRequestResponse, {
                'rejectRequestResponse status is 200': (r) => r.status === 200,
            });
            return;
        }
    }

    // A는 자신의 계좌를 확인하고 임의로 선택
    let accountsResponse = http.get(`${BASE_URL}/accounts`, {
        headers: {
            'Content-Type': 'application/json',
            Authorization: `Basic ${encoding.b64encode(fromUserAuth)}`,
        }
    });
    check(accountsResponse, {
        'accountsResponse status is 200': (r) => r.status === 200,
    });

    let fromAccount = getRandomAccount(accountsResponse.json().map(account => account.number));

    // A는 선택한 계좌에 돈을 10000원 입금
    let depositResponse = http.post(`${BASE_URL}/accounts/${fromAccount}/deposit`, JSON.stringify({amount: 10000}), {
        headers: {
            'Content-Type': 'application/json',
            Authorization: `Basic ${encoding.b64encode(fromUserAuth)}`,
            'Idempotency-Key': generateUUID(),
        }
    });

    check(depositResponse, {
        'depositResponse status is 200': (r) => r.status === 200,
    });

    // A는 계좌 잔액 확인
    let ABalanceResponse = http.get(`${BASE_URL}/accounts/${fromAccount}/history`, {
        headers: {
            'Content-Type': 'application/json',
            Authorization: `Basic ${encoding.b64encode(fromUserAuth)}`,
            'Idempotency-Key': generateUUID(),
        }
    });

    check(ABalanceResponse, {
        'ABalanceResponse status is 200': (r) => r.status === 200,
        'ABalanceResponse account amount greater than 10,000': (r) => r.json("balance.amount") >= 10000,
    });

    // 이체 대상 조회
    let transferTargetResponse = http.get(`${BASE_URL}/accounts/transfer/targets`, {
        headers: {
            'Content-Type': 'application/json',
            Authorization: `Basic ${encoding.b64encode(fromUserAuth)}`,
        }
    });

    check(transferTargetResponse, {
        'transferTargetResponse status is 200': (r) => r.status === 200,
    });

    let transferTargetAccount = getRandomAccount(transferTargetResponse.json("targets").map(request => {
            return {
                account: request.accountNumber,
                email: request.email,
            }
        }).filter(request => request.email === toUser.email)
            .map(request => request.account.number)
    );

    // 10000원 이체
    let transferResponse = http.post(`${BASE_URL}/accounts/${fromAccount}/transfer`, JSON.stringify({
        amount: 10000,
        toAccountNumber: transferTargetAccount
    }), {
        headers: {
            'Content-Type': 'application/json',
            Authorization: `Basic ${encoding.b64encode(fromUserAuth)}`,
            'Idempotency-Key': generateUUID()
        }
    });

    check(transferResponse, {
        'transferResponse status is 200': (r) => r.status === 200,
    });

    // B는 계좌 잔액 확인
    let BBalanceResponse = http.get(`${BASE_URL}/accounts/${transferTargetAccount}/history`, {
        headers: {
            'Content-Type': 'application/json',
            Authorization: `Basic ${encoding.b64encode(toUserAuth)}`,
            'Idempotency-Key': generateUUID()
        }
    });

    check(BBalanceResponse, {
        'BBalanceResponse status is 200': (r) => r.status === 200,
        'BBalanceResponse account amount greater than 10,000': (r) => r.json("balance.amount") >= 10000,
    });

    // B는 10000원 출금
    let withdrawResponse = http.post(`${BASE_URL}/accounts/${transferTargetAccount}/withdraw`, JSON.stringify({amount: 10000}), {
        headers: {
            'Content-Type': 'application/json',
            Authorization: `Basic ${encoding.b64encode(toUserAuth)}`,
            'Idempotency-Key': generateUUID()
        }
    });
    check(withdrawResponse, {
        'withdrawResponse status is 200': (r) => r.status === 200,
    });

    // 간격을 두고 실행
    sleep(1);
}


function getFriends(auth) {
    let friendsResponse = http.get(`${BASE_URL}/members/friends`, {
        headers: {
            'Content-Type': 'application/json',
            Authorization: `Basic ${encoding.b64encode(auth)}`,
        }
    });
    check(friendsResponse, {
        'friendsResponse status is 200': (r) => r.status === 200,
    });

    return friendsResponse.json().friendResponses;
}

function getFriendRequest(auth) {
    let request = {};
    let friendRequestResponse = http.get(`${BASE_URL}/members/friends/requests`, {
        headers: {
            'Content-Type': 'application/json',
            Authorization: `Basic ${encoding.b64encode(auth)}`,
        }
    });
    check(friendRequestResponse, {
        'friendRequestResponse status is 200': (r) => r.status === 200,
    });

    // 응답 값은 friendResponses 배열에서 user id와 requestId를 추출해 확인해야 한다.
    return friendRequestResponse.json().askedFriendResponses;
}


// 랜덤으로 n명의 사용자 선택
function getRandomUsers(users, n) {
    let randomUsers = [];
    while (randomUsers.length < n) {
        let user = users[Math.floor(Math.random() * users.length)];
        if (!randomUsers.includes(user)) {
            randomUsers.push(user);
        }
    }
    return randomUsers;
}

// 랜덤으로 계좌 선택
function getRandomAccount(accounts) {
    return accounts[Math.floor(Math.random() * accounts.length)];
}

function generateUUID() {
    return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function (c) {
        var r = Math.random() * 16 | 0,
            v = c === 'x' ? r : (r & 0x3 | 0x8);
        return v.toString(16);
    });
}