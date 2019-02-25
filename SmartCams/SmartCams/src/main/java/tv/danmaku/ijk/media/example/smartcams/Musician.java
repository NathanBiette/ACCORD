package tv.danmaku.ijk.media.example.smartcams;

import java.io.Serializable;

public class Musician implements Serializable {

    private float xCoord;
    private float yCoord;
    private int id;
    private int instrumentID;

    public Musician(int id, int instrumentID) {
        this.id = id;
        this.instrumentID = instrumentID;
    }

    public float getxCoord() {
        return xCoord;
    }

    public void setxCoord(float xCoord) {
        this.xCoord = xCoord;
    }

    public float getyCoord() {
        return yCoord;
    }

    public void setyCoord(float yCoord) {
        this.yCoord = yCoord;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getInstrumentID() { return instrumentID; }

    public void setInstrumentID(int instrumentID) { this.instrumentID = instrumentID; }

}
