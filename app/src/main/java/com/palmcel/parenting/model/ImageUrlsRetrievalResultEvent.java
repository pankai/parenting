package com.palmcel.parenting.model;

import com.palmcel.parenting.model.ProductPageInfo;

/**
 * EventBus event for image urls retrieval result
 */
public class ImageUrlsRetrievalResultEvent {
    public ProductPageInfo productPageInfo;

    public ImageUrlsRetrievalResultEvent(ProductPageInfo productPageInfo) {
        this.productPageInfo = productPageInfo;
    }
}
