package com.example.currencyexchange.model;

public class ConversionResult {
	
	private double maxValue;
	private double minValue;
	
	public ConversionResult(double maxValue, double minValue) {
        this.maxValue = maxValue;
        this.minValue = minValue;
    }
	
    public double getMaxValue() {
		return maxValue;
	}

	public double getMinValue() {
		return minValue;
	}

}
