package com.graphhopper.routing.util;

import com.graphhopper.util.EdgeIteratorState;

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
        double delay = encoder.isBool(edge.getFlags(), CarStopoverFlagEncoder.K_STOPOVERTURN)? stopoverTurnDelay :0;
        //double delay = edge.getDelay(encoder, prevOrNextEdgeId, reverse);
        return (edge.getDistance() / speed * SPEED_CONV) + delay;
    }

    @Override
    public String toString()
    {
        return "FASTESTSTOPOVERDELAY|" + encoder;
    }
}
