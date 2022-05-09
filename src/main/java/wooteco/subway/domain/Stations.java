package wooteco.subway.domain;

import java.util.List;

public class Stations {
    private List<Station> stations;

    public Stations(List<Station> stations) {
        this.stations = stations;
    }

    public void checkAbleToAdd(Station station) {
        checkNameIsUnique(station);
    }

    private void checkNameIsUnique(Station station) {
        boolean duplicated = stations.stream()
                .anyMatch(it -> it.isSameName(station));
        if (duplicated) {
            throw new IllegalArgumentException("station 이름은 중복될 수 없습니다.");
        }
    }
}
