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

## 🚟 기능 요구사항 

> 1 단계 

### 역 관리
- [x] 역 추가
  - 제한 사항
    - [x] 같은 이름 지하철 역 생성 불가
    - [x] "-역"을 접미사로 가지고 있음
- [x] 역 조회
- [x] 역 삭제

### 노선 관리 
- [x] 노선 생성
  - 제한 사항
    - [x] 같은 이름 || 같은 색깔 노선 생성 불가
    - [x] "-선"을 접미사로 가지고 있음
- [x] 노선 목록 조회 
- [x] 노선 조회  
  - [x] 해당 노선에 포함된 역 출력 
- [x] 노선 수정
  - [x] 이름 || 색깔 수정 가능
- [x] 노선 삭제 

### E2E 테스트 작성
- [x] 노선 기능
<br>

> 2 단계

### Spring Bean 활용
- [x] Service Bean 등록
- [x] DAO Bean 등록 

### 스프링 JDBC H2 DB 저장
- [x] DB 연결
- [x] DB 생성
- [x] DAO 쿼리 작성 
- [x] H2 log/console 확인 

### 스프링 JDBC H2 DB 테스트
- [x] DAO 테스트 작성
<br>
  
> 3 단계

### 노선 추가 기능 수정 
- [x] 3가지 정보 추가
  - [x] 상행 종점 (upStationId)
  - [x] 하행 종점 (downStationId)
  - [x] 두 종점간의 거리 (distance)
- [x] 노선 추가 시 상행-하행 구간 등록 
  
### 구간 추가
- [x] 3가지 정보 추가
  - [x] 상행 종점 (upStationId)
  - [x] 하행 종점 (downStationId)
  - [x] 두 종점간의 거리 (distance)
- [x] 하나의 노선에서 종점역 추가될 경우
  - 새 구간 하행 - 기존 구간 상행 비교 --> 상행 종점 변경
  - 새 구간 상행 - 기존 구간 하행 비교 --> 하행 종점 변경
- [x] 하나의 노선에서 중간역 추가될 경우 재배치
  - [x] Distance 변경
  - 새 구간 상행 - 기존 구간 상행 비교 --> 중간 추가
  - 새 구간 하행 - 기존 구간 하행 비교 --> 중간 추가
- [x] 중간역 등록 시 기존 구간 길이보다 크거나 같으면 등록 불가 
- [x] 해당 노선에 이미 등록된 구간 추가 불가
- [x] 상행역과 하행역 둘 중 하나는 무조건 해당 노선에 포함

### 구간 제거 
- [ ] URI 정보 `/lines/{line_id}/sections?stationId={station_id}`
- [ ] 종점이 제거될 경우 다음으로 오던 역이 종점
- [ ] 중간역이 제거될 경우 재배치
  - [ ] Distance 변경
- [ ] 구간이 하나인 노선에서 마지막 구간 제거 불가

### 노선 조회 기능 수정
- [ ] 상행 -> 하행 역 목록 차례로 출력

### 지하철 역 / 지하철 노선 / 지하철 구간 구조 설계
|Station |Line |Section |
|:------:|:---:|:------:|
|        |     |lineId  |
|        |     |조회 시 구간 정렬|

<br>

## ✏️ Code Review Process
[텍스트와 이미지로 살펴보는 온라인 코드 리뷰 과정](https://github.com/next-step/nextstep-docs/tree/master/codereview)

<br>

## 🐞 Bug Report

버그를 발견한다면, [Issues](https://github.com/woowacourse/atdd-subway-map/issues) 에 등록해주세요 :)

<br>

## 📝 License

This project is [MIT](https://github.com/woowacourse/atdd-subway-map/blob/master/LICENSE) licensed.
