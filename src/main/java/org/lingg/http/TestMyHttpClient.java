package org.lingg.http;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class TestMyHttpClient {

    @Test
    public void testMyHttpClient() throws Exception{

        String url  = "https://test-fls-aflm.pingan.com.cn/awf/vservice/UpdateDetail/Invoke.service";
        String encodes  = "%7B%22parameters.common%22%3A%7B%7D%2C%22data.app.carFinance%22%3A%7B%22content%22%3A%22fTvzZofegJ4Qzjc6uxIQSN356mEjngY112azxT%2Be7%2ByvQUaV8OGhlFSQwM2eZhxBAwbjvqv%2B3iWYoyQcx6Bmktya3WD3lANaJpc%2BTEDm1Ucqsrii4iCqFf9Be4Tz0pxBTBIbwf%2BhCxn5YTqrdAd26lsNNF2KJrjCCLDKJel0PH7biZYQk5y%2Bv6NyjilcO6VF9viMh5zewzlU6KCXeMercN7VlPczBmEtBjWpAFkR43KHOD5rh17zdERgJXvvfN3zHz0klt6FbTjD6ucJDdb6sNxCb6DQn8IEQBbT0tr4KhrG9vIABZJm%2FNeNaWPSV4JmHmRp9vT71VKrD0a2JMNv4g%3D%3D%22%7D%7D";

        Map<String, Object> map = new HashMap<>();
        map.put("detail", encodes);


        String ret = MyHttpclient.sendRequestNoCheckCerPostMap(url, map);

        System.out.println(ret);
    }
}
