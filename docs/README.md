# README

## 차리 & 포키 페어 규칙

- 밥은 거르지 말기
- 의견 충돌 발생 시 노션에 기록
    - 토론한 과정
    - 풀리지 않은 의문
- 9시 46분은 9시 45분이 아니다
- 의견이 너무 팽팽할때는 “새우”라고 외치고 제 3의 대안을 생각해보기
    - 잠깐 생각 환기, cool down
- 토론 시간 아까워하지 말기
- 꽂혀서 급발진할 때 서로서로 break 잘 걸어주기~^.^
- 사이좋게 지내기
- 방역수칙 준수
- 쉬는 시간 “적절히” 가지기~🔥

## 목표

### 공통

- 🐶쩌는 테스트
    - 철저한 테스트 분리
    - 스프링 통합 테스트 경험
    - 목적이 확실한 테스트
    - 언제나 성공하는 테스트 환경 보장
- 🐶쩌는 RESTful 같은 HTTP API 설계

### 차리

- DB 스키마 설계 대충하지 않기.

### 포키

- 동료가 이해하기 좋은 Controller, Service

---

## 기능 요구 사항

### 1. 지하철 역 관리 API 기능 완성하기

- **`StationController`**를 통해 요청을 처리하는 부분은 미리 구현되어 있음
- **`StationDao`**를 활용하여 지하철 역 정보를 관리
- 추가 기능: 지하철역 생성 시 이미 등록된 이름으로 요청한다면 에러를 응답

### 2. 지하철 노선 관리 API 구현하기

- 지하철역과 마찬가지로 같은 이름의 노선은 생성 불가
- 노선 관리 API에 대한 스펙은 [API 문서v1](https://techcourse-storage.s3.ap-northeast-2.amazonaws.com/d5c93e187919493da3280be44de0f17f#Line)를 참고

### 3. End to End 테스트 작성하기

- 노선 기능에 대한 E2E 테스트를 작성
- **`StationAcceptanceTest`** 클래스를 참고

## Source

- 역(Station)
    - 역을 등록한다. `POST /stations -> 201(Created)`
        - [x]  이미 존재할 경우 예외를 던진다.
    - 역 목록을 보여준다. `GET /stations -> 200(OK)`
    - [x] 역을 삭제한다. `DELETE /stations/{id} -> 204(No Content)`
- 노선(Line)
    - [x]  노선을 등록한다. `POST /lines -> 201(Created)`
    - [x]  노선의 목록을 보여준다. `GET /lines -> 200(OK)`
    - [x]  특정 노선을 조회한다. `GET /lines/{id} -> 200(OK)`
    - [x]  특정 노선을 수정한다. `PUT /lines/{id} -> 200(OK)`
    - [x]  특정 노선을 삭제한다. `DELETE /lines/{id} -> 204(No content)`

- Spring
  - [ ] JDBC 활용하여 H2 DB에 저장하기
  - [ ] H2 Console을 활용하여 DB에 저장이 잘 되었는지 확인할 수 있도록 설정하기
  - [x] Spring Bean 활용하기

## Test

- 노선 기능에 대한 E2E Test를 만든다.