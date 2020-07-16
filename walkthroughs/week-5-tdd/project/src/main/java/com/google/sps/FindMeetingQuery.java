// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

public final class FindMeetingQuery {

  private final int TOTAL_DAY_MINUTES = 60 * 24;

  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
    ArrayList<TimeRange> availableTimes;

    // Declaration of an array of boolean that indicates the available minutes in a day.
    Boolean[] availableMinutesAttendees = new Boolean[TOTAL_DAY_MINUTES];
    Arrays.fill(availableMinutesAttendees, true);

    Boolean[] availableMinutesOptAttendees = new Boolean[TOTAL_DAY_MINUTES];
    Arrays.fill(availableMinutesOptAttendees, true);

    Collection<String> attendees = request.getAttendees();
    Collection<String> optionalAttendees = request.getOptionalAttendees();

    for (Event currentEvent: events) {
      Collection<String> eventAttendees = currentEvent.getAttendees();

      // Verify if requested attendees are in the current event.
      Boolean isAttendeeAtEvent = !Collections.disjoint(eventAttendees, attendees);
      Boolean isOptAttendeeAtEvent = !Collections.disjoint(eventAttendees, optionalAttendees);

      int start = currentEvent.getWhen().start();
      int end = start + currentEvent.getWhen().duration();

      if (isAttendeeAtEvent) {
        Arrays.fill(availableMinutesAttendees, start, end, false);
        Arrays.fill(availableMinutesOptAttendees, start, end, false);
      } else if (isOptAttendeeAtEvent) {
        Arrays.fill(availableMinutesOptAttendees, start, end, false);
      }
    }

    // First, try to include optional attendees.
    long duration = request.getDuration();
    availableTimes = computeAvailableTimes(availableMinutesOptAttendees, duration);

    // If there was no time slot available with optional attendees, return time available with mandatory attendees.
    return availableTimes.size() > 0 ? availableTimes : computeAvailableTimes(availableMinutesAttendees, duration);
  }

  /**
   * Function that compute free slots available given request needs.
   * @param minutesAvailable type{Boolean} array that contains final minute availability of the request
   * @param duration type{long} duration wanted for a meeting
   * @return ArrayList<TimeRange> List of time slots that meet the requirements of the query
   */
  private ArrayList<TimeRange> computeAvailableTimes(Boolean[] minutesAvailable, long duration) {
    int durationFreeSlot = 0;
    ArrayList<TimeRange> timeRanges = new ArrayList<>();

    for (int i = 0; i < minutesAvailable.length; i++) {
      // Calculate minutes available from a free slot.
      if (minutesAvailable[i]) {
        durationFreeSlot++;
      } else if (durationFreeSlot > 0) {
        // Create a timerange instance with the start, end and duration of the free slot.
        TimeRange timeRange = TimeRange.fromStartDuration(i - durationFreeSlot, durationFreeSlot);
        // Check if that time slot meets request needs.
        if (timeRange.duration() >= duration) {
          timeRanges.add(timeRange);
        }

        durationFreeSlot = 0;
      }
    }

    // Calculate the remaining free slot (same as inside the for loop).
    TimeRange timeRange = TimeRange.fromStartDuration(minutesAvailable.length - durationFreeSlot, durationFreeSlot);
    if (timeRange.duration() >= duration) {
      timeRanges.add(timeRange);
    }

    return timeRanges;
  }
}
