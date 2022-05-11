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

## 🔧 기능 요구 사항
### 1단계
1. 지하철 역, 노선 관리 API 기능 완성하기
   - 추가 기능: 지하철역, 노선 생성 시 이미 등록된 이름으로 요청한다면 에러를 응답
   - API 에 대한 스펙은 API 문서v1를 참고 : https://techcourse-storage.s3.ap-northeast-2.amazonaws.com/d5c93e187919493da3280be44de0f17f#Line
2. End to End 테스트 작성하기

### 2단계
1. 스프링 JDBC 활용하여 H2 DB에 저장하기
2. H2 DB를 통해 저장된 값 확인하기
3. 스프링 빈 활용하기

### 3단계
1. API 기능 완성하기
   - API 에 대한 스펙은 API 문서v2를 참고 : https://techcourse-storage.s3.ap-northeast-2.amazonaws.com/c682be69ae4e412c9e3905a59ef7b7ed#Station
2. 지하철 역이 삭제되는 기능 수정
   - 구간에서 이미 사용하고 있는 역은 삭제 할 수 없다.
3. 노선이 생성되는 기능 수정
   - 노선이 생성될 때 노선 상, 하역에 해당하는 구간이 생성된다.
4. 노선이 삭제되는 기능 수정
   - 노선이 삭제되면 노선의 모든 구간이 삭제된다.
5. 지하철 구간을 생성하는 기능 구현
   - 등록하고자하는 구간의 상, 하역이 모두 노선 위에 존재하지 않으면 구간을 생성 할 수 없다.
   - 등록하고자하는 구간의 상, 하역이 모두 노선 위에 존재하면 구간을 생성 할 수 없다.
   - 구간이 노선의 상, 하행종점에서 연장하여 생성될 수 있다.
   - 구간이 노선 중간에 생성 될 수 있다.
     - 겹쳐지는 노선의 상행역 혹은 하행역은 수정되며 구간의 길이는 등록하고자하는 구간의 길이만큼 줄어든다.
6. 지하철 구간을 삭제하는 기능 구현
   - 노선 위에 존재하는 특정 역을 삭제하여 구간을 삭제한다.
   - 구간을 삭제하기 위해 노선 중간의 역을 삭제하면 상, 하행 방향으로 존재하는 구간이 하나로 통합된다.
   - 구간을 삭제하기 위해 노선의 상, 하행종점을 삭제하면 바로 직전역이 상, 하행종점이 되며 구간이 삭제된다.

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
