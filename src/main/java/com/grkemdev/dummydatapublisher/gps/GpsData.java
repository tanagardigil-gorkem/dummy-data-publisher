package com.grkemdev.dummydatapublisher.gps;

import com.grkemdev.dummydatapublisher.common.DataType;
import com.grkemdev.dummydatapublisher.common.DataUtils;
import com.grkemdev.dummydatapublisher.common.DummyData;
import lombok.*;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;

/**
 * GPS (Global Positioning System) data model.
 * Supports generation of GPS data in both object and NMEA format.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper=true)
@SuperBuilder
@Slf4j
public class GpsData extends DummyData {

    // GPS specific fields
    private double altitude;        // Altitude in meters
    private double speed;           // Speed in knots
    private double course;          // Course in degrees (0-359.9)
    private int satellites;         // Number of satellites in view
    private int fixQuality;         // Fix quality (0=invalid, 1=GPS fix, 2=DGPS fix)
    private double hdop;            // Horizontal dilution of precision
    private double geoidHeight;     // Height of geoid above WGS84 ellipsoid
    private double magneticVariation; // Magnetic variation

    @Override
    public DummyData generateDummySensorData() {
        log.info("Generating dummy data for GPS");
        return GpsData.builder()
                .timestamp(System.currentTimeMillis())
                .dataType(DataType.GPS)
                .latitude(DataUtils.generateRandomLatitude())
                .longitude(DataUtils.generateRandomLongitude())
                .altitude(generateRandomAltitude())
                .speed(RANDOM.nextDouble() * 100) // 0-100 knots
                .course(RANDOM.nextDouble() * 359.9) // 0-359.9 degrees
                .satellites(RANDOM.nextInt(12) + 1) // 1-12 satellites
                .fixQuality(RANDOM.nextInt(3)) // 0-2 (0=invalid, 1=GPS fix, 2=DGPS fix)
                .hdop(1.0 + (RANDOM.nextDouble() * 9.0)) // 1.0-10.0
                .geoidHeight(-30.0 + (RANDOM.nextDouble() * 60.0)) // -30.0 to +30.0 meters
                .magneticVariation(-20.0 + (RANDOM.nextDouble() * 40.0)) // -20.0 to +20.0 degrees
                .build();
    }

    /**
     * Generates raw GPS data in NMEA format
     * @return NMEA formatted GPS data
     */
    public static String generateRawData() {
        log.info("Generating raw data for GPS");
        GpsData gpsData = (GpsData) new GpsData().generateDummySensorData();
        
        // Generate NMEA sentences
        StringBuilder sb = new StringBuilder();
        
        // Add GGA sentence (Global Positioning System Fix Data)
        sb.append(generateGGA(gpsData)).append("\n");
        
        // Add RMC sentence (Recommended Minimum Navigation Information)
        sb.append(generateRMC(gpsData)).append("\n");
        
        // Add VTG sentence (Track Made Good and Ground Speed)
        sb.append(generateVTG(gpsData));
        
        return sb.toString();
    }

    /**
     * Generates a GGA NMEA sentence (Global Positioning System Fix Data)
     * @param gpsData The GPS data to format
     * @return Formatted GGA sentence
     */
    private static String generateGGA(GpsData gpsData) {
        StringBuilder sb = new StringBuilder("$GPGGA,");
        
        // Time (hhmmss.sss)
        sb.append(DataUtils.formatNmeaTime(gpsData.getTimestamp())).append(",");
        
        // Latitude (ddmm.mmmm,N/S)
        sb.append(DataUtils.formatLatitude(gpsData.getLatitude())).append(",");
        
        // Longitude (dddmm.mmmm,E/W)
        sb.append(DataUtils.formatLongitude(gpsData.getLongitude())).append(",");
        
        // Fix quality (0-2)
        sb.append(gpsData.getFixQuality()).append(",");
        
        // Number of satellites
        sb.append(gpsData.getSatellites()).append(",");
        
        // HDOP
        sb.append(String.format("%.1f", gpsData.getHdop())).append(",");
        
        // Altitude
        sb.append(String.format("%.1f", gpsData.getAltitude())).append(",M,");
        
        // Height of geoid above WGS84 ellipsoid
        sb.append(String.format("%.1f", gpsData.getGeoidHeight())).append(",M,");
        
        // Time since last DGPS update (empty)
        sb.append(",");
        
        // DGPS reference station ID (empty)
        sb.append("");
        
        // Calculate checksum
        String sentence = sb.toString().substring(1); // Remove the $ for checksum calculation
        String checksum = DataUtils.calculateNmeaChecksum(sentence);
        
        return sb.toString() + "*" + checksum;
    }

    /**
     * Generates an RMC NMEA sentence (Recommended Minimum Navigation Information)
     * @param gpsData The GPS data to format
     * @return Formatted RMC sentence
     */
    private static String generateRMC(GpsData gpsData) {
        StringBuilder sb = new StringBuilder("$GPRMC,");
        
        // Time (hhmmss.sss)
        sb.append(DataUtils.formatNmeaTime(gpsData.getTimestamp())).append(",");
        
        // Status (A=valid, V=invalid)
        sb.append(gpsData.getFixQuality() > 0 ? "A" : "V").append(",");
        
        // Latitude (ddmm.mmmm,N/S)
        sb.append(DataUtils.formatLatitude(gpsData.getLatitude())).append(",");
        
        // Longitude (dddmm.mmmm,E/W)
        sb.append(DataUtils.formatLongitude(gpsData.getLongitude())).append(",");
        
        // Speed in knots
        sb.append(String.format("%.1f", gpsData.getSpeed())).append(",");
        
        // Course in degrees
        sb.append(String.format("%.1f", gpsData.getCourse())).append(",");
        
        // Date (ddmmyy)
        sb.append(DataUtils.formatNmeaDate(gpsData.getTimestamp())).append(",");
        
        // Magnetic variation
        double magVar = gpsData.getMagneticVariation();
        sb.append(String.format("%.1f", Math.abs(magVar))).append(",");
        sb.append(magVar >= 0 ? "E" : "W");
        
        // Calculate checksum
        String sentence = sb.toString().substring(1); // Remove the $ for checksum calculation
        String checksum = DataUtils.calculateNmeaChecksum(sentence);
        
        return sb.toString() + "*" + checksum;
    }

    /**
     * Generates a VTG NMEA sentence (Track Made Good and Ground Speed)
     * @param gpsData The GPS data to format
     * @return Formatted VTG sentence
     */
    private static String generateVTG(GpsData gpsData) {
        StringBuilder sb = new StringBuilder("$GPVTG,");
        
        // Course in degrees (true)
        sb.append(String.format("%.1f", gpsData.getCourse())).append(",T,");
        
        // Course in degrees (magnetic)
        double magCourse = gpsData.getCourse() + gpsData.getMagneticVariation();
        if (magCourse < 0) magCourse += 360;
        if (magCourse >= 360) magCourse -= 360;
        sb.append(String.format("%.1f", magCourse)).append(",M,");
        
        // Speed in knots
        sb.append(String.format("%.1f", gpsData.getSpeed())).append(",N,");
        
        // Speed in km/h
        sb.append(String.format("%.1f", gpsData.getSpeed() * 1.852)).append(",K");
        
        // Calculate checksum
        String sentence = sb.toString().substring(1); // Remove the $ for checksum calculation
        String checksum = DataUtils.calculateNmeaChecksum(sentence);
        
        return sb.toString() + "*" + checksum;
    }

    /**
     * Generates a random altitude between -100 and 10000 meters
     * @return Random altitude
     */
    private double generateRandomAltitude() {
        return -100 + (RANDOM.nextDouble() * 10100); // -100 to 10000 meters
    }
}
