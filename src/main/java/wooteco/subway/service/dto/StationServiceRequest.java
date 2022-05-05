package wooteco.subway.service.dto;

public class StationServiceRequest {
    private Long id;
    private String name;

    private StationServiceRequest() {
    }

    public StationServiceRequest(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public StationServiceRequest(String name) {
        this(null, name);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
