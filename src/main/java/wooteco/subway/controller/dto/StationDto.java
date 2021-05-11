package wooteco.subway.controller.dto;

public class StationDto {

    private Long id;
    private String name;

    public StationDto() {
    }

    public StationDto(Long id, String name) {
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
