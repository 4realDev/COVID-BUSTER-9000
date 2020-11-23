package com.co2team.covidbuster.model;

import java.util.Date;

public class RoomCo2Data {

    private int co2ppm;
    private Date created;

    public RoomCo2Data(int co2ppm, Date created) {
        this.co2ppm = co2ppm;
        this.created = created;
    }
}
