package com.grkemdev.dummydatapublisher.lorawan;

import com.grkemdev.dummydatapublisher.common.DataType;
import com.grkemdev.dummydatapublisher.common.DataUtils;
import com.grkemdev.dummydatapublisher.common.DummyData;
import lombok.*;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * LoRaWAN (Long Range Wide Area Network) data model.
 * Supports generation of LoRaWAN messages in both object and raw format.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper=true)
@SuperBuilder
@Slf4j
public class LorawanData extends DummyData {

    // LoRaWAN specific fields
    private String devEui;
    private String devAddr;
    private String appEui;
    private int fPort;
    private boolean confirmed;
    private int messageType;
    private int counter;
    private double rssi;
    private double snr;
    private int spreadingFactor;
    private int bandwidth;
    private int codingRate;
    private String frequency;
    private double frequencyValue;
    private byte[] payload;
    private Map<String, Object> decodedPayload;

    @Override
    public DummyData generateDummySensorData() {
        log.info("Generating dummy data for LoRaWAN");
        SensorType sensorType = getRandomSensorType();
        byte[] randomPayload = generateRandomPayload(sensorType);
        
        return LorawanData.builder()
                .timestamp(System.currentTimeMillis())
                .dataType(DataType.LORAWAN)
                .latitude(DataUtils.generateRandomLatitude())
                .longitude(DataUtils.generateRandomLongitude())
                .devEui(generateRandomDevEui())
                .devAddr(generateRandomDevAddr())
                .appEui(generateRandomAppEui())
                .fPort(1 + RANDOM.nextInt(223)) // Valid port range 1-223
                .confirmed(RANDOM.nextBoolean())
                .messageType(RANDOM.nextInt(6)) // 0-5 (different message types)
                .counter(RANDOM.nextInt(65536)) // 0-65535
                .rssi(-120 + RANDOM.nextInt(80)) // -120 to -40 dBm
                .snr(-20 + RANDOM.nextDouble() * 30) // -20 to +10 dB
                .spreadingFactor(7 + RANDOM.nextInt(6)) // SF7-SF12
                .bandwidth(getBandwidthOptions()[RANDOM.nextInt(getBandwidthOptions().length)])
                .codingRate(1 + RANDOM.nextInt(4)) // CR 1-4 (4/5 to 4/8)
                .frequency(getFrequencyBands()[RANDOM.nextInt(getFrequencyBands().length)])
                .frequencyValue(getRandomFrequency())
                .payload(randomPayload)
                .decodedPayload(decodePayload(randomPayload, sensorType))
                .build();
    }

    /**
     * Generates raw LoRaWAN data in JSON format
     * @return JSON formatted LoRaWAN data
     */
    public static String generateRawData() {
        log.info("Generating raw data for LoRaWAN");
        LorawanData lorawanData = (LorawanData) new LorawanData().generateDummySensorData();
        
        // Format as JSON
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"time\":\"" + formatTimestamp(lorawanData.getTimestamp()) + "\",");
        sb.append("\"device\":\"" + lorawanData.getDevEui() + "\",");
        sb.append("\"devAddr\":\"" + lorawanData.getDevAddr() + "\",");
        sb.append("\"appEui\":\"" + lorawanData.getAppEui() + "\",");
        sb.append("\"gatewayId\":\"" + generateRandomGatewayId() + "\",");
        sb.append("\"fPort\":" + lorawanData.getFPort() + ",");
        sb.append("\"messageType\":\"" + getMessageTypeName(lorawanData.getMessageType()) + "\",");
        sb.append("\"counter\":" + lorawanData.getCounter() + ",");
        sb.append("\"rssi\":" + lorawanData.getRssi() + ",");
        sb.append("\"snr\":" + String.format("%.1f", lorawanData.getSnr()) + ",");
        sb.append("\"spreadingFactor\":" + lorawanData.getSpreadingFactor() + ",");
        sb.append("\"bandwidth\":" + lorawanData.getBandwidth() + ",");
        sb.append("\"codingRate\":\"4/" + (4 + lorawanData.getCodingRate()) + "\",");
        sb.append("\"frequency\":\"" + lorawanData.getFrequency() + "\",");
        sb.append("\"frequencyValue\":" + String.format("%.1f", lorawanData.getFrequencyValue()) + ",");
        
