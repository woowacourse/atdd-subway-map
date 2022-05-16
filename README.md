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

## 기능 요구 사항

### 지하철역

- 지하철역 등록 시 필요한 정보
  - 지하철역 이름
- [x] 지하철역을 등록한다.
  - [x] 동일한 이름의 지하철역을 등록할 수 없다.
- [x] 지하철역의 목록을 조회한다.
- [x] 지하철역을 삭제한다.

### 지하철 노선

- 노선 등록 시 필요한 정보
  - 노선 이름
  - 노선 색상
  - 상행 종점
  - 하행 종점
  - 두 종점 간의 거리
- [x] 지하철 노선을 등록한다.
  - [x] 동일한 이름의 지하철 노선을 등록할 수 없다.
  - [x] 동일한 색상의 지하철 노선을 등록할 수 없다.
  - [ ] 상행역과 하행역이 같은 역일 수 없다.
  - [ ] 거리는 1 이상의 정수여야 한다.
- [x] 지하철 노선의 전체 목록을 조회한다.
- [x] 지하철 노선을 조회한다.
- [x] 지하철 노선을 수정한다.
  - [x] 동일한 이름의 지하철 노선으로 수정할 수 없다.
  - [x] 동일한 색상의 지하철 노선으로 수정할 수 없다.
- [x] 지하철 노선을 삭제한다.

### 지하철 구간

- 구간 등록 시 필요한 정보
  - 상행역
  - 하행역
  - 구간의 거리
- [x] 지하철 구간을 등록한다.
  - [x] 상행역과 하행역이 이미 모두 등록된 경우 등록할 수 없다.
  - [x] 상행역과 하행역이 모두 등록되지 않은 경우 등록할 수 없다.
  - [x] 상행역과 하행역이 같은 경우 등록할 수 없다.
  - [x] 구간의 길이가 기존 역 사이의 길이보다 크거나 같은 경우 등록할 수 없다.
- [ ] 지하철 구간을 삭제한다.
  - [ ] 구간이 하나인 경우 삭제할 수 없다.

### 프레임워크 적용

- [x] 스프링 JDBC를 활용하여 H2 DB에 저장한다.
- [x] H2 DB를 통해 저장된 값을 확인한다.
- [x] 스프링 빈을 활용한다.


## ✏️ Code Review Process
[텍스트와 이미지로 살펴보는 온라인 코드 리뷰 과정](https://github.com/next-step/nextstep-docs/tree/master/codereview)

<br>

## 🐞 Bug Report

버그를 발견한다면, [Issues](https://github.com/woowacourse/atdd-subway-map/issues) 에 등록해주세요 :)

<br>

## 📝 License

This project is [MIT](https://github.com/woowacourse/atdd-subway-map/blob/master/LICENSE) licensed.
