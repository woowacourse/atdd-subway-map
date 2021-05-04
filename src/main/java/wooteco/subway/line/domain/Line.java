package wooteco.subway.line.domain;

import wooteco.subway.station.domain.Station;

import java.util.List;

public class Line {

    private Long id;
    private String name;
    private List<Station> stations;
    private int distance;
    private String color;
    private Long extraFare;

    public Line() {
    }

    public Line(Long id, String name, List<Station> stations, int distance, String color, Long extraFare) {
        this.id = id;
        this.name = name;
        this.stations = stations;
        this.distance = distance;
        this.color = color;
        this.extraFare = extraFare;
    }

    public Line(String name, List<Station> stations, int distance, String color, Long extraFare) {
        this.name = name;
        this.stations = stations;
        this.distance = distance;
        this.color = color;
        this.extraFare = extraFare;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<Station> getStations() {
        return stations;
    }

    public int getDistance() {
        return distance;
    }

    public String getColor() {
        return color;
    }

    public Long getExtraFare() {
        return extraFare;
    }
}
