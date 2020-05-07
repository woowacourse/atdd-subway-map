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

-[x] refactor
-[ ] 지하철 노선명 중복 체크
### Level 2

#### TO-DO

-[x] 지하철 노선 관리 페이지
    -[x] 페이지 호출 시 미리 저장한 지하철 노선 조회
    -[x] 지하철 노선 목록 조회 API 사용
-[x] 노선 추가
    -[x] 노선 추가 버튼을 누르면 아래와 같은 팝업화면이 뜸
    -[x] 노선 이름과 정보를 입력
    -[x] 지하철 노선 추가 API 사용
-[x] 노선 상세 정보 조회
    -[x] 목록에서 노선 선택 시 상세 정보를 조회
-[ ] 노선 수정
    -[ ] 목록에서 우측 수정 버튼을 통해 수정 팝업화면 노출
    -[ ] 수정 팝업 노출 시 기존 정보는 입력되어 있어야 함
    -[ ] 정보 수정 후 지하철 노선 수정 API 사용
-[ ] 노선 삭제
    -[ ] 목록에서 우측 삭제 버튼을 통해 삭제
    -[ ] 지하철 노선 삭제 API 사용

    
