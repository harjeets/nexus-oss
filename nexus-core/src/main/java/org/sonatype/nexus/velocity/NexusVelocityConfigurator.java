/*
 * Sonatype Nexus (TM) Open Source Version
 * Copyright (c) 2007-2012 Sonatype, Inc.
 * All rights reserved. Includes the third-party code listed at http://links.sonatype.com/products/nexus/oss/attributions.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse Public License Version 1.0,
 * which accompanies this distribution and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Sonatype Nexus (TM) Professional Version is available from Sonatype, Inc. "Sonatype" and "Sonatype Nexus" are trademarks
 * of Sonatype, Inc. Apache Maven is a trademark of the Apache Software Foundation. M2eclipse is a trademark of the
 * Eclipse Foundation. All other trademarks are the property of their respective owners.
 */
package org.sonatype.nexus.velocity;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.sonatype.sisu.velocity.internal.VelocityConfigurator;

/**
 * Nexus specific {@link VelocityConfigurator} implementation, that configures {@link VelocityEngine} for Nexus, and
 * supports "development" or "production" mode, depending on configuration (system property
 * {@code nexus.velocity.production} that defaults to {@code true}). By default, it configures the "production" mode,
 * which is basically turning template caching on without modification check (as Nexus uses JARred templates that are
 * not changing at runtime).
 * 
 * @author cstamas
 * @since 2.5
 */
@Singleton
@Named
public class NexusVelocityConfigurator
    implements VelocityConfigurator
{
    private final boolean production;

    @Inject
    public NexusVelocityConfigurator( @Named( "${nexus.velocity.production:-true}" ) final boolean production )
    {
        super();
        this.production = production;
    }

    @Override
    public void configure( final VelocityEngine engine )
    {
        engine.setProperty( RuntimeConstants.RESOURCE_LOADER, "class" );
        engine.setProperty( "class.resource.loader.class",
            "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader" );

        if ( production )
        {
            configureForProduction( engine );
        }
        else
        {
            configureForDevelopment( engine );
        }
    }

    // ==

    protected void configureForProduction( final VelocityEngine engine )
    {
        // caching ON
        engine.setProperty( "class.resource.loader.cache", "true" );
        // never check for template modification (they are JARred)
        engine.setProperty( "class.resource.loader.modificationCheckInterval", "0" );
        // strict mode OFF
        engine.setProperty( "runtime.references.strict", "false" );
    }

    protected void configureForDevelopment( final VelocityEngine engine )
    {
        // caching OFF
        engine.setProperty( "class.resource.loader.cache", "false" );
        // strict mode ON for early error detection
        engine.setProperty( "runtime.references.strict", "true" );
    }
}
