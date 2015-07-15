package com.github.gazonkfoo.gatecontrol;

/**
 * Created by Sebastian Maurer on 13.07.2015.
 */
public enum GateState {

    UNKNOWN(-1, R.string.gate_switch_disabled),
    OPEN(0, R.string.gate_switch_open),
    CLOSED(1, R.string.gate_switch_closed);

    private int value;
    private int resourceID;

    GateState(int value, int resourceID) {
        this.value = value;
        this.resourceID = resourceID;
    }

    public int getValue() {
        return value;
    }

    public int getResourceID() {
        return resourceID;
    }

    public static GateState fromValue(int value) {
        for(GateState state : GateState.values()) {
            if(state.getValue() == value)
                return state;
        }
        return GateState.UNKNOWN;
    }

    public static GateState fromValue(String value) {
        try {
            return fromValue(Integer.parseInt(value));
        } catch(NumberFormatException e) {
            return GateState.UNKNOWN;
        }
    }
}
