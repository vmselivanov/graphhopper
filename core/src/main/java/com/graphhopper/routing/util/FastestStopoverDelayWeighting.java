package com.graphhopper.routing.util;

import com.graphhopper.util.EdgeIterator;
import com.graphhopper.util.EdgeIteratorState;
import com.graphhopper.util.PMap;

/**
 * Created by jan on 27.05.15.
 */
public class FastestStopoverDelayWeighting extends FastestWeighting

{
    // additional delay for a turn
    private final long stopoverTurnDelay;

    public FastestStopoverDelayWeighting( FlagEncoder encoder, long stopoverTurnDelay )
    {
        super(encoder);
        this.stopoverTurnDelay = stopoverTurnDelay;
    }

    public FastestStopoverDelayWeighting( FlagEncoder encoder )
    {
        this(encoder, 0);
    }

    @Override
    public double calcWeight( EdgeIteratorState edge, boolean reverse, int prevOrNextEdgeId )
    {
        double speed = reverse ? encoder.getReverseSpeed(edge.getFlags()) : encoder.getSpeed(edge.getFlags());
        if (speed == 0)
            return Double.POSITIVE_INFINITY;
        double time = (edge.getDistance() / speed * SPEED_CONV);
        if (true) //(prevOrNextEdgeId == EdgeIterator.NO_EDGE)
        {
            double delay = encoder.isBool(edge.getFlags(), CarStopoverFlagEncoder.K_STOPOVERTURN) ? stopoverTurnDelay : 0;
            time += delay;
        }
        //double delay = edge.getDouble(key, new PMap().put("reverse", reverse) );
        return time;
    }

    @Override
    public String toString()
    {
        return "FASTESTSTOPOVERDELAY|" + encoder;
    }
}
