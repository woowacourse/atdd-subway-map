<p align="center">
    <img width="200px;" src="https://raw.githubusercontent.com/woowacourse/atdd-subway-admin-frontend/master/images/main_logo.png"/>
</p>
<p align="center">
  <img alt="npm" src="https://img.shields.io/badge/npm-%3E%3D%205.5.0-blue">
  <img alt="node" src="https://img.shields.io/badge/node-%3E%3D%209.3.0-blue">
  <a href="https://techcourse.woowahan.com/c/Dr6fhku7" alt="woowacuorse subway">
    <img alt="Website" src="https://img.shields.io/website?url=https%3A%2F%2Fedu.nextstep.camp%2Fc%2FR89PYi5H">
  </a>
  <img alt="GitHub" src="https://img.shields.io/github/license/woowacourse/atdd-subway-map">
</p>

<br>

# 지하철 노선도 미션
스프링 과정 실습을 위한 지하철 노선도 애플리케이션

<br>

## 🚀 Getting Started

### Install
#### npm 설치
```
cd frontend
npm install
```
> `frontend` 디렉토리에서 수행해야 합니다.

### Usage
#### webpack server 구동
```
npm run dev
```
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
- [x] 지하철 역 관리
    - [ ] 입력 값이 "~역"으로 끝나지 않으면 예외 처리
    - [x] 같은 지하철 역 생성 불가 기능
    - [x] 삭제 기능
- [x] 지하철 노선 관리 
    - [ ] 입력 값이 "~선"으로 끝나지 않으면 예외 처리
    - [x] 노선 생성
    - [x] 노선 목록 조회
    - [x] 노선 조회
    - [x] 노선 수정
    - [x] 노선 삭제
- [x] 스프링 빈 적용
- [x] H2 적용
- [x] 노선 추가시 구간 추가
    - [x] 상행역, 하행역, 거리 없는 경우 예외처리
    - [x] upStationId와 downStationId가 같다면 예외 처리
    - [x] upStationId나 downStationId가 등록되어 있지 않다면 예외 처리
    - [x] distance가 0 이하면 예외 처리
- [x] 구간 추가
    - [x] 기본 예외 상황
        - [x] 요청한 노선에 upStation와 downStation이 둘 다 없다면 예외처리
        - [x] 요청한 노선에 upstation와 downStation가 둘 다 이미 등록되어 있다면 예외처리
    - [x] 요청한 구간이 종점에 붙이는 구간인 경우
    - [x] 요청한 구간이 기존 역들 사이에 붙이는 경우
        - [x] 요청한 구간의 distance >= 기존 구간의 distance 라면 예외처리
        - [x] 기존의 연결관계를 해제시키고 새로운 연결관계 구축
- [x] 역 목록 응답
    - [x] 상행 종점 ~ 하행 종점의 역 목록을 응답할 것
- [x] 구간 제거
    - [x] 구간이 하나인 노선에서는 구간 제거를 할 수 없음 

## 리팩토링 중점 사안
- [x] Dao에서 도메인을 반환하도록 반환
- [x] DB Unique를 통해 중복 검사 하지 않을 것
    - [x] findBy~ 로 Optional 처리
    - [x] 노선 이름 같은게 있다면 예외처리
- [x] Response에서 null값을 포함하지 않도록 할 것
    - [x] null대신 빈 리스트 "[]"를 반환할 것
- [x] 정적 팩토리 메서드로 도메인 <-> DTO 변환
- [x] 인수테스트 상태코드 말고도 내용 검증 할 것
    - [x] 노선 중복 테스트 진행
- [x] Optional.orElseThrow를 통한 가독성 증대
- [x] Controller에서의 Validation 필요성 재고
    - [ ] Controller는 무엇이고, 어떤 역할을 도맡는가?
- [x] if 안에서는 조건만 체크하고, 필요한 행위는 따로 메서드로 분``리하기
- [x] Service Layer에서 toDB와 같은 계층 간 강한 결합의 네이밍은 변경하기
- [x] Service Layer에서의 로직은 조회 -> 저장 순서로 바꾸기
- [x] SectionRepository를 만들어 온전한 Section을 만들어 Service에 반환하기
- [ ] StationService가 LineService를 호출하여 역 삭제시 필요한 구간 삭제토록 변경

## 질문 사항
- findById 이후 CRUD 로직을 수행하면 DB를 한 번에 2번 찌르는 건데 병목이 발생하진 않을까?
    - Service단에서 Id를 캐싱해두는 것도 방법이 되지 않을까?
    - 2번 정도는 컴퓨팅 능력으로 커버 가능?
- Line 도메인의 List<Station>을 지정해주려면 LineDao 에서만으로 처리가 불가능하다
    - 그래서 Service에서 StationDao에 접근해 해당 LineId에 소속된 station들을 가져오도록 했다
    - 이후 setter를 통해 line에 주입해줬다
        - setter는 지양하는 것으로 알고있다
    - Repository 계층의 필요성에 대해 생각해 보게 되었다!
    - 현재 Dao가 충분히 필요한 정보를 모으지 못한 채 도메인 객체를 반환해서 생기는 문제 같다!
- 역이 제거되면...? Section 테이블의 정보들은...?
- 이미 있는 역인지를 검증하는 게 맞겠지?
- 노선 수정할 때의 LineRequest와 생성할 때의 lineRequest가 다르네
    - 각각 Dto를 만들어줘야 할까?
    - 중복된 필드 값을 사용하는데 각각 필요한 validation이 다를 때 어찌하지?
- Entity와 Domain의 차이점?