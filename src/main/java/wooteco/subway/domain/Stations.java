package wooteco.subway.domain;

import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class Stations {
    private List<Station> stations;

    public Station save(Station station) {
        checkDuplication(station);
        stations.add(station);

        return station;
    }

    private void checkDuplication(Station newStation) {
        boolean existName = stations.stream()
                .anyMatch(station -> station.hasSameName(newStation));

        if(existName){
            throw new IllegalArgumentException("이미 존재하는 역 이름입니다.");
        }
    }
}
