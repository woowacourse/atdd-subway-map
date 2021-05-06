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

## 🚀 기능 구현 목록

### 1, 2단계
- [x] 같은 이름 지하철역 생성 불가 기능 추가 
- [x] 역 삭제 기능 추가
- [x] 서비스 레이어 분리
- [x] 노선 CRUD 구현
    - [x] 노선 등록
        - [x] 예외: 상행 하행 같으면 안됨.
        - [x] 예외: 상행역이 존재해야함.
        - [x] 예외: 하행역이 존재해야함.
        - [x] 에외: 중복된 노선 이름 등록 불가.
    - [x] 노선 조회
    - [x] 노선 전체 조회
    - [x] 노선 업데이트
        - [x] 예외: 중복된 노선 이름으로 수정 불가.
    - [x] 노선 삭제
    
### 3단계
- [ ] 노선 추가시 상행 하행 거리 추가
- [x] 구간 추가 API(downStationId, upStationId, distance)
- [ ] 노선 조회 API 수정(stations list 추가)
- [ ] 구간 제거 API
- [ ] 노선 내의 구간 추가시
    - [x] 예외: 상행이든 하행이든 1개의 역은 새로운 역이어야함
    - [x] 예외: 상행이든 하행이든 1개의 역은 존재하는 역이어야함
    - [x] 상행 종점이 수정 될때
    - [x] 하행 종점이 수정 될때
    - [x] 중간 역이 추가 될때
        - [x] 예외: 거리 계산
- [ ] 노선 내의 구간 삭제시
    - [ ] 중간 역이 삭제 될때
    - [ ] 예외: 구간이 1개뿐일때는 삭제 불가
    

<br>

## 📝 License

This project is [MIT](https://github.com/woowacourse/atdd-subway-map/blob/master/LICENSE) licensed.
