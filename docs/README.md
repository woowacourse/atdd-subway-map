### 1단계 기능 목록

1. 지하철 역 관리 API 기능 완성
    - 지하철 역 생성 시 이미 등록된 이름으로 요청한다면 에러 응답
2. 지하철 노선 관리 API 구현
    - 지하철 노선 등록
        - 등록된 이름으로 요청하면 에러 응답
    - 지하철 노선 목록
    - 지하철 노선 조회
        - 조회된 노선이 없는 경우 에러 응답
    - 지하철 노선 수정
        - 중복된 이름으로 수정 시 에러 응답
    - 지하철 노선 삭제
3. End to End 테스트 작성

### 2단계 기능 목록

1. 데이터를 H2 DB에 저장하기
    - H2 DB 설정하기
    - 테이블 생성하기
2. Spring Bean & Spring JDBC 적용하기
    - `@Controller`, `@Service`, `@Repository` 적용
    - 예외 `@ExceptionHandler`로 처리
    - JdbcTemplate는 `NamedJdbcTemplate` 사용

### 리펙토링 목록
- [x] LINE table column unique 확인
- [x] @Controller & @RestController 차이점 알아보고 적용하기
- [ ] Line Dto -> Line Response로 사용하기
- [ ] 생성자 정리
- [ ] 중복확인 로직 은닉화
- [ ] equals & hashcode 확인하고 적용하기
- [ ] Service & Repository test 작성
- [ ] Service 에서 Dto반환

### 페어 규칙

- 불변 지향
- TDD 지키기
- 모델에서는 toString() 재정의
- 이해안된 것은 꼭 물어보기
- 5분 단위로 페어 프로그래밍
- 의견을 일치시킨 후에 코드 작성하기
