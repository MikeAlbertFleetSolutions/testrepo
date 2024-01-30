package com.mikealbert.accounting.processor.config;

import javax.jms.ConnectionFactory;

import org.apache.camel.component.jms.JmsComponent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Wiring the JMS connection factory with the JMS component. 
 * I expected the starter component to auto configure the 
 * component with the connection factory. Obviously a gap 
 * between spring boot and camel jms component.
 * 
 * @author wayne.sibley
 *
 */
@Configuration
public class JmsConfig {	
	
	@Bean
	public JmsComponent createJmsComponent(final ConnectionFactory connectionFactory) {
		JmsComponent jmsComponent = JmsComponent.jmsComponent(connectionFactory);
		return jmsComponent;
	}
	
}
