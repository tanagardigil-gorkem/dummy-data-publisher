package com.grkemdev.dummydatapublisher.common;

import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Random;

/**
 * Utility class containing common methods used across different data models.
 */
@Slf4j
public class DataUtils {

    private static final Random RANDOM = new Random();

    /**
     * Generates a random latitude between -90 and +90 degrees
     * @return Random latitude value
     */
    public static double generateRandomLatitude() {
        return -90 + (RANDOM.nextDouble() * 180); // Generates latitude between -90 and +90
    }

    /**
     * Generates a random longitude between -180 and +180 degrees
     * @return Random longitude value
     */
    public static double generateRandomLongitude() {
        return -180 + (RANDOM.nextDouble() * 360); // Generates longitude between -180 and +180
    }
    
    /**
     * Formats a timestamp as an ISO 8601 string
     * @param timestamp The timestamp to format in milliseconds
     * @return Formatted timestamp string
     */
    public static String formatIsoTimestamp(long timestamp) {
        Instant instant = Instant.ofEpochMilli(timestamp);
        return instant.toString();
    }
    
    /**
     * Formats a timestamp as time in NMEA format (hhmmss.sss)
     * @param timestamp The timestamp to format in milliseconds
     * @return Formatted time string
     */
    public static String formatNmeaTime(long timestamp) {
        LocalDateTime dateTime = LocalDateTime.ofEpochSecond(timestamp / 1000, 0, ZoneOffset.UTC);
        return dateTime.format(DateTimeFormatter.ofPattern("HHmmss.SSS"));
    }
    
    /**
     * Formats a timestamp as date in NMEA format (ddmmyy)
     * @param timestamp The timestamp to format in milliseconds
     * @return Formatted date string
     */
    public static String formatNmeaDate(long timestamp) {
        LocalDateTime dateTime = LocalDateTime.ofEpochSecond(timestamp / 1000, 0, ZoneOffset.UTC);
        return dateTime.format(DateTimeFormatter.ofPattern("ddMMyy"));
    }
    
    /**
     * Formats latitude in NMEA format (ddmm.mmmm,N/S)
     * @param latitude The latitude to format
     * @return Formatted latitude string
     */
    public static String formatLatitude(double latitude) {
        // Convert decimal degrees to degrees and minutes
        int degrees = (int) Math.abs(latitude);
        double minutes = (Math.abs(latitude) - degrees) * 60;
        
        // Format as ddmm.mmmm,N/S
        String formatted = String.format("%02d%07.4f", degrees, minutes);
        return formatted + "," + (latitude >= 0 ? "N" : "S");
    }
    
    /**
     * Formats longitude in NMEA format (dddmm.mmmm,E/W)
     * @param longitude The longitude to format
     * @return Formatted longitude string
     */
    public static String formatLongitude(double longitude) {
        // Convert decimal degrees to degrees and minutes
        int degrees = (int) Math.abs(longitude);
        double minutes = (Math.abs(longitude) - degrees) * 60;
        
        // Format as dddmm.mmmm,E/W
        String formatted = String.format("%03d%07.4f", degrees, minutes);
        return formatted + "," + (longitude >= 0 ? "E" : "W");
    }
    
    /**
     * Calculates the NMEA checksum for a sentence
     * @param sentence The NMEA sentence without the checksum
     * @return The checksum as a two-character hexadecimal string
     */
    public static String calculateNmeaChecksum(String sentence) {
        int checksum = 0;
        for (int i = 0; i < sentence.length(); i++) {
            checksum ^= sentence.charAt(i);
        }
        return String.format("%02X", checksum);
    }
    
    /**
     * Generates a random hex string of specified length
     * @param length The length of the hex string to generate
     * @param upperCase Whether to use uppercase letters
     * @return Random hex string
     */
    public static String generateRandomHexString(int length, boolean upperCase) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int digit = RANDOM.nextInt(16);
            if (upperCase) {
                sb.append(Integer.toHexString(digit).toUpperCase());
            } else {
                sb.append(Integer.toHexString(digit).toLowerCase());
            }
        }
        return sb.toString();
    }
    
    /**
     * Pads or truncates a string to the specified length
     * @param s The string to pad
     * @param length The desired length
     * @param padChar The character to use for padding
     * @return The padded or truncated string
     */
    public static String padString(String s, int length, char padChar) {
        if (s == null) s = "";
        if (s.length() > length) {
            return s.substring(0, length);
        }
        StringBuilder sb = new StringBuilder(s);
        while (sb.length() < length) {
            sb.append(padChar);
        }
        return sb.toString();
    }
}
