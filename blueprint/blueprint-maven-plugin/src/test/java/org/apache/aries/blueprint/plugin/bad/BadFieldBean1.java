package org.apache.aries.blueprint.plugin.bad;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.aries.blueprint.plugin.test.MyBean1;

@Singleton
public class BadFieldBean1 extends ParentWithField
{
    @Inject
    private MyBean1 field;
}
