package com.ll.codicaster.base.i18nConfig;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

@Configuration
public class I18nConfig {
	@Autowired
	private CustomMessageSource customMessageSource;

	@Bean
	public MessageSource messageSource() {
		ResourceBundleMessageSource messageSource = customMessageSource;
		messageSource.setBasename("messages");
		messageSource.setDefaultEncoding("UTF-8");
		messageSource.setCacheMillis(0); // 캐시를 끈다. 왜냐하면 내가 직접 캐시를 만들거니까
		return messageSource;
	}

	// 브라우저의 요청에 들어있는 언어정보로 언어를 결정
	@Bean
	public LocaleResolver localeResolver() {
		AcceptHeaderLocaleResolver acceptHeaderLocaleResolver = new AcceptHeaderLocaleResolver();
		acceptHeaderLocaleResolver.setDefaultLocale(Locale.US);

		return acceptHeaderLocaleResolver;
	}
}