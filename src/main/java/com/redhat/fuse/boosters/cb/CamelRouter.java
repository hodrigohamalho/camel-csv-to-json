package com.redhat.fuse.boosters.cb;

import org.apache.camel.Exchange;
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
            .marshal()
            .json(JsonLibrary.Jackson).log("${body}")
            .setHeader("authMethod", constant("Basic"))
            .setHeader("authUsername", constant("kieserver"))
            .setHeader("authPassword", constant("kiepass"))
            .setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
            .setHeader(Exchange.HTTP_METHOD, constant("POST"))
            .to("http4://"+kieServerUrl +"/containers/"+ containerId + "/processes/ "+ processId +"/instances");
        
    }

}