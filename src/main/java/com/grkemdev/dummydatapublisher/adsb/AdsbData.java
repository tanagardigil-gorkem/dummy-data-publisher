package com.grkemdev.dummydatapublisher.adsb;

import com.grkemdev.dummydatapublisher.common.DataType;
import com.grkemdev.dummydatapublisher.common.DataUtils;
import com.grkemdev.dummydatapublisher.common.DummyData;
import lombok.*;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;

/**
 * ADSB (Automatic Dependent Surveillance-Broadcast) data model.
 * Supports generation of ADSB messages in both object and raw format.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper=true)
@SuperBuilder
@Slf4j
public class AdsbData extends DummyData {

    // ADSB specific fields
    private String icao;            // ICAO address (24-bit hex)
    private double altitude;        // Altitude in feet
    private double groundSpeed;     // Ground speed in knots
    private double track;           // Track in degrees (0-359)
    private double verticalRate;    // Vertical rate in feet per minute
    private String squawk;          // Squawk code (4-digit octal)
    private boolean alert;          // Alert flag
    private boolean emergency;      // Emergency flag
    private boolean spi;            // Special position identification flag
    private boolean onGround;       // On ground flag

    @Override
    public DummyData generateDummySensorData() {
        log.info("Generating dummy data for ADSB");
        return AdsbData.builder()
                .timestamp(System.currentTimeMillis())
                .dataType(DataType.ADSB)
                .latitude(DataUtils.generateRandomLatitude())
                .longitude(DataUtils.generateRandomLongitude())
                .icao(generateRandomIcao())
                .altitude(generateRandomAltitude())
                .groundSpeed(RANDOM.nextDouble() * 600) // Max speed 600 knots
                .track(RANDOM.nextDouble() * 359.9) // 0-359.9 degrees
                .verticalRate((RANDOM.nextDouble() * 6000) - 3000) // -3000 to +3000 feet per minute
                .squawk(generateRandomSquawk())
                .alert(RANDOM.nextBoolean())
                .emergency(RANDOM.nextBoolean())
                .spi(RANDOM.nextBoolean())
                .onGround(RANDOM.nextBoolean())
                .build();
    }

    /**
     * Generates a raw ADSB message in hex format
     * @return Hex string representing an ADSB message
     */
    public static String generateRawData() {
        log.info("Generating raw data for ADSB");
        AdsbData adsbData = (AdsbData) new AdsbData().generateDummySensorData();
        
        // For simplicity, we'll generate a basic ADSB position message (DF17)
        StringBuilder sb = new StringBuilder();
        
        // DF17 - Extended Squitter (8 bits: 10001 for DF17, 3 bits for CA)
        sb.append("8D");
        
        // ICAO address (24 bits)
        sb.append(adsbData.getIcao());
        
        // Type code (5 bits) - Airborne position (11-18)
        // Surveillance status (3 bits)
        // Time (1 bit)
        // CPR format (1 bit)
        // Encoded latitude (17 bits)
        // Encoded longitude (17 bits)
        
        // For simplicity, we'll just generate random data for the rest of the message
        for (int i = 0; i < 14; i++) {
            sb.append(Integer.toHexString(RANDOM.nextInt(16)).toUpperCase());
        }
        
        return sb.toString();
    }

    /**
     * Generates a random ICAO address (24-bit hex)
     * @return Random ICAO address
     */
    private String generateRandomIcao() {
        return DataUtils.generateRandomHexString(6, true);
    }

    /**
     * Generates a random altitude between 0 and 45000 feet
     * @return Random altitude
     */
    private double generateRandomAltitude() {
        return RANDOM.nextDouble() * 45000; // 0-45000 feet
    }

    /**
     * Generates a random squawk code (4-digit octal)
     * @return Random squawk code
     */
    private String generateRandomSquawk() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            sb.append(RANDOM.nextInt(8)); // 0-7 (octal)
        }
        return sb.toString();
    }
}
