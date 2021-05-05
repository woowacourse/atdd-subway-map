## 요구 사항 정의
- [x] 같은 이름 지하철역 생성 불가 기능
- [x] Station Service 생성
- [x] Station Controller에서 Station Service 사용하도록 수정 
- [ ] 지하철 노선 관리 API 구현
    - [x] 노선 생성
    - [x] 노선 목록 조회
    - [x] 노선 조회
    - [x] 노선 수정
    - [x] 노선 삭제
- [x] End to End 테스트 작성하기
- [ ] Dao 객체가 아닌 DB에서 데이터 관리
- [ ] 스프링 빈 활용하기

## Commit Convention
- 커밋 메시지 언어 : 한글.
- feat : 기능 추가.
- refactor : 구조 개선.
- fix : 에러가 나는 부분 해결.
- docs : document 파일 관련.
- test : 테스트 코드만 추가하거나 바꿀 때 사용.
- style : 들여쓰기, 변수명, 메소드명 수정 및 기타 수정 사항.
- 커밋 단위 : 기능 단위로 커밋을 하되, 테스트와 기능 커밋 구분. (단, 단순한 기능은 한 번에 커밋.)