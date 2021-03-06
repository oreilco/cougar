/*
 * Copyright 2013, The Sporting Exchange Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

// Originally from UpdatedComponentTests/AcceptProtocols/Rest/Rest_Get_XMLRequest_UnsupportedAcceptProtocol.xls;
package com.betfair.cougar.tests.updatedcomponenttests.acceptprotocols.rest;

import com.betfair.testing.utils.cougar.misc.XMLHelpers;
import com.betfair.testing.utils.cougar.assertions.AssertionUtils;
import com.betfair.testing.utils.cougar.beans.HttpCallBean;
import com.betfair.testing.utils.cougar.beans.HttpResponseBean;
import com.betfair.testing.utils.cougar.enums.CougarMessageProtocolRequestTypeEnum;
import com.betfair.testing.utils.cougar.manager.AccessLogRequirement;
import com.betfair.testing.utils.cougar.manager.CougarManager;

import org.testng.annotations.Test;
import org.w3c.dom.Document;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

/**
 * Ensure that when a Rest XML Get is performed on Cougar, specifying an unsupported response protocol, the correct error response is generated
 */
public class RestGetXMLRequestUnsupportedAcceptProtocolTest {
    @Test
    public void doTest() throws Exception {
        // Create the HttpCallBean
        CougarManager cougarManager1 = CougarManager.getInstance();
        HttpCallBean httpCallBeanBaseline = cougarManager1.getNewHttpCallBean();
        CougarManager cougarManagerBaseline = cougarManager1;
        // Get the cougar logging attribute for getting log entries later
        // Point the created HttpCallBean at the correct service
        httpCallBeanBaseline.setServiceName("baseline", "cougarBaseline");

        httpCallBeanBaseline.setVersion("v2");
        // Set up the Http Call Bean to make the request
        CougarManager cougarManager2 = CougarManager.getInstance();
        HttpCallBean getNewHttpCallBean2 = cougarManager2.getNewHttpCallBean("87.248.113.14");
        cougarManager2 = cougarManager2;

        cougarManager2.setCougarFaultControllerJMXMBeanAttrbiute("DetailedFaults", "false");

        getNewHttpCallBean2.setOperationName("testSimpleGet", "simple");

        getNewHttpCallBean2.setServiceName("baseline", "cougarBaseline");

        getNewHttpCallBean2.setVersion("v2");

        Map map3 = new HashMap();
        map3.put("message","foo");
        getNewHttpCallBean2.setQueryParams(map3);
        // Set the response protocols (with an unsupported protocol ranked highest)
        Map map4 = new HashMap();
        map4.put("application/pdf","q=70");
        getNewHttpCallBean2.setAcceptProtocols(map4);
        // Get current time for getting log entries later

        Timestamp getTimeAsTimeStamp10 = new Timestamp(System.currentTimeMillis());
        // Make the XML call to the operation
        cougarManager2.makeRestCougarHTTPCall(getNewHttpCallBean2, com.betfair.testing.utils.cougar.enums.CougarMessageProtocolRequestTypeEnum.RESTXML);
        // Create the expected response as an XML document
        XMLHelpers xMLHelpers6 = new XMLHelpers();
        Document createAsDocument12 = xMLHelpers6.getXMLObjectFromString("<fault><faultcode>Client</faultcode><faultstring>DSC-0013</faultstring><detail/></fault>");
        // Convert the expected response to JSON
        Map<CougarMessageProtocolRequestTypeEnum, Object> convertResponseToRestTypes13 = cougarManager2.convertResponseToRestTypes(createAsDocument12, getNewHttpCallBean2);
        // Check the response is as expected (fault)
        HttpResponseBean getResponseObjectsByEnum14 = getNewHttpCallBean2.getResponseObjectsByEnum(com.betfair.testing.utils.cougar.enums.CougarMessageProtocolResponseTypeEnum.REST);
        AssertionUtils.multiAssertEquals(convertResponseToRestTypes13.get(CougarMessageProtocolRequestTypeEnum.RESTXML), getResponseObjectsByEnum14.getResponseObject());
        AssertionUtils.multiAssertEquals((int) 406, getResponseObjectsByEnum14.getHttpStatusCode());
        AssertionUtils.multiAssertEquals("Not Acceptable", getResponseObjectsByEnum14.getHttpStatusText());

        Map<String, String> map8 = getResponseObjectsByEnum14.getFlattenedResponseHeaders();
        AssertionUtils.multiAssertEquals("application/xml", map8.get("Content-Type"));
        // Check the log entries are as expected

        cougarManagerBaseline.verifyAccessLogEntriesAfterDate(getTimeAsTimeStamp10, new AccessLogRequirement("87.248.113.14", "/cougarBaseline/v2/simple", "MediaTypeNotAcceptable") );
    }

}
