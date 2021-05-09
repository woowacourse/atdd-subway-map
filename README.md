# 지하철역 미션 🚃

## 1단계 기능 목록

#### 1. 지하철 역 관리 API 기능 완성하기
   - [x] 같은 이름 지하철역 생성 불가 구현
   - [x] 지하철역 삭제 구현

#### 2. 지하철 노선 관리 API 구현하기
   - [x] 노선 생성 구현
   - [x] 노선 목록 조회 구현
   - [x] 노선 조회 구현
   - [x] 노선 수정 구현
   - [x] 노선 삭제 구현
   
#### 3. End to End 테스트 작성하기
   - [x] 노선 기능 E2E 테스트 작성
   
## 2단계 기능 목록
   - [x] Dao를 H2 Database로 연결하기
   - [x] 노선 수정 시 기존에 등록된 노선의 이름과 중복 확인하기 
   - [x] Dao Test Code 추가
   - [x] h2 콘솔 로그 남기기
   - [x] 지하철역 이름 패턴화 및 검증
   - [x] 노선 이름 패턴화 및 검증 
   - [x] color 중복 예외 처리하기

## 리팩토링
   - [x] Validator로 이름 유효성 검사
   - [x] Use SimpleJdbcInsert
   - [x] extract methods in tests
   - [x] leave logs
   - [x] findAll size말고 다른 방식으로 검증하기