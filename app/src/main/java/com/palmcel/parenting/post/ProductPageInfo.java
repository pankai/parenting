package com.palmcel.parenting.post;

import android.support.annotation.Nullable;

import com.google.common.base.MoreObjects;

import java.io.Serializable;
import java.util.List;

/**
 * Product page info
 */
public class ProductPageInfo implements Serializable {

    private static final long serialVersionUID = 0L;

    String productPageUrl;
    String title;
    String description;
    List<String> productPictureUrls;

    public ProductPageInfo(
            String productPageUrl,
            @Nullable String title,
            @Nullable String description,
            List<String> productPictureUrls) {
        this.productPageUrl = productPageUrl;
        this.title = title;
        this.description = description;
        this.productPictureUrls = productPictureUrls;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this.getClass())
            .add("productPageUrl", productPageUrl)
            .add("title", title)
            .add("description", description)
            .add("productPictureUrls", productPictureUrls)
            .toString();
    }
}
