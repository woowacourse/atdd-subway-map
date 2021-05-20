## 조금 더 알아보고 싶은 것
* ReflectionUtils.findField()
* 아파치
* 컬러가 뭘까요? 
* body().jsonPath() vs jsonPath()

* removeIF remove 뭐가다르지?
* 래퍼 타입을 쓰는 이유는 DTO에서 null을 확인하지 않고 서비스단까지 끌고간다
비즈니스 로직인데 이를 DTO에서 체크하는게 맞지 않다.

* DTO가 정보를 알고 있는 상황에서 
서비스에서 DTO.get을 해서 엔티티를 만들어내는 것보다
DTO에서 엔티티를 만드는 것이 낫다.
(정보 전문가 패턴)
* 서비스 클래스단에서보다 메서드마다 Transactional 을 붙인다. 
왜냐? 트랜잭션은 무거운 연산이기 때문에
또 트랜잭션으로 관리되지 않아도 되는 메서드도 트랜잭션이 되기 때문에
- 검

## TODO
* 지하철 구간 추가 API 구현
* Custom Exception 만들기
* 노선에 역이 등록되어 있으면 제거할 수 없다.