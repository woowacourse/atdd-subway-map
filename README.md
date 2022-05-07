<p align="center">
    <img width="200px;" src="https://raw.githubusercontent.com/woowacourse/atdd-subway-admin-frontend/master/images/main_logo.png"/>
</p>
<p align="center">
  <a href="https://techcourse.woowahan.com/c/Dr6fhku7" alt="woowacourse subway">
    <img alt="Website" src="https://img.shields.io/website?url=https%3A%2F%2Fedu.nextstep.camp%2Fc%2FR89PYi5H">
  </a>
  <img alt="GitHub" src="https://img.shields.io/github/license/woowacourse/atdd-subway-map">
</p>

<br>

# 지하철 노선도 미션
스프링 과정 실습을 위한 지하철 노선도 애플리케이션

<br>

## 🚀 Getting Started
### Usage
#### application 구동
```
./gradlew bootRun
```
<br>

## ✏️ Code Review Process
[텍스트와 이미지로 살펴보는 온라인 코드 리뷰 과정](https://github.com/next-step/nextstep-docs/tree/master/codereview)

<br>

## 🐞 Bug Report

버그를 발견한다면, [Issues](https://github.com/woowacourse/atdd-subway-map/issues) 에 등록해주세요 :)

<br>

## 📝 License

This project is [MIT](https://github.com/woowacourse/atdd-subway-map/blob/master/LICENSE) licensed.


## 기능목록
### 1단계
- [x] 기존에 존재하는 지하철역 이름으로 지하철역을 생성할 시에 에러 처리를 한다
- [x] 지하철 노선을 등록한다.
- [x] 전체 지하철 노선 목록을 조회한다. 
- [x] 단일 지하철 노선을 조죄한다.
- [x] 지하철 노선을 수정한다.
- [x] 지하철 노선을 삭제한다.

### 2단계
- [x] 스프링 JDBC 활용하여 H2 DB에 저장하기
- [x] Dao 객체가 아닌 DB에서 데이터를 관리하기
- [x] DB에 접근하기 위한 JdbcTemplate 이용하기 
- [x] 스프링 빈 활용하기

### Dao
**JdbcStationDao**
- [x] jdbcTemplate 사용하기
- [x] Station 저장하기
- [x] Station 전체 조회하기
- [x] Station 삭제하기
- [x] Station이 DB에 저장되어 있는지 여부 알려주기

**JdbcLineDao**
- [x] jdbcTemplate 사용하기
- [x] Line 저장하기
- [x] Line 전체 조회하기
- [x] Line 1개 id로 조회하기
- [x] Line 수정하기
- [x] Line 삭제하기
- [x] Line이 DB에 저장되어 있는지 여부 알려주기

### Service
**StationService**
- [x] 컴포넌트로 등록하기
- [x] Dto와 도메인 사이에서 형변환 해주기
- [x] 지하철역 저장할 때 중복인지 검증하기
- [x] 삭제가 제대로 되지 않은 경우 (삭제하려는 역이 존재하지 않을 때) 예외 던지기

**LineService**
- [x] 컴포넌트로 등록하기
- [x] Dto와 도메인 사이에서 형변환 해주기
- [x] 지하철 노선 저장할 때 중복인지 검증하기
- [x] 삭제가 제대로 되지 않은 경우 (삭제하려는 노선이 존재하지 않을 때) 예외 던지기

### Controller
**StationController, LineController**
- [x] 요청값으로 들어오는 json을 Dto객체에 바인딩 받기
- [x] API 문서에 맞게 상태코드 반환하기

**ExceptinController**
- [x] Service에서 명시한 예외 상황(IllegalArgumentException) 에 맞게 에러 핸들링하기
- [x] Exception으로 예기치 못한 예외 상황 에러 핸들링하기
