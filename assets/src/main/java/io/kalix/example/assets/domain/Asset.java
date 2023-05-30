package io.kalix.example.assets.domain;

import kalix.javasdk.annotations.TypeName;

public record Asset(String description, String location, AssetStatus status) {

    public enum AssetStatus {
        NON_EXISTING,
        EXISTING,
        DISPOSED
    }

    public static final Asset NON_EXISTING = new Asset("", "", AssetStatus.NON_EXISTING);

    public Asset register(String description, String location) {
        return new Asset(description, location, AssetStatus.EXISTING);
    }

    public Asset moveTo(String newLocation) {
        return new Asset(description, newLocation, status);
    }

    public Asset dispose() {
        return new Asset(description, location, AssetStatus.DISPOSED);
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
