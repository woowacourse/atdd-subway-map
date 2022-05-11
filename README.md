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

## 1단계 기능 요구사항

- 지하철 역
    - [x] 등록
        - [x] 이미 등록된 이름이라면 에러가 발생한다.
    - [x] 목록
    - [x] 삭제
        - [x] 등록되지 않은 역이라면 에러가 발생한다.

- 지하철 노선
    - [x] 등록
        - [x] 이미 등록된 이름이라면 에러가 발생한다.
    - [x] 목록
    - [x] 조회
        - [x] 등록되지 않은 노선이라면 에러가 발생한다.
    - [x] 수정
        - [x] 이미 등록된 이름이라면 에러가 발생한다.
    - [x] 삭제
        - [x] 등록되지 않은 노선이라면 에러가 발생한다.

## 2단계 기능 요구사항

- [x] 스프링 JDBC를 활용하여 H2 데이터베이스에 데이터를 관리한다.
- [x] 스프링 빈을 활용하여 객체를 관리한다.

## 3단계 기본 기능 요구사항

- 지하철 노선
    - [x] 지하철 노선 등록시 상행종점, 하행종점, 두 종점간의 거리를 추가로 입력받는다.
        - [x] 종점이 등록되지 않은 지하철 역이라면 에러가 발생한다.
        - [x] 상행종점과 하행종점이 같다면 에러가 발생한다.
        - [x] 거리가 1이상의 정수가 아니라면 에러가 발생한다.
- [x] 두 종점간의 연결정보를 저장하는 구간 정보가 함께 등록된다.

## 3단계 추가 기능 요구사항

- [ ] 지하철 노선에 구간을 추가할 수 있다.
    - [x] 상행 종점을 추가할 수 있다.
    - [x] 하행 종점을 추가할 수 있다.
    - [ ] 기존 역 사이의 거리보다 크다면 추가할 수 없다.
    - [ ] 이미 등록되어 있는 상행역과 하행역이라면 추가할 수 없다.
    - [ ] 상행역과 하행역 중 하나에 포함되어있지 않으면 추가할 수 없다.
- [ ] 지하철 노선에 구간을 삭제할 수 있다.
    - [ ] 종점이 제거된 경우 다음으로 오던 역이 새로운 종점이 된다.
    - [ ] 삭제된 구간은 재배치 된다. (이때, 거리는 두 구간의 합으로 한다.)
    - [ ] 구간이 하나인 노선에서는 삭제할 수 없다.
