# 기능 구현 목록

## Step1
### 1. 지하철 역 관리 API 기능 완성하기
- [x] 중복 된 이름 테스트 통과

### 2. 지하철 노선 관리 API 구현하기
- [x] 지하철 역(Station) 객체 참고해서 Line 객체 생성
- [x] 노선 생성 Request/Response
- [x] 전체 노선 목록 조회 Request/Response
- [x] 단일 노선 조회 Request/Response
- [x] 지하철 노선 수정 Request/Response
- [x] 지하철 노선 삭제 Request/Response
  
### 3. End to End 테스트 작성하기
- [x] 노선 기능에 대한 E2E 테스트를 작성

<br>

## Step2
### 1. 스프링 JDBC를 활용하여 H2 DB에 저장하기
- [x] Dao 객체가 아닌 DB에서 데이터를 관리하도록 변경
- [x] DB에 접근하기 위한 spring jdbc 라이브러리를 활용하기(JDBCTemplate emd)

### 2. H2 DB
- [x] 실제로 DB에 저장이 잘 되었는지 확인할 수 있도록 설정하기
    - log, console 등
    
### 3. Spring Bean
- [x] 매번 생성하지 않아도 되는 객체와 싱글톤이나 static으로 구현되었던 객체들을 스프링 빈으로 관리해도 좋음.

## Step3
### 1. Section 관련 서비스 기능 작성
- [ ] Section 객체 구현
  - [ ] distance가 양수 범위 안에 존재를 해야한다.
    - 엔티티에서 잡을 것. DTO가 양수인지 아닌지를 알면 안될거같음
  - [ ] upStation과 downStation이 같아선 안된다.
    - 엔티티에서 잡을 것. DTO가 중복 검증을 알면 안될거같음
  - [ ] upStation이 null은 허용되지 않는다.
    - request DTO 레벨에서 잡아주도록 추가
  - [ ] downStation이 null은 허용되지 않는다.
    - request DTO 레벨에서 잡아주도록 추가
  - [ ] line이 null은 허용되지 않는다.
    - request DTO 레벨에서 잡아주도록 추가

- [ ] Section 추가 서비스 구현
  - [ ] 기존의 Section이 존재를 하고, 이어주는 상황
  - [ ] 기존의 Section들과 겹치는 역이 없을 경우의 예외
  - [ ] upStation, downStation이 해당 line에 이미 Section으로 등록되어 있으면 안된다.
  - [ ] 기존에 있는 Section 사이에 들어갈 경우에 두 개의 Section으로 재배치해주어야 한다.
    - 재배치되는 Section의 구간 길이는 이전의 구간 길이보다 짧아야 한다.

- [ ] Section 제거 기능 구현
  - [ ] 노선의 유일한 Section이면 제거하면 안된다.
  - [ ] 중간 Section을 제거할 경우에는 자동으로 재배치해줘야 한다.

### 2. Section 관련 Dao 기능 작성
- [ ] DB 테이블 수정 고려
- [ ] Section 데이터 베이스 추가 기능 구현
- [ ] Section  데이터 제거 기능 구현

### 3. Section과 연관된 다른 Service 기능 추가 구현
- [ ] Line 추가 시에 Section도 같이 추가 적용
- [ ] Line 조회 시에 해당 Station도 모두 조회

### 4. Line 추가시 2개의 Station 연결정보(Section)도 함께 등록
노선 추가 시 3가지 정보를 추가로 입력 받음

- upStationId: 상행 종점
- downStationId: 하행 종점
- distance: 두 종점간의 거리

3개 정보 중 1개라도 없을 경우 예외처리

### 5. Section관련 Controller 기능 구현
- [ ] 서비스와 연결