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


# 🚇 기능 요구사항
- [X] 지하철역 등록 기능
- [X] 지하철역 목록 조회 기능
- [X] 지하철역 삭제 기능
- [X] 지하철 노선 등록 기능
- [X] 지하철 노선 목록 기능
- [X] 지하철 노선 조회 기능
- [X] 지하철 노선 삭제 기능
- [X] 지하철 노선 수정 기능
- API 스펙은 [API 문서 v1](https://techcourse-storage.s3.ap-northeast-2.amazonaws.com/d5c93e187919493da3280be44de0f17f#Line) 참고

🛠추가된 요구사항
- [ ] 노선 추가시 3가지 정보를 추가로 입력받기
  - upStationId: 상행 종점
  - downStationId: 하행 종점
  - distance: 두 종점간의 거리
- [ ] 두 종점간의 연결 정보를 이용하여 구간(Section) 정보도 함께 등록
- [ ] 노선에 구간을 추가하기
- [ ] 노선에 포함된 구간 정보를 통해 상행 종점부터 하행 종점까지의 역 목록을 응답하기
- [ ] 구간 제거하기
- 변경된 API 스펙은 [API 문서 v2](https://techcourse-storage.s3.ap-northeast-2.amazonaws.com/c682be69ae4e412c9e3905a59ef7b7ed) 참고



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
