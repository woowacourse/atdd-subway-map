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

<br>

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


## 기능 구현 목록

- 지하철 역
- [x] 지하철역 생성 시 이미 등록된 이름으로 요청한다면 에러를 응답

- 지하철 노선
- [x] 동일한 노선 생성 불가
- api
- [x] post - /lines 등록 - name, color
- [x] get - /lines - List - id, name, color 
- [x] get - /lines/{id} - id, name, color
- [x] put - /lines/{id} - name, color
- [x] delete - /lines/{id}

- 구간
- [x] 구간 등록
  - [x] 구간 중복이 없는지
  - [x] 거리가 0 초과인지
  - [x] 이미 이어진 구간인지 (반대로도, 순환 불가)
  - [x] 상행, 하행 둘 중 하나라도 구간에 존재하는지
- [x] 갈래길 제거
    - [x] 상행과 연결인지 하행과 연결인지 확인
    - [x] 상행 or 하행 to ? 의 길이와 새로운 섹션의 길이 비교
    - [x] 기존 데이터 업데이트, 새로운 노선은 추가
- [ ] 구간 삭제
    - [x] 전체 Section이 하나일 경우 예외 
    - [x] stationId를 포함한 Section을 모두 가져온다.
    - [x] 삭제 할 Section들 반환
