
# 1단계 - 지하철역/노선 관리 기능
### 기능 요구 사항
1. 지하철 역 관리 API 기능 완성하기
- [x] 지하철 역 이름 중복 생성 불가
- [x] 지하철 역 삭제
2. 지하철 노선 관리 API 구현하기
- [x] 노선 생성
- [x] 노선이름 중복 생성 불가
- [x] 노선 조회
- [x] 노선 수정
- [x] 노선 삭제
3. End to End 테스트 작성하기
- [x] 노선 기능 E2E 테스트 작성

# 2단계 - 프레임워크 적용
### 기능 요구 사항
1. 스프링 JDBC 활용하여 H2 DB에 저장하기
- [x] Dao 객체를 H2 Database에서 관리하기
- [x] spring jdbc 라이브러리 활용하기
2. H2 DB를 통해 저장된 값 확인하기
- [x] H2 콘솔 로그 남기기
3. 스프링 빈 활용하기
- [x] 스프링 빈으로 관리하기

# 3단계 - 지하철 구간 관리 기능
### 기능 요구 사항
1. 지하철 노선 추가 API 수정
- [ ] 노선 추가 시 3가지 정보를 추가로 입력 받음
  - upStationId: 상행 종점
  - downStationId: 하행 종점
  - distance: 두 종점간의 거리
- [x] 두 종점간의 연결 정보를 이용하여 노선 추가 시 구간(Section) 정보도 함께 등록
2. 구간 관리 API 구현
- [x] 노선에 구간을 추가
- [ ] 노선에 포함된 구간 정보를 통해 상행 종점부터 하행 종점까지의 역 목록을 응답
- [x] 구간 제거

## 📝 License

This project is [MIT](https://github.com/woowacourse/atdd-subway-map/blob/master/LICENSE) licensed.
