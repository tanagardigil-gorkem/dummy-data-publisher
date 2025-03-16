# Dummy Data Publisher

## Overview

Dummy Data Publisher is a Spring Boot application that generates and serves realistic dummy sensor data for various types of sensors. The application is designed to simulate data from different sensor types for testing and development purposes, providing both object and raw data formats.

## Supported Sensor Types

The application currently supports the following sensor types:

1. **ADSB (Automatic Dependent Surveillance-Broadcast)**
   - Aircraft position, altitude, speed, and identification data
   - Available in both object and raw message formats

2. **GPS (Global Positioning System)**
   - Position, altitude, speed, course, and satellite information
   - Available in both object and NMEA sentence formats

3. **LoRaWAN (Long Range Wide Area Network)**
   - Device identification, message information, signal metrics, and payload data
   - Available in both object and JSON message formats
   - Simulates various sensor types like temperature/humidity, soil moisture, air quality, etc.

4. **AIS (Automatic Identification System)**
   - Vessel position, identification, and voyage data
   - Available in both object and NMEA message formats
   - Supports multiple AIS message types (position reports, static data, etc.)

## Technical Details

- **Java Version**: 21
- **Framework**: Spring Boot 3.4.3
- **API Type**: RESTful with reactive endpoints using Spring WebFlux
- **Data Format**: JSON objects and raw sensor-specific formats

## API Endpoints

### ADSB Data
- `GET /dummy-data/adsb` - Stream of ADSB data objects (SSE)
- `GET /dummy-data/adsb-raw` - Stream of raw ADSB messages (SSE)
- `GET /dummy-data/adsb/sample` - Single ADSB data object
- `GET /dummy-data/adsb-raw/sample` - Single raw ADSB message

### GPS Data
- `GET /dummy-data/gps` - Stream of GPS data objects (SSE)
- `GET /dummy-data/gps-raw` - Stream of NMEA formatted GPS messages (SSE)
- `GET /dummy-data/gps/sample` - Single GPS data object
- `GET /dummy-data/gps-raw/sample` - Single NMEA formatted GPS message

### LoRaWAN Data
- `GET /dummy-data/lorawan` - Stream of LoRaWAN data objects (SSE)
- `GET /dummy-data/lorawan-raw` - Stream of JSON formatted LoRaWAN messages (SSE)
- `GET /dummy-data/lorawan/sample` - Single LoRaWAN data object
- `GET /dummy-data/lorawan-raw/sample` - Single JSON formatted LoRaWAN message

### AIS Data
- `GET /dummy-data/ais` - Stream of AIS data objects (SSE)
- `GET /dummy-data/ais-raw` - Stream of NMEA formatted AIS messages (SSE)
- `GET /dummy-data/ais/sample` - Single AIS data object
- `GET /dummy-data/ais-raw/sample` - Single NMEA formatted AIS message
- `GET /dummy-data/ais/type/{type}` - Stream of AIS data objects of a specific message type (SSE)

## Project Structure

```
src/main/java/com/grkemdev/dummydatapublisher/
├── common/                  # Common classes and utilities
│   ├── DataType.java        # Enum of supported data types
│   ├── DataUtils.java       # Utility methods for data generation
│   ├── DummyData.java       # Abstract base class for all data models
│   └── DummyDataController.java # REST controller for all endpoints
├── adsb/                    # ADSB data model and generation
│   └── AdsbData.java        # ADSB data model implementation
├── ais/                     # AIS data model and generation
│   └── AisData.java         # AIS data model implementation
├── gps/                     # GPS data model and generation
│   └── GpsData.java         # GPS data model implementation
├── lorawan/                 # LoRaWAN data model and generation
│   └── LorawanData.java     # LoRaWAN data model implementation
└── DummyDataPublisherApplication.java # Main application class
```

## Running the Application

### Prerequisites
- Java 21 or higher
- Maven 3.6 or higher

### Build and Run

```bash
# Clone the repository
git clone https://github.com/yourusername/DummyDataPublisher.git
cd DummyDataPublisher

# Build the project
./mvnw clean package

# Run the application
./mvnw spring-boot:run
```

By default, the application runs on port 8080. You can access the endpoints at `http://localhost:8080/dummy-data/...`

### Custom Port

To run on a different port:

```bash
./mvnw spring-boot:run -Dspring-boot.run.arguments=--server.port=8081
```

## Use Cases

- Testing IoT data processing pipelines
- Development of dashboards and visualization tools
- Simulation of sensor networks for training and demos
- Testing data integration with third-party systems
- Performance testing with realistic data patterns

## Design Principles

- **Modularity**: Each sensor type is implemented as a separate module
- **Extensibility**: Easy to add new sensor types by extending the base classes
- **Reusability**: Common utilities shared across different data models
- **Realistic Data**: Generated data mimics real-world sensor behavior

## License

[MIT License](LICENSE)
