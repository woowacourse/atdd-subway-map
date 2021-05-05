# 기능 목록

## 지하철 역 관리 API

- [x] 역 생성
  - [x] 같은 이름 지하철역 생성 불가
- [x] 역 목록 불러오기
- [x] 역 삭제
  - [x] 존재하지 않는 id의 경우 예외 처리
    
## 지하철 노선 관리 API

- [x] 노선 생성
    - [x] 같은 이름 노선 생성 불가
    - [x] 같은 색깔 노선 생성 불가
    - 존재하지 않는 색깔인 경우 예외 처리(optional)
- [x] 노선 목록 조회
- [x] 노선 조회
    - [x] 존재하지 않는 id의 경우 예외 처리
- [x] 노선 수정
    - [x] 존재하지 않는 id의 경우 예외 처리
    - [x] 같은 이름 노선으로 변경 불가
    - [x] 같은 색깔 노선으로 변경 불가
    - 존재하지 않는 색깔인 경우 예외 처리(optional)
- [x] 노선 삭제
    - [x] 존재하지 않는 id의 경우 예외 처리
    
## End-to-End Test

- 노선 기능에 대한 E2E 테스트를 작성
- StationAcceptanceTest 클래스를 참고

## DAO 를 JDBC 사용하도록 수정

## Spring Bean을 사용하여 생명주기 관리