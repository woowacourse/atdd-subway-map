package wooteco.subway.dto;

public class StationCreateResponse {

    private Long id;
    private String name;

    public StationCreateResponse() {
    }

    public StationCreateResponse(Long id, String name) {
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
