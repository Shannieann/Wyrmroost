package com.github.shannieann.wyrmroost.client.render;

import com.github.shannieann.wyrmroost.client.model.tome.TarragonTomeModel;
import com.github.shannieann.wyrmroost.item.book.TarragonTomeItem;
import software.bernie.geckolib3.renderers.geo.GeoItemRenderer;


public class TarragonTomeRenderer extends GeoItemRenderer<TarragonTomeItem>
{
    public TarragonTomeRenderer() {
        super(new TarragonTomeModel());
    }

}
