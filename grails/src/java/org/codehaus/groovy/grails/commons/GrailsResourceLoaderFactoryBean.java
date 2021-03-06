/* Copyright 2006-2007 Graeme Rocher
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.codehaus.groovy.grails.commons;

import grails.util.Environment;
import org.codehaus.groovy.grails.compiler.support.GrailsResourceLoader;
import org.codehaus.groovy.grails.compiler.support.GrailsResourceLoaderHolder;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.util.Assert;

import java.io.IOException;

/**
 * A factory bean that constructs the Grails ResourceLoader used to load Grails classes
 *
 * @author Graeme Rocher
 * @since 0.4
 *        <p/>
 *        Created: Jul 27, 2007
 *        Time: 12:34:33 PM
 */
public class GrailsResourceLoaderFactoryBean implements FactoryBean, InitializingBean {

    private org.codehaus.groovy.grails.commons.spring.GrailsResourceHolder grailsResourceHolder;
    private GrailsResourceLoader resourceLoader;

    public void setGrailsResourceHolder(org.codehaus.groovy.grails.commons.spring.GrailsResourceHolder grailsResourceHolder) {
        this.grailsResourceHolder = grailsResourceHolder;
    }

    public Object getObject() throws Exception {
        return this.resourceLoader;
    }

    public Class getObjectType() {
        return GrailsResourceLoader.class;
    }

    public boolean isSingleton() {
        return true;
    }

    public void afterPropertiesSet() throws Exception {
        Assert.notNull(grailsResourceHolder, "Property [grailsResourceHolder] must be set!");

        this.resourceLoader = GrailsResourceLoaderHolder.getResourceLoader();
        if(resourceLoader == null) {
            if(Environment.getCurrent().isReloadEnabled()) {
                ResourcePatternResolver resolver = new  PathMatchingResourcePatternResolver();
                try {
                    Resource[] resources = resolver.getResources("file:"+Environment.getCurrent().getReloadLocation() + "/grails-app/**/*.groovy");
                    this.resourceLoader = new GrailsResourceLoader(resources);
                }
                catch (IOException e) {
                    createDefaultInternal();
                }
            }
            else {
                createDefaultInternal();
            }
            GrailsResourceLoaderHolder.setResourceLoader(this.resourceLoader);
        }
    }

    private void createDefaultInternal() {
        this.resourceLoader = new GrailsResourceLoader(grailsResourceHolder.getResources());
    }
}
