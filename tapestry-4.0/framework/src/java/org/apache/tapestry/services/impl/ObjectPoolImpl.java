// Copyright 2004, 2005 The Apache Software Foundation
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package org.apache.tapestry.services.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.tapestry.event.ReportStatusEvent;
import org.apache.tapestry.event.ReportStatusListener;
import org.apache.tapestry.event.ResetEventListener;
import org.apache.tapestry.services.ObjectPool;

/**
 * Implementation of the {@link org.apache.tapestry.services.ObjectPool} interface.
 * <p>
 * This ia a minimal implementation, one that has no concept of automatically removing unused pooled
 * objects. Eventually, it will also register for notifications about general cache cleaning.
 * 
 * @author Howard Lewis Ship
 * @since 4.0
 */
public class ObjectPoolImpl implements ObjectPool, ResetEventListener, ReportStatusListener
{
    private String _serviceId;

    private int _count = 0;

    /**
     * Pool of Lists (of pooled objects), keyed on arbitrary key.
     */
    private Map _pool = new HashMap();

    public synchronized Object get(Object key)
    {
        List pooled = (List) _pool.get(key);

        if (pooled == null || pooled.isEmpty())
            return null;

        _count--;

        return pooled.remove(0);
    }

    public synchronized void store(Object key, Object value)
    {
        List pooled = (List) _pool.get(key);

        if (pooled == null)
        {
            pooled = new LinkedList();
            _pool.put(key, pooled);
        }

        pooled.add(value);

        _count++;
    }

    public synchronized void resetEventDidOccur()
    {
        _pool.clear();

        _count = 0;
    }

    public synchronized void reportStatus(ReportStatusEvent event)
    {
        event.title(_serviceId);

        event.property("total count", _count);

        event.section("Count by Key");

        Iterator i = _pool.entrySet().iterator();

        while (i.hasNext())
        {
            Map.Entry entry = (Map.Entry) i.next();

            String key = entry.getKey().toString();

            List pooled = (List) entry.getValue();

            event.property(key, pooled.size());
        }
    }

    public void setServiceId(String serviceId)
    {
        _serviceId = serviceId;
    }

}
