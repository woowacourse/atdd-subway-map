# 기능 요구 사항

## 1,2 단계
### 1. 역 관리

- [x] 중복된 이름을 가진 역을 만들 수 없다.
- [x] 역을 삭제할 수 있다.

### 2. 노선 관리

- [x] 지하철 노선을 등록하는 기능
  - [x] 이름
  - [x] 색깔
  - [x] 중복된 이름을 가진 노선을 만들 수 없다.
- [x] 전체 지하철 노선을 조회하는 기능
- [x] 지하철 노선 하나를 조회하는 기능
- [x] 지하철 노선 내용을 수정하는 기능 
  - [x] 이름
  - [x] 색깔
- [x] 지하철 노선을 삭제하는 기능

## 3단계
### 1. 노선 추가 API 수정

- [x] 노선 추가 시 3가지 정보 추가(upStationId, downStationId, distance)
  - [x] 지하철 노선을 등록하는 기능
  - [x] 지하철 노선 목록을 조회하는 기능
- [x] 노선 추가 시 구간 정보도 함께 등록하는 기능

### 2. 구간 관리
- [ ] 구간을 등록하는 기능
- [ ] 구간을 제거하는 기리



### Reference

 [API 문서v1](https://techcourse-storage.s3.ap-northeast-2.amazonaws.com/d5c93e187919493da3280be44de0f17f#Line)
 [API 문서v2](https://techcourse-storage.s3.ap-northeast-2.amazonaws.com/c682be69ae4e412c9e3905a59ef7b7ed#Line)





