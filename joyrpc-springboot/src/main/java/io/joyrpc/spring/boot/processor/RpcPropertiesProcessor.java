package io.joyrpc.spring.boot.processor;

/*-
 * #%L
 * joyrpc
 * %%
 * Copyright (C) 2019 joyrpc.io
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import io.joyrpc.config.AbstractIdConfig;
import io.joyrpc.spring.boot.properties.RpcProperties;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static io.joyrpc.spring.boot.processor.AnnotationBeanDefinitionProcessor.REGISTRY_NAME;
import static io.joyrpc.spring.boot.processor.AnnotationBeanDefinitionProcessor.SERVER_NAME;

/**
 * 根据配置生成Server和Registry配置对象
 */
public class RpcPropertiesProcessor {

    public void processProperties(final BeanDefinitionRegistry registry, final RpcProperties properties) {
        //注册
        register(registry, properties.getRegistry(), REGISTRY_NAME);
        register(registry, properties.getServer(), SERVER_NAME);
        register(registry, properties.getServers(), "server-boot");
        register(registry, properties.getRegistries(), "registry-boot");
    }

    /**
     * 注册
     *
     * @param registry      注册表
     * @param configs       多个配置
     * @param defNamePrefix 默认名称
     */
    protected <T extends AbstractIdConfig> void register(final BeanDefinitionRegistry registry,
                                                         final List<T> configs, final String defNamePrefix) {
        if (configs != null) {
            AtomicInteger counter = new AtomicInteger(0);
            for (T config : configs) {
                register(registry, config, defNamePrefix + "-" + counter.getAndIncrement());
            }
        }
    }

    /**
     * 注册
     *
     * @param registry
     * @param config
     * @param defName
     * @param <T>
     */
    protected <T extends AbstractIdConfig> void register(final BeanDefinitionRegistry registry, final T config,
                                                         final String defName) {
        if (config == null) {
            return;
        }
        String beanName = config.getId();
        if (!StringUtils.hasText(beanName)) {
            beanName = defName;
        }
        if (!registry.containsBeanDefinition(beanName)) {
            //TODO 要验证是否正确注入了环境变量
            registry.registerBeanDefinition(beanName, new RootBeanDefinition((Class<T>) config.getClass(), () -> config));
        }
    }

}