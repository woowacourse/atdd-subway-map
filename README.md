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

# 1단계 기능 요구 사항

## station controller 추가

- 이미 등록된 이름 요청시 에러 응답

## line controller 추가

- 노선 등록
- 노선 조회
- 노선 목록
- 노선 수정
- 노선 삭제

## line controller test 추가

# 2단계 기능 요구 사항

- H2에 지하철 데이터 저장하기
    - 기존 List 자료구조 H2 DB로 변경
    - sql문으로 기능 코드 대체
    - jdbcTemplate로 작성된 sql문 실행
    - H2 DB를 이용해 DB 저장, 확인, console 활용
- 스프링 빈을 활용하기
    - 객체와 싱글톤이나 static으로 구현 객체들을 스프링 빈으로 관리

# 3단계 기능 요구 사항

## 지하철 노선 추가 API 수정

- [x] 노선 추가 시 3가지 정보를 추가로 입력 받음
    - upStationId: 상행 종점
    - downStationId: 하행 종점
    - distance: 두 종점간의 거리
- [ ] 두 종점간의 연결 정보를 이용하여 노선 추가 시 구간(Section) 정보도 함께 등록
    - 변경된 API 스펙은 [API 문서v2](https://github.com/jinyoungchoi95/atdd-subway-map.git) 참고

## 구간 관리 API 구현

- [x] 구간/구간들 도메인 생성
- [ ] 구간 등록
    - [x] [예외]상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없음
    - [x] [예외]상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없음
    - [ ] [예외] 역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없음
    - [ ] [예외] 하나의 노선에는 갈래길이 허용되지 않기 때문에 새로운 구간이 추가되기 전에 갈래길이 생기지 않도록 기존 구간을 변경
- [x] 노선에 구간을 추가
- [ ] 노선에 포함된 구간 정보를 통해 상행 종점부터 하행 종점까지의 역 목록을 응답
- [ ] 구간 제거
- [ ] 구간 관리 API 스펙은 [API 문서v2](https://github.com/jinyoungchoi95/atdd-subway-map.git) 참고
