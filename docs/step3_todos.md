# 3-1 피드백

- [ ] post, get API등을 메소드로 Acceptance Test로 분리
- [x] build.gradle: 11 -> 8
- [x] 도메인에 대해 유니테스트 진행
- [x] 깃 리베이스 사용해서 시도해보기
- [ ] AcceptanceTest에서 drop table 쿼리 제거
- [ ] dao 여러개 제거는 IN절을 사용하여 수정
- [ ] 인수테스트에서는 service, persistence layer 호출하지 않고 RestAssured 사용
- [x] Sections: List 인터페이스 타입
- [x] Line: insert 메소드명?
- [x] Section: 조건 로직 메소드 분리
- [x] Sections: List<Station> 인데 Sections라고 하는지?
- [x] Sections: 로직 정리 및 메소드이름 정리, 매직넘버, 스트림은 결과를 변수로 한번 추출하고 진행
- [x] Optional 금지!
- [ ] SubwayException은 왜 다 badRequest
- [ ] LineService: 파라미터 갯수 줄이기, Info 금지
- [ ] Dao: 쿼리 static? 왜?
- [ ] SectionDao: create, insert 둘다 insert 구문 실행 무슨차이?