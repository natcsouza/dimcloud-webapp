package br.com.fiap.dimcloud;

import com.microsoft.applicationinsights.attach.ApplicationInsights;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DimCloudApplication {

    public static void main(String[] args) {
        // Liga o agente do Application Insights antes do Spring subir.
        // A connection string vem da variável APPLICATIONINSIGHTS_CONNECTION_STRING.
        ApplicationInsights.attach();
        SpringApplication.run(DimCloudApplication.class, args);
    }
}
