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

## ✏️ Code Review Process
[텍스트와 이미지로 살펴보는 온라인 코드 리뷰 과정](https://github.com/next-step/nextstep-docs/tree/master/codereview)

<br>

## 🐞 Bug Report

버그를 발견한다면, [Issues](https://github.com/woowacourse/atdd-subway-map/issues) 에 등록해주세요 :)

<br>

## 📝 License

This project is [MIT](https://github.com/woowacourse/atdd-subway-map/blob/master/LICENSE) licensed.


## 기능 구현 목록
- [x] 지하철 역 관리
    - [x] 같은 지하철 역 생성 불가 기능
    - [x] 삭제 기능

- [x] 지하철 노선 관리 
    - [x] 노선 생성
    - [x] 노선 목록 조회
    - [x] 노선 조회
    - [x] 노선 수정
    - [x] 노선 삭제
- [x] 스프링 빈 적용
- [x] H2 적용

## 리팩토링 중점 사안
- [ ] Controller에서만 Response/Request를 처리하도록 변경
    - [ ] 아래 Layer들에서는 Domain 객체를 활용토록 함
- [ ] Dao에서 도메인을 반환하도록 반환
- [ ] DB Unique를 통해 중복 검사 하지 않을 것
    - [ ] findBy~ 로 Optional 처리
- [ ] Response에서 null값을 포함하지 않도록 할 것
    - [ ] null대신 빈 리스트 "[]"를 반환할 것
- [ ] Service -> Dao에 도메인 객체를 넘겨 줄 것
    - [ ] dao가 column에 의존적이지 않도록!
- [ ] line.from(lineResponse) 등으로 도메인 객체 활용
    - [ ] Controller에서 Service에 정보 넘겨줄 때 해당 메서드 활용
- [ ] 인수테스트 상태코드 말고도 내용 검증 할 것

## 질문 사항
- findById 이후 CRUD 로직을 수행하면 DB를 한 번에 2번 찌르는 건데 병목이 발생하진 않을까?
    - Service단에서 Id를 캐싱해두는 것도 방법이 되지 않을까?
    - 2번 정도는 컴퓨팅 능력으로 커버 가능?
