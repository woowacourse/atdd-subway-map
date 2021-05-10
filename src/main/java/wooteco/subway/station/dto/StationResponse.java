package wooteco.subway.station.dto;

import wooteco.subway.station.domain.Station;

public class StationResponse {
    private Long id;
    private String name;

    public StationResponse() {
    }

<<<<<<< HEAD:src/main/java/wooteco/subway/station/dto/StationResponse.java
    public StationResponse(Station station) {
        this.id = station.getId();
        this.name = station.getName();
=======
    public StationResponse(Long id, String name) {//TODO 삭제
        this.id = id;
        this.name = name;
>>>>>>> 31560b2 (feat: 노선 조회 추가 기능 구현):src/main/java/wooteco/subway/station/StationResponse.java
    }

    public StationResponse(Station station) {
        this.id = station.getId();
        this.name = station.getName();
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
