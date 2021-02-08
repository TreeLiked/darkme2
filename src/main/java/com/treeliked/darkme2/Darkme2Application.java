package com.treeliked.darkme2;

import org.apache.catalina.Context;
import org.apache.catalina.connector.Connector;
import org.apache.tomcat.util.descriptor.web.SecurityCollection;
import org.apache.tomcat.util.descriptor.web.SecurityConstraint;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 应用入口
 *
 * @author lqs2
 */
@SpringBootApplication
@EnableScheduling
public class Darkme2Application {

    private int httpPort = 80;

    @Value("${server.port}")
    private int httpsPort;

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(Darkme2Application.class);
        app.setBannerMode(Banner.Mode.OFF);
        app.run(args);
    }

    /**
     * it's for set http url auto change to https
     */
     @Bean
     public ServletWebServerFactory servletContainer() {
         TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory() {
             @Override
             protected void postProcessContext(Context context) {
                 SecurityConstraint securityConstraint = new SecurityConstraint();
                 securityConstraint.setUserConstraint("CONFIDENTIAL");
                 SecurityCollection collection = new SecurityCollection();
                 collection.addPattern("/*");
                 securityConstraint.addCollection(collection);
                 context.addConstraint(securityConstraint);
             }
         };
         tomcat.addAdditionalTomcatConnectors(redirectConnector());
         return tomcat;
     }

     private Connector redirectConnector() {
         Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
         connector.setScheme("http");
         connector.setPort(httpPort);
         connector.setSecure(false);
         connector.setRedirectPort(httpsPort);
         return connector;
     }


    //@Bean
    //public ServletWebServerFactory servletContainer() {
    //    TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory();
    //    tomcat.addAdditionalTomcatConnectors(createHTTPConnector());
    //    return tomcat;
    //}
    //
    //private Connector createHTTPConnector() {
    //    Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
    //    connector.setScheme("http");
    //    connector.setSecure(false);
    //    // http 端口
    //    connector.setPort(30001);
    //    //https 端口
    //    connector.setRedirectPort(8080);
    //    return connector;
    //}
}

