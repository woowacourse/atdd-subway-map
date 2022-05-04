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

## 기능 요구 사항

### 지하철 역 관리 API 기능 완성하기

- [x] StationDao를 활용하여 지하철 역 정보를 관리
- [x] 지하철 역 등록
    - [x] [예외] 지하철역 생성 시 이미 등록된 이름으로 요청한다면 에러를 응답
- [x] 지하철역 db 생성 및 데이터 연결
- [x] static 객체를 스프링 빈으로 변경

### 지하철 노선 관리 API 구현하기

- [x] 지하철 노선 등록
    - [x] [예외] 지하철역과 마찬가지로 같은 이름의 노선은 생성 불가
- [x] 지하철 노선 목록
- [x] 지하철 노선 조회
- [x] 지하철 노선 수정
- [x] 지하철 노선 삭제
- [ ] 지하철 노선 db 생성 및 데이터 연결
- [ ] static 객체를 스프링 빈으로 변경

### End to End 테스트 작성하기

-[x] 노선 기능에 대한 E2E 테스트를 작성
