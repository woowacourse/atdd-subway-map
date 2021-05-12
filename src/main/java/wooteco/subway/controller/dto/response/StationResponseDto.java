package wooteco.subway.controller.dto.response;

public class StationResponseDto {

    private Long id;
    private String name;

    public StationResponseDto() {
    }

    public StationResponseDto(Long id, String name) {
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
