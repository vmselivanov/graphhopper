package com.graphhopper.routing.util;

import com.graphhopper.util.PMap;

/**
 * Created by jan on 27.05.15.
 */
public class CarStopoverFlagEncoder extends CarFlagEncoder
{
    public final static int K_STOPOVERTURN = 12251;
    protected long stopOverTurnBitMask;

    public CarStopoverFlagEncoder()
    {
        this(5, 5, 0);
    }

    public CarStopoverFlagEncoder( PMap properties ) {
        this(
                (int)properties.getLong("speedBits", 5),
                properties.getDouble("speedFactor", 5),
                properties.getBool("turnCosts", false) ? 3 : 0
        );
        this.properties = properties;
        this.setBlockFords(properties.getBool("blockFords", true));
    }

    public CarStopoverFlagEncoder( String propertiesStr )
    {
        this(new PMap(propertiesStr));
    }

    public CarStopoverFlagEncoder( int speedBits, double speedFactor, int maxTurnCosts )
    {
        super(speedBits, speedFactor, maxTurnCosts);
    }

    /**
     * Define the place of the speedBits in the edge flags for car.
     */
    @Override
    public int defineWayBits( int index, int shift )
    {
        // bits encoded by superclasses
        shift = super.defineWayBits(index, shift);
        stopOverTurnBitMask = 1L << shift;
        return shift ++;
    }

    @Override
    public long setBool( long flags, int key, boolean value )
    {
        if (key == K_STOPOVERTURN)
        {
            Long newFlags = value ? flags | stopOverTurnBitMask : flags & ~stopOverTurnBitMask;
            return newFlags;
        } else
        {
            return super.setBool(flags, key, value);
        }
    }

    @Override
    public boolean isBool( long flags, int key )
    {
        if (key == K_STOPOVERTURN)
        {
            return (flags & stopOverTurnBitMask) != 0;
        } else
        {
            return super.isBool(flags, key);
        }
    }

}
