## 피드백 정리

### 지하철 노선도 미션 1,2단계 - 1차 피드백

- [x] 오타
    - 요구사항 정리 오타 (17라인)
    - `LineDaoTest` - beforeEach()
- [x] `DAO`에서 사용하는 JdbcTemplate 불변
- [ ] `LineDao` 사용하지 않는 메서드 존재
    - `deleteAll()`
- [ ] `LineController`
    - [ ] 중복 관련 에러메세지 상세화 - 어떤 속성이 중복되는지 적절한 메세지 전달 필요
    - [ ] `updateLine()` - 유효성 검사 세분화
    - [ ] <컨트롤러 - 서비스 - 영속성> 레이어 관련 학습
        - 서비스 레이어가 추가되었을 때 프로젝트 구조 변화
        - 컨트롤러의 역할의 변화
    - [ ] `@ExceptionHandler` 예외 처리
        - 만약, Exception이 발생한다면?
- [ ] `LineAcceptanceTest`
    - [ ] 중복된 코드 Fixture 객체 or 메서드 분리
    - [ ] `Stream`과 `람다` 활용
    - [ ] 존재하지 않는 노선 제거에 대한 테스트
- [ ] 테스트 DisplayName에 행위뿐만이 아닌 검증하려는 결과도 표현
    - 기존 방식과의 차이는?
- [ ] `RestAssured`는 어떤 기능을 제공하는 라이브러리일까?
- [ ] `LineDaoTest`
    - [ ] `assertAll` 검증 함수 학습 및 적용
    - [ ] 하나의 메서드에서 2개의 테스트를 검증
- [ ] 세가지 종류의 테스트, 사용한 목적과 어떤 기능을 상요했는지 작성
