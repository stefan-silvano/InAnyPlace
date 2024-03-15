package com.example.inanyplace.Event;

public class HideFABCartEvent {
    private boolean hidden;

    public HideFABCartEvent(boolean hidden) {
        this.hidden = hidden;
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }
}
