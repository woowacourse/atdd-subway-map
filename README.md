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

# 기능 구현 목록

## 지하철역

* DAO

- [x] 새로운 역을 저장한다.
    - [x] 똑같은 이름의 역이 존재할 경우 예외를 발생시킨다.
- [x] 역 목록을 조회한다.
- [x] 역을 삭제한다.
    - [x] 존재하지 않는 역을 삭제하려는 경우 예외를 발생시킨다.

* Controller

- [x] 새로운 역을 등록한다.
  - [x] 성공적으로 등록된 경우 상태코드 201로 응답한다.
  - [x] 똑같은 이름의 역이 존재할 경우 예외를 응답한다.
- [x] 역 목록을 조회한다.
  - [x] 등록된 모든 역의 id와 name을 응답한다.
- [x] 역을 삭제한다.
  - [x] 성공적으로 삭제한 경우 상태코드 204로 응답한다.
  - [x] 존재하지 않는 역을 삭제하려는 경우 예외를 응답한다.

## 노선

* DAO

-[ ] 노선을 저장한다
    -[ ] 똑같은 이름의 노선이 존재할 경우 예외를 발생시킨다.
-[ ] 노선 목록을 조회한다.
-[ ] 노선 하나를 조회한다.
    -[ ] 존재하지 않는 노선을 조회하려는 경우 예외를 발생시킨다.
-[ ] 노선을 수정한다.
    -[ ] 존재하지 않는 노선을 수정하려는 경우 예외를 발생시킨다.
-[ ] 노선을 삭제한다.
    -[ ] 존재하지 않는 노선을 삭제하려는 경우 예외를 발생시킨다.

# 체크리스트

- [ ] E2E 테스트를 작성한다.

# 제약사항

* @Controller 이외의 스프링 기능을 사용하지 않는다.
* 스프링 빈을 사용하지 않는다.
* 데이터 저장은 DB가 아닌 List로 한다.
* 모든 기능은 테스트로 정상 동작하는지 확인한다.

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
