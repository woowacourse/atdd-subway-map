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



- [ ] 노선 목록조회

    - [ ] 요청

  ```json
  GET /lines HTTP/1.1
  accept: application/json
  host: localhost:49468
  ```

    - [ ] 응답

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

- [ ] 노선조회

    - [ ] 요청

  ```json
  GET /lines/1 HTTP/1.1
  accept: application/json
  host: localhost:49468
  ```

    - [ ] 응답

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

- [ ] 노선 수정

    - [ ] 요청

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

    - [ ] 응답

  ```json
  HTTP/1.1 200 
  Date: Fri, 13 Nov 2020 00:11:51 GMT
  ```



- [ ] 노션 삭제

    - [ ] 요청

  ```json
  HTTP/1.1 200 
  Date: Fri, 13 Nov 2020 00:11:51 GMT
  ```

    - [ ] 응답

  ```json
  HTTP/1.1 200 
  Date: Fri, 13 Nov 2020 00:11:51 GMT
  ```




## 📝 License

This project is [MIT](https://github.com/woowacourse/atdd-subway-map/blob/master/LICENSE) licensed.


