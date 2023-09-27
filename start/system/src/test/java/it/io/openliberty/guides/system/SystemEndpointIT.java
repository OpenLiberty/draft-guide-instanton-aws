// tag::copyright[]
/*******************************************************************************
 * Copyright (c) 2018, 2022 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
// end::copyright[]

package it.io.openliberty.guides.system;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.Response;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public class SystemEndpointIT {

    private static String clusterUrl;
    private ResteasyClient client;
    private Response response;
    private SSLContext sslContext;

    @BeforeAll
    public static void oneTimeSetup() {
        String clusterIp = System.getProperty("cluster.ip");
        String nodePort = System.getProperty("system.node.port");
        clusterUrl = "https://" + clusterIp + ":" + nodePort + "/system/properties/";
    }

    @BeforeEach
    public void setup() { 
        response=null;
        try {
            X509TrustManager customTrustManager = new X509TrustManager() {
                @Override
                public void checkClientTrusted(X509Certificate[] chain, String authType)
                    throws CertificateException { }

                @Override
                public void checkServerTrusted(X509Certificate[] chain, String authType)
                    throws CertificateException { }

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
            };
            sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, new TrustManager[] { customTrustManager }, null);

        } catch (Exception e) {
            e.printStackTrace();
        }
        ClientBuilder builder = ResteasyClientBuilder.newBuilder();
        builder.sslContext(sslContext).hostnameVerifier(new HostnameVerifier() {
                        public boolean verify(String hostname, SSLSession session) {
                            return true;
                        }
                    });
        client = (ResteasyClient) builder.build();
    }   

    @AfterEach
    public void teardown() {
        client.close();
    }

    @Test
    @Order(1)
    public void testPodNameNotNull() {
        response = this.getResponse();
        this.assertResponse(response);
        String greeting = response.getHeaderString("X-Pod-Name");

        assertNotNull(greeting,
            "Container name should not be null but it was."
            + "The service is probably not running inside a container");
    }

    @Test
    @Order(2)
    public void testGetProperties() {
        response = this.getResponse();
        assertEquals(200, response.getStatus(),
                     "Incorrect response code from " + clusterUrl);
        response.close();
    }

    private Response getResponse(){
        return client.target(clusterUrl).request().header("Host", System.getProperty("host-header")).get();
    }

    private void assertResponse(Response response) {
        assertEquals(200, response.getStatus(),
            "Incorrect response code from " + clusterUrl);
    }
}