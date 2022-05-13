## 피드백 정리

### 지하철 노선도 미션 1,2단계 - 1차 피드백

- [x] 오타
    - 요구사항 정리 오타 (17라인)
    - `LineDaoTest` - beforeEach()
- [x] `DAO`에서 사용하는 JdbcTemplate 불변
- [x] `LineDao` 사용하지 않는 메서드 존재
    - `deleteAll()`
- [x] `LineController`
    - [x] 중복 관련 에러메세지 상세화 - 어떤 속성이 중복되는지 적절한 메세지 전달 필요
    - [x] `updateLine()` - 유효성 검사 세분화
    - [x] <컨트롤러 - 서비스 - 영속성> 레이어 관련 학습
        - 서비스 레이어가 추가되었을 때 프로젝트 구조 변화
        - 컨트롤러의 역할의 변화
    - [x] `@ExceptionHandler` 예외 처리
        - 만약, Exception이 발생한다면?
        - [x] `Exception`에 대한 예외 처리 추가
- [x] `LineAcceptanceTest`
    - [x] 중복된 코드 Fixture 객체 or 메서드 분리
    - [x] `Stream`과 `람다` 활용
    - [x] 존재하지 않는 노선 제거에 대한 테스트
- [x] 테스트 DisplayName에 행위뿐만이 아닌 검증하려는 결과도 표현
    - 기존 방식과의 차이는?
    - 적용해보니 기존의 방식은 테스트하는 메서드의 행위만을 표현했다면 변경한 방식은 그 행위를 통해 받을 수 있는 결과까지 표현할 수 있음.
- [x] `RestAssured`는 어떤 기능을 제공하는 라이브러리일까?
    - REST API를 테스트하기 위한 라이브러리(실제 웹과 동일한 기능 수행)
- [x] `LineDaoTest`
    - [x] `assertAll` 검증 함수 학습 및 적용
        - assertThat과 다르게 패키지 경로가 `junit.jupiter.api~`라는 차이점도 존재
    - [x] 하나의 메서드에서 2개의 테스트를 검증
- [x] 세가지 종류의 테스트, 사용한 목적과 어떤 기능을 사용했는지 작성

### 지하철 노선도 미션 1,2단계 - 2차 피드백

- [x] `RequestBody`의 사용 목적(용도)
    - JSON 데이터를 Spring에서 Object로 변환하여 사용하기 위함.
- [x] `LineAcceptaceTest`
    - [x] when 주석 제거
    - [x] TODO 제거
    - [x] 예외 상태 코드 검증에 메시지 검증 추가하기
- [x] `InjectMock`과 `@Mock`의 차이점
    - mock : 어노테이션을 통해서 Mock 객체를 반환(즉, StationDao 클래스의 mock 객체를 반환)합니다.
    - Injectmocks : 어노테이션이 붙은 클래스의 인스턴스를 만들고 @mock을 통해 생성한 Mock 객체를 주입합니다.
- [x] `JdbcTemplate` 빈으로 등록한 적이 없으나 빈으로 관리되는 이유
  - `spirng-boot-stater-jdbc`에서 주입해주기때문에 자동 Bean 등록 가능
- [ ] 예외 메시지 - 톤 앤 매너
- [x] `ControllerAdvice`

### 지하철 노선도 미션 3단계 - 1차 피드백

- [ ] public 메서드는 테스트 필요
    - 인텔리제이의 test coverage 기능을 이용
- [ ] `Section`
    - [ ] boolean 타입을 반환하지만 메서드명이 `find`로 시작함 - 네이밍 수정 필요
    - [ ] `isLongDistance` - 더 길다의 의미를 가질 수 있도록 수정
- [ ] `Sections`
    - [ ] 역과 Station 단어가 혼용되어 사용됨. 일관성있게 수정 필요
    - [ ] `Sections()` 기본 생성자 사용하는 곳이 있는지
    - [ ] 변수명에 동사 사용 -> 메서드의 네이밍 컨벤션
    - [ ] `findDownSection`은 if문을 통과하여 도달했기에 항상 Optional에 값이 채워져있는지?
    - [ ] `addSplitByUpStation`
        - if문이 메서드에 있어야 하는지
        - add/split 두가지 동가사 이름에 들어있을을 고려
        - `stream()`에서 바로 get을 한다면 발생하는 문제
    - [ ] `removeWayPointSection`
        - 함수명에 대한 고민 필요, 내부에서는 제거뿐만 아니라 추가도함.
    - [ ] `SectionsTest`
        - [ ] 상행, 하행 종점 `Section`의 상수화
        - [ ] `hasSection()` 테스트에만 사용되기 때문에 테스트 클래스에서 private으로 사용 권장
        - [ ] 오타 수정
        - [ ] 코드를 보는 사람이 이해할 수 있도록 변수명 수정
- [ ] `LineService`
    - [ ] 원자성과 트랜잭션 학습
        - 메서드가 진행되다가 예기치 못한 상황으로 애플리케이션이 종료된다면?
    - [ ] `Section`에 대한 책임을 분리
    - [ ] 변수 이름에 자료형을 사용하면 좋지 않은 이유 (originSectionList)
    - [ ] `deleteSection()`에 대한 테스트
    - [ ] 메서드명 수정
        - 인자를 통한 의미 중복 제거
        - 메서드의 기능을 나타낼 수 있는 이름으로 수정
        - Dao와 같이 프로젝트의 구조가 메서드에 드러나는 부분 수정

