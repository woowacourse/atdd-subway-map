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

# 기능 요구사항

## 지하철 노선 관리 기능

1. 지하철 노선을 삭제할 수 있다.
    - 존재하지 않는 노선을 삭제하는 경우 예외 처리
2. 지하철 노선을 수정할 수 있다.
    - 존재하지 않는 노선을 수정하는 경우 예외 처리
3. 지하철 노선을 추가할 수 있다.
    - 이미 존재하는 이름이나 색상은 추가할 수 없다.
    - 노선을 추가하면서 상행역, 하행역, 거리를 입력받은 후 구간을 추가한다.
4. 지하철 노선을 조회할 수 있다.
    - 존재하지 않는 노선을 조회하는 경우 예외 처리
    - 포함하는 지하철 역을 상행 - 하행 순으로 조회한다.
5. 지하철 노선 목록을 조회할 수 있다.

## 지하철 역 관리 기능

1. 지하철역을 등록할 수 있다.
   - 이미 존재하는 이름의 지하철역은 등록할 수 없다.
2. 지하철역 목록을 조회할 수 있다.
3. 지하철역을 삭제할 수 있다.
   - 존재하지 않는 지하철역을 삭제하는 경우 예외 처리

## 구간 기능

1. 구간을 등록할 수 있다.
   - 상행 역과 하행 역이 이미 노선 안에 존재한다면 저장할 수 없다.
   - 상행 역과 하행 역 모두 노선 안에 존재하지 않는다면 저장할 수 없다.
   - 하행 역이 같은 다른 구간이 존재하는 경우
      - 기존 구간보다 새로운 구간의 길이가 더 길면 저장할 수 없다.
      - 새로운 구간, 새로운 구간의 하행역-기존 구간의 하행역으로 분할하여 저장한다.
   - 상행 역이 같은 다른 구간이 존재하는 경우
      - 기존 구간보다 새로운 구간의 길이가 더 길면 저장할 수 없다.
      - 새로운 구간, 기존 구간의 상행역-새로운 구간의 상행역으로 분할하여 저장한다.

2. 구간을 제거할 수 있다.
   - 지하철 역이 상행 종점이거나 하행 종점인 경우 해당 구간을 제거해 준다.
   - 지하철 역이 상행과 하행에 겹쳐있는 경우, 두 구간을 하나로 합쳐준다.

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
