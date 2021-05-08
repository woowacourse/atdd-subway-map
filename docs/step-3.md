# 🚀 3단계 - 지하철 구간 관리 기능

## 3단계 - DB 모델 설계 
![image](https://user-images.githubusercontent.com/48986787/117430878-869d2200-af63-11eb-923d-5cc7a5592394.png)


## 3단계 - 지하철 구간 관리 기능
- [ ] 지하철 노선 추가 API 수정
    - [x] 노선 추가 시 3가지 정보를 추가로 입력 받음
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
    - [x] 노선 추가시 구간 정보도 함께 등록

- [x] 지하철 구간 추가 API 구현
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
- [x] 노선 조회 시 구간에 포함된 역 목록 응답
- [x] 노선에 포함된 구간 정보를 통해 상행 종점부터 하행 종점까지의 역 목록을 응답
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
  
- [ ] 지하철 구간 제거
    ```json
    DELETE /lines/1/sections?stationId=2 HTTP/1.1
    accept: */*
    host: localhost:52165
    ```

## 3단계 기능 요구사항
### 구간 등록/제거 부가 설명
새로 등록할 구간의 상행역과 하행역 중 노선에 이미 등록되어있는 역을 기준으로 새로운 구간을 추가한다.
- [x] 상행 종점 등록
- [x] 하행 종점 등록
- [ ] 갈래길 방지
    - [ ] 하나의 노선에는 갈래길이 허용되지 않기 때문에 새로운 구간이 추가되기 전에 갈래길이 생기지 않도록 기존 구간을 변경
- [ ] 구간 제거
    - [ ] 종점이 제거될 경우 다음으로 오던 역이 종점이 됨
    - [ ] 중간역이 제거될 경우 재배치를 함
    - [ ] 노선에 A - B - C 역이 연결되어 있을 때 B역을 제거할 경우 A - C로 재배치 됨
    - [ ] 거리는 두 구간의 거리의 합으로 정함


### 구간 등록/제거 예외 설명
- [ ] 역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없음
- [ ] 상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없음
    - [ ] A-B, B-C 구간이 등록된 상황에서 B-C 구간을 등록할 수 없음(A-C 구간도 등록할 수 없음)
- [ ] 상행역과 하행역 둘 중 하나가 노선에 포함되어있지 않으면 추가할 수 없음
- [ ] 구간이 하나인 노선에서 마지막 구간을 제거할 때
    - [ ] 제거할 수 없음