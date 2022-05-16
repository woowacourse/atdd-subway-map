package wooteco.subway.domain;

public enum DeleteMatchingResult {

    POSSIBLE_TO_DELETE,
    NO_MATCHED
    ;

    public static DeleteMatchingResult matchStation(final Section section, final Station target) {
        if (section.isSameWithDownStation(target)) {
            return POSSIBLE_TO_DELETE;
        }
        return NO_MATCHED;
    }

    public static DeleteMatchingResult matchWithLastUpStation(final Section section, final Station target) {
        if (section.isSameWithUpStation(target)) {
            return POSSIBLE_TO_DELETE;
        }
        return NO_MATCHED;
    }

    public static DeleteMatchingResult matchWithLastDownStation(final Section section, final Station taret) {
        if (section.isSameWithDownStation(taret)) {
            return POSSIBLE_TO_DELETE;
        }
        return NO_MATCHED;
    }
}
