package wooteco.subway.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LineResponse {
    private Long id;
    private String name;
    private String color;
    private List<StationResponse> stations;
}
