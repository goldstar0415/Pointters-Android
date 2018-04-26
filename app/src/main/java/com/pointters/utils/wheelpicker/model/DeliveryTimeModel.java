package com.pointters.utils.wheelpicker.model;

import java.util.List;

public class DeliveryTimeModel {
	private String name;
	private List<String> times;
	
	public DeliveryTimeModel() {
		super();
	}

	public DeliveryTimeModel(String name, List<String> cityList) {
		super();
		this.name = name;
		this.times = cityList;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<String> getTimeList() {
		return times;
	}

	public void setTimeList(List<String> cityList) {
		this.times = cityList;
	}

	@Override
	public String toString() {
		return times + name;
	}
	
}
