package com.redhat.fuse.boosters.cb;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.dataformat.bindy.csv.BindyCsvDataFormat;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.spi.DataFormat;
import org.springframework.stereotype.Component;

@Component
public class CamelRouter extends RouteBuilder {
	
	DataFormat bindy = new BindyCsvDataFormat(Person.class);

    @Override
    public void configure() throws Exception {

        String kieServerUrl = "localhost:8080/kie-server/services/rest/server";
        String containerId = "order-management";
        String processId = "Order-Management.order-management";

        

        from("file:/tmp?fileName=file.csv").streamCaching()
            .unmarshal(bindy)
            .log("${body}")
            .process(new Processor(){
                    @Override
                    public void process(Exchange exchange) throws Exception {
                        Person person = exchange.getIn().getBody(Person.class);
                        PersonWrapper pw = new PersonWrapper(person);
                        
                        exchange.getIn().setBody(pw);
                    }
                
            })
            .marshal()
            .json(JsonLibrary.Jackson, true)
            .log("${body}")
            .setHeader(Exchange.HTTP_QUERY, constant("authMethod=Basic"))
            .setHeader(Exchange.HTTP_QUERY, constant("authUsername=kieserver"))
            .setHeader(Exchange.HTTP_QUERY, constant("authPassword=kiepass"))
            .setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
            .setHeader(Exchange.HTTP_METHOD, constant("POST"));
            // .to("http4://"+kieServerUrl +"/containers/"+ containerId + "/processes/ "+ processId +"/instances");
        
    }

}