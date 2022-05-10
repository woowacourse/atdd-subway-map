# 지하철 노선도 미션🚃

---

## 1단계 기능 요구사항 
### 지하철 역 정보 관리 API
- [x] 등록 기능
- [x] 목록 조회 기능
  - [x] 이미 등록된 이름이면 에러 응답 
- [x] 삭제 기능

### 지하철 노선 관리 API
- [x] 등록 기능
  - [x] 이미 등록된 이름이면 에러 응답
- [x] 전체 목록 조회 기능
- [x] 단일 노선 조회 기능
- [x] 수정 기능
  - [x] 수정 된 노선 이름이 중복이면 에러 응답
- [x] 삭제 기능

## 1단계 프로그래밍 제약 사항
- 스프링 빈 사용 금지(@controller 제외)

---

## 2단계 기능 요구사항
- [x] 스프링 빈 활용하기
- [x] 스프링 JDBC 활용하여 H2 DB에 저장하기
  - [x] Dao 객체가 아닌 DB에서 데이터를 관리하기
- [x] H2 DB를 통해 저장

---

## 3 단계 기능 요구사항 🤸‍
### 지하철 노선 추가 API 수정
- [ ] 노선 추가 시 3가지 정보를 추가로 입력 받음
  - [ ] 상행 종점, 하행 종점, 두 종점간 거리
- [ ] 두 종점간의 연결 정보를 이용하여 노선 추가 시 구간(Section) 정보도 함께 등록

### 구간 관리 API 구현
#### 구간 등록
새로 등록할 구간의 상행역과 하행역 중 노선에 이미 등록된 역 기준 새 구간 추가
- [x] 상행 종점 등록
- [x] 하행 종점 등록
- 갈래길 방지
  - [x] 새로운 구간이 추가될 때 갈래길이 생기지 않도록 기존 구간 변경
- 예외
  - [x] 역 사이 새로운 역 등록시 기존 역 사이 길이보다 크거나 같으면(>=) 등록 불가
  - [x] 상행역과 하행역이 이미 노선에 모두 등록되어 있다면 등록 불가
  - [x] 상행역과 하행역 모두가 포함되어있지 않으면 등록 불가

#### 구간 삭제
- [x] 종점이 삭제될 경우 인접한 역이 종점이 됨
- [x] 중간역이 삭제될 경우 거리는 상행, 하행 두 구간 거리의 합이 됨
- 예외
  - [x] 구간이 하나인 노선에서 구간 삭제 불가


## 도메인 설계
### 역 (station)
#### 역 등록 (add)
- 상행 종점 등록
  - 역 목록 가장 앞에 추가
- 하행 종점 등록
  - 역 목록 가장 뒤에 추가
- 중간 역 등록
  - 상행역의 하행역을 등록할 역으로 지정
  - 하행역의 상행역을 등록할 역으로 지정

## 도메인 용어 사전
#### 종점 (destination)
양 끝 역
- 상행 종점 (up destination)
  - 왼쪽 끝 역
- 하행 종점 (down destination)
  - 오른쪽 끝 역

#### 역 (station)
- 상행역 (up station)
  - 이전 역
- 하행역 (down station)
  - 다음 역
  
#### 역 목록 (stations)

#### 호선 (line)

