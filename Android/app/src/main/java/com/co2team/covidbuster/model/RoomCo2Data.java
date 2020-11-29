package com.co2team.covidbuster.model;

import java.time.LocalDateTime;

public class RoomCo2Data {

    private final int co2ppm;
    private final LocalDateTime created;

    public RoomCo2Data(int co2ppm, LocalDateTime created) {
        this.co2ppm = co2ppm;
        this.created = created;
    }

    public int getCo2ppm() {
        return co2ppm;
    }

    public LocalDateTime getCreated() {
        return created;
    }
}
