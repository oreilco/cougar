/*
 * Copyright 2014, Simon Matić Langford
 * Copyright 2014, The Sporting Exchange Limited
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

package com.betfair.cougar.tests.updatedcomponenttests.uuid;

import com.betfair.testing.utils.cougar.CougarBaseline2_8TestingInvoker;
import com.betfair.testing.utils.cougar.enums.CougarMessageContentTypeEnum;
import com.betfair.testing.utils.cougar.manager.LogTailer;
import com.betfair.testing.utils.cougar.manager.RequestLogTailer;
import org.testng.Assert;
import org.testng.annotations.Test;

public class JsonRpcUuidTest {

    @Test
    public void noParentUuid() {
        LogTailer.LogLine[] requestLogEntries =
                CougarBaseline2_8TestingInvoker.create()
                .setOperation("testSimpleGet")
                .addJsonRpcMethodCall("1","testSimpleGet","[\"foo\"]")
                .setExpectedHttpResponse(200, "OK")
                .addJsonRpcExpectedResponse("{\"id\":1,\"result\":{\"message\":\"foo\"},\"jsonrpc\":\"2.0\"}")
                .makeJsonRpcCalls()
                .verify()
                .getRequestLogEntries();
        String uuid0 = (String) requestLogEntries[0].getFields().get(RequestLogTailer.REQUEST_UUID);
        Assert.assertFalse(uuid0.contains(":"));
    }
    @Test
    public void withOldUuid() {
        LogTailer.LogLine[] requestLogEntries =
                CougarBaseline2_8TestingInvoker.create()
                .setOperation("testSimpleGet")
                .addJsonRpcMethodCall("1","testSimpleGet","[\"foo\"]")
                .addHeaderParam("X-UUID", "localhost-abc123-00001")
                .setExpectedHttpResponse(200, "OK")
                .makeJsonRpcCalls()
                .verify()
                .getRequestLogEntries();
        String uuid0 = (String) requestLogEntries[0].getFields().get(RequestLogTailer.REQUEST_UUID);
        Assert.assertEquals(uuid0,"localhost-abc123-00001");
    }
    @Test
    public void withParentUuid() {
        LogTailer.LogLine[] requestLogEntries =
                CougarBaseline2_8TestingInvoker.create()
                .setOperation("testSimpleGet") // todo: this is not right
                .addJsonRpcMethodCall("1","testSimpleGet","[\"foo\"]")
                .addHeaderParam("X-UUID", "localhost-abc123-00001")
                .addHeaderParam("X-UUID-Parents", "root-abcdef123-00001:prev-abcdef123-00001")
                .setExpectedHttpResponse(200, "OK")
                .makeJsonRpcCalls()
                .verify()
                .getRequestLogEntries();

        String expectedUuid = "root-abcdef123-00001:prev-abcdef123-00001:localhost-abc123-00001";

        String uuid0 = (String) requestLogEntries[0].getFields().get(RequestLogTailer.REQUEST_UUID);
        Assert.assertEquals(uuid0,expectedUuid);
    }

}
