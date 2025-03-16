package com.grkemdev.dummydatapublisher.ais;

import com.grkemdev.dummydatapublisher.common.DataType;
import com.grkemdev.dummydatapublisher.common.DataUtils;
import com.grkemdev.dummydatapublisher.common.DummyData;
import lombok.*;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;

/**
 * AIS (Automatic Identification System) data model.
 * Supports generation of AIS messages in both object and NMEA format.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper=true)
@SuperBuilder
@Slf4j
public class AisData extends DummyData {

    // AIS specific fields
    private int mmsi; // Maritime Mobile Service Identity
    private String vesselName;
    private String callSign;
    private int imo; // International Maritime Organization number
    private double speedOverGround; // in knots
    private double courseOverGround; // in degrees
    private double heading; // in degrees
    private int navigationalStatus;
    private int shipType;
    private double draught; // in meters
    private String destination;
    private int messageType;
    private boolean positionAccuracy;
    private int raimFlag; // Receiver Autonomous Integrity Monitoring flag
    private int timeStamp; // UTC second when the report was generated
    private int maneuverIndicator;
    private String eta; // Estimated Time of Arrival
    private double dimensionToBow;
    private double dimensionToStern;
    private double dimensionToPort;
    private double dimensionToStarboard;
    private double rateOfTurn; // in degrees per minute

    @Override
    public DummyData generateDummySensorData() {
        log.info("Generating dummy data for AIS");
        
        return AisData.builder()
                .timestamp(System.currentTimeMillis())
                .dataType(DataType.AIS)
                .latitude(DataUtils.generateRandomLatitude())
                .longitude(DataUtils.generateRandomLongitude())
                .mmsi(generateRandomMmsi())
                .vesselName(generateRandomVesselName())
                .callSign(generateRandomCallSign())
                .imo(generateRandomImo())
                .speedOverGround(RANDOM.nextDouble() * 30) // 0-30 knots
                .courseOverGround(RANDOM.nextDouble() * 360) // 0-360 degrees
                .heading(RANDOM.nextDouble() * 360) // 0-360 degrees
                .navigationalStatus(RANDOM.nextInt(15)) // 0-15 status codes
                .shipType(RANDOM.nextInt(99) + 1) // 1-99 ship type codes
                .draught(RANDOM.nextDouble() * 20) // 0-20 meters
                .destination(generateRandomDestination())
                .messageType(generateRandomMessageType())
                .positionAccuracy(RANDOM.nextBoolean())
                .raimFlag(RANDOM.nextInt(2)) // 0-1
                .timeStamp(RANDOM.nextInt(60)) // 0-59 seconds
                .maneuverIndicator(RANDOM.nextInt(3)) // 0-2
                .eta(generateRandomEta())
                .dimensionToBow(5 + RANDOM.nextDouble() * 295) // 5-300 meters
                .dimensionToStern(5 + RANDOM.nextDouble() * 95) // 5-100 meters
                .dimensionToPort(5 + RANDOM.nextDouble() * 45) // 5-50 meters
                .dimensionToStarboard(5 + RANDOM.nextDouble() * 45) // 5-50 meters
                .rateOfTurn(-720 + RANDOM.nextDouble() * 1440) // -720 to +720 degrees per minute
                .build();
    }

    /**
     * Generates raw AIS data in NMEA format
     * @return NMEA formatted AIS data
     */
    public static String generateRawData() {
        log.info("Generating raw data for AIS");
        AisData aisData = (AisData) new AisData().generateDummySensorData();
        
        // Choose a random message type to encode
        switch (aisData.getMessageType()) {
            case 1: case 2: case 3: // Position report
                return encodePositionReport(aisData);
            case 5: // Static and voyage related data
                return encodeStaticVoyageData(aisData);
            case 18: // Standard Class B position report
                return encodeClassBPositionReport(aisData);
            case 24: // Static data report
                return encodeStaticDataReport(aisData);
            default:
                return encodePositionReport(aisData); // Default to position report
        }
    }

    /**
     * Encodes a position report (message types 1, 2, 3)
     * @param aisData The AIS data to encode
     * @return NMEA formatted position report
     */
    private static String encodePositionReport(AisData aisData) {
        // In a real implementation, this would encode the binary AIS message
        // Here we'll create a simplified NMEA sentence
        StringBuilder payload = new StringBuilder();
        payload.append("!AIVDM,1,1,,A,");
        
        // Create a simplified payload - in reality this would be properly encoded
        String simplifiedPayload = String.format("%d%09d%02d%.4f%.4f%.1f%.1f%d",
                aisData.getMessageType(),
                aisData.getMmsi(),
                aisData.getNavigationalStatus(),
                aisData.getLatitude(),
                aisData.getLongitude(),
                aisData.getSpeedOverGround(),
                aisData.getCourseOverGround(),
                aisData.getTimeStamp());
        
        payload.append(simplifiedPayload);
        payload.append(",0*");
        
        // Calculate checksum (XOR of all characters between ! and *)
        String checksum = DataUtils.calculateNmeaChecksum(payload.substring(1, payload.length() - 2));
        payload.append(checksum);
        
        return payload.toString();
    }

    /**
     * Encodes static and voyage related data (message type 5)
     * @param aisData The AIS data to encode
     * @return NMEA formatted static and voyage data
     */
    private static String encodeStaticVoyageData(AisData aisData) {
        // In a real implementation, this would encode the binary AIS message
        // Here we'll create a simplified NMEA sentence
        StringBuilder payload = new StringBuilder();
        payload.append("!AIVDM,2,1,,A,");
        
        // Create a simplified payload - in reality this would be properly encoded
        String simplifiedPayload1 = String.format("%d%09d%d%s%d%s",
                aisData.getMessageType(),
                aisData.getMmsi(),
                aisData.getImo(),
                padRight(aisData.getCallSign(), 7),
                aisData.getShipType(),
                padRight(aisData.getVesselName(), 20));
        
        payload.append(simplifiedPayload1);
        payload.append(",0*");
        
        // Calculate checksum for first part
        String checksum1 = DataUtils.calculateNmeaChecksum(payload.substring(1, payload.length() - 2));
        payload.append(checksum1);
        
        String part1 = payload.toString();
        
        // Second part
        payload = new StringBuilder();
        payload.append("!AIVDM,2,2,,A,");
        
        String simplifiedPayload2 = String.format("%.1f%s%.1f%.1f%.1f%.1f%s",
                aisData.getDraught(),
                aisData.getEta(),
                aisData.getDimensionToBow(),
                aisData.getDimensionToStern(),
                aisData.getDimensionToPort(),
                aisData.getDimensionToStarboard(),
                padRight(aisData.getDestination(), 20));
        
        payload.append(simplifiedPayload2);
        payload.append(",0*");
        
        // Calculate checksum for second part
        String checksum2 = DataUtils.calculateNmeaChecksum(payload.substring(1, payload.length() - 2));
        payload.append(checksum2);
        
        return part1 + "\n" + payload.toString();
    }

    /**
     * Encodes a Class B position report (message type 18)
     * @param aisData The AIS data to encode
     * @return NMEA formatted Class B position report
     */
    private static String encodeClassBPositionReport(AisData aisData) {
        // Similar to position report but with Class B specific fields
        StringBuilder payload = new StringBuilder();
        payload.append("!AIVDM,1,1,,A,");
        
        // Create a simplified payload
        String simplifiedPayload = String.format("%d%09d%.4f%.4f%.1f%.1f%d%d",
                aisData.getMessageType(),
                aisData.getMmsi(),
                aisData.getLatitude(),
                aisData.getLongitude(),
                aisData.getSpeedOverGround(),
                aisData.getCourseOverGround(),
                (int)aisData.getHeading(),
                aisData.getTimeStamp());
        
        payload.append(simplifiedPayload);
        payload.append(",0*");
        
        // Calculate checksum
        String checksum = DataUtils.calculateNmeaChecksum(payload.substring(1, payload.length() - 2));
        payload.append(checksum);
        
        return payload.toString();
    }

    /**
     * Encodes a static data report (message type 24)
     * @param aisData The AIS data to encode
     * @return NMEA formatted static data report
     */
    private static String encodeStaticDataReport(AisData aisData) {
        // Part A - Vessel name
        StringBuilder payloadA = new StringBuilder();
        payloadA.append("!AIVDM,1,1,,A,");
        
        String simplifiedPayloadA = String.format("%d%09d%d%s",
                aisData.getMessageType(),
                aisData.getMmsi(),
                0, // Part number 0
                padRight(aisData.getVesselName(), 20));
        
        payloadA.append(simplifiedPayloadA);
        payloadA.append(",0*");
        
        String checksumA = DataUtils.calculateNmeaChecksum(payloadA.substring(1, payloadA.length() - 2));
        payloadA.append(checksumA);
        
        // Part B - Ship type and call sign
        StringBuilder payloadB = new StringBuilder();
        payloadB.append("!AIVDM,1,1,,A,");
        
        String simplifiedPayloadB = String.format("%d%09d%d%s%d%.1f%.1f%.1f%.1f",
                aisData.getMessageType(),
                aisData.getMmsi(),
                1, // Part number 1
                padRight(aisData.getCallSign(), 7),
                aisData.getShipType(),
                aisData.getDimensionToBow(),
                aisData.getDimensionToStern(),
                aisData.getDimensionToPort(),
                aisData.getDimensionToStarboard());
        
        payloadB.append(simplifiedPayloadB);
        payloadB.append(",0*");
        
        String checksumB = DataUtils.calculateNmeaChecksum(payloadB.substring(1, payloadB.length() - 2));
        payloadB.append(checksumB);
        
        return payloadA.toString() + "\n" + payloadB.toString();
    }

    /**
     * Generates a random MMSI (Maritime Mobile Service Identity)
     * @return Random MMSI between 200000000 and 799999999
     */
    private int generateRandomMmsi() {
        // MMSI format: MID (3 digits) + National ID (6 digits)
        // MID (Maritime Identification Digits) range from 200-799
        return 200000000 + RANDOM.nextInt(600000000);
    }

    /**
     * Generates a random vessel name
     * @return Random vessel name
     */
    private String generateRandomVesselName() {
        String[] prefixes = {"MV", "SS", "MY", "SY", "MSC", "USNS", "HMS", "RMS"};
        String[] names = {"EXPLORER", "VOYAGER", "DISCOVERY", "PIONEER", "ENDEAVOR", "NAVIGATOR", 
                         "MARINER", "ADVENTURER", "PATHFINDER", "SURVEYOR", "INVESTIGATOR", "RESEARCHER"};
        
        return prefixes[RANDOM.nextInt(prefixes.length)] + " " + names[RANDOM.nextInt(names.length)];
    }

    /**
     * Generates a random call sign
     * @return Random call sign
     */
    private String generateRandomCallSign() {
        // Call signs typically have a prefix (1-2 letters) followed by numbers and/or letters
        String[] prefixes = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", 
                           "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};
        
        StringBuilder callSign = new StringBuilder();
        callSign.append(prefixes[RANDOM.nextInt(prefixes.length)]);
        callSign.append(prefixes[RANDOM.nextInt(prefixes.length)]);
        
        // Add 2-4 digits
        int numDigits = 2 + RANDOM.nextInt(3);
        for (int i = 0; i < numDigits; i++) {
            callSign.append(RANDOM.nextInt(10));
        }
        
        return callSign.toString();
    }

    /**
     * Generates a random IMO number
     * @return Random IMO number between 1000000 and 9999999
     */
    private int generateRandomImo() {
        // IMO numbers are 7 digits
        return 1000000 + RANDOM.nextInt(9000000);
    }

    /**
     * Generates a random destination
     * @return Random destination port
     */
    private String generateRandomDestination() {
        String[] destinations = {"NEW YORK", "ROTTERDAM", "SINGAPORE", "SHANGHAI", "HONG KONG", 
                               "TOKYO", "BUSAN", "LOS ANGELES", "HAMBURG", "ANTWERP", "DUBAI", 
                               "SANTOS", "VALENCIA", "ALGECIRAS", "PORT SAID", "COLOMBO"};
        
        return destinations[RANDOM.nextInt(destinations.length)];
    }

    /**
     * Generates a random ETA (Estimated Time of Arrival)
     * @return Random ETA in format MM-DD HH:MM
     */
    private String generateRandomEta() {
        int month = 1 + RANDOM.nextInt(12);
        int day = 1 + RANDOM.nextInt(28); // Simplified, not accounting for different month lengths
        int hour = RANDOM.nextInt(24);
        int minute = RANDOM.nextInt(60);
        
        return String.format("%02d-%02d %02d:%02d", month, day, hour, minute);
    }

    /**
     * Generates a random AIS message type
     * @return Random message type (1, 2, 3, 5, 18, or 24)
     */
    private int generateRandomMessageType() {
        int[] messageTypes = {1, 2, 3, 5, 18, 24};
        return messageTypes[RANDOM.nextInt(messageTypes.length)];
    }

    /**
     * Pads a string to the right with spaces to a specified length
     * @param s The string to pad
     * @param length The desired length
     * @return Padded string
     */
    private static String padRight(String s, int length) {
        if (s.length() >= length) {
            return s.substring(0, length);
        }
        StringBuilder sb = new StringBuilder(s);
        while (sb.length() < length) {
            sb.append(' ');
        }
        return sb.toString();
    }
}
