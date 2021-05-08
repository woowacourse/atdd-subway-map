package wooteco.subway.name.domain;

public enum NameType {
    STATION(Values.STATION), LINE(Values.LINE);

    private final String type;

    NameType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public static class Values {
        public static final String STATION = "S";
        public static final String LINE = "L";
    }
}
