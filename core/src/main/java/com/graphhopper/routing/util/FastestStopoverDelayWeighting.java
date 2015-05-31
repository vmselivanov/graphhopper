package com.graphhopper.routing.util;

import com.graphhopper.routing.VirtualEdgeIteratorState;
import com.graphhopper.util.EdgeIterator;
import com.graphhopper.util.EdgeIteratorState;
import com.graphhopper.util.PMap;

/**
 * Created by jan on 27.05.15.
 */
public class FastestStopoverDelayWeighting extends FastestWeighting

{
    // additional delay for a turn
    private final long directionPenalty;

    public FastestStopoverDelayWeighting( FlagEncoder encoder, long directionPenalty )
    {
        super(encoder);
        this.directionPenalty = directionPenalty;
    }

    public FastestStopoverDelayWeighting( FlagEncoder encoder )
    {
        this(encoder, 0);
    }

    @Override
    public double calcWeight( EdgeIteratorState edge, boolean reverse, int prevOrNextEdgeId )
    {
        double speed = super.calcWeight(edge, reverse, prevOrNextEdgeId);
        if (Double.isInfinite(speed))
            return Double.POSITIVE_INFINITY;
        
        double time = (edge.getDistance() / speed * SPEED_CONV);
        
        // add direction penalties at start/stop/via points
        boolean penalizeEdge = edge.getBoolean(EdgeIteratorState.DISPREFERED_STARTSTOPEDGE, false,
                    new PMap().put("reverse", reverse));
        if (penalizeEdge)
        {
            time += directionPenalty;
        }

        return time;
    }

    @Override
    public String toString()
    {
        return "FASTESTSTOPOVERDELAY|" + encoder;
    }
}
