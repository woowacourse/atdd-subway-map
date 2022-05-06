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

# 기능 구현 목록

## 1단계

### 지하철역

* DAO

- [x] 새로운 역을 저장한다.
    - [x] 똑같은 이름의 역이 존재할 경우 예외를 발생시킨다.
- [x] 역 목록을 조회한다.
- [x] 역을 삭제한다.
    - [x] 존재하지 않는 역을 삭제하려는 경우 예외를 발생시킨다.

* Controller

- [x] 새로운 역을 등록한다.
  - [x] 성공적으로 등록된 경우 상태코드 201로 응답한다.
  - [x] 똑같은 이름의 역이 존재할 경우 예외를 응답한다.
- [x] 역 목록을 조회한다.
  - [x] 등록된 모든 역의 id와 name을 응답한다.
- [x] 역을 삭제한다.
  - [x] 성공적으로 삭제한 경우 상태코드 204로 응답한다.
  - [x] 존재하지 않는 역을 삭제하려는 경우 예외를 응답한다.

### 노선

* DAO

-[x] 노선을 저장한다
    -[x] 똑같은 이름의 노선이 존재할 경우 예외를 발생시킨다.
-[x] 노선 목록을 조회한다.
-[x] 노선 하나를 조회한다.
    -[x] 존재하지 않는 노선을 조회하려는 경우 예외를 발생시킨다.
-[x] 노선을 수정한다.
    -[x] 존재하지 않는 노선을 수정하려는 경우 예외를 발생시킨다.
-[x] 노선을 삭제한다.
    -[x] 존재하지 않는 노선을 삭제하려는 경우 예외를 발생시킨다.

* Controller

- [x] 새로운 노선을 등록한다.
  - [x] 성공적으로 등록된 경우 상태코드 201로 응답한다.
  - [x] 똑같은 이름의 노선이 존재할 경우 예외를 응답한다.
- [x] 노선 목록을 조회한다.
  - [x] 등록된 모든 노선의 id, name, color를 응답한다.
- [x] 단건의 노선을 조회한다.
  - [x] 존재하지 않는 노선을 조회하려는 경우 예외를 응답한다.
- [x] 단건의 노선을 수정한다.
  - [x] 성공적으로 수정된 경우 상태코드 200으로 응답한다.
  - [x] 존재하지 않는 노선을 수정하려는 경우 예외를 응답한다.
- [x] 노선을 삭제한다.
  - [x] 성공적으로 삭제한 경우 상태코드 204로 응답한다.
  - [x] 존재하지 않는 노선을 삭제하려는 경우 예외를 응답한다.

# 2단계

* 스프링 빈과 스프링 JDBC 적용하기

1. 스프링 JDBC 활용하여 H2 DB에 저장하기
   Dao 객체가 아닌 DB에서 데이터를 관리하기
   DB에 접근하기 위한 spring jdbc 라이브러리를 활용하기 (JdbcTemplate 등)
2. H2 DB를 통해 저장된 값 확인하기
   실제로 DB에 저장이 잘 되었는지 확인할 수 있도록 설정하기
   h2 console 활용 가능
3. 스프링 빈 활용하기
   매번 생성하지 않아도 되는 객체와 싱글톤이나 static으로 구현되었던 객체들을 스프링 빈으로 관리해도 좋음

- [x] jdbc 적용하기
- [x] 스프링 빈 적용


# 체크리스트

- [x] E2E 테스트를 작성한다.

# 고민할 사항

* ~~Line에 equals&hashCode는 어떤 필드가 들어가는 것이 적절한가?~~
  * 도메인 규칙 상 노선의 이름과 색깔이 같아야 같은 노선으로 판단할 것이므로 equals&hashCode는 name과 color 필드로 비교하고 DB에 제약조건 추가
* ~~Line 수정 API에서 name, color 두 필드 중 빈값이 존재할 때 어떻게 해야할까?~~
  * API 스펙상 PUT 메서드를 사용하기 때문에 두 필드 모두 변경하는 것으로 결정
