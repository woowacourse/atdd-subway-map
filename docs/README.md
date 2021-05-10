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
    - [x] 상행역, 하행역, 거리 입력 포함하기
      - [x] 하나라도 값이 없는 경우 예외처리
    - [x] 초기 구간을 생성한다.
    - [x] req
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
    
## 구간 관리
- [ ] 구간 생성
  - [ ] 노선 추가시 구간의 정보도 함께 등록
        - [ ] upStationId가 존재하지 않는 지하철 역이면 예외 발생
        - [ ] downStationId가 존재하지 않는 지하철 역이면 예외 발생
        - [ ] upStationId와 downStationId가 같은 경우 예외 발생
  - [ ] req
        - POST, /lines/{lineId}/sections, BODY : {lineId, upStationId, downStationId, distance}
  - [ ] res
        - 201 CREATED, Location = /lines/{lineId}/sections/{sectionId}, BODY : {id, lineId, upStationId, downStationId, distance}
  - [ ] 구간 추가 시 고려할 점
        - [ ] 구간의 up이 현재 노선의 구간들의 up / down 어디에도 속하지 않고, 구간의 down이 구간들의 start 중 하나에 속하는 경우
            - [ ] 종점인 경우 그냥 추가
            - [ ] 종점이 아닌 경우
                - [ ] 거리 비교 후 구간 업데이트하고 삽입
                    - [ ] 추가하려는 구간의 down을 현재 down으로 가지고있는 구간 찾기
                    - [ ] 찾은 구간의 거리와 추가하려는 구간의 거리 비교
                        - [ ] 추가하려는 구간의 거리 >= 찾은 구간의 거리 -> 예외 발생
                    - [ ] 찾은 구간의 down을 추가하려는 구간의 up으로 update
                    - [ ] 추가하려는 구간을 구간 테이블에 저장
        - [ ] 구간의 down이 현재 노선의 구간들의 up / down 어디에도 속하지 않고, 구간의 up이 구간들의 end 중 하나에 속하는 경우
            - [ ] 종점인 경우 그냥 추가
            - [ ] 종점이 아닌 경우
                - [ ] 거리 비교 후 구간 업데이트하고 삽입
                    - [ ] 거리 비교 후 구간 업데이트하고 삽입
                        - [ ] 추가하려는 구간의 up을 현재 up으로 가지고있는 구간 찾기
                        - [ ] 찾은 구간의 거리와 추가하려는 구간의 거리 비교
                            - [ ] 추가하려는 구간의 거리 >= 찾은 구간의 거리 -> 예외 발생
                        - [ ] 찾은 구간의 up을 추가하려는 구간의 down으로 update
                        - [ ] 추가하려는 구간을 구간 테이블에 저장
        - [ ] 구간의 down은 현재 노선의 구간들의 up / down 어디에도 속하지 않고, 구간의 up이 구간들의 start 중 하나에 속하는 경우
            - [ ] 거리 비교 후 구간 업데이트 하고 삽입
                - [ ] 추가하려는 구간의 up을 현재 up으로 가지고있는 구간 찾기
                - [ ] 찾은 구간의 거리와 추가하려는 구간의 거리 비교
                    - [ ] 추가하려는 구간의 거리 >= 찾은 구간의 거리 -> 예외 발생
                - [ ] 찾은 구간의 up을 추가하려는 구간의 down으로 update
                - [ ] 추가하려는 구간을 구간 테이블에 저장
        - [ ] 구간의 up은 현재 노선의 구간들의 up / down 어디에도 속하지 않고, 구간의 down이 구간들의 end 중 하나에 속하는 경우
            - [ ] 추가하려는 구간의 down을 현재 down으로 가지고있는 구간 찾기
            - [ ] 찾은 구간의 거리와 추가하려는 구간의 거리 비교
                - [ ] 추가하려는 구간의 거리 >= 찾은 구간의 거리 -> 예외 발생
            - [ ] 찾은 구간의 down을 추가하려는 구간의 up으로 update
            - [ ] 추가하려는 구간을 구간 테이블에 저장
        - [ ] 나머지 경우 모두 예외
- [ ] 구간 삭제
    - [ ] req
        - DELETE, /lines/{lineId}/sections?stationId={sectionId}
    - res
        - 204 noContent
    - [ ] 구간이 하나인 노선인 경우 삭제 불가
    - [ ] 중간 구간이 제거된 경우
        - [ ] 삭제하려는 구간의 up을 down으로 가지고있는 구간을 찾아, 찾은 구간의 down을 삭제하려는 구간의 down으로 변경
        - [ ] 삭제하려는 구간의 down을 up으로 가지고있는 구간을 찾아, 찾은 구간의 up을 삭제하려는 구간의 up으로 변경
    - [ ] 종점이 제거된 경우
    
## 2단계 - 프레임워크 적용
1. 스프링 JDBC 활용하여 H2 DB에 저장하기
  - [x] Dao 객체가 아닌 DB에서 데이터를 관리하기
  - [x] DB에 접근하기 위한 spring jdbc 라이브러리를 활용하기 (JdbcTemplate 등)
2. H2 DB를 통해 저장된 값 확인하기
  - [x] 실제로 DB에 저장이 잘 되었는지 확인할 수 있도록 설정하기
  - [x] log, console 등
3. 스프링 빈 활용하기
  - [x] 매번 생성하지 않아도 되는 객체와 싱글톤이나 static으로 구현되었던 객체들을 스프링 빈으로 관리해도 좋음