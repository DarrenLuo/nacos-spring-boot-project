/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.boot.nacos.config.util;

import com.alibaba.boot.nacos.config.NacosConfigConstants;
import com.alibaba.boot.nacos.config.properties.NacosConfigProperties;
import com.ulisesbocchio.jasyptspringboot.EncryptablePropertyDetector;
import com.ulisesbocchio.jasyptspringboot.EncryptablePropertyFilter;
import com.ulisesbocchio.jasyptspringboot.EncryptablePropertyResolver;
import com.ulisesbocchio.jasyptspringboot.EncryptablePropertySourceConverter;
import com.ulisesbocchio.jasyptspringboot.InterceptionMode;
import com.ulisesbocchio.jasyptspringboot.configuration.EnvCopy;
import com.ulisesbocchio.jasyptspringboot.detector.DefaultLazyPropertyDetector;
import com.ulisesbocchio.jasyptspringboot.encryptor.DefaultLazyEncryptor;
import com.ulisesbocchio.jasyptspringboot.environment.EncryptableEnvironment;
import com.ulisesbocchio.jasyptspringboot.environment.StandardEncryptableEnvironment;
import com.ulisesbocchio.jasyptspringboot.filter.DefaultLazyPropertyFilter;
import com.ulisesbocchio.jasyptspringboot.resolver.DefaultLazyPropertyResolver;
import org.jasypt.encryption.StringEncryptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.core.ResolvableType;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;

import java.util.Collections;
import java.util.List;

/**
 * Springboot used to own property binding configured binding
 *
 * @author <a href="mailto:liaochunyhm@live.com">liaochuntao</a>
 * @since 0.2.3
 */
public class NacosConfigPropertiesUtils {

	private static final Logger logger = LoggerFactory
			.getLogger(NacosConfigPropertiesUtils.class);

	public static NacosConfigProperties buildNacosConfigProperties(
			ConfigurableEnvironment environment) {
		NacosConfigProperties nacosConfigProperties = new NacosConfigProperties();
		
		logger.info("Initializing Environment: {}", environment.getClass().getSimpleName());
		InterceptionMode actualInterceptionMode = InterceptionMode.WRAPPER;
		List<Class<PropertySource<?>>> actualSkipPropertySourceClasses = Collections.emptyList();
		EnvCopy envCopy = new EnvCopy(environment);
		EncryptablePropertyFilter actualFilter = new DefaultLazyPropertyFilter(envCopy.get());
		StringEncryptor actualEncryptor = new DefaultLazyEncryptor(envCopy.get());
		EncryptablePropertyDetector actualDetector = new DefaultLazyPropertyDetector(envCopy.get());
		EncryptablePropertyResolver actualResolver = new DefaultLazyPropertyResolver(actualDetector, actualEncryptor, environment);
		EncryptablePropertySourceConverter converter = new EncryptablePropertySourceConverter(actualInterceptionMode, actualSkipPropertySourceClasses, actualResolver, actualFilter);
		converter.convertPropertySources(environment.getPropertySources());
		MutablePropertySources encryptableSources = converter.convertMutablePropertySources(InterceptionMode.WRAPPER, environment.getPropertySources(), envCopy);
		EncryptableEnvironment standardEncryptableEnvironment = new StandardEncryptableEnvironment();
		standardEncryptableEnvironment.setEncryptablePropertySources(encryptableSources);
		
		Binder binder = Binder.get(standardEncryptableEnvironment);
		ResolvableType type = ResolvableType.forClass(NacosConfigProperties.class);
		Bindable<?> target = Bindable.of(type).withExistingValue(nacosConfigProperties);
		binder.bind(NacosConfigConstants.PREFIX, target);
		logger.info("nacosConfigProperties : {}", nacosConfigProperties);
		return nacosConfigProperties;
	}

}
