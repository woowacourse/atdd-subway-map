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
- [ ] 노선 기능에 대한 E2E 테스트를 작성

<br>

## Step2
### 1. 스프링 JDBC를 활용하여 H2 DB에 저장하기
- [ ] Dao 객체가 아닌 DB에서 데이터를 관리하도록 변경
- [ ] DB에 접근하기 위한 spring jdbc 라이브러리를 활용하기(JDBCTemplate emd)

### 2. H2 DB
- [ ] 실제로 DB에 저장이 잘 되었는지 확인할 수 있도록 설정하기
    - log, console 등
    
### 3. Spring Bean
- [ ] 매번 생성하지 않아도 되는 객체와 싱글톤이나 static으로 구현되었던 객체들을 스프링 빈으로 관리해도 좋음.