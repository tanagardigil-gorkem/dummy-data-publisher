package com.grkemdev.dummydatapublisher.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Random;

/**
 * Abstract base class for all dummy data models.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public abstract class DummyData {

    // Common fields for all sensor data
    protected long timestamp;
    protected DataType dataType;
    protected double latitude;
    protected double longitude;
    
    // Shared random instance for all data generators
    protected static final Random RANDOM = new Random();
    
    /**
     * Abstract method to be implemented by each data model to generate dummy sensor data
     * @return A new instance of the data model with randomly generated values
     */
    public abstract DummyData generateDummySensorData();
}
