<br>

# 스프링 - 지하철 노선도 관리

<br>

## 3단계 - 지하철 구간 관리 기능

<br>

- ### 지하철 노선(Line) 추가

  - 요청
  ```http
  POST /lines HTTP/1.1
  accept: */*
  content-type: application/json; charset=UTF-8
  
  {
      "name": "신분당선",
      "color": "bg-red-600",
      "upStationId": "1",
      "downStationId": "2",
      "distance": "10"
  }
  ```
  <br>

  - 처리
      - 해당 구간(Section)을 포함한 노선(Line)을 생성한다.
      - 노선을 생성한 노선으로, 상행 종점역을 상행역으로, 하행 종점역을 하행역으로 해서 구간(Section)을 생성한다.
      - DB에는 다대일(N:1)의 형태로 구간(Section)이 노선(Line)의 id를 FK로 참조하는 형태로 저장한다.
      - 노선을 저장한다.
      - 구간을 저장한다.

  <br>

  - 응답
  ```http
  HTTP/1.1 201 
  Location: /lines/1
  Content-Type: application/json
  Date: Fri, 13 Nov 2020 00:11:51 GMT
  
  {
      "id": 1,
      "name": "신분당선",
      "color": "bg-red-600",
      "upStationId": "1",
      "downStationId": "2",
      "distance": "10"
  }
  ```
  - - -
  <br>

- ### 지하철 구간(Section) 추가

  - 요청
  ```http
  POST /lines/1/sections HTTP/1.1
  accept: */*
  content-type: application/json; charset=UTF-8
  host: localhost:52165
  
  {
      "upStationId": "2",
      "downStationId": "4",
      "distance": 10
  }
  ```
  <br>

  - 처리
      - 구간 추가 유효성 검사
          - 요청 정보대로 새로운 구간(Section)을 생성한다.
          - 기존 노선(Line)의 모든 구간(Section)들을 가져온다.
          -  기존 노선(Line)의 모든 구간(Section)들 중에, 새로 추가할 구간(Section)의 상행역(Station) 또는 하행역(Station)을 갖고있는 구간(Section)들을 모두 찾는다. __(1)__
          - 찾은 구간들에 있는 역(Station)들의 Id를 중복을 제거해서 추출한다.
          - 추출된 역들의 Id 중에서 새로 추가할 구간(Section)의 상행역(Station)  Id 또는 하행역(Station) Id를 필터링한다. __(2)__
              - 필터링 된 개수가 1개가 아니면, 예외를 발생시킨다.
          - __(2)__ 의 결과는 추가 기준 역(Station)의 Id다. __(1)__ 에서 이 역 Id를 포함하는 기존 구간(Section)들을 모두 찾는다. __(3)__
              - __(3)__ 의 개수가 1개일 때,
                  - 추가 기준 역이 새로운 구간의 상행역이고 기존 구간의 하행역 이거나, 추가 기준 역이 새로운 구간의 하행역이고 기존 구간의 상행역 일 때
                      - 새로 추가할 구간(Section)은 노선(Line)의 맨 앞 또는 맨 뒤의 구간이 된다.
                      - 새로운 구간(Section)을 DB에 바로 저장하면 구간 추가 작업은 끝난다.
              - 위의 조건에 해당하지 않을 경우, 새로 추가할 구간(Section)은 노선(Line)의 중간에 추가된다.
                  - 추가 기준역이 새로 추가할 구간(Section)의 어느 방향 역인지 구한다. __(4)__
                      - 추가 기준역을 __(4)__ 방향으로 갖고있는 기존 구간(Section)을 __(3)__ 에서 찾는다. __(5)__
                      - 새로 추가할 구간의 길이가 __(5)__ 의 길이보다 크거나 같으면 예외를 발생시킨다.
                      - 새로 추가할 구간의 길이가 __(5)__ 의 길이보다 작으면,
                          - 쪼개진 새로운 구간을 생성한다.
                              - __(4)__ 방향의  반대방향 역을 __(5)__ 의 __(4)__ 방향의 반대방향 역으로 한다.
                              - __(4)__  방향의 역을 새로운 역으로 한다.
                              - 길이를 __(5)__ 의 길이 - 새로 추가할 구간의 길이로 한다.
                          - __(5)__ 를 DB에서 삭제한다.
                          - 쪼개진 새로운 구간을 DB에 저장한다.
                          - 새로 추가할 구간을 DB에 저장한다.

  <br>

  - 응답
  ```http
  HTTP/1.1 201 
  Location: /lines/1/sections/1
  Content-Type: application/json
  Date: Fri, 13 Nov 2020 00:11:51 GMT
  
  {
      "id": 1,
      "upStationId": "2",
      "downStationId": "4",
      "distance": 10
  }
  ```
  - - -
  <br>

- ### 노선(Line) 조회 시, 해당 노선의 상행 종점역부터 하행 종점역까지 연결된 순서대로 응답

  - 요청
  ```http
  GET /lines/1 HTTP/1.1
  accept: application/json
  host: localhost:49468
  ```
  <br>
  
  - 처리
      - 조회할 노선(Line)의 id를 FK로 갖는 구간(Section)들을 DB에서 모두 가져온다.
      - 첫 번째 구간(Section)을 기준으로 한다.
      - 빈 LinkedList를 만들고, 첫 번째 구간(Section)을 넣는다.
      - LinkedList의 0번째 인덱스에 있는 구간의 상행역(Station)을 하행역(Station)으로 갖고있는 구간(Section)을 LinkedList의 맨 앞에 추가한다. 더 이상 맨 앞에 추가할 구간(Section)이 없을 때 까지 반복한다.
      - LinkedList의 마지막에 있는 구간의 하행역(Station)을 상행역(Station)으로 갖고있는 구간(Section)을 LinkedList의 맨 뒤에 추가한다. 더 이상 맨 뒤에 추가할 구간(Section)이 없을 때 까지 반복한다.
      - LinkedList의 데이터를 응답 DTO 형태에 맞게 변환해 요청의 응답으로 반환한다.
  
  <br>
  
  - 응답
  ```http
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
  - - -
  <br>

- ### 지하철 구간(역) 제거

  - 요청
  ```http
  DELETE /lines/1/sections?stationId=2 HTTP/1.1
  accept: */*
  host: localhost:52165
  ```
  <br>
  
  - 처리
      - 삭제할 구간(역)이 존재하는 노선(Line)의 모든 구간들을 DB에서 조회해 온다.
      - 노선(Line)에 단 한 개의 구간(Section)만 존재하면 예외를 발생시킨다.
      - 삭제할 역(Station)이 존재하는 모든 구간(Section)을 찾는다.
          - 한 개의 구간(Section)도 없다면, 예외를 발생시킨다.
          - 한 개의 구간이 존재한다면, 해당 구간을 DB에서 삭제한다.
          - 두 개의 구간(A구간, B구간)이 존재한다면,
              - 새로운 구간을 만든다.
                  - 삭제할 역(Station)이 하행역으로 존재하는 구간(Section)의 상행역을 새로운 구간의 상행역으로 한다.
                  - 삭제할 역(Station)이 상행역으로 존재하는 구간(Section)의 하행역을 새로운 구간의 하행역으로 한다.
                  - 새로운 구간의 길이는 `A구간의 길이 + B구간의 길이` 로 한다.
                  - DB에서 A구간, B구간을 삭제한다.
                  - DB에 새로 만든 구간을 저장한다.
  
  <br>
  
  - 응답
  ```http
  HTTP/1.1 204 
  Date: Fri, 13 Nov 2020 00:11:51 GMT
  ```
  - - -
  <br>