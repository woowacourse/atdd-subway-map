# 3단계

---

## 지하철 노선 변경사항

### 지하철 노선 등록

- [ ] [노선 등록](https://techcourse-storage.s3.ap-northeast-2.amazonaws.com/c682be69ae4e412c9e3905a59ef7b7ed#_%EC%A7%80%ED%95%98%EC%B2%A0_%EB%85%B8%EC%84%A0_%EB%93%B1%EB%A1%9D)
  - 노선 추가 시 3가지 정보를 추가로 입력 받음
    - 상행종점, 하행종점, 두 종점간의 거리


## 지하철 구간

### 지하철 구간 등록

- [ ] [구간 등록](https://techcourse-storage.s3.ap-northeast-2.amazonaws.com/c682be69ae4e412c9e3905a59ef7b7ed#_%EA%B5%AC%EA%B0%84_%EB%93%B1%EB%A1%9D)

#### 상행 종점 등록
- [x] 새로운 상행 종점을 등록한다
  - 새로운 구간의 하행역과 기존에 있는 상행역이 같을 경우
-
#### 하행 종점 등록
- [x] 새로운 하행 종점을 등록한다
  - 새로운 구간의 상행역과 기존에 있는 하행역이 같을 경우

#### 갈래길 방지
- [x] 하나의 노선에는 갈래길이 허용되지 않기 때문에 새로운 구간이 추가되기 전에 갈래길이 생기지 않도록 기존 구간을 변경한다
  - [x] 상행역이 같을 때
  - [x] 하행역이 같을 때
- [x] [`예외`] 역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없음
- [ ] [`예외`] 상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없음
- [ ] [`예외`] 상행역과 하행역 둘 모두 포함되어있지 않으면 추가할 수 없음

### 구간 목록

- [ ] 노선에 포함된 구간 정보를 통해 상행 종점부터 하행 종점까지의 역 목록을 응답

### 지하철 구간 삭제

- [ ] [구간 삭제](https://techcourse-storage.s3.ap-northeast-2.amazonaws.com/c682be69ae4e412c9e3905a59ef7b7ed#_%EA%B5%AC%EA%B0%84_%EC%A0%9C%EA%B1%B0)

### 기능 요구 사항 부가 설명: 구간 제거

- [ ] 종점이 제거될 경우 다음으로 오던 역이 종점이 됨
- [ ] 중간역이 제거될 경우 재배치를 함
  - 노선에 A - B - C 역이 연결되어 있을 때 B역을 제거할 경우 A - C로 재배치 됨
  - 거리는 두 구간의 거리의 합으로 정함


# 1 ~ 2단계

---

# 기능 목록

## 지하철역

### 지하철역 등록

#### 정상 케이스

- [X] [지하철역 등록 요청 & 응답](https://techcourse-storage.s3.ap-northeast-2.amazonaws.com/d5c93e187919493da3280be44de0f17f#_%EC%A7%80%ED%95%98%EC%B2%A0%EC%97%AD_%EB%93%B1%EB%A1%9D)

#### 예외 케이스

- [X] 공백으로만 된 문자열을 허용하지 않는다 (null, white space)

  - Bad Request: 400

- [X] 지하철역 생성 시 이미 등록된 이름으로 요청한다면 에러를 응답
  - Bad Request: 400

### 지하철역 목록

#### 정상 케이스

- [x] [지하철역 목록 요청 & 응답](https://techcourse-storage.s3.ap-northeast-2.amazonaws.com/d5c93e187919493da3280be44de0f17f#_%EC%A7%80%ED%95%98%EC%B2%A0%EC%97%AD_%EB%AA%A9%EB%A1%9D)

### 지하철역 삭제

#### 정상 케이스

- [x] [지하철역 삭제 요청 & 응답](https://techcourse-storage.s3.ap-northeast-2.amazonaws.com/d5c93e187919493da3280be44de0f17f#_%EC%A7%80%ED%95%98%EC%B2%A0%EC%97%AD_%EC%82%AD%EC%A0%9C)

#### 예외 케이스

- [x] 지하철 역이 존재하지 않을 경우
  - Not Found: 404

## 지하철 노선

### 지하철 노선 등록

#### 정상 케이스

- [x] [지하철 노선 등록 요청 & 응답](https://techcourse-storage.s3.ap-northeast-2.amazonaws.com/d5c93e187919493da3280be44de0f17f#_%EC%A7%80%ED%95%98%EC%B2%A0_%EB%85%B8%EC%84%A0_%EB%93%B1%EB%A1%9D)

#### 예외 케이스

- [x] 공백으로만 된 문자열을 허용하지 않는다 (null, white space)
  - Bad Request: 400

- [x] 지하철역가 마찬가지로 중복된 노선 이름 허용하지 않음
  - Bad Request: 400

### 지하철 노선 목록

#### 정상 케이스

- [x] [지하철 노선 목록 요청 & 응답](https://techcourse-storage.s3.ap-northeast-2.amazonaws.com/d5c93e187919493da3280be44de0f17f#_%EC%A7%80%ED%95%98%EC%B2%A0_%EB%85%B8%EC%84%A0_%EB%AA%A9%EB%A1%9D)

### 지하철 노선 조회

#### 정상 케이스

- [x] [지하철 노선 조회 요청 & 응답](https://techcourse-storage.s3.ap-northeast-2.amazonaws.com/d5c93e187919493da3280be44de0f17f#_%EC%A7%80%ED%95%98%EC%B2%A0_%EB%85%B8%EC%84%A0_%EC%A1%B0%ED%9A%8C)

#### 예외 케이스

- [x] 지하철 노선이 존재하지 않을 경우
  - Not Found: 404

### 지하철 노선 수정

#### 정상 케이스

- [x] [지하철 노선 수정 요청 & 응답](https://techcourse-storage.s3.ap-northeast-2.amazonaws.com/d5c93e187919493da3280be44de0f17f#_%EC%A7%80%ED%95%98%EC%B2%A0_%EB%85%B8%EC%84%A0_%EC%88%98%EC%A0%95)

#### 예외 케이스

- [x] 공백으로만 된 문자열을 허용하지 않는다 (null, white space)
  - Bad Request: 400

- [x] 중복된 노선 이름 허용하지 않음
  - Bad Request: 400

- [x] 지하철 노선이 존재하지 않을 경우
  - Not Found: 404

### 지하철 노선 삭제

#### 정상 케이스

- [x] [지하철 노선 삭제 요청 & 응답](https://techcourse-storage.s3.ap-northeast-2.amazonaws.com/d5c93e187919493da3280be44de0f17f#_%EC%A7%80%ED%95%98%EC%B2%A0_%EB%85%B8%EC%84%A0_%EC%82%AD%EC%A0%9C)

#### 예외 케이스

- [x] 지하철 노선이 존재하지 않을 경우
  - Not Found: 404
