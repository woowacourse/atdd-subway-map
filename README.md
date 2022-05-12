## 지하철 노선도 미션

## 기능 요구 사항

지하철역 관리 API 기능 완성하기

### 지하철역

- [x] 지하철역 등록 : POST /stations
    - 요청 성공시, 상태코드 201을 반환한다.
    - 중복되는 이름의 지하철역이 존재하는 경우, 상태코드 400을 반환한다.
- [x] 지하철역 목록 : GET /stations
    - 요청 성공시, 상태코드 200을 반환한다.
- [x] 지하철역 삭제 : DELETE /stations/:id
    - 요청 성공시, 상태코드 204를 반환한다.
    - 삭제하려는 지하철역이 존재하지 않는 경우, 상태코드 400을 반환한다.

### 지하철 노선

<br>

## 🐞 Bug Report

버그를 발견한다면, [Issues](https://github.com/woowacourse/atdd-subway-map/issues) 에 등록해주세요 :)

<br>

## 📝 License

This project is [MIT](https://github.com/woowacourse/atdd-subway-map/blob/master/LICENSE) licensed.
