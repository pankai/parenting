package com.palmcel.parenting.post;

import java.util.ArrayList;

/**
 * EventBus event for image urls retrieval result
 */
public class ImageUrlsRetrievalResultEvent {
    ArrayList<String> imageUrls;

    public ImageUrlsRetrievalResultEvent(ArrayList<String> imageUrls) {
        this.imageUrls = imageUrls;
    }
}
