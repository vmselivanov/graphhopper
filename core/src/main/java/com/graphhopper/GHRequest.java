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
package com.graphhopper;

import com.graphhopper.routing.util.WeightingMap;
import com.graphhopper.util.Helper;
import com.graphhopper.util.shapes.GHPoint;

import java.util.*;

/**
 * GraphHopper request wrapper to simplify requesting GraphHopper.
 * <p/>
 * @author Peter Karich
 * @author ratrun
 */
public class GHRequest
{
    private String algo = "";
    private List<GHPoint> points;
    private final WeightingMap hints = new WeightingMap();
    private String vehicle = "";
    private boolean possibleToAdd = false;
    private Locale locale = Locale.US;

    // List of preferred start (1st element) and arrival directions (all other). 
    // Directions are north based azimuth (clockwise) in (0,2pi) or NaN for equal preference
    private List<Double> preferredDirections;

    public GHRequest()
    {
        this(5);
    }

    public GHRequest( int size )
    {
        points = new ArrayList<GHPoint>(size);
        initDirectionsList(size);
        possibleToAdd = true;
    }

    /**
     * Calculate the path from specified startPlace (fromLat, fromLon) to endPlace (toLat, toLon).
     */
    public GHRequest( double fromLat, double fromLon, double toLat, double toLon )
    {
        this(new GHPoint(fromLat, fromLon), new GHPoint(toLat, toLon));
    }

    /**
     * Calculate the path from specified startPlace to endPlace.
     */
    public GHRequest( GHPoint startPlace, GHPoint endPlace )
    {
        if (startPlace == null)
            throw new IllegalStateException("'from' cannot be null");

        if (endPlace == null)
            throw new IllegalStateException("'to' cannot be null");
        points = new ArrayList<GHPoint>(2);
        points.add(startPlace);
        points.add(endPlace);
        
        initDirectionsList(2);
    }

    public GHRequest( List<GHPoint> points )
    {
        this.points = points;
        initDirectionsList(points.size());
    }

    public GHRequest addPoint( GHPoint point , Double preferredDirection)
    {
        if (point == null)
            throw new IllegalArgumentException("point cannot be null");
        if (!possibleToAdd)
            throw new IllegalStateException("Please call empty constructor if you intent to use "
                    + "more than two places via addPlace method.");

        points.add(point);
        preferredDirections.add(preferredDirection);
        return this;
    }

    public GHRequest addPoint( GHPoint point)
    {
        addPoint(point, Double.NaN);
        return this;
    }

    /**
     * initialize directions list with non-preferred directions
     */
    private void initDirectionsList( int size )
    {
        preferredDirections = new ArrayList<Double>(Collections.nCopies(size, Double.NaN));
    }

    /**
     * Sets preferred directions for starting (start point) and ending (via and end points)
     * Directions are north based azimuth (clockwise) in (0,2pi) or NaN for equal preference
     */
    public GHRequest setPreferredDirections(List<Double> directions)
    {
      if (points.size() != directions.size())
      {
          throw new IllegalArgumentException("Size of directions (" + directions.size() + 
                  ") must match size of points (" + points.size() + ")"); 
      }
      for (Double direction : directions)
      {
          validateAzimuthValue(direction);
      }
      this.preferredDirections = directions;
      return this;
    }

    /**
     * Sets preferred direction for a specific point of the request
     * @param direction north based azimuth (clockwise) in (0,2pi) or NaN for equal preference
     * @param position point for which direction is set. Starts with 0 for the start point  
     */
    public GHRequest setPreferredDirection(Double direction, int position)
    {
        validateAzimuthValue(direction);
        if (position >= preferredDirections.size() || -position > preferredDirections.size())
        {
            throw new IllegalArgumentException("Position " + position + "out of range for " + points.size() + " points");
        }
        preferredDirections.set(position, direction);
        return this;
    }
    
    /**
    * @return north based azimuth (clockwise) in (0,2pi) or NaN for equal preference
    */
    public double getPreferredDirection( int i )
    {
        return preferredDirections.get(i);
    }

    /**
     * @return if there exist a preferred direction for any start/via/end point
     */
    public boolean hasPreferredDirection()
    {
        return !Double.isNaN(Collections.min(preferredDirections));        
    }
    
    // validate Azimuth entry
    private void validateAzimuthValue( Double direction )
    {
        // direction must be in (0, 2Pi) oder Nan
        if (!Double.isNaN(direction) && 
                (Double.compare(direction,(2* Math.PI)) > 0) 
                || (Double.compare(direction, 0) < 0))
        {
            throw new IllegalArgumentException("Direction " + direction + " must be in range (0,2pi)");
        }        
    }
    
    public List<GHPoint> getPoints()
    {
        return points;
    }

    /**
     * For possible values see AlgorithmOptions.*
     */
    public GHRequest setAlgorithm( String algo )
    {
        if (algo != null)
            this.algo = algo;
        return this;
    }

    public String getAlgorithm()
    {
        return algo;
    }

    public Locale getLocale()
    {
        return locale;
    }

    public GHRequest setLocale( Locale locale )
    {
        if (locale != null)
            this.locale = locale;
        return this;
    }

    public GHRequest setLocale( String localeStr )
    {
        return setLocale(Helper.getLocale(localeStr));
    }

    /**
     * By default it supports fastest and shortest. Or specify empty to use default.
     */
    public GHRequest setWeighting( String w )
    {
        hints.setWeighting(w);
        return this;
    }

    public String getWeighting()
    {
        return hints.getWeighting();
    }

    /**
     * Specifiy car, bike or foot. Or specify empty to use default.
     */
    public GHRequest setVehicle( String vehicle )
    {
        if (vehicle != null)
            this.vehicle = vehicle;
        return this;
    }

    public String getVehicle()
    {
        return vehicle;
    }

    @Override
    public String toString()
    {
        String res = "";
        for (GHPoint point : points)
        {
            if (res.isEmpty())
                res = point.toString();
            else
                res += "; " + point.toString();
        }
        return res + "(" + algo + ")";
    }

    public WeightingMap getHints()
    {
        return hints;
    }
}
