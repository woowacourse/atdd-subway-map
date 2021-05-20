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

---

# 스프링 - 지하철 노선도 관리 미션

- API 요청에 대한 처리와 Dao 객체를 활용하여 데이터를 관리하는 연습을 위한 미션
- 지하철 역과 지하철 노선을 관리하기 위한 API를 구현하기
- 미리 제공된 프론트엔드 코드에서 역 관리 기능과 노선 관리기능이 잘 동작하도록 완성하기 (노선 생성 시 상행역, 하행역, 거리 입력은 무시)

## 1단계 기능 요구사항 : 지하철 노선도 정보 관리 API 구현하기

1. 지하철 역 관리 API 기능 완성하기

- 같은 이름 지하철역 생성 불가 등의 예외 처리 기능 추가
- StationDao를 활용하여 지하철 역 정보를 관리 (StationController는 구현되어 있음)

2. 지하철 노선 관리 API 구현하기

- 역 관리 API 기능과 유사하게 구현

3. End to End 테스트 작성하기

- 노선 기능에 대한 E2E 테스트를 작성 (StationAcceptanceTest 클래스 참고)

## 프로그래밍 제약 사항

- ```@Controller``` 등의 필수 컴포넌트를 제외한 스프링 빈 사용 금지
- 객체를 직접 생성하고 의존 관계를 맺어주기

## 구현할 기능 목록

* 역 삭제 기능
* 노선 추가 기능
* 노선 조회 기능
* 노선 삭제 기능

## 2단계 기능 요구사항 : 프레임워크 적용

1. 지하철 역 관리 프레임워크 적용

- 스프링 JDBC H2 DB 이용
- 스프링 빈 활용

## 구현할 기능 목록

* 1단계에서 구현한 4기능에 대해서 스프링 JDBC 적용
* 1단계에서 구현한 4기능에 대해서 스프링 빈 적용

## 3 단계 기능 요구사항 : 구간 기능 추가

* 노선 추가 시 상행, 하행, 종점간의 거리를 입력받음
* 노선에서 구간을 추가 할 수 있음
* 노선에서 구간을 삭제 할 수 있음
* 노선 목록을 응답 할 수 있음

### 구간 등록 예외 처리
