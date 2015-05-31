/*
 *  Licensed to GraphHopper and Peter Karich under one or more contributor
 *  license agreements. See the NOTICE file distributed with this work for 
 *  additional information regarding copyright ownership.
 *
 *  GraphHopper licenses this file to you under the Apache License, 
 *  Version 2.0 (the "License"); you may not use this file except in 
 *  compliance with the License. You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.graphhopper.routing.util;

import com.graphhopper.util.EdgeIteratorState;
import com.graphhopper.util.PointList;
import gnu.trove.set.hash.TIntHashSet;

import java.util.List;

/**
 *  Extends an existing filter to exclude specific edge ids
 *  @author jan soe
 */

public class ExcludeIdEdgeFilter implements EdgeFilter
{
    private final TIntHashSet rejectedEdgeIds;
    private final EdgeFilter baseFilter;

    /**
     * Creates an edges filter which extends an existing edge filter to exclude specific edge ids
     */
    public ExcludeIdEdgeFilter( EdgeFilter baseFilter, List<Integer> edgeIds )
    {
        this.baseFilter = baseFilter;
        this.rejectedEdgeIds = new TIntHashSet(edgeIds.size());
        rejectedEdgeIds.addAll(edgeIds);
    }

    @Override
    public final boolean accept( EdgeIteratorState iter )
    {
        return baseFilter.accept(iter)? !rejectedEdgeIds.contains(iter.getEdge()) : false;
    }

    @Override
    public String toString()
    {
        return baseFilter.toString() + "excluding: " + rejectedEdgeIds.toString();
    }
}
