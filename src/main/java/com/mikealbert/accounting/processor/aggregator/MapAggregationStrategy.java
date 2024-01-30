package com.mikealbert.accounting.processor.aggregator;

import java.util.ArrayList;

import org.apache.camel.Exchange;
import org.apache.camel.processor.aggregate.AggregationStrategy;

public class MapAggregationStrategy implements AggregationStrategy {

	@SuppressWarnings("unchecked")
	@Override
	public Exchange aggregate(Exchange oldEx, Exchange newEx) {
		Object newBody = newEx.getIn().getBody();
		ArrayList<Object> list = null;
		if(oldEx == null){
			list = new ArrayList<Object>();
			list.add(newBody);
			newEx.getIn().setBody(list);
			return newEx;
		} else {
			list = oldEx.getIn().getBody(ArrayList.class);
			list.add(newBody);
			return oldEx;
		}
	}

}
