package org.josegrangetto.model;

public class Telemetry {
    public float
            aX,
            aY,
            aZ,
            roll,   // TODO debe ser "pitch"
            pitch,  // TODO debe ser "roll"
            temp;


    public float reformat(float num) {
        return Math.round(num * 100) / 100f;
    }

    public void reformatAll() {
        aX = reformat(aX);
        aY = reformat(aY);
        aZ = reformat(aZ);
        roll = reformat(roll);
        pitch = reformat(pitch);
        temp = reformat(temp);
    }

    @Override
    public String toString() {
        return "Telemetry{" +
                "aX=" + aX +
                ", aY=" + aY +
                ", aZ=" + aZ +
                ", roll=" + roll +
                ", pitch=" + pitch +
                ", temp=" + temp +
                '}';
    }
}
