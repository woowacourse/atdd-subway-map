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

## 기능 요구 사항

## 기능요구사항

- [x] 노선 생성

    - [x] 요청

  ```json
  POST /lines HTTP/1.1
  accept: */*
  content-type: application/json; charset=UTF-8
  
  {
      "color": "bg-red-600",
      "name": "신분당선"
  }
  ```

    - [x] 응답

  ```json
  HTTP/1.1 201 
  Location: /lines/1
  Content-Type: application/json
  Date: Fri, 13 Nov 2020 00:11:51 GMT
  
  {
      "id": 1,
      "name": "신분당선",
      "color": "bg-red-600"
  }
  ```



- [x] 노선 목록조회

    - [x] 요청

  ```json
  GET /lines HTTP/1.1
  accept: application/json
  host: localhost:49468
  ```

    - [x] 응답

  ```json
  HTTP/1.1 200 
  Content-Type: application/json
  Date: Fri, 13 Nov 2020 00:11:51 GMT
  
  [
      {
          "id": 1,
          "name": "신분당선",
          "color": "bg-red-600"
      },
      {
          "id": 2,
          "name": "2호선",
          "color": "bg-green-600"
      }
  ]
  ```

- [x] 노선조회

    - [x] 요청

  ```json
  GET /lines/1 HTTP/1.1
  accept: application/json
  host: localhost:49468
  ```

    - [x] 응답

  ```json
  HTTP/1.1 200 
  Content-Type: application/json
  Date: Fri, 13 Nov 2020 00:11:51 GMT
  
  {
      "id": 1,
      "name": "신분당선",
      "color": "bg-red-600"
  }
  ```

- [x] 노선 수정

    - [x] 요청

  ```json
  HTTP/1.1 200 
  Content-Type: application/json
  Date: Fri, 13 Nov 2020 00:11:51 GMT
  
  {
      "id": 1,
      "name": "신분당선",
      "color": "bg-red-600"
  }
  ```

    - [x] 응답

  ```json
  HTTP/1.1 200 
  Date: Fri, 13 Nov 2020 00:11:51 GMT
  ```



- [x] 노션 삭제

    - [x] 요청

  ```json
  HTTP/1.1 200 
  Date: Fri, 13 Nov 2020 00:11:51 GMT
  ```

    - [x] 응답

  ```json
  HTTP/1.1 200 
  Date: Fri, 13 Nov 2020 00:11:51 GMT
  ```

## 3단계 요구사항
- [x] 지하철 노선 추가 API 수정
  - 노 추가시 3가지 정보 추가 입력
    - upStationId : 상행 종점
    - downStationId : 하행 종점
    - distance : 두 종점간의 거리
  - 두 종점간의 연결 정보 이용하여 노선 추가 시 구간 정보도 함께 등록
    - [x] 요청이 들어오면 upStationId 와 downStationId 간의 구간을 생성한다. - service 로직
    
  - [x] 예외: 존재하지 않는 역 사이의 구간이 입력됐을 경우 예외
  - [x] exception: when get a new Line Request that has already existing line name.

- [x] 지하철 구간 추가 API 구현
  - 노선에 구간을 추가하는 API 만들기
  - path는 /lines/{lineId}/sections 활용
  - [x] downStationId와 연결된 upStationId가 없다면 이는 상행 종점
  - [x] upStationId와 연결된 downStationId가 없다면 이는 하행 종점.
  - [x] 구간 추가되기 전에갈래길이 생기지 않도록 기존 구간 변경.
  
  - [x] 예외 : 역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같은면 등록을 할 수 없다.
  - [x] 예외 : 상행선과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없다.
  - [x] 예외 : 상행역과 하행역 둘 중 하나도 포함되어 있지 않으면 추가할 수 없다.
    

- [x] 노선 조회 시 구간에 포함된 역 목록 응답
  - 노선에 포함된 구간 정보를 통해 상행 종점부터 하행 종점까지의 역 목록을 응답.
  
- [x] 지하철 구간 제거 API
  - [x] 종점이 제거될 경우 다음역으로 오던 역이 조점이 됨.
  - [x] 중간역이 제거될 경우 재배치

  - [x] 예외 : 구간이 하나인 노선에서 마지막 구간을 제거할 수 없다.
  
- 지하철 노선 제거
  - [x] 구간 정보도 함께 지워져야 한다.
- 지하철 역 제거
  - [x] 역을 참조하는 구간이 있을 경우 삭제할 수 없다.

- 공통
  - [x] id는 0보다 커야 한다.
  - [x] name등 String value는 1글자 이상이어야 한다.

## 📝 License

This project is [MIT](https://github.com/woowacourse/atdd-subway-map/blob/master/LICENSE) licensed.


