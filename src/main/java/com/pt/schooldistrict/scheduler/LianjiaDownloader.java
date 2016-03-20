package com.pt.schooldistrict.scheduler;

import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.downloader.HttpClientDownloader;

/**
 * Created by da.zhang on 16/3/19.
 */
public class LianjiaDownloader extends HttpClientDownloader{



    /**
     * 在遇到exception时会call这个方法
     * @param request
     */
    @Override
    protected void onError(Request request) {
        request

    }


}
