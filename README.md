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

## 📍 목표

**1,2단계**

- 프레임워크의 역할과 사용했을 때의 장점을 이해한다.
- TDD를 최대한 적용한다.

**3단계**

- **지하철역, 노선, 구간 객체의 의존 관계 설정**에 대해 의식적으로 생각해본다.
- TDD를 최대한 적용한다.
- 단위, 통합, 인수 테스트의 역할에 대해 다시 생각해본다.
- 도메인과 엔티티의 차이에 대해 생각해본다.

### 페어 규칙

- 쉬는 시간은 따로 없이 쭉 진행한다.
- ~~일일 회고를 진행한다.~~
- 각자 코딩하는 시간은 5분으로 한다.
- 기능 구현을 우선으로 하고, 이후 리팩터링을 진행한다.
- 5월 4일 23시 59분까지 기능 구현을 완료한다.

<br>

## 🛠 기능 목록

**1단계**

- 지하철역 관리 API
    - 지하철역을 등록한다.
        - [ERROR] 요청한 이름이 공백이라면 에러를 응답한다.
        - [ERROR] 이미 등록된 이름으로 요청한다면 에러를 응답한다.
    - 지하철역 목록을 조회한다.
    - 지하철역을 삭제한다.
        - [ERROR] 존재하지 않는 ID 로 요청한다면 에러를 응답한다.
- 지하철 노선 관리 API
    - 지하철 노선을 등록한다.
        - [ERROR] 요청한 이름이 공백이라면 에러를 응답한다.
        - [ERROR] 요청한 색상이 공백이라면 에러를 응답한다.
        - [ERROR] 이미 등록된 이름으로 요청한다면 에러를 응답한다.
    - 지하철 노선 목록을 조회한다.
    - 지하철 노선을 조회한다.
        - [ERROR] 존재하지 않는 ID 로 요청한다면 에러를 응답한다.
    - 지하철 노선을 수정한다.
        - [ERROR] 존재하지 않는 ID 로 요청한다면 에러를 응답한다.
    - 지하철 노선을 삭제한다.
        - [ERROR] 존재하지 않는 ID 로 요청한다면 에러를 응답한다.

**2단계**

- DB 를 설정한다.
- 스프링 JDBC를 적용해서 리팩터링한다.
- 스프링 빈을 적용해서 리팩터링한다.

**3단계**

- 지하철 역 관리 API의 변경사항을 반영한다. 
  - 지하철 역을 삭제한다.
    - [ERROR] 해당 역을 지나는 구간이 있는 경우 예외를 발생시킨다.
- 지하철 노선 관리 API의 변경사항을 반영한다.
    - 지하철 노선을 등록한다.
        - 요청 body에 upStationId, downStationId, distance 데이터가 추가되었다.
        - **지하철 구간 정보도 함께 등록한다.**
        - 구간 정보도 함께 등록한다. 
          - 예) A역에서 B역으로의 노선을 등록한다면, A역에서 B역으로의 구간도 함께 등록한다. 
        - 응답 body에 stations를 추가한다.
          - stations에는 노선에 포함된 역들이 있어야한다. 
    - 지하철 노선 목록을 조회한다.
        - 응답 body에 stations를 추가한다.
    - 지하철 노선을 조회한다.
        - 응답 body에 stations를 추가한다.
- 지하철 구간 관리 API를 구현한다.
    - 지하철 노선에 구간을 등록한다.
      - [ERROR] upStationId, downStationId가 서로 같을 경우 예외를 발생시킨다. 
      - [ERROR] upStationId, downStationId가 모두 중복 될 경우 예외를 발생시킨다. 
      - 갈래길 방지 : A-C 구간이 있을 때 A-B 구간을 등록하면, A-C 구간이 B-C 구간으로 변경된다.
        - [ERROR] A-C 구간의 길이가 A-B 구간의 길이보다 같거나 작을 경우 예외를 발생시킨다.
        - [ERROR] A-B 구간, B-C 구간이 있을 때 A-C 구간을 등록하는 경우 예외를 발생시킨다. 
    - 지하철 노선에서 구간을 삭제한다.
      - 상행 종점이 삭제되는 경우 그 다음 역이 상행 종점이 된다. 
      - 하행 종점이 삭제되는 경우 그 전 역이 하행 종점이 된다.
      - 중간역이 제거되는 경우 구간을 재배치한다. 
        - A-B, B-C 구간이 있을 때, B역을 삭제하는 경우 A-C 구간이 생성되고 거리는 A-B, B-C 거리의 합이 된다. 
      - [ERROR] 노선에 구간이 하나일 경우 예외가 발생한다. 

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
