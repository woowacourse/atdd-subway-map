package wooteco.subway.service.dto.station;

public class StationSaveResponse {

    private Long id;
    private String name;

    public StationSaveResponse(Long id, String name) {
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
