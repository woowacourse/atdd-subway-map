# 지하철 노선도 미션

<hr>

# 1단계

## 지하철 역 관리 API 기능 완성하기

* 같은 이름 지하철역 생성 불가와 같은 기능을 추가
* StationController를 통해 요청을 처리하는 부분은 미리 구현되어 있음
* StationDao를 활용하여 지하철 역 정보를 관리

### API 구현

> 테스트를 통해 기능 동작 여부를 확인

* [x] 노선 생성
* [x] 노선 목록 조회
* [x] 노선 조회
* [x] 노선 수정
* [x] 노선 삭제

# 2단계

## 프레임워크 적용

* [x] 스프링 JDBC 활용하여 H2 DB에 저장하기
    * [x] Spring JDBK 의존성 추가 (build.gradle)
    * [x] H2 데이터베이스 설정(build.gradle, application.properties)
    * [x] Dao 객체가 아닌 DB에서 데이터를 관리하기
    * [x] DB에 접근하기 위한 spring jdbc 라이브러리를 활용하기 (JdbcTemplate 등)
* [x]  H2 DB를 통해 저장된 값 확인하기
    * [x] 실제로 DB에 저장이 잘 되었는지 확인할 수 있도록 설정하기
* [x]  스프링 빈 활용하기
    * [x] 매번 생성하지 않아도 되는 객체와 싱글톤이나 static으로 구현되었던 객체들을 스프링 빈으로 관리해도 좋음