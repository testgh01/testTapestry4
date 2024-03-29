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

/**
 * Throws when a book may not be borrowed.
 * 
 * @see IOperations#borrowBook(Integer,Integer)
 * @author Howard Lewis Ship
 */

public class BorrowException extends Exception
{
    private static final long serialVersionUID = 7761447010967099141L;

	public BorrowException(String message)
    {
        super(message);
    }
}