# 기능 구현 목록

## 역 관리
- [x] 중복되는 역의 이름이 존재하는 경우 역을 추가할 수 없다. (res : BAD_REQUEST)
- [x] StationDao를 가진 StationService 레이어가 존재한다.
- [x] DELETE 요청이 들어오는 경우 요청에 맞는 역을 찾아 삭제한다.


## 노선 관리
- LineController
    - 각 요청에 맞는 처리와 응답을 한다.
- LineService

- LineDao

- Line
    - id, name, color, List<Station>

- [x] 노선 생성
    - [x] 같은 이름은 추가할 수 없다.
    - [ ] 상행역, 하행역, 거리 입력 포함하기
        - [ ] 하나라도 값이 없는 경우 예외처리
    - [ ] req
        - POST, /lines, BODY : {color, name, upStationId, downStationId, distance}
    - res
        - 201 CREATED, Location = /lines/{id}, BODY : {id, name, color}
- [x] 노선 목록 조회
    - 전체 등록된 노선을 조회한다.
    - req
        - GET, /lines
    - res
        - 200 OK, BODY : {list - id, name, color}
- [x] 노선 조회
    - req
        - GET, /lines/{id}
    - [ ] res
        - 200 OK, BODY : {id, name, color, stations}
- [x] 노선 수정
    - [x] 이미 존재하는 이름으로 수정할 수 없다.
        - req
            - PUT, /lines/{id}, BODY : {color, name}
        - res
            - 200 OK
- [x] 노선 삭제
    - req
        - DELETE, /lines/{id}
    - res
        - 204 noContent


## 2단계 - 프레임워크 적용
1. 스프링 JDBC 활용하여 H2 DB에 저장하기
- [x] Dao 객체가 아닌 DB에서 데이터를 관리하기
- [x] DB에 접근하기 위한 spring jdbc 라이브러리를 활용하기 (JdbcTemplate 등)
2. H2 DB를 통해 저장된 값 확인하기
- [x] 실제로 DB에 저장이 잘 되었는지 확인할 수 있도록 설정하기
- [x] log, console 등
3. 스프링 빈 활용하기
- [x] 매번 생성하지 않아도 되는 객체와 싱글톤이나 static으로 구현되었던 객체들을 스프링 빈으로 관리해도 좋음


## 3단계 - 구간 관리
- [ ] 구간 생성
    - [ ] 노선 추가시 구간의 정보도 함께 등록
    - [ ] req
        - POST, /lines/{lineId}/sections, BODY : {upStationId, downStationId, distance}
    - [ ] res
        - 201 CREATED, Location = /lines/{lineId}/sections/{sectionId}, BODY : {id, lineId, upStationId, downStationId, distance}
- [ ] 구간 삭제
    - [ ] req
        - DELETE, /lines/{lineId}/sections?stationId={sectionId}
    - res
          - 204 noContent
- 구간 관리 규칙
    - 구간은 이미 등록되어 있는 역을 기준으로 상행역과 하행역을 등록한다.
    - 갈래길은 허용하지 않는다. (이미 존재하는 구간인 A-C에 A-B or B-C 구간 추가 시 A-B, B-C 노선이 존재하게 된다.)
        - 예외) 구간 사이에 새로운 역이 등록되는 경우 기존 역 사이의 길이보다 크거나 같을 수 없다.
        - 예외) 이미 등록되어 있는 구간은 등록 불가능하다.
        - 예외) 상행역과 하행역 둘 중 하나에도 포함되어 있지 않으면 등록할 수 없다.
    - 종점이 제거되는 경우 다음으로 오던 역이 종점이 된다. (A-B-C 구간에서 C역 삭제 시 A-B 구간만 존재)
        - 예외) 구간이 한 개인 노선의 경우 마지막 구간은 제거할 수 없다.
    


