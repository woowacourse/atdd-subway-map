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

- [ ] [지하철 노선 등록 요청 & 응답](https://techcourse-storage.s3.ap-northeast-2.amazonaws.com/d5c93e187919493da3280be44de0f17f#_%EC%A7%80%ED%95%98%EC%B2%A0_%EB%85%B8%EC%84%A0_%EB%93%B1%EB%A1%9D)

#### 예외 케이스

- [x] 공백으로만 된 문자열을 허용하지 않는다 (null, white space)

  - Bad Request: 400

- 지하철역가 마찬가지로 중복된 노선 이름 허용하지 않음
  - 에러코드는 지하철역과 동일

### 지하철 노선 목록

#### 정상 케이스

- [지하철 노선 목록 요청 & 응답](https://techcourse-storage.s3.ap-northeast-2.amazonaws.com/d5c93e187919493da3280be44de0f17f#_%EC%A7%80%ED%95%98%EC%B2%A0_%EB%85%B8%EC%84%A0_%EB%AA%A9%EB%A1%9D)

### 지하철 노선 조회

#### 정상 케이스

- [지하철 노선 조회 요청 & 응답](https://techcourse-storage.s3.ap-northeast-2.amazonaws.com/d5c93e187919493da3280be44de0f17f#_%EC%A7%80%ED%95%98%EC%B2%A0_%EB%85%B8%EC%84%A0_%EC%A1%B0%ED%9A%8C)

### 지하철 노선 수정

#### 정상 케이스

- [지하철 노선 수정 요청 & 응답](https://techcourse-storage.s3.ap-northeast-2.amazonaws.com/d5c93e187919493da3280be44de0f17f#_%EC%A7%80%ED%95%98%EC%B2%A0_%EB%85%B8%EC%84%A0_%EC%88%98%EC%A0%95)

#### 예외 케이스

- 공백으로만 된 문자열을 허용하지 않는다 (null, white space)

  - Bad Request: 400

- 지하철역가 마찬가지로 중복된 노선 이름 허용하지 않음
  - 에러코드는 지하철역과 동일

### 지하철 노선 삭제

#### 정상 케이스

- [지하철 노선 삭제 요청 & 응답](https://techcourse-storage.s3.ap-northeast-2.amazonaws.com/d5c93e187919493da3280be44de0f17f#_%EC%A7%80%ED%95%98%EC%B2%A0_%EB%85%B8%EC%84%A0_%EC%82%AD%EC%A0%9C)

#### 예외 케이스

- 지하철 노선이 존재하지 않을 경우
  - Not Found: 404
