/**
 * Copyright 2012 Impetus Infotech.
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
package com.impetus.client.cassandra.common;

import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.Properties;
import java.util.UUID;

import org.scale7.cassandra.pelops.Bytes;

import com.impetus.kundera.Constants;
import com.impetus.kundera.PersistenceProperties;
import com.impetus.kundera.metadata.model.KunderaMetadata;
import com.impetus.kundera.metadata.model.PersistenceUnitMetadata;
import com.impetus.kundera.property.PropertyAccessorFactory;
import com.impetus.kundera.property.accessor.DateAccessor;

/**
 * Provides utilities methods
 * 
 * @author amresh.singh
 */
public class CassandraUtilities
{

    public static String toUTF8(byte[] value)
    {
        return value == null ? null : new String(value, Charset.forName(Constants.CHARSET_UTF8));
    }

    public static String getKeyspace(String persistenceUnit)
    {
        PersistenceUnitMetadata persistenceUnitMetadata = KunderaMetadata.INSTANCE.getApplicationMetadata()
                .getPersistenceUnitMetadata(persistenceUnit);
        Properties props = persistenceUnitMetadata.getProperties();
        String keyspace = (String) props.get(PersistenceProperties.KUNDERA_KEYSPACE);
        return keyspace;
    }

    public static Bytes toBytes(Object value, Field f)
    {
        return toBytes(value, f.getType());

    }

    /**
     * @param value
     * @param f
     * @return
     */
    public static Bytes toBytes(Object value, Class<?> clazz)
    {
        if (clazz.isAssignableFrom(String.class))
        {
            return Bytes.fromByteArray(((String) value).getBytes());
        }
        else if (clazz.equals(int.class) || clazz.isAssignableFrom(Integer.class))
        {
            return Bytes.fromInt(Integer.parseInt(value.toString()));
        }
        else if (clazz.equals(long.class) || clazz.isAssignableFrom(Long.class))
        {
            return Bytes.fromLong(Long.parseLong(value.toString()));
        }
        else if (clazz.equals(boolean.class) || clazz.isAssignableFrom(Boolean.class))
        {
            return Bytes.fromBoolean(Boolean.valueOf(value.toString()));
        }
        else if (clazz.equals(double.class) || clazz.isAssignableFrom(Double.class))
        {
            return Bytes.fromDouble(Double.valueOf(value.toString()));
        }
        else if (clazz.isAssignableFrom(java.util.UUID.class))
        {
            return Bytes.fromUuid(UUID.fromString(value.toString()));
        }
        else if (clazz.equals(float.class) || clazz.isAssignableFrom(Float.class))
        {
            return Bytes.fromFloat(Float.valueOf(value.toString()));
        }
        else if (clazz.isAssignableFrom(Date.class))
        {
            DateAccessor dateAccessor = new DateAccessor();
            return Bytes.fromByteArray(dateAccessor.toBytes(value));
        }
        else
        {
            if (value.getClass().isAssignableFrom(String.class))
            {
                value = PropertyAccessorFactory.getPropertyAccessor(clazz).fromString(clazz, value.toString());
            }
            return Bytes.fromByteArray(PropertyAccessorFactory.getPropertyAccessor(clazz).toBytes(value));
        }
    }

}
