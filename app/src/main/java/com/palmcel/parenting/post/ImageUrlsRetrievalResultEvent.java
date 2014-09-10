package com.palmcel.parenting.post;

import java.util.ArrayList;

/**
 * EventBus event for image urls retrieval result
 */
public class ImageUrlsRetrievalResultEvent {
    ProductPageInfo productPageInfo;

    public ImageUrlsRetrievalResultEvent(ProductPageInfo productPageInfo) {
        this.productPageInfo = productPageInfo;
    }
}
