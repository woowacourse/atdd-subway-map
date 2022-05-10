### 기능 요구사항 목록
- [x] 지하철 역 관리 API 구현
    - [x] 지하철역 생성 시 이미 등록된 이름에 대한 요청이 오면 에러 응답
- [x] 지하철 노선 관리 API 구현
    - [x] 지하철 노선 등록 API 구현
        - [x] 지하철 노선 생성 시 이미 등록된 이름에 대한 요청이 오면 에러 응답
    - [x] 지하철 노선 목록 조회 API 구현
    - [x] 지하철 노선 조회 API 구현
    - [x] 지하철 노선 수정 API 구현
    - [x] 지하철 노선 삭제 API 구현

---
### 2단계 요구사항 목록
- [x] 직접 관리하던 객체를 스프링 컨테이너가 관리할 수 있도록 수정
- [x] 스프링 JDBC 적용
  - [x] DAO 에서 관리하던 정보를 DB 로 이동
  - [x] DB는 H2를 이용
- [x] 컨트롤러에서 사용되던 비즈니스 로직을 서비스 레이어로 이동

---
### 3단계 요구사항 목록
- [x] 노선을 등록하면 구간 정보도 함께 등록
- [x] 구간 추가 기능
  - [x] 갈래길 방지 기능
  - [x] 새로운 구간의 길이가 기존 구간의 길이보다 크거나 같으면 등록 불가
  - [x] 추가하는 구간의 상행과 하행이 모두 노선에 등록되어 있으면 등록 불가
  - [x] 추가하는 구간의 상행과 하행이 모두 포함되어 있지 않으면 등록 불가
- [ ] 구간 삭제 기능
  - [ ] 중간 역이 제거될 경우 재배치 기능
    - [ ] 거리는 두 구간의 거리의 합
  - [ ] 구간이 하나인 노선은 구간 제거 불가
