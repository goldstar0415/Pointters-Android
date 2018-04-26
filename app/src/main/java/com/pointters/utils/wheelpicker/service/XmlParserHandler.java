package com.pointters.utils.wheelpicker.service;

import com.pointters.utils.wheelpicker.model.DeliveryTimeModel;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.List;

public class XmlParserHandler extends DefaultHandler {

	/**
	 * 存储所有的解析对象
	 */
	private List<DeliveryTimeModel> provinceList = new ArrayList<DeliveryTimeModel>();

	public XmlParserHandler() {

	}

	public List<DeliveryTimeModel> getDataList() {
		return provinceList;
	}

	@Override
	public void startDocument() throws SAXException {
		// 当读到第一个开始标签的时候，会触发这个方法
	}

	DeliveryTimeModel provinceModel = new DeliveryTimeModel();
	String timeModel = "";

	@Override
	public void startElement(String uri, String localName, String qName,
                             Attributes attributes) throws SAXException {
		// 当遇到开始标记的时候，调用这个方法
		if (qName.equals("units")) {
			provinceModel = new DeliveryTimeModel();
			provinceModel.setName(attributes.getValue(0));
			provinceModel.setTimeList(new ArrayList<String>());
		}else if (qName.equals("time")) {
			timeModel = "";
			timeModel = attributes.getValue(0);
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		// 遇到结束标记的时候，会调用这个方法
		if (qName.equals("time")) {
			provinceModel.getTimeList().add(timeModel);
		} else if (qName.equals("units")) {
			provinceList.add(provinceModel);
		}
	}

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
	}

}
