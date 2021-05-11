package wooteco.subway.line.state;

import wooteco.subway.line.domain.Sections;

public class StateFactory {
    private StateFactory() {
    }

    public static State initialize(Sections sections) {
        return new UnModified(sections);
    }
}
