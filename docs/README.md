## 기능 요구 사항

### 1. 지하철 역 관리 API 기능 완성하기

- `StationController`를 통해 요청을 처리하는 부분은 미리 구현되어 있음
- `StationDao`를 활용하여 지하철 역 정보를 관리
- 추가 기능: 지하철역 생성 시 이미 등록된 이름으로 요청한다면 에러를 응답

### 2. 지하철 노선 관리 API 구현하기

- 지하철역과 마찬가지로 같은 이름의 노선은 생성 불가
- 노선 관리 API에 대한 스펙은 [API 문서v1](https://techcourse-storage.s3.ap-northeast-2.amazonaws.com/d5c93e187919493da3280be44de0f17f#Line)를 참고

### 3. End to End 테스트 작성하기

- 노선 기능에 대한 E2E 테스트를 작성
- `StationAcceptanceTest` 클래스를 참고



## 프로그래밍 제약 사항

### `@Service`, `@Component` 등 스프링 빈 사용 금지

- 스프링 컨테이너 사용 전/후의 차이를 명확히 경험하기 위해 스프링 빈 사용을 금지
  (API 요청을 받기위한 필수 컴포넌트를 제외 ex. `@Controller`)
- 스프링 빈을 사용하지 않고 객체를 직접 생성하고 의존 관계를 맺어주기

### 데이터 저장은 XXXDao을 활용

- 1단계에서는 DB를 사용하지 않고 Dao객체 내부의 List에서 데이터를 관리함
- 기능 구현간 필요한 로직은 추가가 가능하고 기존 코드도 변경이 가능함

```java
public class StationDao {
    private static Long seq = 0L;
    private static List<Station> stations = new ArrayList<>();

    public static Station save(Station station) {
        Station persistStation = createNewObject(station);
        stations.add(persistStation);
        return persistStation;
    }

    public static List<Station> findAll() {
        return stations;
    }

    public static void deleteById(Long id) {
        stations.removeIf(it -> it.getId().equals(id));
    }

    public static Optional<Station> findById(Long id) {
        return stations.stream()
                .filter(it -> it.getId() == id)
                .findFirst();
    }

    private static Station createNewObject(Station station) {
        Field field = ReflectionUtils.findField(Station.class, "id");
        field.setAccessible(true);
        ReflectionUtils.setField(field, station, ++seq);
        return station;
    }
}
```

# 힌트

## 테스트를 통해 기능 동작 여부 확인

- 애플리케이션을 실행 시키고 브라우저를 띄워서 확인하지 않아도 테스트 코드를 통해 기능이 정상 동작하는지 여부를 확인할 수 있음
- 테스트를 통해 동작 여부 확인 시 애플리케이션 + 브라우저 로드 시간을 줄일 수 있음
- `StationAcceptanceTest` 테스트 참고 하기

## StationController에서 StationDao 접근하기

- StationController에서 요청이 올 때 마다 StationDao에 접근하기 위해 new StationDao()로 StationDao 객체를 생성해 줄 수 없음.
- 매번 생성될 때 마다 Dao 내부에서 관리하는 List는 달라짐
- StationController에서 매번 다른 요청이 오더라도 같은 List에서 데이터를 관리할 수 있게 해야 함

## 관련 학습 테스트

- [Spring MVC 학습 테스트](https://github.com/next-step/spring-learning-test/tree/mvc)

## Spring MVC 애너테이션 및 사용법

### Controller Annotation

- `@Controller`를 클래스에 붙이면 요청에 따른 처리를 할 수 있음
- `@RestController`는 `@Controller`에 `@ResponseBody`가 포함되어 있음

### Request Mapping

- **요청**과 **컨트롤러 메서드**를 맵핑하기 위해서 애너테이션을 사용할 수 있음
- `@RequestMapping`와 이를 좀 더 편하게 사용하기위해 만든 `@GetMapping` 등
- [Spring MVC - Request Mapping](https://docs.spring.io/spring/docs/5.2.5.RELEASE/spring-framework-reference/web.html#mvc-ann-requestmapping)

### Method Argument

- 컨트롤러 메서드의 인자를 통해 요청에 포함된 정보를 활용
- `@RequestParam`, `@RequestBody`, `@PathVariable` 등
- [Spring MVC - Method Argument](https://docs.spring.io/spring/docs/5.2.5.RELEASE/spring-framework-reference/web.html#mvc-ann-arguments)

### Return Value

- 컨트롤러 메서드의 리턴값을 통해 응답 형식을 결정
- `@ResponseBody`, `String`, `ResponseEntity` 등
- [Spring Mvc - Return Value](https://docs.spring.io/spring/docs/5.2.5.RELEASE/spring-framework-reference/web.html#mvc-ann-return-types)