        // Add payload as hex string
        StringBuilder hexPayload = new StringBuilder();
        for (byte b : lorawanData.getPayload()) {
            hexPayload.append(String.format("%02X", b & 0xFF));
        }
        sb.append("\"payload\":\"" + hexPayload.toString() + "\",");
        
        // Add decoded payload
        sb.append("\"decodedPayload\":{" + formatDecodedPayload(lorawanData.getDecodedPayload()) + "}");
        
        sb.append("}");
        return sb.toString();
    }

    /**
     * Formats the decoded payload as a JSON string
     * @param decodedPayload The decoded payload map
     * @return JSON formatted string of the decoded payload
     */
    private static String formatDecodedPayload(Map<String, Object> decodedPayload) {
        if (decodedPayload == null || decodedPayload.isEmpty()) {
            return "";
        }
        
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        
        for (Map.Entry<String, Object> entry : decodedPayload.entrySet()) {
            if (!first) {
                sb.append(",");
            }
            first = false;
            
            sb.append("\"" + entry.getKey() + "\":");
            
            Object value = entry.getValue();
            if (value instanceof String) {
                sb.append("\"" + value + "\"");
            } else if (value instanceof Number) {
                sb.append(value);
            } else if (value instanceof Boolean) {
                sb.append(value);
            } else {
                sb.append("\"" + value + "\"");
            }
        }
        
        return sb.toString();
    }

    /**
     * Generates a random DevEUI (Device Extended Unique Identifier)
     * @return Random DevEUI as a 16-character hex string
     */
    private String generateRandomDevEui() {
        return DataUtils.generateRandomHexString(16, false);
    }

    /**
     * Generates a random DevAddr (Device Address)
     * @return Random DevAddr as an 8-character hex string
     */
    private String generateRandomDevAddr() {
        return DataUtils.generateRandomHexString(8, false);
    }

    /**
     * Generates a random AppEUI (Application EUI)
     * @return Random AppEUI as a 16-character hex string
     */
    private String generateRandomAppEui() {
        return DataUtils.generateRandomHexString(16, false);
    }

    /**
     * Generates a random Gateway ID
     * @return Random Gateway ID as a 16-character hex string
     */
    private static String generateRandomGatewayId() {
        return DataUtils.generateRandomHexString(16, false);
    }

    /**
     * Gets the available bandwidth options for LoRaWAN
     * @return Array of bandwidth values in kHz
     */
    private int[] getBandwidthOptions() {
        return new int[] {125, 250, 500};
    }

    /**
     * Gets the available frequency bands for LoRaWAN
     * @return Array of frequency band names
     */
    private String[] getFrequencyBands() {
        return new String[] {"EU868", "US915", "AU915", "AS923", "KR920", "IN865"};
    }

    /**
     * Gets a random frequency value based on common LoRaWAN frequencies
     * @return Random frequency in MHz
     */
    private double getRandomFrequency() {
        // Common LoRaWAN frequencies
        double[] frequencies = {
            // EU868
            868.1, 868.3, 868.5, 867.1, 867.3, 867.5, 867.7, 867.9,
            // US915
            902.3, 902.5, 902.7, 902.9, 903.1, 903.3, 903.5, 903.7,
            // AS923
            923.2, 923.4, 923.6, 923.8, 924.0, 924.2, 924.4, 924.6
        };
        
        return frequencies[RANDOM.nextInt(frequencies.length)];
    }

    /**
     * Gets the message type name for a given message type code
     * @param messageType The message type code
     * @return The message type name
     */
    private static String getMessageTypeName(int messageType) {
        switch (messageType) {
            case 0: return "JoinRequest";
            case 1: return "JoinAccept";
            case 2: return "UnconfirmedDataUp";
            case 3: return "UnconfirmedDataDown";
            case 4: return "ConfirmedDataUp";
            case 5: return "ConfirmedDataDown";
            default: return "Unknown";
        }
    }

    /**
     * Formats a timestamp as an ISO 8601 string
     * @param timestamp The timestamp to format
     * @return Formatted timestamp string
     */
    private static String formatTimestamp(long timestamp) {
        return DataUtils.formatIsoTimestamp(timestamp);
    }

    /**
     * Enum representing different types of sensors that might use LoRaWAN
     */
    private enum SensorType {
        TEMPERATURE_HUMIDITY,
        SOIL_MOISTURE,
        AIR_QUALITY,
        WATER_LEVEL,
        PARKING_SENSOR,
        DOOR_WINDOW_SENSOR,
        WASTE_BIN,
        ASSET_TRACKER
    }

    /**
     * Gets a random sensor type
     * @return Random sensor type
     */
    private SensorType getRandomSensorType() {
        SensorType[] types = SensorType.values();
        return types[RANDOM.nextInt(types.length)];
    }

    /**
     * Generates a random payload based on the sensor type
     * @param sensorType The type of sensor
     * @return Random payload bytes
     */
    private byte[] generateRandomPayload(SensorType sensorType) {
        switch (sensorType) {
            case TEMPERATURE_HUMIDITY:
                // 2 bytes temperature, 1 byte humidity
                return new byte[] {
                    (byte)(RANDOM.nextInt(100) - 20), // -20 to 80 degrees C
                    (byte)(RANDOM.nextInt(100) / 100.0 * 256), // fractional part
                    (byte)(RANDOM.nextInt(101)) // 0-100% humidity
                };
                
            case SOIL_MOISTURE:
                // 1 byte moisture, 2 bytes temperature
                return new byte[] {
                    (byte)(RANDOM.nextInt(101)), // 0-100% moisture
                    (byte)(RANDOM.nextInt(60) - 10), // -10 to 50 degrees C
                    (byte)(RANDOM.nextInt(100) / 100.0 * 256) // fractional part
                };
                
            case AIR_QUALITY:
                // 2 bytes PM2.5, 2 bytes PM10, 1 byte CO2 level
                return new byte[] {
                    (byte)(RANDOM.nextInt(256)), // PM2.5 high byte
                    (byte)(RANDOM.nextInt(256)), // PM2.5 low byte
                    (byte)(RANDOM.nextInt(256)), // PM10 high byte
                    (byte)(RANDOM.nextInt(256)), // PM10 low byte
                    (byte)(RANDOM.nextInt(101)) // CO2 level 0-100 (representing 400-2000 ppm)
                };
                
            case WATER_LEVEL:
                // 2 bytes water level, 1 byte temperature
                return new byte[] {
                    (byte)(RANDOM.nextInt(256)), // water level high byte
                    (byte)(RANDOM.nextInt(256)), // water level low byte
                    (byte)(RANDOM.nextInt(50)) // 0-50 degrees C
                };
                
            case PARKING_SENSOR:
                // 1 byte status, 1 byte battery level
                return new byte[] {
                    (byte)(RANDOM.nextInt(2)), // 0=vacant, 1=occupied
                    (byte)(RANDOM.nextInt(101)) // 0-100% battery
                };
                
            case DOOR_WINDOW_SENSOR:
                // 1 byte status, 1 byte battery level, 1 byte count
                return new byte[] {
                    (byte)(RANDOM.nextInt(2)), // 0=closed, 1=open
                    (byte)(RANDOM.nextInt(101)), // 0-100% battery
                    (byte)(RANDOM.nextInt(256)) // event count
                };
                
            case WASTE_BIN:
                // 1 byte fill level, 1 byte temperature, 1 byte battery
                return new byte[] {
                    (byte)(RANDOM.nextInt(101)), // 0-100% fill level
                    (byte)(RANDOM.nextInt(80) - 20), // -20 to 60 degrees C
                    (byte)(RANDOM.nextInt(101)) // 0-100% battery
                };
                
            case ASSET_TRACKER:
                // 4 bytes for lat/long, 1 byte battery, 1 byte status
                return new byte[] {
                    (byte)(RANDOM.nextInt(256)), // latitude high byte
                    (byte)(RANDOM.nextInt(256)), // latitude low byte
                    (byte)(RANDOM.nextInt(256)), // longitude high byte
                    (byte)(RANDOM.nextInt(256)), // longitude low byte
                    (byte)(RANDOM.nextInt(101)), // 0-100% battery
                    (byte)(RANDOM.nextInt(4)) // status (0=stationary, 1=moving, 2=alert, 3=error)
                };
                
            default:
                // Default 3 bytes of random data
                return new byte[] {
                    (byte)(RANDOM.nextInt(256)),
                    (byte)(RANDOM.nextInt(256)),
                    (byte)(RANDOM.nextInt(256))
                };
        }
    }

    /**
     * Decodes a payload based on the sensor type
     * @param payload The payload bytes
     * @param sensorType The type of sensor
     * @return Decoded payload as key-value pairs
     */
    private Map<String, Object> decodePayload(byte[] payload, SensorType sensorType) {
        Map<String, Object> decoded = new HashMap<>();
        
        switch (sensorType) {
            case TEMPERATURE_HUMIDITY:
                if (payload.length >= 3) {
                    double temperature = payload[0] + (payload[1] & 0xFF) / 256.0;
                    int humidity = payload[2] & 0xFF;
                    decoded.put("temperature", String.format("%.1f", temperature));
                    decoded.put("humidity", humidity);
                    decoded.put("unit", "°C");
                }
                break;
                
            case SOIL_MOISTURE:
                if (payload.length >= 3) {
                    int moisture = payload[0] & 0xFF;
                    double temperature = payload[1] + (payload[2] & 0xFF) / 256.0;
                    decoded.put("moisture", moisture);
                    decoded.put("temperature", String.format("%.1f", temperature));
                    decoded.put("unit", "°C");
                }
                break;
                
            case AIR_QUALITY:
                if (payload.length >= 5) {
                    int pm25 = ((payload[0] & 0xFF) << 8) | (payload[1] & 0xFF);
                    int pm10 = ((payload[2] & 0xFF) << 8) | (payload[3] & 0xFF);
                    int co2Level = payload[4] & 0xFF;
                    int co2ppm = 400 + (co2Level * 16); // Map 0-100 to 400-2000 ppm
                    decoded.put("pm25", pm25);
                    decoded.put("pm10", pm10);
                    decoded.put("co2", co2ppm);
                    decoded.put("unit", "μg/m³");
                }
                break;
                
            case WATER_LEVEL:
                if (payload.length >= 3) {
                    int waterLevel = ((payload[0] & 0xFF) << 8) | (payload[1] & 0xFF);
                    int temperature = payload[2] & 0xFF;
                    decoded.put("waterLevel", waterLevel);
                    decoded.put("temperature", temperature);
                    decoded.put("unit", "cm");
                }
                break;
                
            case PARKING_SENSOR:
                if (payload.length >= 2) {
                    boolean occupied = (payload[0] & 0xFF) == 1;
                    int battery = payload[1] & 0xFF;
                    decoded.put("occupied", occupied);
                    decoded.put("battery", battery);
                }
                break;
                
            case DOOR_WINDOW_SENSOR:
                if (payload.length >= 3) {
                    boolean open = (payload[0] & 0xFF) == 1;
                    int battery = payload[1] & 0xFF;
                    int count = payload[2] & 0xFF;
                    decoded.put("open", open);
                    decoded.put("battery", battery);
                    decoded.put("count", count);
                }
                break;
                
            case WASTE_BIN:
                if (payload.length >= 3) {
                    int fillLevel = payload[0] & 0xFF;
                    int temperature = payload[1] & 0xFF;
                    int battery = payload[2] & 0xFF;
                    decoded.put("fillLevel", fillLevel);
                    decoded.put("temperature", temperature - 20); // Adjust for negative temps
                    decoded.put("battery", battery);
                }
                break;
                
            case ASSET_TRACKER:
                if (payload.length >= 6) {
                    int latRaw = ((payload[0] & 0xFF) << 8) | (payload[1] & 0xFF);
                    int lonRaw = ((payload[2] & 0xFF) << 8) | (payload[3] & 0xFF);
                    // Convert to actual lat/lon (-90 to 90, -180 to 180)
                    double lat = (latRaw / 65535.0 * 180.0) - 90.0;
                    double lon = (lonRaw / 65535.0 * 360.0) - 180.0;
                    int battery = payload[4] & 0xFF;
                    int status = payload[5] & 0xFF;
                    String statusText = "";
                    switch (status) {
                        case 0: statusText = "stationary"; break;
                        case 1: statusText = "moving"; break;
                        case 2: statusText = "alert"; break;
                        case 3: statusText = "error"; break;
                    }
                    decoded.put("latitude", String.format("%.6f", lat));
                    decoded.put("longitude", String.format("%.6f", lon));
                    decoded.put("battery", battery);
                    decoded.put("status", statusText);
                }
                break;
                
            default:
                // For unknown types, just add the raw bytes as hex
                StringBuilder hexPayload = new StringBuilder();
                for (byte b : payload) {
                    hexPayload.append(String.format("%02X", b & 0xFF));
                }
                decoded.put("rawData", hexPayload.toString());
        }
        
        // Add sensor type to all payloads
        decoded.put("sensorType", sensorType.name());
        
        return decoded;
    }
}
