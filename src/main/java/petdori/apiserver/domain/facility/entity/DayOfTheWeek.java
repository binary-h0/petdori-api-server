package petdori.apiserver.domain.facility.entity;

public enum DayOfTheWeek {
    SUN("일"), MON("월"), TUE("화"), WED("수"), THU("목"), FRI("금"), SAT("토");

    private final String value;

    DayOfTheWeek(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
