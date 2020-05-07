# 지하철 노선 관리 기능

## 레벨 1

### 요구 사항

- 인수 테스트(LineAcceptanceTest) 성공 시키기
- LineController를 구현하고 인수 테스트에 맞는 기능을 구현하기
- 테스트의 중복을 제거하기

### 기능 목록

1. 지하철 노선 추가 API

2. 지하철 노선 목록 조회 API

3. 지하철 노선 수정 API

4. 지하철 노선 단건 조회 API

5. 지하철 노선 제거 API



### 시나리오
~~~
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
~~~

### 프로그래밍 제약 사항

- 지하철 노선 이름은 중복될 수 없다.

### 미션 수행 순서

- 인수 조건 파악하기 (제공)
- 인수 테스트 작성하기 (제공)
- 인수 테스트 성공 시키기
- 기능 구현


## 레벨 2

### 요구 사항
인수 테스트를 통해 구현한 기능을 페이지에 연동하기

![img](https://techcourse-storage.s3.ap-northeast-2.amazonaws.com/2020-05-01T13%3A06%3A01.229image.png){: width="100", height="100"}

### 기능 목록
**지하철 노선 관리 페이지**
- 페이지 호출 시 미리 저장한 지하철 노선 조회  
- 지하철 노선 목록 조회 API 사용  
**노선 추가**  
- 노선 추가 버튼을 누르면 아래와 같은 팝업화면이 뜸  
- 노선 이름과 정보를 입력  
- 지하철 노선 추가 API 사용
![img](https://techcourse-storage.s3.ap-northeast-2.amazonaws.com/2020-05-01T13%3A06%3A35.629image.png){: width="100", height="100"}

**노선 상세 정보 조회**
- 목록에서 노선 선택 시 상세 정보를 조회
![img](https://techcourse-storage.s3.ap-northeast-2.amazonaws.com/2020-05-01T11%3A58%3A08.462image.png){: width="100", height="100"}

**노선 수정**
- 목록에서 우측 수정 버튼을 통해 수정 팝업화면 노출
- 수정 팝업 노출 시 기존 정보는 입력되어 있어야 함
- 정보 수정 후 지하철 노선 수정 API 사용
![img](https://techcourse-storage.s3.ap-northeast-2.amazonaws.com/2020-05-01T13%3A06%3A48.748image.png){: width="100", height="100"}

**노선 삭제**
- 목록에서 우측 삭제 버튼을 통해 삭제
- 지하철 노선 삭제 API 사용
![img](https://techcourse-storage.s3.ap-northeast-2.amazonaws.com/2020-05-01T13%3A06%3A01.229image.png){: width="100", height="100"}

