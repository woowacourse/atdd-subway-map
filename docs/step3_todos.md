# 3-1 피드백

- [x] post, get API등을 메소드로 Acceptance Test로 분리
- [x] build.gradle: 11 -> 8
- [x] 도메인에 대해 유니테스트 진행
- [x] 깃 리베이스 사용해서 시도해보기
- [x] AcceptanceTest에서 drop table 쿼리 제거
- [x] dao 여러개 제거는 IN절을 사용하여 수정
- [x] 인수테스트에서는 service, persistence layer 호출하지 않고 RestAssured 사용
- [x] Sections: List 인터페이스 타입
- [x] Line: insert 메소드명?
- [x] Section: 조건 로직 메소드 분리
- [x] Sections: List<Station> 인데 Sections라고 하는지?
- [x] Sections: 로직 정리 및 메소드이름 정리, 매직넘버, 스트림은 결과를 변수로 한번 추출하고 진행
- [x] Optional 금지!
- [x] SubwayException은 왜 다 badRequest
- [x] LineService: 파라미터 갯수 줄이기, Info 금지
- [x] Dao: 쿼리 static? 왜?
- [x] SectionDao: create, insert 둘다 insert 구문 실행 무슨차이?

# 3-2 피드백

- [x] 디버깅시 사용했던 불필요한 출력문 제거
- [x] Sections: Map 일급 컬렉션?, 메서드명 확인
- [x] Station: 기본 생성자 필요?
- [x] StationName: 단순 문자열로 변경
- [x] 내부로직 예외: 500번대
- [x] Dao: 인터페이스 분리
- [x] Service: 메소드명 ~~ById, dto 생성도 서비스로
- [x] 중복역 체크는 비즈니스로직으로 Service 레이어로 이동

### 적용에 질문 있는 내용
- Sections: Arrays.asList
- Section에서 line 정보를 가지게?