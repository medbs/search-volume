/*package com.sv.score;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class ScoreServiceTest {

    @Test
    public void whenSendPostRequestUsingHttpClient_thenCorrect()
            throws ClientProtocolException, IOException {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost("http://completion.amazon.com/search/complete");

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("search-alias", "aps"));
        params.add(new BasicNameValuePair("client", "amazon-search-ui"));
        params.add(new BasicNameValuePair("mkt", "1"));
        params.add(new BasicNameValuePair("q", "samsung"));
        httpPost.setEntity(new UrlEncodedFormEntity(params));

        CloseableHttpResponse response = client.execute(httpPost);
        assertThat(response.getStatusLine().getStatusCode(), equalTo(200));
        client.close();
    }


    @Test
    public void getSuggestionsAutoComplete()
            throws ClientProtocolException, IOException {
        HttpClient client = HttpClientBuilder.create().setRedirectStrategy(new LaxRedirectStrategy()).build();
        //HttpPost httpPost = new HttpPost("http://completion.amazon.com/search/complete");

        HttpPost httpPost = new HttpPost("http://completion.amazon.com/search/complete?search-alias=aps&client=amazon-search-ui&mkt=1&q=samsung");

        HttpResponse response = client.execute(httpPost);
        //HttpEntity httpEntity = response.getEntity();
        String responseEntity = EntityUtils.toString(response.getEntity());

        assertThat(response.getStatusLine().getStatusCode(), equalTo(200));




    @Test
    public void givenRedirectingPOST_whenConsumingUrlWhichRedirectsWithPOST_thenRedirected()
            throws ClientProtocolException, IOException {
        HttpClient instance =
                HttpClientBuilder.create().setRedirectStrategy(new LaxRedirectStrategy()).build();
        HttpResponse response = instance.execute(new HttpPost("http://completion.amazon.com/search/complete"));
        assertThat(response.getStatusLine().getStatusCode(), equalTo(200));
    }

}
*/