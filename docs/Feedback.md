## 피드백 정리

### 지하철 노선도 미션 1,2단계 - 1차 피드백

- [x] 오타
    - 요구사항 정리 오타 (17라인)
    - `LineDaoTest` - beforeEach()
- [x] `DAO`에서 사용하는 JdbcTemplate 불변
- [x] `LineDao` 사용하지 않는 메서드 존재
    - `deleteAll()`
- [ ] `LineController`
    - [x] 중복 관련 에러메세지 상세화 - 어떤 속성이 중복되는지 적절한 메세지 전달 필요
    - [x] `updateLine()` - 유효성 검사 세분화
    - [x] <컨트롤러 - 서비스 - 영속성> 레이어 관련 학습
        - 서비스 레이어가 추가되었을 때 프로젝트 구조 변화
        - 컨트롤러의 역할의 변화
    - [ ] `@ExceptionHandler` 예외 처리
        - 만약, Exception이 발생한다면?
        - [ ] `Exception`에 대한 예외 처리 추가
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
- [ ] 세가지 종류의 테스트, 사용한 목적과 어떤 기능을 사용했는지 작성

### 지하철 노선도 미션 1,2단계 - 2차 피드백
- [ ] `RequestBody`의 사용 목적(용도)
- [ ] `LineAcceptaceTest`
  - [ ] when 주석 제거
  - [ ] TODO 제거 
  - [ ] 예외 상태 코드 검증에 메시지 검증 추가하기
- [ ] `InjectMock`과 `@Mock`의 차이점
- [ ] `JdbcTemplate` 빈으로 등록한 적이 없으나 빈으로 관리되는 이유
- [ ] 예외 메시지 - 톤 앤 매너
- [ ] `ControllerAdvice`
  