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

## 기능 요구 사항

### 지하철 역 관리 API 기능 완성하기

- [x] StationDao를 활용하여 지하철 역 정보를 관리
- [x] 지하철 역 등록
    - [x] [예외] 지하철역 생성 시 이미 등록된 이름으로 요청한다면 에러를 응답
- [x] 지하철역 db 생성 및 데이터 연결
- [x] static 객체를 스프링 빈으로 변경

### 지하철 노선 관리 API 구현하기

- [x] 지하철 노선 등록
    - [x] [예외] 지하철역과 마찬가지로 같은 이름의 노선은 생성 불가
    - [x] 노선 추가 시 이름, 노선 색상 외에도 3가지 정보를 추가로 입력 받음
        - [x] 구간(Section) 정보도 함께 등록
            - [x] upStationId: 상행 종점
            - [x] downStationId: 하행 종점
            - [x] distance: 두 종점간의 거리
- [x] 지하철 노선 목록
- [x] 지하철 노선 조회
- [x] 지하철 노선 수정
- [x] 지하철 노선 삭제
- [x] 지하철 노선 db 생성 및 데이터 연결

### 구간 관리 API 구현

- [x] 노선에 구간을 추가
    - [x] 지하철 역의 상행종점이 구간의 하행종점과 같을 때
        - 상행 종점 등록
    - [x] 지하철 역의 하행종점이 구간의 상행종점과 같을 때
        - 하행 종점 등록
    - [x] 지하철 역의 하행/상행 종점이 구간의 하행/ 상행종점과 같을 때
        - 역 사이에 새로운 역을 등록 (갈래길 방지)
        - [x] [예외] 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없음
    - [x] [예외] 상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없음
    - [x] [예외] 상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없음
    - [x] [예외] 상행역과 하행역이 동일할 수 없음
- [x] 노선에 포함된 구간 정보를 통해 상행 종점부터 하행 종점까지의 역 목록을 응답
- [ ] 구간 제거
    - [ ] 종점이 제거될 경우 다음으로 오던 역이 종점이 됨
    - [ ] 중간역이 제거될 경우 재배치를 함
        - [ ] 노선에 A - B - C 역이 연결되어 있을 때 B역을 제거할 경우 A - C로 재배치 됨
        - [ ] 거리는 두 구간의 거리의 합으로 정함
    - [ ] [예외] 구간이 하나인 노선에서 마지막 구간을 제거할 수 없음

### End to End 테스트 작성하기

- [ ] 노선 기능에 대한 E2E 테스트를 작성

## TODO

- [x] 컨트롤러 테스트코드 간소화
- [x] vo 동등성 기준 변경
- [x] Line 테이블에 상/하행/거리 정보가 제외되도록 변경
- [x] station이 포함된 Section 클래스 생성
- [ ] 지하철역 삭제 후 어떻게?
    - [ ] 외래키로 이어서 같이 삭제되게 하는건 어떨까?
- [ ] MockMVC 사용해 컨트롤러/서비스단 수정
- [ ] 예외처리 꼼꼼하게~ 수정^^ CustomException 써보거나 기존 쓸만한 Exception 적용

### 1단계 피드백

- [x] DAO 안에서 값 존재여부 검증
- [x] 업데이트나 삭제에도 검증 추가
    - [x] 수정/삭제 시 예외케이스 테스트
- [x] station/ line 자체의 동등성비교로 검증
- [x] given when then 에서 // when then 으로 붙여쓰기
- [x] 사이즈가 0이어도 괜찮겠지만 해당 라인이름으로 검색시 조회가 되지않도록 하는게 더 나을 듯
- [x] findAll()이 when이고 결과를 then에서 검증
- [x] 에러 메세지도 같이 던져주기
- [ ] SpringBootTest 사용 및 JdbcTest와 차이 공부
