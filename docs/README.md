
## 지하철 노선도 미션
### 요구사항 정리
- [ ] 지하철 역 관리 API 기능 완성하기
  - [x] 지하철역 등록 `POST /stations`
    - `요청` json으로 name을 전송한다. 
    - 결과 상태 코드는 `201 Created` 이다.
    - `응답` json으로 id, name을 반환한다.
    - Location 헤더는 `/stations/{id}` 이다.
    - `예외` 지하철 역 생성 시 이름이 중복된다면 에러를 응답한다.
  - [ ] 지하철역 목록 조회 `GET /stations`
    - 결과 상태 코드는 `200 OK` 이다.
    - `응답` json으로 id, name을 가진 리스트를 반환한다.
  - [ ] 지하철역 삭제 `DELETE /stations/{id}`
    - 결과 상태 코드는 `204 No Content` 이다.
- [ ] 지하철 노선 관리 API 기능 완성하기
  - [ ] 노선 등록 `POST /lines`
    - `요청` json으로 name, color를 전송한다.
    - 결과 상태 코드는 `201 Created` 이다.
    - Location 헤더는 `/lines/{id}` 이다.
    - `응답` json으로 id, name, color를 반환한다.
  - [ ] 노선 목록 조회 `GET /lines`
    - 결과 상태 코드는 `200 OK` 이다.
    - `응답` json으로 id, name, color를 가진 리스트를 반환한다.
  - [ ] 노선 조회 `GET /lines/{id}`
    - 결과 상태 코드는 `200 OK` 이다.
    - `응답` json으로 id, name, color를 반환한다.
  - [ ] 노선 수정 `PUT /lines/{id}`
    - `요청` json으로 name, color를 전송한다.
    - 결과 상태 코드는 `200 OK` 이다.
  - [ ] 노선 삭제 `DELETE /lines/{id}`
    - 결과 상태 코드는 `204 No Content` 이다.

