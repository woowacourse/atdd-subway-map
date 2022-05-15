package wooteco.subway.service.dto;

public class StationServiceResponse {

    private Long id;
    private String name;

    public StationServiceResponse() {
    }

    public StationServiceResponse(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