* ~~save, update 등 부수효과를 일으키는 메서드에서 객체를 반환하지 않도록 수정해야하지 않을까?(CQS 패턴)~~
* LineDao와 StationDao 사이의 중복??
* LineAcceptanceTest에서 존재하지 않는 노선의 ID로 조회하는 것이 사용자 입장에서 가능한가?
  * 사용자가 어떤 노선 ID에 대해 존재하는지 존재하지 않는지 알 수 있을까?


# 1단계 피드백 사항

* lineDao.findById(id) -> 존재하지 않는 id가 들어가면 어떻게 되나요? , Optional 적용을 고민해볼 수 있을 것 같아요.
  * 존재하지 않는 id로 조회할 경우 DB에서 예외처리를 하고 있음.

```java
@Override
    public Line findById(final Long id) {
        final String sql = "SELECT id, name, color FROM LINE WHERE id = (?)";

        try {
            return jdbcTemplate.queryForObject(sql, (resultSet, rowNum) -> new Line(
                    resultSet.getLong("id"),
                    resultSet.getString("name"),
                    resultSet.getString("color")
            ), id);
        } catch (EmptyResultDataAccessException exception) {
            throw new NoSuchLineException();
        }
    }
```

Dao에서는 Optional로 반환하고 서비스에서 예외처리를 하는 것이 맞을까? 아니면 Dao에서 예외처리를 해주는 것이 맞을까. 
-> 해당 id가 존재하는지 검증하는 책임은 누구에게 있을까? Service일까 Dao일까

<br>

* 동등성 비교를 name과 color를 기준으로 하고 있네요. id는 어떤 역할을 하나요?
  * Line의 동등성 비교는 무엇으로 해야할까? 도메인 규칙상 name과 color로 비교해야한다고 결론지었음.
  * id는 auto_increment를 사용하기 때문에 id 값을 얻으려면 DB에 한번 저장이 되어야함. 그 전까지는 null
  * DB 스키마로 name과 color를 unique 속성으로 제한했기 때문에 같은 name과 color를 가진 두 개의 데이터는 존재할 수 없음

<br>

* 존재하지 않는 id를 삭제 시도 하면 어떻게 되나요?
  * 원래 존재하지 않는 id로 삭제 시도를 할 경우 예외를 던지도록 설계했었음.
  * JDBC로 DELETE문을 실행했을 때, 존재하지 않는 id라면 예외가 던져지지 않고 affected row가 0으로 반환됨.
  * 사용자 입장에서 생각해봤을때, 삭제가 일어나지 않으면 문제이지만, 없는 id로 삭제 요청을 보내면 삭제가 되든 안되든 별로 중요하지 않음. 원래 존재하지 않았으니까.

<br>
* 현재 IllegalArgumentException, NoSuchElementException 예외만 처리해주고 있는데 다른 예외가 발생하면 어떻게 되나요?

ExceptionHandler에 등록되지 않은 에러가 발생하면 [BasicErrorController](https://docs.spring.io/spring-boot/docs/current/api/org/springframework/boot/autoconfigure/web/servlet/error/BasicErrorController.html) 가 동작한다.

브라우저에서 요청하면 `errorHtml()` 메서드가 호출되어 Html 응답이 반환되고, postman이나 Curl같은 브라우저가 아닌 곳에서 요청을 하면 `error()` 메서드가 호출되어 JSON으로 응답을 받는다.
현재 의미있는 에러 메시지를 줄만한 에러는  IllegalArgumentException과 NoSuchElementException 밖에 없다고 생각함.

ExceptionHandler에 Exception 클래스를 등록해서 모든 예외를 핸들링하도록 처리할 수는 있으나 스프링이 기본 제공하는 에러 핸들링보다 나을 것이 없을 것 같다는 생각이 든다.



# 제약사항

* @Controller 이외의 스프링 기능을 사용하지 않는다.
* 스프링 빈을 사용하지 않는다.
* 데이터 저장은 DB가 아닌 List로 한다.
* 모든 기능은 테스트로 정상 동작하는지 확인한다.

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
