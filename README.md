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


## 1단계 요구사항 도출 

### 지하철역
- [x] 지하철 역을 등록한다. 
    - [x] 같은 역의 이름으로 중복될 수 없다.
    - [x] 역 이름은 10글자를 넘길 수 없다.
    - [x] 역 이름은 2글자 이상이어야 한다.
    - [x] 역 이름은 한글과 숫자의 조합이어야 한다.
- [x] 지하철 역을 조회한다.
- [x] 지하철 역을 삭제한다.
    - [x] 삭제할 역이 있어야 한다.

### 지하철 노선
- [x] 지하철 노선을 등록한다.
    - [x] 같은 노선의 이름으로 중복될 수 없다.
    - [x] 색깔은 중복 될 수 없다.
    - [x] 노선 이름은 10글자를 넘길 수 없다.
    - [x] 노선 이름은 3글자 이상이어야 한다.
    - [x] 노선 이름은 한글과 숫자의 조합이어야 한다.
    - [x] 이름과 색깔이 있어야 한다.
- [x] 지하철 노선 목록을 조회한다.
- [x] 지하철 노선 조회
    - [x] 조회할 노선이 있어야 한다.
- [x] 지하철 노선 수정
    - [x] 같은 노선의 이름으로 중복될 수 없다.
    - [x] 색깔은 중복 될 수 없다.
    - [x] 노선 이름은 10글자를 넘길 수 없다.
    - [x] 노선 이름은 3글자 이상이어야 한다.
    - [x] 노선 이름은 한글이어야 한다.
    - [x] 이름과 색깔이 있어야 한다.
- [x] 지하철 노선 삭제

## 2단계 요구사항 도출
- [x] H2 설정하기
- [x] DAO 에 JDBC Template 사용하여 DB 에 저장하기
- [x] 스프링 빈으로 변경
- [x] 기존 테스트코드를 DAO test 로 변경
