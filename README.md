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

## 지하철 노선 추가 API 수정

- [x] 두 종점간의 연결 정보를 이용하여 노선 추가 시 구간 정보도 함께 등록



```json
POST /lines HTTP/1.1
accept: */*
content-type: application/json; charset=UTF-8

{
    "color": "bg-red-600",
    "name": "신분당선",
    "upStationId": "1",
    "downStationId": "2",
    "distance": "10"
}
```



## 지하철 구간 추가 API 구현

- [ ] 노선에 구간을 추가하는 API를 만들기
  - 새로 등록할 구간의 상행역과 하행역 중 노선에 이미 등록되어있는 역을 기준으로 새로운 구간을 추가한다.
  - 하나의 노선에는 갈래길이 허용되지 않기 때문에 구간이 추가되기 전에 갈래길이 생기지 않도록 기존 구간을 변경한다.

- request

```json
POST /lines/1/sections HTTP/1.1
accept: */*
content-type: application/json; charset=UTF-8
host: localhost:52165

{
    "downStationId": "4",
    "upStationId": "2",
    "distance": 10
}
```



- 예외

  - [x] 역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없음

  - [ ] 상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없음

  - [ ] 상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없음



### 노선 조회 시 구간에 포함된 역 목록 응답

- [ ] 노선에 포함된 구간 정보를 통해 상행 종점부터 하행 종점까지 역목록 응답

- response

```json
HTTP/1.1 201 
Location: /lines/1
Content-Type: application/json
Date: Fri, 13 Nov 2020 00:11:51 GMT

{
    "id": 1,
    "name": "신분당선",
    "color": "bg-red-600",
    "stations": [
        {
            "id": 1,
            "name": "강남역"
        },
        {
            "id": 2,
            "name": "역삼역"
        }
    ]
}

```



### 지하철 구간 제거 API

- [ ] 지하철 구간 제거

  - 종점이 제거될 경우 다음으로 오던 역이 종점이 됨

  - 중간역이 제거 될 경우 재배치를 함

    - 노선에 A-B-C 역이 연결되어 있을 때 B역을 제거할 경우 A-C로 배치됨
    - 거리는 두 구간의 거리의 합으로 정함

    - [ ] 예외
      - 구간이 하나인 노선에서 마지막 구간을 제거 할 때
        - 제거 할 수 없음

- Request

```java
DELETE /lines/1/sections?stationId=2 HTTP/1.1
accept: */*
host: localhost:52165
```





### 기능 목록

- 노선을 추가한다.
  - 두 종점간의 연결 정보를 이용하여 노선 추가 시 구간 정보도 함께 등록한다.
  - 예외 - 두 종점 (역)은 등록되어 있어야 한다.
  - 예외 - 노선의 이름은 ~역 이어야 한다.
  - 예외 - 노선의 색은 중복되면 안된다.

- 구간을 추가한다.

  - 구간의 상행, 하행을 체크한다.

    - 예외 - 상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가 할 수 없다.
    - 예외 - 상행역과 하행역 둘중 하나도 포함되어 있지 않으면 추가할 수 없다.

  - 구간의 길이가 총 노선의 길이를 넘어가는지 체크한다.

    - 예외 - 기존의 역 사이 길이보다 크거나 같으면 등록 할 수 없다.



- 구간을 제거한다.

  - 종점이 제거 될 경우
    - 시작을 포함한 구간
      - 다음 역이 노선의 종점이 됨
    - 끝을 포함한 구간
      - 이전 역이 노선의 종점이 됨
  - 중간역이 제거 될 경우
    - A-B-C 역이 연결되어 있을 때 B역을 제거할 경우 A-C로 재배치됨

- 노선을 조회한다.

  - 노선의 구간을 정렬한다.



## 📝 License

This project is [MIT](https://github.com/woowacourse/atdd-subway-map/blob/master/LICENSE) licensed.


