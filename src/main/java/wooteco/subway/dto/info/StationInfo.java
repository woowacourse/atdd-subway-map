package wooteco.subway.dto.info;

public class StationInfo {
    private Long id;
    private String name;

    public StationInfo(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public StationInfo(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
