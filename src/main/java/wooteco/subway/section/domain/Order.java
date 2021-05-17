package wooteco.subway.section.domain;

import wooteco.subway.line.domain.FinalStations;
import wooteco.subway.line.exception.LineException;

import java.util.LinkedList;
import java.util.List;

public class Order {

    private final List<Long> orderedIds;

    public Order(List<Long> orderedIds) {
        this.orderedIds = orderedIds;
    }

    public boolean isFirstSection(final Section section) {
        return isFirst(section.backStationId()) || isLast(section.frontStationId());
    }

    public boolean isFirst(final Long stationId){
        return first().equals(stationId);
    }

    private boolean isLast(final Long stationId) {
        return last().equals(stationId);
    }

    public boolean isInclude(final Long stationId) {
        return orderedIds.contains(stationId);
    }

    public Long first() {
        return orderedIds.get(0);
    }

    public Long last(){
        return orderedIds.get(orderedIds.size()-1);
    }

    public void validateAbleToAddSection(final Section section){
        if(isInclude(section.frontStationId()) == isInclude(section.backStationId())){
            throw new LineException("하나의 역이 포함되어있어야 합니다.");
        }
    }

    public FinalStations finalStations(){
        return new FinalStations(first(), last());
    }

    public boolean isFinalSection(Section section) {
        return finalStations().isFinalSection(section);
    }
}
