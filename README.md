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

<br>


## 1, 2단계 기능 구현 목록

### Station
- [x] [예외] 역 이름에 중복이 있어서는 안 된다.
- [x] 역 삭제 기능([DELETE] /stations/${stationId})을 구현한다.

### Line
- [x] Line Controller 추가
- [x] Line Dao 추가
- [x] Line CRUD API 구현
  - [x] Create
    - [x] [예외] 이름이 중복되어선 안 된다.
  - [x] Read
  - [x] Delete

## 3단계 기능 구현 목록
- [ ] 노선 상세 조회 시, 노선에 등록된 역 목록을 응답
- [ ] 노선 생성자 API에서 상, 하행, 종점간 거리를 입력받도록 수정
  - [ ] 상, 하행 종점 역의 id (Body)
  - [ ] 두 종점 간의 거리 (Body)
- [ ] 두 종점 간의 연결 정보를 이용하여 노선을 추가할 때, 구간(Section) 정보도 함께 등록
- [ ] 구간 추가 API 구현
  - [ ] 노선 id (URI Params)
  - [ ] 상, 하행 역의 id (Body)
  - [ ] 두 역 간의 거리 (Body)
- [ ] 구간 제거 API 구현


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
