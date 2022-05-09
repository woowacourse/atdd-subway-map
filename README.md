<p align="center">
    <img width="200px;" src="https://raw.githubusercontent.com/woowacourse/atdd-subway-admin-frontend/master/images/main_logo.png"/>
</p>
<p align="center">
  <a href="https://techcourse.woowahan.com/c/Dr6fhku7" alt="woowacourse subway">
    <img alt="Website" src="https://img.shields.io/website?url=https%3A%2F%2Fedu.nextstep.camp%2Fc%2FR89PYi5H">
  </a>
  <img alt="GitHub" src="https://img.shields.io/github/license/woowacourse/atdd-subway-map">
</p>

<br>

# 지하철 노선도 미션
스프링 과정 실습을 위한 지하철 노선도 애플리케이션

# 요구사항 정리
- 지하철 역
  - 역 관리 API 기능 완성한다.
  - 지하철역 생성 시 이미 등록된 이름으로 요청한다면 에러를 응답한다.
- 지하철 노선
  - 노선 추가 시 3가지 정보도 추가로 입력받는다.
    - upStationId: 상행 종점
    - downStatiionId: 하행 종점
    - distance: 두 종점간의 거리
    - 두 종점간의 연결 정보를 이용하여 노선 추가 시 구간(Section) 정보도 함께 등록
  - [예외] 같은 이름의 노선은 생성 불가하다.
  - 노선 관리 API 구현
    - [API 문서](https://techcourse-storage.s3.ap-northeast-2.amazonaws.com/c682be69ae4e412c9e3905a59ef7b7ed#Line)
- 노선 기능에 대한 End to End 테스트 작성한다.
- 구간 관리 API 구현
  - 추가 기능
    - 노선에 구간을 추가
    - 노선에 포함된 구간 정보를 통해 상행 종점부터 하행 종점까지의 역 목록을 응답
    - 구간 제거
  - 구간 관리 API 스펙은 [API 문서](https://techcourse-storage.s3.ap-northeast-2.amazonaws.com/c682be69ae4e412c9e3905a59ef7b7ed#Section) 참고

# API
## 지하철 역
- [ ] 지하철 역 등록 `POST /stations`
- [ ] 지하철 역 목록 `GET /stations`
- [ ] 지하철 역 삭제 `DELETE /stations/{id}`

## 지하철 노선
- [ ] 지하철 노선 등록 `POST /lines`
- [ ] 지하철 노선 목록 `GET /lines`
- [ ] 지하철 노선 조회 `GET /lines/{id}`
- [ ] 지하철 노선 수정 `PUT /lines/{id}`
- [ ] 지하철 노선 삭제 `DELETE /lines/{id}`

## 구간
- [ ] 구간 등록 `POST /lines/1/sections`
- [ ] 구간 제거 `DELETE /lines/1/sections?stationId=2`


## 🚀 Getting Started
### Usage
#### application 구동
```
./gradlew bootRun
```
<br>

## ✏️ Code Review Process
[텍스트와 이미지로 살펴보는 온라인 코드 리뷰 과정](https://github.com/next-step/nextstep-docs/tree/master/codereview)

<br>

## 🐞 Bug Report

버그를 발견한다면, [Issues](https://github.com/woowacourse/atdd-subway-map/issues) 에 등록해주세요 :)

<br>

## 📝 License

This project is [MIT](https://github.com/woowacourse/atdd-subway-map/blob/master/LICENSE) licensed.
