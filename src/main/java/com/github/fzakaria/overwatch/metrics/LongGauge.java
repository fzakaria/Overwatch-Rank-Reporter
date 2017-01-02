package com.github.fzakaria.overwatch.metrics;

import com.codahale.metrics.Gauge;

public class LongGauge implements Gauge<Long> {

    private long value;

    public LongGauge(long value) {
        this.value = value;
    }

    public void setValue(long value) {
        this.value = value;
    }

    @Override
    public Long getValue() {
        return value;
    }
}
