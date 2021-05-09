# 1-2  피드백
- [ ] Optional 리턴을 코드에서 제외 (오히려 boolean 리턴을 받고 유효한 값을 검사)
- [x] InMemory~Dao로 이름 변경
- [ ] 쿼리 결과가 list가 아닌 것으로 변경 (findLineByName)
- [x] jdbcTemplate이 가지고 있는 datasource로 변경
- [x] exceptionController ResponseEntity 리턴타입 명시
- [x] transactional 어노테이션 사용
- [x] 각 생성자 간단하게 정리 (정적팩토리메소드 사용)
- [x] 메소드 이름 정리 (lineDao에서라면 굳이 line이 메소드 이름 자체에 없어도 됨)
- [x] Station 도 Service 레이어 생성 (컨트롤러는 http 처리만 하는 것이 좋음)
- [ ] Request, Response 객체로 넣거나 반환하는 방식 추가
- [x] Dao - DataSource 필요할까?
- [ ] 객체지향적인 코드로 변경해보기 (읽기 좋은 코드로 작성하는 것 외에도 객체를 통해서 업데이트 하라는 의미)
- [ ] update 로직 고민
```text
1. 중복된 name의 Line이 있는지 검사
2. id로 Line 조회
    없으면 orElseThrow로 LineNotFoundException 발생
3. 조회한 Line 객체의 changeInfo 메서드로 객체 업데이트
4. lineDao로 update 쿼리 실행
```
