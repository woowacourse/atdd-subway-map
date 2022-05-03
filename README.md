### 1. 지하철 역 관리 API 기능 완성하기
- 지하철역 생성 시 이미 등록된 이름으로 요청한다면 에러를 응답
    - 400 Bad Request
    - StationService에서 CustomException을 만들면 될 것
    - Dao에서 존재하는지 확인하는 기능

- Dao는 static 메소드로 사용
- Service를 추가


- StationDao 기능 추가 및 테스트
    - save()
    - findAll()
    - delete() - 구현 필요
    - existByName() - 구현 필요
- StationService 기능 추가 및 테스트
    - save()
        - 중복 시 예외 발생 기능 추가
        - station 저장 기능 추가
    - findAll()
    - delete()
- StationController 수정 사항
    - Service 로직 적용
    - Dto 정적 팩토리 메소드 수정
