package wooteco.subway.admin.dto;

public class LineStationDto {
    private String name;
    private String preStationName;
    private String arrivalStationName;

    public LineStationDto() {
    }

//    public LineStationDto(String name, String preStationName, String arrivalStationName) {
//        this.name = name;
//        this.preStationName = preStationName;
//        this.arrivalStationName = arrivalStationName;
//    }

    public String getName() {
        return name;
    }

    public String getPreStationName() {
        return preStationName;
    }

    public String getArrivalStationName() {
        return arrivalStationName;
    }
}
