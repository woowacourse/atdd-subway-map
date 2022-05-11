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

<details>
<summary>미션 정보</summary>

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

</details>

<details>
<summary>1단계 기능 요구 사항</summary>

## station controller 추가
- 이미 등록된 이름 요청시 에러 응답

## line controller 추가
- 노선 등록
- 노선 조회
- 노선 목록
- 노선 수정
- 노선 삭제

## line controller test 추가

</details>

<details>
<summary>2단계 기능 요구 사항</summary>

- H2에 지하철 데이터 저장하기
  - 기존 List 자료구조 H2 DB로 변경
  - sql문으로 기능 코드 대체
  - jdbcTemplate로 작성된 sql문 실행
  - H2 DB를 이용해 DB 저장, 확인, console 활용
- 스프링 빈을 활용하기
  - 객체와 싱글톤이나 static으로 구현 객체들을 스프링 빈으로 관리

</details>

<details>
<summary>3단계 기능 요구사항</summary>

## 수정할 부분
- [ ] dao test 시 deleteAll() 말고 다른 방법 사용
- [x] 이름에 대한 중복 처리를 도메인 로직으로 이동
- 노선 추가 시
  - [x] upStaionId, downStaionId, distance 정보 추가 입력
  - [x] 두 종점간의 연결 정보를 이용해 구간(section) 정보도 함께 등록

## 추가할 부분
- 구간 관리
  - [x] 노선에 구간 추가
    - 구간 등록 : 이미 등록되어 있는 역 기준으로 새로운 구간 추가
      - 상행 종점 등록
      - 하행 종점 등록
      - [x] 갈래길 방지 : 갈래길이 생기지 않도록 변경
      - [X] 예외1 : 기존 역 사이 길이보다 크거나 같으면 등록할 수 없음
      - [X] 예외2 : 하행역과 상행역이 이미 노선에 등록되어 있으면 추가할 수 없음
      - [X] 예외3 : 상행역과 하행역 둘 중 하나도 포함되어 있지 않으면 추가할 수 없음
  - [ ] 구간 제거
    - [ ] 종점이 제거될 경우 다음으로 오던 역이 종점
    - [ ] 중간역이 제거될 경우 재배치
      - ex) A - B - C 중 B제거 -> A - C
      - 거리는 두 구간의 거리 합
    - [ ] 예외 : 구간이 하나인 노선에서 마지막 구간 제거할 수 없음
  - [ ] 노선에 포함된 구간 정보를 통해 상행 종점부터 하행 종점까지의 역 목록을 응답

</details>
