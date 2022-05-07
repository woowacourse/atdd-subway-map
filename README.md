### 1. 지하철 역 관리 API 기능 완성하기
- 지하철역 생성 시 이미 등록된 이름으로 요청한다면 에러를 응답
  - 400 Bad Request
  - StationService에서 CustomException을 만들면 될 것
  - Dao에서 존재하는지 확인하는 기능

- Dao는 static 메소드로 사용
- Service를 추가


- StationDao 기능 추가 및 테스트
  - save()
  - findAll()
  - delete() - 구현 필요
  - existByName() - 구현 필요
- StationService 기능 추가 및 테스트
  - save()
    - 중복 시 예외 발생 기능 추가
    - station 저장 기능 추가
  - findAll()
  - delete()
- StationController 수정 사항
  - Service 로직 적용
  - Dto 정적 팩토리 메소드 수정

### 2. 지하철 노선 관리 API 구현하기 + e2e 테스트
- 등록(POST) 200 ok
  `/lines`
  [ERROR] 같은 이름의 노선이 생성될 경우 예외 발생

- 전체 목록(GET) 200 ok
  `/lines`

- 단일 조회(GET) 200 ok
  `/lines/{lineId}`

- 수정(PUT) 200 ok
  `/lines/{lineId}`

- 삭제(DELETE) 204 no content
  `/lines/{lineId}`

### 3. 지하철 구간 관리 기능

#### Line 등록 변경 사항
- Line을 저장할 때 상행, 하행을 받아 저장할 수 있다.
- [ERROR] 상행과 하행 station은 존재하지 않는 station일 경우 예외가 발생한다.
- [ERROR] distance가 0이하인 경우 예외가 발생한다.
- 반환 시 Line의 Station 목록들을 반환한다.

#### Section 추가 요구사항
- Line에 새로운 section을 추가 등록할 수 있다.
  - [ERROR] 존재하지 않는 Line에 추가하는 경우 예외가 발생한다.
  - [ERROR] 존재하지 않는 station id를 추가하는 경우 예외가 발생한다.
  - 받은 station의 둘 중 하나가 상행 혹은 하행인 경우 연장되어 저장된다.

  - 받은 station이 특정 section 사이에 있는 경우 중간에 삽입된다.
  - [ERROR] 기존에 있는 section과 distance가 같거나 길 경우 예외가 발생한다.
  - 기존에 거리는 그만큼 제거되어 distance가 수정된다.

  - [ERROR] 받은 station 두개가 Line의 어디에도 포함되지 않는 경우 예외가 발생한다.
  - [ERROR] 이미 존재하는 Section인 경우 예외가 발생한다.
- Line에 기존 section station을 제거할 수 있다.
  - [ERROR] 포함되지 않는 station인 경우 예외가 발생한다.
  - [ERROR] 제거했을 때 노선이 없어지면 예외가 발생한다.
  - 중간역이 제거되는 경우 재배치를 한다. -> 사이에 있는 두 station이 distance를 합친 길이로 수정한다.
  - 종점이 제거되는 경우 다음 역이 종점이 된다. (수정되는 것 없이 삭제만 됨)


#### Sections
- 상행역에서 하행역으로 갈 수 있는 길이 이미 존재한다면 예외가 발생한다.
- 상행역 하행역 중 포함되는 역이 없다면 예외가 발생한다.

- 받은 Section이 상행 종점에 연결될 경우 상행 종점에 추가한다.
  - Service에서 단순 저장
- 받은 Section이 하행 종점에 연결될 경우 하행 종점에 추가한다.
  - Service에서 단순 저장
- 받은 Section이 현재 있는 구간 중간에 있는 경우
  - 사이에 저장할 수 있다.
  - 상행선이 일치할 경우
    - 하행역 둘을 비교한다.
    - 기존의 하행역 길이보다 긴 경우 예외가 발생한다.
    - 사이에 저장하며 변경될 Section을 반환한다.
      - 길이는 (기존 길이 - 새로 들어온 길이)
      - 상행역은 들어온 하행역이며 하행역은 기존 하행역이다.
  - 하행선이 일치할 경우
    - 상행역 둘을 비교한다.
    - 기존의 상행역 길이보다 긴 경우 예외가 발생한다.
    - 사이에 저장하며 변경될 Section을 반한환다.
      - 길이는 (기존 길이 - 새로 들어온 길이)
      - 하행역은 들어온 상행역이며  상행역은 기존 상행역이다.
