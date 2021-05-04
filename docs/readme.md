## 요구사항 정의
+ [ ] 도메인 구현
    + [ ] 역 관리
        + [ ] 같은 이름 지하철역 생성 불가 기능 구현
    + [ ] 노선 관리
        + [ ] 노선 생성 기능 구현
            + request : color, name
            + response : id, name, color
        + [ ] 노선 목록 조회 
            + request : x
            + response : List(id, name, color)
        + [ ] 단일 노선 조회
            + request : x
            + response : id, name, color
        + [ ] 노선 수정
            + request : color, name
            + response : x
        + [ ] 노선 삭제
            + request : x
            + response : x
