package wooteco.subway.section.domain;

import wooteco.subway.line.exception.LineException;
import wooteco.subway.station.domain.Station;

import java.util.List;

public class StationIdsInLine {
    private final List<Long> ids;

    public StationIdsInLine(List<Long> ids) {
        this.ids = ids;
    }

    public boolean isFinalSection(final Section section) {
        return isFirstSection(section) != isLastSection(section);
    }

    public boolean isFirstSection(final Section section){
        return firstStationId().equals(section.backStationId());
    }

    public boolean isLastSection(final Section section){
        return lastStationId().equals(section.frontStationId());
    }

    public Long firstStationId(){
        return ids.get(0);
    }

    public Long lastStationId(){
        return ids.get(ids.size()-1);
    }

    public void addSection(final Section section) {
        for(int i =0; i<ids.size(); i++){
            if(section.isFrontStationId(ids.get(i))){
                ids.add(i+1, section.backStationId());
                return;
            }
        }

        for(int i =0; i<ids.size(); i++){
            if(section.isBackStationId(ids.get(i))){
                ids.add(i, section.frontStationId());
                return;
            }
        }

        throw new LineException("추가하는 구간의 역 중 하나는 노선에 등록되어 있어야 합니다.");
    }

    public boolean isFirstStation(final Long stationId) {
        return firstStationId().equals(stationId);
    }

    public boolean isLastStation(final Long stationId) {
        return lastStationId().equals(stationId);
    }

    public void delete(final Long stationId) {
        if(!ids.contains(stationId)){
            throw new LineException("존재하지 않는 역을 제거할 수 없습니다.");
        }
        ids.remove(stationId);
    }

    public List<Long> ids(){
        return ids;
    }

    public boolean isFinalStation(final Long stationId) {
        return isFirstStation(stationId) || isLastStation(stationId);
    }

    public Long frontOf(final Long stationId){
        return ids.get(ids.indexOf(stationId)-1);
    }

    public Long backOf(final Long stationId){
        return ids.get(ids.indexOf(stationId)+1);
    }

    private boolean isInLine(final Long stationId){
        return ids.contains(stationId);
    }
}
