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

package org.apache.tapestry.vlib.ejb;

import java.rmi.RemoteException;

import javax.ejb.CreateException;
import javax.ejb.EJBHome;
import javax.ejb.FinderException;

/**
 * Home interface for the {@link IPublisher} entity bean.
 * 
 * @author Howard Lewis Ship
 */

public interface IPublisherHome extends EJBHome
{
    public IPublisher create(String name) throws CreateException, RemoteException;

    public IPublisher findByPrimaryKey(Integer key) throws FinderException, RemoteException;

    /**
     * Finds Publisher with exact match on name.
     */

    public IPublisher findByName(String name) throws FinderException, RemoteException;

}