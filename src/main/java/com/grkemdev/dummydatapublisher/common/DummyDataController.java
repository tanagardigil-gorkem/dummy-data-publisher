package com.grkemdev.dummydatapublisher.common;

import com.grkemdev.dummydatapublisher.adsb.AdsbData;
import com.grkemdev.dummydatapublisher.ais.AisData;
import com.grkemdev.dummydatapublisher.gps.GpsData;
import com.grkemdev.dummydatapublisher.lorawan.LorawanData;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

/**
 * Controller for serving simulated sensor data
 */
@RestController
@RequestMapping("/dummy-data")
public class DummyDataController {

    /**
     * Streams AIS data as JSON objects
     * @return Flux of AIS data objects
     */
    @GetMapping(value = "/ais", produces = "text/event-stream")
    public Flux<AisData> streamAisData() {
        return Flux.interval(Duration.ofSeconds(2))
                .map(data -> (AisData) new AisData().generateDummySensorData());
    }

    /**
     * Streams raw NMEA formatted AIS messages
     * @return Flux of NMEA formatted strings
     */
    @GetMapping(value = "/ais-raw", produces = "text/event-stream")
    public Flux<String> streamAisRawData() {
        return Flux.interval(Duration.ofSeconds(2))
                .map(data -> AisData.generateRawData());
    }
    
    /**
     * Streams AIS data of a specific message type as JSON objects
     * @param type The AIS message type (1-27)
     * @return Flux of AIS data objects of the specified type
     */
    @GetMapping(value = "/ais/type/{type}", produces = "text/event-stream")
    public Flux<AisData> streamAisDataByType(@PathVariable int type) {
        if (type < 1 || type > 27) {
            return Flux.error(new IllegalArgumentException("AIS message type must be between 1 and 27"));
        }
        
        return Flux.interval(Duration.ofSeconds(2))
                .map(data -> {
                    AisData aisData;
                    do {
                        aisData = (AisData) new AisData().generateDummySensorData();
                    } while (aisData.getMessageType() != type);
                    return aisData;
                });
    }
    
    /**
     * Gets a single AIS data sample
     * @return Mono of AIS data object
     */
    @GetMapping("/ais/sample")
    public Mono<AisData> getAisSample() {
        return Mono.just((AisData) new AisData().generateDummySensorData());
    }
    
    /**
     * Gets a single raw NMEA formatted AIS message
     * @return Mono of NMEA formatted string
     */
    @GetMapping("/ais-raw/sample")
    public Mono<String> getAisRawSample() {
        return Mono.just(AisData.generateRawData());
    }

    /**
     * Streams ADSB data as JSON objects
     * @return Flux of ADSB data objects
     */
    @GetMapping(value = "/adsb", produces = "text/event-stream")
    public Flux<AdsbData> streamAdsbData() {
        return Flux.interval(Duration.ofSeconds(2))
                .map(data -> (AdsbData) new AdsbData().generateDummySensorData());
    }

    /**
     * Streams raw ADSB messages
     * @return Flux of raw ADSB formatted strings
     */
    @GetMapping(value = "/adsb-raw", produces = "text/event-stream")
    public Flux<String> streamAdsbRawData() {
        return Flux.interval(Duration.ofSeconds(2))
                .map(data -> AdsbData.generateRawData());
    }
    
    /**
     * Gets a single ADSB data sample
     * @return Mono of ADSB data object
     */
    @GetMapping("/adsb/sample")
    public Mono<AdsbData> getAdsbSample() {
        return Mono.just((AdsbData) new AdsbData().generateDummySensorData());
    }
    
    /**
     * Gets a single raw ADSB message
     * @return Mono of raw ADSB formatted string
     */
    @GetMapping("/adsb-raw/sample")
    public Mono<String> getAdsbRawSample() {
        return Mono.just(AdsbData.generateRawData());
    }
    
    /**
     * Streams GPS data as JSON objects
     * @return Flux of GPS data objects
     */
    @GetMapping(value = "/gps", produces = "text/event-stream")
    public Flux<GpsData> streamGpsData() {
        return Flux.interval(Duration.ofSeconds(1))
                .map(data -> (GpsData) new GpsData().generateDummySensorData());
    }

    /**
     * Streams raw NMEA formatted GPS messages
     * @return Flux of NMEA formatted strings
     */
    @GetMapping(value = "/gps-raw", produces = "text/event-stream")
    public Flux<String> streamGpsRawData() {
        return Flux.interval(Duration.ofSeconds(1))
                .map(data -> GpsData.generateRawData());
    }
    
    /**
     * Gets a single GPS data sample
     * @return Mono of GPS data object
     */
    @GetMapping("/gps/sample")
    public Mono<GpsData> getGpsSample() {
        return Mono.just((GpsData) new GpsData().generateDummySensorData());
    }
    
    /**
     * Gets a single raw NMEA formatted GPS message
     * @return Mono of NMEA formatted string
     */
    @GetMapping("/gps-raw/sample")
    public Mono<String> getGpsRawSample() {
        return Mono.just(GpsData.generateRawData());
    }
    
    /**
     * Streams LoRaWAN data as JSON objects
     * @return Flux of LoRaWAN data objects
     */
    @GetMapping(value = "/lorawan", produces = "text/event-stream")
    public Flux<LorawanData> streamLorawanData() {
        return Flux.interval(Duration.ofSeconds(5)) // LoRaWAN typically has less frequent updates
                .map(data -> (LorawanData) new LorawanData().generateDummySensorData());
    }

    /**
     * Streams raw LoRaWAN messages in JSON format
     * @return Flux of JSON formatted strings
     */
    @GetMapping(value = "/lorawan-raw", produces = "text/event-stream")
    public Flux<String> streamLorawanRawData() {
        return Flux.interval(Duration.ofSeconds(5))
                .map(data -> LorawanData.generateRawData());
    }
    
    /**
     * Gets a single LoRaWAN data sample
     * @return Mono of LoRaWAN data object
     */
    @GetMapping("/lorawan/sample")
    public Mono<LorawanData> getLorawanSample() {
        return Mono.just((LorawanData) new LorawanData().generateDummySensorData());
    }
    
    /**
     * Gets a single raw LoRaWAN message in JSON format
     * @return Mono of JSON formatted string
     */
    @GetMapping("/lorawan-raw/sample")
    public Mono<String> getLorawanRawSample() {
        return Mono.just(LorawanData.generateRawData());
    }
}
