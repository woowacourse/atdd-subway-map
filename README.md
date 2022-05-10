# 지하철 노선도 미션
스프링 과정 실습을 위한 지하철 노선도 애플리케이션

## 🚀 1단계 기능 요구 사항

### 1. 지하철 역 관리 API 기능 완성하기
- [x] 지하철 역을 등록할 수 있다.
- [x] 지하철 역을 조회할 수 있다.
- [x] 지하철 역을 삭제할 수 있다.
- [x] [ERROR] 이미 등록된 이름의 지하철 역은 생성할 수 없다.
- [x] [ERROR] 존재하지 않는 지하철 역은 제거할 수 없다.

### 2. 지하철 노선 관리 API 구현하기
- [x] 지하철 노선을 등록할 수 있다.
- [x] 지하철 노선을 목록을 조회할 수 있다.
- [x] 지하철 노선을 조회할 수 있다.
- [x] 지하철 노선을 수정할 수 있다.
- [x] 지하철 노선을 삭제할 수 있다.
- [x] [ERROR] 이미 등록된 이름의 지하철 노선은 생성할 수 없다.
- [x] [ERROR] 존재하지 않는 지하철 노선은 조회할 수 없다.
- [x] [ERROR] 존재하지 않는 지하철 노선은 제거할 수 없다.

## 🚀 2단계 기능 요구 사항

### 1. 스프링 JDBC 활용하여 H2 DB에 저장하기
- [x] Dao에서 데이터를 DB에 저장한다.

## 🚀 3단계 기능 요구 사항
### 지하철 노선 추가 API 수정
- [x] 지하철 노선 등록
    - [x] 요청) 노선 추가 시 3가지 정보를 추가로 입력받는다
        - [x] upStationId(상행 종점), downStationId(하행 종점), distance(두 종점 간 거리)
    - [x] 응답) 포함된 역들(stations) 리스트를 함께 반환한다.
    - [x] 노선 추가 시 구간(section) 정보도 함께 등록한다.
    - [x] [ERROR] 노선 추가 시 upstaionId와 downStationId사이에 같은 노선 구간이 존재하는 경우
- [x] 지하철 노선 목록 조회
    - [x] 응답) 포함된 역들(stations) 리스트를 함께 반환한다.
- [x] 지하철 노선 ID로 조회
    - [x] 응답) 포함된 역들(stations) 리스트를 함께 반환한다.


### 구간 관리 API 구현
- [ ] 구간 등록
    - [ ] 요청) 구간 등록 시 3가지 정보를 입력받는다.
        - [x] upStationId, downStationId, distance
    - [ ] 구간을 추가하며 상-하행 종점을 추가 할 수 있다.
    - [ ] A-C 존재 시 A-B 추가시 B-C 구간을 새로 만든다.(갈래길을 방지한다)
        - [ ] A-C 길이 7일 시, A-B가 길이4 면 새 구간 B-C의 길이는 3이다.
    - [ ] [ERROR] 역 사이에 새로운 역 추가 시 기존 역간 길이보다 추가하는 새로운 역 길이가 크거나 같은 경우
      - [ ] 사이에 구간을 추가할 경우, 사이 구간은 기존 구간 길이보다 작아야한다.
    - [ ] [ERROR] 기존에 등록된 구간의 일부일 경우 구간을 등록할 수 없다.
    - [ ] 상,하행역 둘 중 하나는 무조건 구간에 추가되어 있어야한다.  
    - [ ] 응답) 200 ok
- [ ] 구간 제거
    - [ ] 요청) requestParam으로 stationId를 입력받는다.
    - [ ] 응답) 200 ok
    - [ ] 종점이 제거되는 경우 다음으로 오던 역이 종점이다.
    - [ ] 구간 사이 중간 역이 제거되는 경우, 새 구간이 생긴다.
      - [ ] 새 구간의 길이는 기존 역간 길이를 더한다.
    - [ ] [ERROR] 구간이 하나만 있는 노선에서는 구간을 제거할 수 없다.
  

### 프로그램 흐름
1. 역들을 등록한다
- 역 등록시에는 그냥 무슨 역인지 역 이름만으로 등록한다.
2. 노선을 새로 만든다
- 노선을 만들 때에는 등록한 역들 중 상행 종점과 하행 종점을 추가해서 만든다.
  - 이때, 처음의 상행 종점과 하행 종점을 연결하는 구간을 추가한다.
3. 구간을 추가한다
- 구간을 만들 때에는 등록한 노선을 먼저 선택한다.
- 노선을 선택한 후 + 버튼을 통해 구간을 추가할 수 있다.
  - 기본으로 상행 종점, 하행 종점이 있고, 새 구간을 추가한다.
- 구간 추가 시에는 상행역, 하행역, 상-하행역 간의 거리를 추가한다.

### 나중에 체크해볼 것
- [x] lineService.save() 에서 sectionService.save()에서 예외나면 롤백되나 확인
- [ ] 구간 추가 시 A-B, B-A 같은지 체크
- [ ] 구간 추가 시 A-B-C라는 구간이 1호선에 있을 때 A-B를 2호선으로 등록할 수 있게 하기
