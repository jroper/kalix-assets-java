package io.kalix.example.locations;

import io.kalix.example.locations.domain.Asset;
import kalix.javasdk.annotations.*;
import kalix.javasdk.view.View;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Collection;

@ViewId("view_assets_by_location")
@Table("assets_by_location")
@Subscribe.Stream(service = "assets", id = "asset-events")
public class AssetsByLocation extends View<Asset> {
    public UpdateEffect<Asset> onEvent(Asset.Event.AssetRegistered event) {
        return effects().updateState(Asset.register(
            updateContext().eventSubject().orElse(""),
            event.description(),
            event.location()
        ));
    }

    public UpdateEffect<Asset> onEvent(Asset.Event.AssetMoved event) {
        return effects().updateState(
                viewState().moveTo(event.location())
        );
    }

    public UpdateEffect<Asset> onEvent(Asset.Event.AssetDisposed event) {
        return effects().deleteState();
    }

    public record AssetsResponse(Collection<Asset> assets) { }

    @GetMapping("/location/{location}")
    @Query("""
        SELECT * as assets
        FROM assets_by_location
        WHERE location = :location
            """)
    @Acl(allow = @Acl.Matcher(principal = Acl.Principal.INTERNET))
    public AssetsResponse getAssets(String location) {
        return null;
    }
}