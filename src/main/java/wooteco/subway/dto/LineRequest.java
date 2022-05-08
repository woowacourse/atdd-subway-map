package wooteco.subway.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class LineRequest {

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    public static class Post{
        private String name;
        private String color;
        private Long upStationId;
        private Long downStationId;
        private int distance;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    public static class Put{
        private String name;
        private String color;
    }
}