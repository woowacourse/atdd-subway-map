# 기능 구현 목록

## 역 관리
- [ ] 중복되는 역의 이름이 존재하는 경우 역을 추가할 수 없다. (res : BAD_REQUEST)
- [ ] StationDao를 가진 StationService 레이어가 존재한다.
- [ ] DELETE 요청이 들어오는 경우 요청에 맞는 역을 찾아 삭제한다.


## 노선 관리
- LineController
    - 각 요청에 맞는 처리와 응답을 한다.
- LineService

- LineDao

- Line
    - id, name, color, List<Station>

- [ ] 노선 생성
    - [ ] 같은 이름은 추가할 수 없다.
    - req
        - POST, /lines, BODY : {color, name}
    - res
        - 201 CREATED, Location = /lines/{id}, BODY : {id, name, color}
- [ ] 노선 목록 조회
    - 전체 등록된 노선을 조회한다.
    - req
        - GET, /lines
    - res
        - 200 OK, BODY : {list - id, name, color}
- [ ] 노선 조회
    - req
        - GET, /lines/{id}
    - res
        - 200 OK, BODY : {id, name, color}
- [ ] 노선 수정
    - req
        - PUT, /lines/{id}, BODY : {color, name}
    - res
        - 200 OK
- [ ] 노선 삭제
    - req
        - DELETE, /lines/{id}
    - res
        - 204 noContent