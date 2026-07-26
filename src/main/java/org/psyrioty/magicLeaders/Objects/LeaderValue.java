package org.psyrioty.magicLeaders.Objects;

public class LeaderValue {
    private double startValue = 0;
    private double value = 0;

    public LeaderValue(
            double startValue,
            double value
    ){
        this.startValue = startValue;
        this.value = value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public double getResult(){
        return value - startValue;
    }
}
