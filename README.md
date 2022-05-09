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

### 3단계 
### 지하철 노선 추가 API 수정
- [ ] 노선 추가 시 3가지 정보를 추가로 입력 받기 
    - upStationId(상행 종점), downStationId(하행 종점), distance(두 종점간의 거리)\
    
- [ ] 노선 추가 시 구간(Section) 정보도 함께 등록
### 구간 관리 API 구현
- [ ] 노선에 구간을 추가한다. 
- [ ] 노선에 포함된 구간 정보를 통해 상행 종점부터 하행 종점까지의 역 목록을 조회한다.
- [ ] 구간을 제거한다. 
    - 종점이 제거될 경우 다음으로 오던 역이 종점이 된다. 
    - 중간역이 제거될 경우 재배치를 한다. 

### 예외 사항 
#### 구간 등록 
- [ ] 역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없다. 
- [ ] 상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없다.
#### 구간 제거
- [ ] 구간이 하나인 노선에서 마지막 구간을 제거할 수 없다. 
