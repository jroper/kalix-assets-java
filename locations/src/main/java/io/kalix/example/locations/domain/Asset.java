package io.kalix.example.locations.domain;

import kalix.javasdk.annotations.TypeName;

public record Asset(String assetId, String description, String location) {
    public static Asset register(String assetId, String description, String location) {
        return new Asset(assetId, description, location);
    }

    public Asset moveTo(String newLocation) {
        return new Asset(assetId, description, newLocation);
    }

    public sealed interface Event {
        @TypeName("asset-registered")
        record AssetRegistered(String description, String location) implements Event {}
        @TypeName("asset-moved")
        record AssetMoved(String location) implements Event {}
        @TypeName("asset-disposed")
        record AssetDisposed() implements Event {}
    }
}