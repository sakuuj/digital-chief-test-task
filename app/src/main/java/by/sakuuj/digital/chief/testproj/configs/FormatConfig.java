package by.sakuuj.digital.chief.testproj.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.datetime.standard.DateTimeFormatterRegistrar;
import org.springframework.format.support.DefaultFormattingConversionService;
import org.springframework.format.support.FormattingConversionService;

import java.time.format.DateTimeFormatter;

@Configuration(proxyBeanMethods = false)
public class FormatConfig {

    @Bean(name = "conversionService")
    public FormattingConversionService conversionService() {

        var conversionService = new DefaultFormattingConversionService(false);

        var dateTimeRegistrar = new DateTimeFormatterRegistrar();
        dateTimeRegistrar.setDateTimeFormatter(DateTimeFormatter.ofPattern("uuuu-MM-dd'T'HH:mm:ss"));
        dateTimeRegistrar.registerFormatters(conversionService);

        return conversionService;
    }
}
