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

## 기능 목록 구현

1. 지하철역 관리 기능
    - [ ]  입력받은 이름으로 지하철 역 생성
        - [ ]  이미 있는 역의 이름이 입력으로 들어올 경우 예외 발생
    - [ ]  전체 지하철역 조회
    - [ ]  입력받은 이름의 지하철역 삭제

2. 지하철 노선 관리 기능
    - [ ]  입력받은 이름과 노선 컬러로 노선 생성
        - [ ]  이미 존재하는 노선과 이름 또는 노선 컬러가 겹칠 경우 예외 발생
    - [ ]  지하철 전체 노선 조회
    - [ ]  입력받은 지하철 노선의 정보 조회
    - [ ]  지하철 노선 수정
        - [ ]  수정하려는 이름 혹은 컬러가 다른 노선과 겹칠 경우 예외 발생
    - [ ]  지하철 노선 삭제

## 🚀 Getting Started

### Usage

#### application 구동

```
./gradlew bootRun
```

## 📝 License

This project is [MIT](https://github.com/woowacourse/atdd-subway-map/blob/master/LICENSE) licensed.
