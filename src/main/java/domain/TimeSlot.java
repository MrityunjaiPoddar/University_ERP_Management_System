package domain;

import java.time.LocalTime;

public class TimeSlot {
   private LocalTime startTime;
   private LocalTime endTime;

   public TimeSlot(LocalTime startTime, LocalTime endTime) {
      this.startTime = startTime;
      this.endTime = endTime;
   }

   public static TimeSlot parse(String str) {
      if (str == null || !str.contains("-")) {
         throw new IllegalArgumentException("Invalid time slot format: " + str);
      }
      try {
         String[] parts = str.trim().split("-");
         LocalTime start = LocalTime.parse(parts[0].trim());
         LocalTime end = LocalTime.parse(parts[1].trim());
         return new TimeSlot(start, end);
      } catch (Exception e) {
         throw new IllegalArgumentException("Error parsing time slot: " + str, e);
      }
   }

   public boolean overlaps(TimeSlot other) {
      return !this.endTime.isBefore(other.startTime) && !this.startTime.isAfter(other.endTime);
   }

   public String toString() {
      String var = String.valueOf(this.startTime);
      return var + " - " + String.valueOf(this.endTime);
   }
}
