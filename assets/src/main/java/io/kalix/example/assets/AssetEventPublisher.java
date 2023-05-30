package io.kalix.example.assets;

import io.kalix.example.assets.domain.Asset;
import kalix.javasdk.action.Action;
import kalix.javasdk.annotations.Acl;
import kalix.javasdk.annotations.Publish;
import kalix.javasdk.annotations.Subscribe;

@Subscribe.EventSourcedEntity(AssetEntity.class)
@Publish.Stream(id = "asset-events")
@Acl(allow = @Acl.Matcher(service = "locations"))
public class AssetEventPublisher extends Action {
    public Effect<Asset.Event.AssetRegistered> onEvent(Asset.Event.AssetRegistered registered) {
        return effects().reply(registered);
    }

    public Effect<Asset.Event.AssetMoved> onEvent(Asset.Event.AssetMoved moved) {
        return effects().reply(moved);
    }

    public Effect<Asset.Event.AssetDisposed> onEvent(Asset.Event.AssetDisposed disposed) {
        return effects().reply(disposed);
    }
}