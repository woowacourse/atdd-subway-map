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
- [x] 노선 추가시 upStationId(상행 종점), downStationId(하행 종점), distance(두 종점사이 거리)를 추가로 받음
  - [x] 상행 종점, 하행 종점은 달라야 함
  - [x] distance는 양의 정수만 허용
- [x] 노선 목록/조회 시 포함된 모든 역에 대한 정보도 body에 담아서 줌
- [x] 특정 노선에 section을 추가
  - [x] 추가되는 section의 역 a-b에서 a 혹은 b는 이미 존재하는 SECTION에 포함되어 있는 역이여야 함(둘다 포함 혹은 둘다 미포함이면 예외)
  - [x] 추가하는 section a-c 는 이미 존재하는 a-b 사이의 거리보다 작아야 함. 이 외는 모두 예외(같거나 크면 안됨)
  - [x] 갈래길은 없음 a-b(5)에 a-c(3)을 추가하면 a-c-b(3, 2)가 됨
