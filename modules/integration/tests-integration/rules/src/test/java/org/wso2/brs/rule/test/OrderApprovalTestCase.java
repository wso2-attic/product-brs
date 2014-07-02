/*
*  Copyright (c) WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*  WSO2 Inc. licenses this file to you under the Apache License,
*  Version 2.0 (the "License"); you may not use this file except
*  in compliance with the License.
*  You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/
package org.wso2.brs.rule.test;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.brs.integration.common.utils.BRSMasterTest;
import org.wso2.brs.integration.common.utils.RequestSender;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.xml.stream.XMLStreamException;
import java.io.ByteArrayInputStream;
import java.io.File;

import static org.testng.Assert.assertNotNull;

public class OrderApprovalTestCase extends BRSMasterTest {

    private static final Log log = LogFactory.getLog(OrderApprovalTestCase.class);
    ServiceClient serviceClient;
    RequestSender requestSender;

    @BeforeClass(groups = {"wso2.brs"})
    public void login() throws Exception {
        init();
        requestSender = new RequestSender();
    }

    @Test(groups = {"wso2.brs"})
    public void uploadOrderApprovalService() throws Exception {
        String samplesDir = System.getProperty("samples.dir");
        String OrderApprovalServiceAAR = samplesDir + File.separator + "orderApproval.service/service/target/OrderApprovalService.aar";
        log.info(OrderApprovalServiceAAR);
        FileDataSource fileDataSource = new FileDataSource(OrderApprovalServiceAAR);
        DataHandler dataHandler = new DataHandler(fileDataSource);
        getRuleServiceFileUploadClient().uploadService("OrderApprovalService.aar", dataHandler);

    }

    @Test(groups = {"wso2.brs"}, dependsOnMethods = {"uploadOrderApprovalService"})
    public void testPlaceOrder() throws Exception {

        requestSender.waitForProcessDeployment(getContext().getContextUrls().getServiceUrl() + "/OrderApprovalService");
        serviceClient = new ServiceClient();
        Options options = new Options();
        options.setTo(new EndpointReference(getContext().getContextUrls().getServiceUrl() + "/OrderApprovalService"));
        options.setAction("urn:placeOrder");
        serviceClient.setOptions(options);

        OMElement result = serviceClient.sendReceive(createPayload());
        assertNotNull(result, "Result cannot be null");
    }

    private OMElement createPayload() throws XMLStreamException {
        String request = "<p:placeOrderRequest xmlns:p=\"http://brs.carbon.wso2.org\">\n" +
                "   <!--Zero or more repetitions:-->\n" +
                "   <p:PlaceOrder>\n" +
                "      <!--Zero or 1 repetitions:-->\n" +
                "      <xs:price xmlns:xs=\"http://userguide.brs/xsd\">14</xs:price>\n" +
                "      <!--Zero or 1 repetitions:-->\n" +
                "      <xs:quantity xmlns:xs=\"http://userguide.brs/xsd\">223</xs:quantity>\n" +
                "      <!--Zero or 1 repetitions:-->\n" +
                "      <xs:symbol xmlns:xs=\"http://userguide.brs/xsd\">IBM</xs:symbol>\n" +
                "   </p:PlaceOrder>\n" +
                "</p:placeOrderRequest>";
        return new StAXOMBuilder(new ByteArrayInputStream(request.getBytes())).getDocumentElement();
    }
}
