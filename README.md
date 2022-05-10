# 지하철 노선도 미션

스프링 과정 실습을 위한 지하철 노선도 애플리케이션

# 기능 목록

## 지하철역 관리

### 지하철 역을 생성한다

- 성공 : Http status(201) / 지하철 이름(name)과 식별자(id)를 응답한다.
- 실패(이미 존재하는 역 이름) : Http status(400) / 에러 메시지를 응답한다.

### 모든 지하철 역을 조회한다

- 성공 : Http status(200) 모든 지하철 역의 이름(name)과 식별자(id)를 반환한다.

### 지하철 역을 삭제한다.

- 성공 : Http status(204)
- 실패(존재하지 않는 역) : Http status(400) / 에러메시지를 반환한다.

## 지하철 노선 관리

### 지하철 노선을 생성한다.

- 성공 : Http status(201) / 식별자(id)와 노선 이름(name), 노선 색(color)를 응답한다.
- 실패(이미 존재하는 노선 이름) : Http status(400) / 에러 메시지를 응답한다.

### 지하철 노선 목록을 조회한다.

- 성공 : Http status(200) / 모든 노선의 식별자(id)와 노선 이름(name), 노선 색(color)를 응답한다.

### 지하철 노선을 조회한다.

- 성공 : Http status(200) / 노선의 식별자(id)와 노선 이름(name), 노선 색(color)를 응답한다.
- 실패(존재하지 않는 노선) : Http status(400) / 에러 메시지를 응답한다.

### 지하철 노선을 수정한다.

- 성공 : Http status(200)
- 실패(존재하지 않는 노선) : Http status(400) / 에러 메시지를 응답한다.
- 실패(이미 존재하는 노선 색) : Http status(400) / 에러 메시지를 응답한다.

### 지하철 노선을 삭제한다.

- 성공 : Http status(204)
- 실패(존재하지 않는 노선) : Http status(400) / 에러 메시지를 응답한다.

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


<br>

## 🚀 Getting Started
### Usage
#### application 구동

```
./gradlew bootRun
```

<br>

## 📝 License

This project is [MIT](https://github.com/woowacourse/atdd-subway-map/blob/master/LICENSE) licensed.

## 추가 기능 목록

### 지하철 노선 추가 기능

    - upStationId: 상행 종점
    - downStationId: 하행 종점
    - distance: 두 종점간의 거리
    - 두 종점간의 연결 정보를 이용하여 노선 추가시 구간(Section) 정보도 함께 등록

### 구간 관리 기능

    -  노선에 구간을 추가할 수 있다.
    -  노선에 포함된 구간 정보를 통해 상행 종점부터 하행 종점까지의 역 목록을 응답할 수 있다.
    -  구간을 제거할 수 있다.

### 구간 등록 기능

    - [ ] 상행 종점 등록 기능
        - [ ] 새로 등록할 구간의 하행역이 이미 노선의 상행 종착역 으로 등록되어 있으면, 상행 종점으로 등록한다.
    - [ ] 하행 종점 등록 기능
        - [ ] 새로 등록할 구간의 상행역이 이미 노선의 하행 종착역으로 등록되어 있으면, 하행 종점으로 등록한다.
    - [ ] 갈래길 방지 기능
        - [ ] 새로 등록할 구간이 모두 노선의 상행 종착역과 하행 종착역이 아닐 경우
            - [ ] 존재하는 상행역이 같으면, 기존 구간의 상핵역을 새로 등록할 구간의 하행역으로 변경한다.
            - [ ] 존재하는 하행역이 같으면, 기존 구간의 하행역을 새로 등록할 구간의 상행역으로 변경한다.
        - [ ] 예외
            - [ ] 새로운 구간의 길이가 기존 역 사이 길이보다 크거나 같으면 예외가 발생한다.
    - [ ] 예외
        - [ ] 새로운 구간의 상행역과 하행역이 이미 노선에 등록되어 있는 경우, 추가할 수 없다. 
            - [ ] A-B, B-C 구간이 등록되어 있는 경우 A-B 구간, B-C 구간, A-C구간 모두 등록할 수 없다.
        - [ ] 상행역과 하행 둘 중 하나도 포함되어 있지 않으면 추가할 수 없다.

### 구간 제거 기능

    - 종점이 제거 될 경우 다음으로 오던 역이 종점이 된다.
    - 중간역이 제거될 경우 재배치를 한다.
        - A - B - C 역이 연결되어 있을 때 B역을 제거할 경우 A - C로 재배치 된다.
    - 이 때 두 구간의 거리는 두 구간의 거리의 합이다. 
    - 구간이 하나인 노선에서 마지막 구간을 제거하려고 하면 예외가 발생한다.