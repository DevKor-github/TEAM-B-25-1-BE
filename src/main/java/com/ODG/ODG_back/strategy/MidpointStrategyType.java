package com.ODG.ODG_back.strategy;

public enum MidpointStrategyType {
    TIME_MATRIX("timeMatrixStrategy"),
    SUBWAY("subwayStrategy");

    private final String beanName;

    MidpointStrategyType(String beanName) {
        this.beanName = beanName;
    }

    public String getBeanName() {
        return beanName;
    }
}
