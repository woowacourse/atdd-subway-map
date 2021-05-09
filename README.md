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
- 역 추가 기능
    - [예외] 이미 존재하는 이름의 역을 추가하려고 했을 때
- 역 삭제 기능
    - [예외] 존재하지 않는 역을 삭제하려고 했을 때
- 노선 추가 기능
    - [예외] 이미 존재하는 이름의 노선을 추가하려고 했을 때
- 노선 목록 조회 기능
    
- 노선 조회 기능
    - [예외] 존재하지 않는 노선을 조회하려고 했을 때
- 노선 수정 기능
    - [예외] 존재하지 않는 노선을 수정하려고 했을 때
    - [예외] 수정하고자 하는 노선의 이름이 이미 존재하는 이름일 때
- 노선 삭제 기능
    - [예외] 존재하지 않는 노선을 삭제하려고 했을 때
## 2단계 기능 요구사항 : 프레임워크 적용

1. 지하철 역 관리 프레임워크 적용

- 스프링 JDBC H2 DB 이용
- 스프링 빈 활용

## 구현할 기능 목록

* 1단계에서 구현한 4기능에 대해서 스프링 JDBC 적용
* 1단계에서 구현한 4기능에 대해서 스프링 빈 적용

## 3단계 : 지하철 구간 관리 기능

- 지하철 역과 역 사이의 연결 정보인 `지하철 구간`을 도출하고 이를 관리하는 API를 만드는 미션
- 지하철역, 노선, 구간 객체의 의존 관계 설정을 연습해보는 미션
- 미리 제공된 프론트엔드 코드를 바탕으로 기능이 잘 동작하도록 완성하기
- 미리 제공된 프론트엔드 코드에서 `노선 관리` 기능과 `구간 관리` 기능이 잘 동작하도록 완성하기 (노선 생성 시 **상행역**, **하행역**, **거리** 입력 포함하기, 없는 경우 예외처리)

## 기능 요구 사항 : 지하철 구간 관리 API 구현하기

1. 지하철 노선 추가 API 수정

  - 노선 추가 시 상행 종점, 하행 종점, 두 종점간의 거리를 추가로 입력받는다.
  - 두 종점간의 연결 정보를 이용하여 노선 추가 시 구간 정보도 함께 등록한다.

2. 지하철 구간 추가 API 구현

  - 노선에 구간을 추가하는 API를 만들기

    - 새로 등록할 구간의 상행역과 하행역 중 노선에 이미 등록되어 있는 역을 기준으로 새로운 구간을 추가한다.

  - path는 ```/lines/{lineId}/sections``` 을 활용

  - [예외] 한 구간 사이에 새로운 역을 등록하면서 두 개의 구간으로 나뉠 경우 새로운 구간의 길이가 기존 구간의 길이보다 크거나 같으면 등록할 수 없다.

  - [예외] 상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없다.

    ex) A - B, B - C 구간이 등록된 상황에서 A - B, B - C, A - C 구간을 등록할 수 없다.

  - [예외] 노선에 상행역과 하행역 둘 중 하나도 포함되어 있지 않으면 구간을 추가할 수 없다.

3. 노선 조회 시 구간에 포함된 역 목록 응답

  - 노선에 포함된 구간 정보를 통해 상행 종점부터 하행 종점까지의 역 목록을 응답

4. 지하철 구간 제거 API 구현

  - 종점이 제거될 경우 다음으로 오던 역이 종점이 된다.
  - 중간역이 제거될 경우 재배치한다.
    - 노선에 A - B - C 역이 연결되어 있을 때 B역을 제거할 경우 A - C로 재배치한다.
    - 거리는 두 구간의 거리의 합으로 정한다.
  - [예외] 구간이 하나인 노선에서 마지막 구간을 제거할 수 없다.

