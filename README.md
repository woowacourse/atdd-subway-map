# 🚀 기능 목록

## 🚉 지하철역

### 지하철역 등록

* **정상 케이스**
  - [X] [지하철역 등록 요청 & 응답](https://techcourse-storage.s3.ap-northeast-2.amazonaws.com/c682be69ae4e412c9e3905a59ef7b7ed#_%EC%A7%80%ED%95%98%EC%B2%A0%EC%97%AD_%EB%93%B1%EB%A1%9D)

* **예외 케이스**
  - [X] 공백으로만 된 문자열을 허용하지 않는다 (null, white space)
    - Bad Request: 400
  - [X] 지하철역 생성 시 이미 등록된 이름으로 요청한다면 에러를 응답
    - Bad Request: 400

### 지하철역 목록

* **정상 케이스**
  - [x] [지하철역 목록 요청 & 응답](https://techcourse-storage.s3.ap-northeast-2.amazonaws.com/c682be69ae4e412c9e3905a59ef7b7ed#_%EC%A7%80%ED%95%98%EC%B2%A0%EC%97%AD_%EB%AA%A9%EB%A1%9D)

### 지하철역 삭제

* **정상 케이스**

- [x] [지하철역 삭제 요청 & 응답](https://techcourse-storage.s3.ap-northeast-2.amazonaws.com/c682be69ae4e412c9e3905a59ef7b7ed#_%EC%A7%80%ED%95%98%EC%B2%A0%EC%97%AD_%EC%82%AD%EC%A0%9C)

* **예외 케이스**
  - [x] 지하철 역이 존재하지 않을 경우
    - Not Found: 404

## 🛤 지하철 노선

### 지하철 노선 등록

* **정상 케이스**
  - [x] [지하철 노선 등록 요청 & 응답](https://techcourse-storage.s3.ap-northeast-2.amazonaws.com/c682be69ae4e412c9e3905a59ef7b7ed#_%EC%A7%80%ED%95%98%EC%B2%A0_%EB%85%B8%EC%84%A0_%EB%93%B1%EB%A1%9D)

* **예외 케이스**
  - [x] 공백으로만 된 문자열을 허용하지 않는다 (null, white space)
    - Bad Request: 400
  - [x] 중복된 노선 이름 허용하지 않음
    - Bad Request: 400
  - [x] 상행, 하행 지하철역이 존재하지 않는 경우
    - Not Found: 404
  
### 지하철 노선 목록

* **정상 케이스**
  - [ ] [지하철 노선 목록 요청 & 응답](https://techcourse-storage.s3.ap-northeast-2.amazonaws.com/c682be69ae4e412c9e3905a59ef7b7ed#_%EC%A7%80%ED%95%98%EC%B2%A0_%EB%85%B8%EC%84%A0_%EB%AA%A9%EB%A1%9D)

### 지하철 노선 조회

* **정상 케이스**
  - [x] [지하철 노선 조회 요청 & 응답](https://techcourse-storage.s3.ap-northeast-2.amazonaws.com/c682be69ae4e412c9e3905a59ef7b7ed#_%EC%A7%80%ED%95%98%EC%B2%A0_%EB%85%B8%EC%84%A0_%EC%A1%B0%ED%9A%8C)

* **예외 케이스**
  - [x] 지하철 노선이 존재하지 않을 경우
    - Not Found: 404

### 지하철 노선 수정

* **정상 케이스**
  - [ ] [지하철 노선 수정 요청 & 응답](https://techcourse-storage.s3.ap-northeast-2.amazonaws.com/c682be69ae4e412c9e3905a59ef7b7ed#_%EC%A7%80%ED%95%98%EC%B2%A0_%EB%85%B8%EC%84%A0_%EC%88%98%EC%A0%95)

* **예외 케이스**
  - [x] 공백으로만 된 문자열을 허용하지 않는다 (null, white space)
    - Bad Request: 400
  - [x] 중복된 노선 이름 허용하지 않음
    - Bad Request: 400
  - [x] 지하철 노선이 존재하지 않을 경우
    - Not Found: 404

### 지하철 노선 삭제

* **정상 케이스**
  - [ ] [지하철 노선 삭제 요청 & 응답](https://techcourse-storage.s3.ap-northeast-2.amazonaws.com/c682be69ae4e412c9e3905a59ef7b7ed#_%EC%A7%80%ED%95%98%EC%B2%A0_%EB%85%B8%EC%84%A0_%EC%82%AD%EC%A0%9C)

* **예외 케이스**
  - [x] 지하철 노선이 존재하지 않을 경우
    - Not Found: 404

## 🚟 지하철 구간

### 지하철 구간 등록

* **정상 케이스**
  - [ ] [지하철 구간 등록 요청 & 응답](https://techcourse-storage.s3.ap-northeast-2.amazonaws.com/c682be69ae4e412c9e3905a59ef7b7ed#_%EA%B5%AC%EA%B0%84_%EB%93%B1%EB%A1%9D)
  
* **예외 케이스**
  - [ ] 지하철 노선이 존재하지 않을 경우
    - Not Found: 404
  - [ ] 구간에 등록할 지하철 역이 존재하지 않을 경우
    - Not Found: 404
  - [ ] 구간을 등록할 수 없는 경우
    - Bad Request: 400
    > 구간 등록 세부 사항 
    > * [ ] 상행 또는 하행 종점 추가 가능
    > * [ ] 갈래 길을 방지하기 위해 이미 존재하는 구간이 있을 경우 해당 구간 사이에 추가
    > * [ ] 기존 구간 사이에 추가시 기존의 길이보다 길 경우 추가 불가
    > * [ ] 상행역과 하행역 모두 이미 노선에 추가된 경우 추가 불가 
    > * [ ] 종점을 제외하고 상행역과 하행역 둘중 하나라도 노선에 없는경우 추가 불가
    
### 지하철 구간 제거

* **정상 케이스**
  - [ ] [지하철 구간 제거 요청 & 응답](https://techcourse-storage.s3.ap-northeast-2.amazonaws.com/c682be69ae4e412c9e3905a59ef7b7ed#_%EA%B5%AC%EA%B0%84_%EC%A0%9C%EA%B1%B0)
  
* **예외 케이스**
  - [ ] 지하철 노선이 존재하지 않을 경우
    - Not Found: 404
  - [ ] 구간으로 등록된 지하철 역이 존재하지 않을 경우
    - Not Found: 404
  - [ ] 구간을 제거할 수 없는 경우
    - Bad Request: 400
    > 구간 제거 세부 사항
    > * [ ] 종점 제거시 직전의 역이 종점이 됨
    > * [ ] 기존 역 사이의 구간을 제거시 양 옆의 구간이 합쳐짐
    > * [ ] 노선에 추가된 구간이 하나인 경우 해당 역 제거 불가