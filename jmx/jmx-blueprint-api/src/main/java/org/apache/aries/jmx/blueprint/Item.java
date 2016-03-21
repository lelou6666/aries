/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.aries.jmx.blueprint;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.management.openmbean.ArrayType;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.OpenDataException;
import javax.management.openmbean.OpenType;
import javax.management.openmbean.TabularType;

/**
 * The item class enables the definition of open types in the appropriate interfaces.
 *
 * This class contains a number of methods that make it possible to create open types for {@link CompositeType},
 * {@link TabularType}, and {@link ArrayType}. The normal creation throws a checked exception, making it impossible to
 * use them in a static initializer. They constructors are also not very suitable for static construction.
 *
 *
 * An Item instance describes an item in a Composite Type. It groups the triplet of name, description, and Open Type.
 * These Item instances allows the definitions of an item to stay together.
 *
 * @Immutable
 */
public class Item {

    /**
     * The name of this item.
     */
    private final String name;

    /**
     * The description of this item.
     */
    private final String description;

    /**
     * The type of this item.
     */
    private final OpenType/*<?>*/ type;

    /**
     * Create a triple of name, description, and type. This triplet is used in the creation of a Composite Type.
     *
     * @param name
     *            The name of the item.
     * @param description
     *            The description of the item.
     * @param type
     *            The Open Type of this item.
     * @param restrictions
     *            Ignored, contains list of restrictions
     */
    public Item(String name, String description, OpenType/*<?>*/ type, String... restrictions) {
        this.name = name;
        this.description = description;
        this.type = type;
    }

    /**
     * Create a Tabular Type.
     *
     * @param name
     *            The name of the Tabular Type.
     * @param description
     *            The description of the Tabular Type.
     * @param rowType
     *            The Open Type for a row
     * @param index
     *            The names of the items that form the index .
     * @return A new Tabular Type composed from the parameters.
     * @throws RuntimeException
     *             when the Tabular Type throws an OpenDataException
     */
    static public TabularType tabularType(String name, String description, CompositeType rowType, String... index) {
        try {
            return new TabularType(name, description, rowType, index);
        } catch (OpenDataException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Create a Composite Type
     *
     * @param name
     *            The name of the Tabular Type.
     * @param description
     *            The description of the Tabular Type.
     * @param items
     *            The items that describe the composite type.
     * @return a new Composite Type
     * @throws RuntimeException
     *             when the Tabular Type throws an OpenDataException
     */
    static public CompositeType compositeType(String name, String description, Item... items) {
        return extend(null, name, description, items);
    }

    /**
     * Extend a Composite Type by adding new items. Items can override items in the parent type.
     *
     * @param parent
     *            The parent type, can be <code>null</code>
     * @param name
     *            The name of the type
     * @param description
     *            The description of the type
     * @param items
     *            The items that should be added/override to the parent type
     * @return A new Composite Type that extends the parent type
     * @throws RuntimeException
     *             when an OpenDataException is thrown
     */
    public static CompositeType extend(CompositeType parent, String name, String description, Item... items) {
        Set<Item> all = new LinkedHashSet<Item>();

        if (parent != null) {
            for (Object nm : parent.keySet()) {
                String key = (String) nm;
                all.add(new Item((String) nm, parent.getDescription(key), parent.getType(key)));
            }
        }

        all.addAll(Arrays.asList(items));

        String names[] = new String[all.size()];
        String descriptions[] = new String[all.size()];
        OpenType types[] = new OpenType[all.size()];

        int n = 0;
        for (Item item : all) {
            names[n] = item.name;
            descriptions[n] = item.description;
            types[n] = item.type;
            n++;
        }

        try {
            return new CompositeType(name, description, names, descriptions, types);
        } catch (OpenDataException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Return a new Array Type.
     *
     * @param dim
     *            The dimension
     * @param elementType
     *            The element type
     * @return A new Array Type
     */
    public static <T> ArrayType<T> arrayType(int dim, OpenType<T> elementType) {
        try {
            return new ArrayType<T>(dim, elementType);
        } catch (OpenDataException e) {
            throw new RuntimeException(e);
        }
     }


    /**
     * Return a new primaArray Type.
     *
     * @param elementType
     *            The element type
     * @return A new Array Type
     */
    /*
     * For the compatibility  with java 5, we have to cancel this method temporarily.
     *
     * public static ArrayType<?> primitiveArrayType(SimpleType<?> elementType) {
        try {
            return new ArrayType(elementType, true);
        } catch (OpenDataException e) {
            throw new RuntimeException(e);
        }
    }
    */
}
