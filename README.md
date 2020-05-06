## 지하철 정보 관리

## 기능 요구사항

### Level 1

#### 시나리오
```gherkin
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
#### TO-DO
-[x] create : 지하철 노선을 추가할 수 있다.
-[x] read : 지하철 노선을 조회할 수 있다.
    -[x] `findAll`
    -[x] `findById`
-[x] update : 지하철 노선 및 시간을 변경할 수 있다.
-[x] delete : 등록된 노선을 삭제할 수 있다.

-[ ] refactor