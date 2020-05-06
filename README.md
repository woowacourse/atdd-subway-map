# 지하철 정보 관리 서비스

## 기능 목록

### 1단계

- [X] 지하철 노선 추가 API
- [X] 지하철 노선 목록 조회 API
- [X] 지하철 노선 수정 API
- [X] 지하철 노선 단건 조회 API
- [X] 지하철 노선 제거 API

### 2단계

- [ ] 지하철 노선 관리 페이지 연동
    - [X] 페이지 호출 시 미리 저장한 지하철 노선 조회
    - [X] 노선 추가 버튼을 통해 노선 추가
    - [ ] 노선 상세 정보 조회
    - [ ] 노선 수정
    - [ ] 노선 삭제 

## 시나리오
```
Feature: 지하철 노선 관리

  Scenario: 지하철 노선을 관리한다.
    When 지하철 노선 n개 추가 요청을 한다.
    Then 지하철 노선이 추가 되었다.
    
    When 지하철 노선 목록 조회 요청을 한다.
    Then 지하철 노선 목록을 응답 받는다.
    And 지하철 노선 목록은 n개이다.
    
    When 지하철 노선 수정 요청을 한다.
    Then 지하철 노선이 수정 되었다.

    When 지하철 노선 제거 요청을 한다.
    Then 지하철 노선이 제거 되었다.
    
    When 지하철 노선 목록 조회 요청을 한다.
    Then 지하철 노선 목록을 응답 받는다.
    And 지하철 노선 목록은 n-1개이다.
```
