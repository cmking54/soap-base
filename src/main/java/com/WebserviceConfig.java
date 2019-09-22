package com;

import javax.sql.DataSource;

import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.ws.config.annotation.EnableWs;
import org.springframework.ws.config.annotation.WsConfigurerAdapter;
import org.springframework.ws.transport.http.MessageDispatcherServlet;
import org.springframework.ws.wsdl.wsdl11.DefaultWsdl11Definition;
import org.springframework.xml.xsd.SimpleXsdSchema;
import org.springframework.xml.xsd.XsdSchema;

@EnableWs
@Configuration
@SuppressWarnings({"rawtypes", "unchecked"})
public class WebserviceConfig extends WsConfigurerAdapter {
	
	@Bean // Spring takes the responsibility of managing the connection using this datasource
	public ServletRegistrationBean messageDispatcherServlet(ApplicationContext applicationContext) {
		MessageDispatcherServlet servlet = new MessageDispatcherServlet();
		servlet.setApplicationContext(applicationContext);
		servlet.setTransformWsdlLocations(true);
		return new ServletRegistrationBean(servlet, "/ws/*");
	}

	@Bean(name = "persons") 
	// defines the location of the schema file in the classpath
	public DefaultWsdl11Definition defaultWsdl11Definition(XsdSchema personsSchema) {
		DefaultWsdl11Definition wsdl11Definition = new DefaultWsdl11Definition();
		wsdl11Definition.setPortTypeName("PersonPort");
		wsdl11Definition.setLocationUri("/ws");
		wsdl11Definition.setTargetNamespace("http://www.example.org/person");
		wsdl11Definition.setSchema(personsSchema);
		return wsdl11Definition;
	}

	@Bean
	// responsible for creating and publishing a wsdl based on the contract (person.xsd). The URL 
	// of the wsdl will end with /ws/persons.wsdl 
	public XsdSchema personsSchema() {
		return new SimpleXsdSchema(new ClassPathResource("person.xsd"));
	}

	@Bean
	// defines a dispatcher servlet which listens to all requests which starts with /ws and dispatches 
	// it to the webservice end point
	public DataSource dataSource() {
		DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
		dataSourceBuilder.url("jdbc:mysql://localhost:3306/testdb");
		dataSourceBuilder.username("root");
		dataSourceBuilder.password("root");
		return dataSourceBuilder.build();
	}
}
