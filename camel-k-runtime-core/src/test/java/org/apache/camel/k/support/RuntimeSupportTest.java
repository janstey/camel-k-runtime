/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.k.support;

import java.util.List;
import java.util.Properties;

import org.apache.camel.CamelContext;
import org.apache.camel.component.properties.PropertiesComponent;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.k.Constants;
import org.apache.camel.k.ContextCustomizer;
import org.apache.camel.k.InMemoryRegistry;
import org.apache.camel.k.Runtime;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class RuntimeSupportTest {

    @Test
    public void testLoadCustomizers() {
        PropertiesComponent pc = new PropertiesComponent();
        Runtime.Registry registry = new InMemoryRegistry();
        CamelContext context = new DefaultCamelContext(registry);
        context.addComponent("properties", pc);

        NameCustomizer customizer = new NameCustomizer("from-registry");
        registry.bind("name", customizer);

        List<ContextCustomizer> customizers = RuntimeSupport.configureContext(context, registry);
        assertThat(context.getName()).isNotEqualTo("from-registry");
        assertThat(context.getName()).isNotEqualTo("default");
        assertThat(customizers).hasSize(0);

        Properties properties = new Properties();
        properties.setProperty(Constants.PROPERTY_CAMEL_K_CUSTOMIZER, "name");
        pc.setInitialProperties(properties);

        customizers = RuntimeSupport.configureContext(context, registry);
        assertThat(context.getName()).isEqualTo("from-registry");
        assertThat(customizers).hasSize(1);
    }

    @Test
    public void testLoadCustomizersOrdering() {
        PropertiesComponent pc = new PropertiesComponent();
        Runtime.Registry registry = new InMemoryRegistry();
        CamelContext context = new DefaultCamelContext(registry);
        context.addComponent("properties", pc);

        Properties properties = new Properties();
        properties.setProperty(Constants.PROPERTY_CAMEL_K_CUSTOMIZER, "name");
        pc.setInitialProperties(properties);

        List<ContextCustomizer> customizers = RuntimeSupport.configureContext(context, registry);
        assertThat(context.getName()).isEqualTo("default");
        assertThat(customizers).hasSize(1);

        NameCustomizer customizer = new NameCustomizer("from-registry");
        registry.bind("name", customizer);

        customizers = RuntimeSupport.configureContext(context, registry);
        assertThat(context.getName()).isEqualTo("from-registry");
        assertThat(customizers).hasSize(1);
    }

}
