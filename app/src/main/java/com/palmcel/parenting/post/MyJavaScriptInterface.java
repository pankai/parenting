package com.palmcel.parenting.post;

import android.webkit.JavascriptInterface;

import com.google.common.collect.Lists;
import com.palmcel.parenting.common.ExecutorUtil;
import com.palmcel.parenting.common.Log;
import com.palmcel.parenting.model.ImageUrlsRetrievalResultEvent;
import com.palmcel.parenting.model.ProductPageInfo;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;

/**
 * An instance of this class will be registered as a JavaScript interface
 * See: http://stackoverflow.com/questions/2376471/how-do-i-get-the-web-page-contents-from-a-webview
 */
public class MyJavaScriptInterface {

    private static final String TAG = "MyJavaScriptInterface";

    private String mProductWebPageUrl;

    public String getProductWebPageUrl() {
        return mProductWebPageUrl;
    }

    public void setProductWebPageUrl(String productWebPageUrl) {
        mProductWebPageUrl = productWebPageUrl;
    }

    @JavascriptInterface
    @SuppressWarnings("unused")
    public void processHTML(final String html) {
        ExecutorUtil.execute(new Runnable() {

            @Override
            public void run() {
                Document doc = Jsoup.parse(html);
                Elements images = doc.select("img");
                ArrayList<String> urlList = Lists.newArrayList();
                int count = 0;
                for (Element el : images) {
                    String imageUrl = el.attr("src").trim();
                    Log.d(TAG, "processHTML, imageUrl=" + imageUrl);
                    if (!imageUrl.startsWith("http://") && !imageUrl.startsWith("https://")) {
                        // Ignore relative urls
                        continue;
                    }
                    urlList.add(imageUrl);
                    if (++count == 6) {
                        break;
                    }
                }

                String title = doc.title();
                String description = null;
                for(Element meta : doc.select("meta")) {
                    if ("description".equals(meta.attr("name"))) {
                        description = meta.attr("content");
                        break;
                    }
                }

                ProductPageInfo productPageInfo = new ProductPageInfo(mProductWebPageUrl,
                        title, description, urlList);
                EventBus.getDefault().post(
                        new ImageUrlsRetrievalResultEvent(productPageInfo));
            }
        });
    }
}
