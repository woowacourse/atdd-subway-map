<p align="center">
    <img width="200px;" src="https://raw.githubusercontent.com/woowacourse/atdd-subway-admin-frontend/master/images/main_logo.png"/>
</p>
<p align="center">
  <img alt="npm" src="https://img.shields.io/badge/npm-%3E%3D%205.5.0-blue">
  <img alt="node" src="https://img.shields.io/badge/node-%3E%3D%209.3.0-blue">
  <a href="https://techcourse.woowahan.com/c/Dr6fhku7" alt="woowacuorse subway">
    <img alt="Website" src="https://img.shields.io/website?url=https%3A%2F%2Fedu.nextstep.camp%2Fc%2FR89PYi5H">
  </a>
  <img alt="GitHub" src="https://img.shields.io/github/license/woowacourse/atdd-subway-map">
</p>

<br>

# 지하철 노선도 미션

스프링 과정 실습을 위한 지하철 노선도 애플리케이션

## 구현 기능 목록

1. 지하철 역 관리 기능 완성하기

- [x] 같은 이름 예외처리
- [x] 역 삭제

2. 지하철 노선 관리 API 구현

- [x] 노선 생성
    - [x] 노선 생성 시 구간 추가
- [x] 노선 목록 조회
- [x] 노선 조회
    - [x] 노선 조회 시 구간 조회
- [x] 노선 수정
- [x] 노선 삭제
- [x] e2e 테스트 구현

3. 지하철 구간 관리 API 구현
- [x] 구간 추가
    - [x] 양 끝의 역이 둘 다 노선에 존재하지 않는 역이라면 예외 발생
    - [x] 양 끝의 역이 둘 다 노선에 등록되어 있는 역이라면 예외 발생
    - [x] 역 사이에 추가 시 기존 역 사이의 길이보다 크거나 같다면 예외 발생
    - [x] 역 사이에 추가 시에 갈래길 생성 없이 기존 구간 변경

- [x] 노선 조회
- [x] 구간 삭제
    - [x] 종점 삭제 시 그 다음 역이 종점이 됨
    - [x] 중간역 삭제 시 재배치
    - [x] 구간이 하나인 노선에서는 제거할 수 없다.

<br>

## 🚀 Getting Started

### Install

#### npm 설치

```
cd frontend
npm install
```

> `frontend` 디렉토리에서 수행해야 합니다.

### Usage

#### webpack server 구동

```
npm run dev
```

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
