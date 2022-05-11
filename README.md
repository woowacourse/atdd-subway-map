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

## 기능 구현 목록

### API

#### Station

- [x] 지하철역을 등록 할 수 있다.
    - [x] 이름이 중복이면 등록 할 수 었다.
    - [x] 이름이 공백이 될 수 없다.
    - [x] 이름의 길이가 15자가 넘으면 안된다.
    - [x] 등록을 하기 위해서 name이 필요하다.
    - [x] 등록을 하면 역 ID와 name을 응답한다.
- [x] 모든 지하철역 목록을 조회 할 수 있다.
- [x] 지하철역을 삭제 할 수 있다.
    - 역 ID를 받아서 해당하는 역을 삭제한다
    - 존재하지 않는 역 id는 삭제할 수 없다.

#### Line

- [x] 노선을 등록 할 수 있다.
    - 노선의 이름과, 색, 구간(시작역, 끝역, 거리)가 필요하다.
    - 이름이 중복이면 등록 할 수 었다.
    - 등록을 하면 역 ID와 name, color, 노선에 포함된 역들을 응답한다.
- [x] 모든 노선을 목록을 조회 할 수 있다.
    - 각 노선의 id, name, color 역들을 포함한다.
- [x] id에 해당하는 노선을 조회 할 수 있다.
- [x] id에 해당하는 노선 정보를 수정 할 수 있다.
- [x] 노선을 삭제 할 수 있다.
    - 노선 ID를 받아서 해당하는 노선을 삭제한다

#### Section

- [x] 구간을 등록할 수 있다.
  - 상행 역 ID, 하행 역 ID, 구간 길이가 필요하다.
  - 성공시 200 상태 코드만 응답한다.
  - 이미 있는 역들을 가진 구간을 등록하려면 예외 발생한다.
  - 구간을 내부로 삽입하는 경우 기존 구간보다 삽입되는 구간이 길면 예외 발생한다.
  - 
- [x] 구간을 삭제할 수 있다.
  - 역 ID가 필요하다
  - 성공시 200 상태 코드만 응답한다.

### Domain

- Station
  - [x] 역의 이름이 공백인지 검사한다.
  - [x] 역의 이름이 15자를 초과하는지 검사한다.
- Line
  - [x] 노선의 이름이 공백인지 검사한다.
  - [x] 노선은 반드시 한 개 이상의 구간을 가진다.
- Section
  - [x] 구간이 쪼갤 수 있는 지 확인한다.
  - [x] 구간을 쪼개서 반환한다.
    - [x] 시작점과 끝점이 모두 같으면 예외를 반환한다.
    - [x] 구간을 쪼개는 경우 삽입되는 구간이 원래 구간보다 길거나 같으면 예외를 반환한다.
  - [x] 구간에 특정 역이 포함되는 지 확인한다.
  - [x] 두 구간을 연결한 한 구간을 만든다.
    - [x] 두 구간이 연결되지 않으면 예외를 반환한다.
    - [x] 두 구간이 시작과 끝이 동일하면 예외를 반환한다.
- Sections
  - [x] 특정 구간과 분할된 구간을 찾는다.
    - [x] 특정 구간이 이미 연결된 구간이면 예외를 반환한다.
    - [x] 특정 구간이 현재 구간들과 접점이 없으면 예외를 반환한다.
  - [x] 역 id로 삭제할 구간들을 구한다.
    - [x] 현재 구간이 1개인데 삭제할 구간을 구하면 예외를 던진다.
  - [x] 특정 역과 근접한 구간들을 반환한다.
- DeletableSection
  - [x] 삭제할 구간이 2개 이상이면 예외를 반환한다.
  - [x] 근접한 구간을 합친 하나의 구간을 반환한다.
- StationDao
   - [x] 중복되지않는 이름의 역을 저장한다.
   - [x] 저장된 모든 역을 반환한다.
   - [x] ID에 해당하는 역을 삭제한다.
- LineDao
    - [x] 중복되지않는 이름의 노선을 저장한다.
    - [x] 저장된 모든 노선을 반환한다.
    - [x] ID에 해당하는 노선을 삭제한다.
    - [x] ID에 해당하는 노선을 반환한다.
- SectionDao
  - [x] 구간을 저장한다.
  - [x] 특정 노선의 구간들을 반환한다.
  - [x] 특정 노선을 삭제한다.

#### 미구현
- Name
  - 역의 이름 끝에 역이 없으면 역을 추가한다.
  - 노선의 이름 끝에 "선"이 없으면 "선" 문자를 추가한다.
  - 이름이 공백이 될 수 없다.
  - 이름의 길이가 15자가 넘으면 안된다.
  - 다른 이름과 동일한지 확인한다.
- Color
  - 입력값이 "bg-xxxx-x00"의 형식을 따르는지 검사한다.
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
