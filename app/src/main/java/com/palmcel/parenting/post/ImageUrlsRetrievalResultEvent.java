package com.palmcel.parenting.post;

import com.palmcel.parenting.model.ProductPageInfo;

/**
 * EventBus event for image urls retrieval result
 */
public class ImageUrlsRetrievalResultEvent {
    ProductPageInfo productPageInfo;

    public ImageUrlsRetrievalResultEvent(ProductPageInfo productPageInfo) {
        this.productPageInfo = productPageInfo;
    }
}
