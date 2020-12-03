package com.redhat.fuse.boosters.cb;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.dataformat.bindy.csv.BindyCsvDataFormat;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.camel.spi.DataFormat;
import org.springframework.stereotype.Component;

@Component
public class CamelRouter extends RouteBuilder {
	
	DataFormat bindy = new BindyCsvDataFormat(Person.class);


    @Override
    public void configure() throws Exception {

        from("file:/tmp?fileName=file.csv")
        	.streamCaching()
            .log("OPA: ${body}")
             .unmarshal(bindy)
             .marshal()
             .json(JsonLibrary.Jackson).log("${body}")
             .setHeader(Exchange.HTTP_METHOD, constant("POST"))
             .to("http4://localhost:8080/camel/hello")
             .to("file:/tmp/?fileName=file-in-json.json");

         restConfiguration()
             .component("servlet")
             .bindingMode(RestBindingMode.json);
        
         rest("/hello").description("Greetings REST service")
             .consumes("application/json")
             .produces("application/json")
             
             .post().outType(String.class)
                 .responseMessage().code(200).endResponseMessage()
                 .to("direct:impl");

         from("direct:impl").description("REST service implementation route")
         	.log("Body -> ${body}");
    }

}