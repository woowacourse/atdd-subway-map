
## 지하철 노선도 미션
### 1단계 요구사항 정리
- [x] 지하철 역 관리 API 기능 완성하기
  - [x] 지하철역 등록 `POST /stations`
    - `요청` json으로 name을 전송한다. 
    - 결과 상태 코드는 `201 Created` 이다.
    - `응답` json으로 id, name을 반환한다.
    - Location 헤더는 `/stations/{id}` 이다.
    - `예외` 지하철 역 생성 시 이름이 중복된다면 에러를 응답한다.
  - [x] 지하철역 목록 조회 `GET /stations`
    - 결과 상태 코드는 `200 OK` 이다.
    - `응답` json으로 id, name을 가진 리스트를 반환한다.
  - [x] 지하철역 삭제 `DELETE /stations/{id}`
    - 결과 상태 코드는 `204 No Content` 이다.
- [x] 지하철 노선 관리 API 기능 완성하기
  - [x] 노선 등록 `POST /lines`
    - `요청` json으로 name, color를 전송한다.
    - 결과 상태 코드는 `201 Created` 이다.
    - Location 헤더는 `/lines/{id}` 이다.
    - `응답` json으로 id, name, color를 반환한다.
    - `예외` 지하철 노선 생성 시 이름이 중복된다면 에러를 응답한다.
  - [x] 노선 목록 조회 `GET /lines`
    - 결과 상태 코드는 `200 OK` 이다.
    - `응답` json으로 id, name, color를 가진 리스트를 반환한다.
  - [x] 노선 조회 `GET /lines/{id}`
    - 결과 상태 코드는 `200 OK` 이다.
    - `응답` json으로 id, name, color를 반환한다.
  - [x] 노선 수정 `PUT /lines/{id}`
    - `요청` json으로 name, color를 전송한다.
    - 결과 상태 코드는 `200 OK` 이다.
  - [x] 노선 삭제 `DELETE /lines/{id}`
    - 결과 상태 코드는 `204 No Content` 이다.

### 2단계 요구사항 정리
- [x] 스프링 JDBC 활용하여 H2 DB에 저장하기
  - Dao 객체가 아닌 DB에서 데이터 관리
  - DB에 접근하기 위한 Spring JDBC 라이브러리 활용
- [x] H2 DB를 통해 저장된 값 확인하기
  - 실제 DB에 저장이 잘 되었는지 확인할 수 있도록 설정하기
  - h2 console 활용 가능
- [x] 스프링 빈 활용하기
