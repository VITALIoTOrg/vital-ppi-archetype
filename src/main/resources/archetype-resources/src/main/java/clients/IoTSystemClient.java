package eu.vital.ppi.clients;

import eu.vital.ppi.utils.HttpCommonClient;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URI;

public class IoTSystemClient
{
    private HttpCommonClient httpCC;

    public IoTSystemClient() {
        httpCC = HttpCommonClient.getInstance();
    }

    // TODO: make it generic, support other HTTP methods
    private String performRequest(URI uri) throws ClientProtocolException, IOException {
    	String response = null;
    	int code;

    	HttpGet get = new HttpGet(uri);
    	get.setConfig(RequestConfig.custom().setConnectionRequestTimeout(3000).setConnectTimeout(3000).setSocketTimeout(10000).build());

        CloseableHttpResponse resp;
        try {
            resp = httpCC.httpc.execute(get);
            code = resp.getStatusLine().getStatusCode();
            if(code >= 200 && code <= 299) {
            	response = EntityUtils.toString(resp.getEntity());
            }
            resp.close();
        } catch (IOException e) {
            try {
            	try {
					Thread.sleep(1000);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
            	get.setConfig(RequestConfig.custom().setConnectionRequestTimeout(7000).setConnectTimeout(7000).setSocketTimeout(20000).build());
                resp = httpCC.httpc.execute(get);
                code = resp.getStatusLine().getStatusCode();
                if(code >= 200 && code <= 299) {
                	response = EntityUtils.toString(resp.getEntity());
                }
                resp.close();
            } catch (IOException ea) {
            	try {
					Thread.sleep(1000);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
            	get.setConfig(RequestConfig.custom().setConnectionRequestTimeout(12000).setConnectTimeout(12000).setSocketTimeout(1800000).build());
                resp = httpCC.httpc.execute(get);
                code = resp.getStatusLine().getStatusCode();
                if(code >= 200 && code <= 299) {
                	response = EntityUtils.toString(resp.getEntity());
                }
                resp.close();
            }
        }

    	return response;
    }

    /*
     * TODO: in this class you must implement the methods to retrieve the data
     * from the IoT system(s) and return it in appropriate objects.
     *
     * The following examples are taken from the "vital-ppi-citybikes":

        public CityBikesNetwork getNetwork(String apiBasePath, String networkId) {
            URI uri;
            String respString = null;
            CityBikesNetwork network = null;

            try {
                uri = new URI(apiBasePath + "/" + networkId);
                try {
                    respString = performRequest(uri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if(respString != null) {
                    try {
                        network = (CityBikesNetwork) JsonUtils.deserializeJson(respString, CityBikesNetwork.class);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }

            return network;
        }

        public CityBikesNetworks getNetworks(String apiBasePath) {
            URI uri;
            String respString = null;
            CityBikesNetworks networks = null;

            try {
                uri = new URI(apiBasePath);
                try {
                    respString = performRequest(uri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if(respString != null) {
                    try {
                        networks = (CityBikesNetworks) JsonUtils.deserializeJson(respString, CityBikesNetworks.class);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }

            return networks;
        }
     */
}

