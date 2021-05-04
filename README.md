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
- [ ] 노선 기능

<br>

## ✏️ Code Review Process
[텍스트와 이미지로 살펴보는 온라인 코드 리뷰 과정](https://github.com/next-step/nextstep-docs/tree/master/codereview)

<br>

## 🐞 Bug Report

버그를 발견한다면, [Issues](https://github.com/woowacourse/atdd-subway-map/issues) 에 등록해주세요 :)

<br>

## 📝 License

This project is [MIT](https://github.com/woowacourse/atdd-subway-map/blob/master/LICENSE) licensed.
