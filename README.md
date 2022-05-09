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


# 🚇 기능 요구사항
- [X] 지하철역 등록
- [X] 지하철역 목록 조회
- [X] 지하철역 삭제
- [X] 지하철 노선 등록
- [X] 지하철 노선 목록
- [X] 지하철 노선 조회
- [X] 지하철 노선 삭제
- [X] 지하철 노선 수정

- [ ] 지하철 노선 등록
  - [ ] [ERROR] 이미 존재하는 노선이라면 예외 발생
  - [ ] [ERROR] 상행역과 하행역이 이미 노선에 모두 등록되어 있다면 예외 발생
  - [ ] 노선의 상행 or 하행 정보가 존재한다면 업데이트
  - [ ] 노선 갈래길 방지로 기존 구간 변경
    - [ ] 역 사이에 새로운 역을 등록할 때, 기존 역 사이 길이보다 크거나 같으면 예외 발생
- [ ] 지하철 노선 삭제


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
