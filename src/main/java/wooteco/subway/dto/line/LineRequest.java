package wooteco.subway.dto.line;

public class LineRequest {
    private String name;
    private String color;

    public LineRequest() {
    }

    public LineRequest(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public LineRequest(LineCreateRequest lineCreateRequest){
        this(lineCreateRequest.getName(), lineCreateRequest.getColor());
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }
}
