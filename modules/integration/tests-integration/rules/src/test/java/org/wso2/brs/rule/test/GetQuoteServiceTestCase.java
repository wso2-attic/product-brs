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
import org.wso2.carbon.samples.test.quoteService.customerDetail.Customer;
import org.wso2.carbon.samples.test.quoteService.customerDetail.PlaceCustomerDetail;
import org.wso2.carbon.samples.test.quoteService.customerDetail.Quotation;
import org.wso2.carbon.samples.test.quoteService.stub.GetQuoteServiceStub;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.xml.stream.XMLStreamException;
import javax.xml.xpath.XPathExpressionException;
import java.io.ByteArrayInputStream;
import java.io.File;

import static org.testng.Assert.assertNotNull;

public class GetQuoteServiceTestCase extends BRSMasterTest {

    private static final Log log = LogFactory.getLog(GetQuoteServiceTestCase.class);
    ServiceClient serviceClient;
    RequestSender requestSender;

    @BeforeClass(groups = {"wso2.brs"})
    public void login() throws Exception {
        init();
        requestSender = new RequestSender();
    }

    @Test(groups = {"wso2.brs"})
    public void uploadGetQuoteService() throws Exception {
        String samplesDir = System.getProperty("samples.dir");
        String GetQuoteServiceAAR = samplesDir + File.separator + "quotation.service/service/target/GetQuoteService.aar";
        log.info(GetQuoteServiceAAR);
        FileDataSource fileDataSource = new FileDataSource(GetQuoteServiceAAR);
        DataHandler dataHandler = new DataHandler(fileDataSource);
        getRuleServiceFileUploadClient().uploadService("GetQuoteService.aar", dataHandler);

    }

    @Test(groups = {"wso2.brs"}, dependsOnMethods = {"uploadGetQuoteService"})
    public void testGetQuote() throws Exception {

        requestSender.waitForProcessDeployment(getContext().getContextUrls().getServiceUrl() + "/GetQuoteService");
        ServiceClient serviceClient = new ServiceClient();
        Options options = new Options();
        options.setTo(new EndpointReference(getContext().getContextUrls().getServiceUrl() + "/GetQuoteService"));
        options.setAction("urn:getQuote");
        serviceClient.setOptions(options);

        OMElement result = serviceClient.sendReceive(createPayload());
        assertNotNull(result, "Result cannot be null");
    }

    private OMElement createPayload() throws XMLStreamException {
        String request = "<p:getQuoteRequest xmlns:p=\"http://brs.carbon.wso2.org\">\n" +
                "   <!--Zero or more repetitions:-->\n" +
                "   <p:Customer>\n" +
                "      <!--Zero or 1 repetitions:-->\n" +
                "      <xs:status xmlns:xs=\"http://quotation.brs/xsd\">bronze</xs:status>\n" +
                "   </p:Customer>\n" +
                "</p:getQuoteRequest>";
        return new StAXOMBuilder(new ByteArrayInputStream(request.getBytes())).getDocumentElement();
    }

    @Test(groups = {"wso2.brs"}, dependsOnMethods = {"uploadGetQuoteService"})
    public void testGetQuoteStub() throws Exception {

        requestSender.waitForProcessDeployment(getContext().getContextUrls().getServiceUrl() + "/GetQuoteService");
        GetQuoteServiceStub getQuoteServiceStub = new GetQuoteServiceStub(getContext().getContextUrls().getServiceUrl() + "/GetQuoteService");

        PlaceCustomerDetail placeCustomerDetail = new PlaceCustomerDetail();
        Customer customer = new Customer();
        customer.setStatus("gold");
        Customer[] customers = new Customer[1];
        customers[0] = customer;
        placeCustomerDetail.setCustomerDetail(customers);

        Quotation[] quotations = getQuoteServiceStub.getQuote(customers);
        int result = quotations[0].getPrice();
        assertNotNull(result, "Result cannot be null");


    }
}
