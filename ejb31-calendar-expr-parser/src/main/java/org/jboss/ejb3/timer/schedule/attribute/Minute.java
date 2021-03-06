/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2009, Red Hat Middleware LLC, and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.ejb3.timer.schedule.attribute;

import java.util.Calendar;
import java.util.SortedSet;

import javax.ejb.ScheduleExpression;

import org.jboss.ejb3.timer.schedule.value.ScheduleExpressionType;

/**
 * Represents the value of a minute constructed out of a {@link ScheduleExpression#getMinute()}
 *
 * <p>
 *  A {@link Minute} can hold only {@link Integer} as its value. The only exception to this being the wildcard (*)
 *  value. The various ways in which a 
 *  {@link Minute} value can be represented are:
 *  <ul>
 *      <li>Wildcard. For example, minute = "*"</li>
 *      <li>Range. For example, minute = "0-20"</li>
 *      <li>List. For example, minute = "10, 30, 45"</li>
 *      <li>Single value. For example, minute = "8"</li>
 *      <li>Increment. For example, minute = "10 &#47; 15"</li>
 *  </ul>
 * </p>
 *
 * @author Jaikiran Pai
 * @version $Revision: $
 */
public class Minute extends IntegerBasedExpression
{

   /**
    * Maximum allowed value for a {@link Minute}
    */
   public static final Integer MAX_MINUTE = 59;

   /**
    * Minimum allowed value for a {@link Minute}
    */
   public static final Integer MIN_MINUTE = 0;

   /**
    * Creates a {@link Minute} by parsing the passed {@link String} <code>value</code>
    * <p>
    *   Valid values are of type {@link ScheduleExpressionType#WILDCARD}, {@link ScheduleExpressionType#RANGE},
    *   {@link ScheduleExpressionType#LIST} {@link ScheduleExpressionType#INCREMENT} or 
    *   {@link ScheduleExpressionType#SINGLE_VALUE}
    * </p>
    * @param value The value to be parsed
    * 
    * @throws IllegalArgumentException If the passed <code>value</code> is neither a {@link ScheduleExpressionType#WILDCARD}, 
    *                               {@link ScheduleExpressionType#RANGE}, {@link ScheduleExpressionType#LIST}, 
    *                               {@link ScheduleExpressionType#INCREMENT} nor {@link ScheduleExpressionType#SINGLE_VALUE}.
    */
   public Minute(String value)
   {
      super(value);
   }

   public int getFirst()
   {
      if (this.scheduleExpressionType == ScheduleExpressionType.WILDCARD)
      {
         return 0;
      }
      SortedSet<Integer> eligibleMinutes = this.getEligibleMinutes();
      if (eligibleMinutes.isEmpty())
      {
         throw new IllegalStateException("There are no valid minutes for expression: " + this.origValue);
      }
      return eligibleMinutes.first();
   }

   /**
    * Returns the maximum allowed value for a {@link Minute}
    * 
    * @see Minute#MAX_MINUTE
    */
   @Override
   protected Integer getMaxValue()
   {
      return MAX_MINUTE;
   }

   /**
    * Returns the minimum allowed value for a {@link Minute}
    * 
    * @see Minute#MIN_MINUTE
    */
   @Override
   protected Integer getMinValue()
   {
      return MIN_MINUTE;
   }
   
   @Override
   public boolean isRelativeValue(String value)
   {
      // minute doesn't support relative values, hence
      // return false always
      return false;
   }
   
   @Override
   protected boolean accepts(ScheduleExpressionType scheduleExprType)
   {
      switch (scheduleExprType)
      {
         case RANGE :
         case LIST :
         case SINGLE_VALUE :
         case WILDCARD :
         case INCREMENT :
            return true;
         default :
            return false;
      }
   }
   
   private SortedSet<Integer> getEligibleMinutes()
   {
      return this.absoluteValues;
   }

   public Integer getNextMatch(Calendar currentCal)
   {
      if (this.scheduleExpressionType == ScheduleExpressionType.WILDCARD)
      {
         return currentCal.get(Calendar.MINUTE);
      }
      if (this.absoluteValues.isEmpty())
      {
         return null;
      }
      int currentMinute = currentCal.get(Calendar.MINUTE);
      for (Integer minute : this.absoluteValues)
      {
         if (currentMinute == minute)
         {
            return currentMinute;
         }
         if (minute > currentMinute)
         {
            return minute;
         }
      }
      return this.absoluteValues.first();
   }
}
