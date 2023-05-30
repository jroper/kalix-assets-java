package io.kalix.example.assets;

import io.kalix.example.assets.domain.Asset;
import kalix.javasdk.Metadata;
import kalix.javasdk.StatusCode;
import kalix.javasdk.annotations.Acl;
import kalix.javasdk.annotations.EntityKey;
import kalix.javasdk.annotations.EntityType;
import kalix.javasdk.annotations.EventHandler;
import kalix.javasdk.eventsourcedentity.EventSourcedEntity;
import org.springframework.web.bind.annotation.*;

import java.util.function.Function;

@EntityType("asset")
@EntityKey("assetId")
@RequestMapping("/asset/{assetId}")
@Acl(allow = @Acl.Matcher(principal = Acl.Principal.INTERNET))
public class AssetEntity extends EventSourcedEntity<Asset, Asset.Event> {
    @Override
    public Asset emptyState() {
        return Asset.NON_EXISTING;
    }

    @GetMapping()
    public Effect<Asset> getAsset() {
        if (currentState().status() != Asset.AssetStatus.EXISTING) {
            return effects().error("Asset not found", StatusCode.ErrorCode.NOT_FOUND);
        }
        return effects().reply(currentState());
    }

    @PostMapping()
    public Effect<Asset> registerAsset(@RequestBody RegisterAsset register) {
        if (currentState().status() != Asset.AssetStatus.NON_EXISTING) {
            return effects().error("Asset already exists", StatusCode.ErrorCode.CONFLICT);
        }
        return effects()
                .emitEvent(new Asset.Event.AssetRegistered(register.description, register.location))
                .thenReply(Function.identity(), Metadata.EMPTY.add("_kalix-http-code", "204"));
    }

    @PutMapping("/location/{location}")
    public Effect<Asset> moveAsset(@PathVariable String location) {
        if (currentState().status() != Asset.AssetStatus.EXISTING) {
            return effects().error("Asset not found", StatusCode.ErrorCode.NOT_FOUND);
        }
        return effects()
                .emitEvent(new Asset.Event.AssetMoved(location))
                .thenReply(Function.identity(), Metadata.EMPTY.add("_kalix-http-code", "204"));
    }

    @DeleteMapping()
    public Effect<Asset> disposeOfAsset() {
        if (currentState().status() != Asset.AssetStatus.EXISTING) {
            return effects().error("Asset not found", StatusCode.ErrorCode.NOT_FOUND);
        }
        return effects()
                .emitEvent(new Asset.Event.AssetDisposed())
                .thenReply(Function.identity(), Metadata.EMPTY.add("_kalix-http-code", "204"));
    }

    @EventHandler
    public Asset assetRegistered(Asset.Event.AssetRegistered assetRegistered) {
        return currentState().register(assetRegistered.description(), assetRegistered.location());
    }

    @EventHandler
    public Asset assetMoved(Asset.Event.AssetMoved assetMoved) {
        return currentState().moveTo(assetMoved.location());
    }

    @EventHandler
    public Asset assetDisposed(Asset.Event.AssetDisposed assetDisposed) {
        return currentState().dispose();
    }

    public record RegisterAsset(String description, String location) {}
}