## 기능목록

### 지하철역/노선 관리 기능

- [x] 기존에 존재하는 지하철역 이름으로 등록을 시도하면 400을 반환하는 기능
- [x] 노선 생성 기능
- [x] 노선 목록 조회 기능
- [x] 노선 조회 기능
- [x] 노선 수정 기능
- [x] 노선 삭제 기능

### 2단계 프레임워크 적용

- [x] h2, schema 설정
- [x] Service Layer 구현
    - [x] StationService 구현
    - [x] LineService 구현

- [x] 스프링 JDBC를 적용
    - [x] Station Repository
        - [x] Save, Exist 기능 구현
        - [x] get 기능 구현
        - [x] delete 기능 구현
    - [x] Line Repository
        - [x] 이름이랑 색깔을 입력받아 Line 생성기능 구현
        - [x] Line이 존재하는지 확인하는 기능 구현
        - [x] 전체 Line 리스트 조회 기능 구현
        - [x] id를 통해 특정 Line 조회 기능 구현
        - [x] id를 통해 특정 Line 정보 수정 기능 구현
        - [x] id를 통해 특정 Line 삭제 기능 구현

### 3단계 구간 관리 기능

- [x] 노선 추가시 (상행 종점, 하행 종점, 두 종점간의 거리)를 추가로 입력받는다
- [x] section table schema 수정하기 (POST /lines)
    - [x] line_id (foreign key) -> line table의 id
    - [x] 상행선 station id (foreign key) -> station table의 id
    - [x] 하행선 station id (foreign key) -> station table의 id
- [x] 종점간의 연결 정보를 이용하여 노선 추가 시 구간(Section) 정보도 함께 등록

- [x] 노선 조회 시 노선에 포함된 역 목록도 반환해준다 (json List 형태)

- [x] 구간 제거 기능 (DELETE /lines/1/sections?stationId=2)
    - [x] 구간이 하나인 노선에서 마지막 구간을 제거할 때 제거 할 수 없음
    - [x] 종점이 제거될 경우 다음으로 오던 역이 종점이 됨
    - [x] 노선에 A - B - C 역이 연결되어 있을 때 B역을 제거할 경우 A - C로 재배치 됨
    - [x] 거리는 두 구간의 거리의 합으로 정함

- [x] 노선에 구간 추가 기능 (POST /lines/1/sections)
    - [x] 하행선, 상행선, 거리 입력 받는다
    - [x] 역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없음
    - [x] 상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없음
    - [x] 상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없음