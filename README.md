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

## 기능 구현 목록

- 지하철 역
- [x] 지하철역 생성 시 이미 등록된 이름으로 요청한다면 에러를 응답

- 지하철 노선
- [x] 동일한 노선 생성 불가
- api
    - [x] post - /lines 등록 - name, color
    - [x] get - /lines - List - id, name, color
    - [x] get - /lines/{id} - id, name, color
    - [x] put - /lines/{id} - name, color
    - [x] delete - /lines/{id}

## 3단계

- [x] 노선 추가 시 3가지 정보를 추가로 입력 받는다.
    - upStationId: 상행 종점
    - downStationId: 하행 종점
    - distance: 두 종점간의 거리
- [x] 노선 추가 시 `Section`도 함께 등록한다.

### 구간 관리 API 구현

- [ ] 노선에 구간을 추가한다.
    - [ ] 새로 등록할 구간의 상행역과 하행역 중 `노선에 이미 등록되어있는 역`을 기준으로 새로운 구간을 추가한다
    - [ ] 새로운 구간이 추가될 떄, 갈래길이 생기지 않도록 기존 구간을 변경한다.
- [ ] 노선에 포함된 구간 정보를 통해 상행 종점부터 하행 종점까지의 역 목록을 보여준다.
- [ ] 구간을 제거한다.
    - [ ] 종점이 제거될 경우 다음으로 오던 역이 종점이 된다.
    - [ ] 노선에 A - B - C 역이 연결되어 있을 때 B역을 제거할 경우 A - C로 재배치 된다. 거리는 두 구간의 합이다.

- 예외 처리 사항
    - [x] 역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없다.
    - [x] 상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없다.
    - [ ] 상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없다.
    - [ ] 구간이 하나인 노선에서 마지막 구간을 제거할 수 없다.
