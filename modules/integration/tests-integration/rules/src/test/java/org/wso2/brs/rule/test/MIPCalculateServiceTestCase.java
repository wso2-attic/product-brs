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
import org.wso2.carbon.samples.test.mipCalculateService.clientDetail.Client;
import org.wso2.carbon.samples.test.mipCalculateService.clientDetail.MIP;
import org.wso2.carbon.samples.test.mipCalculateService.clientDetail.PlaceClientDetail;
import org.wso2.carbon.samples.test.mipCalculateService.stub.MIPCalculateServiceStub;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.xml.stream.XMLStreamException;
import java.io.ByteArrayInputStream;
import java.io.File;

import static org.testng.Assert.assertNotNull;

public class MIPCalculateServiceTestCase extends BRSMasterTest {

    private static final Log log = LogFactory.getLog(MIPCalculateServiceTestCase.class);
    ServiceClient serviceClient;
    RequestSender requestSender;

    @BeforeClass(groups = {"wso2.brs"})
    public void login() throws Exception {
        init();
        requestSender = new RequestSender();
    }

    @Test(groups = {"wso2.brs"})
    public void uploadMIPCalculateService() throws Exception {
        String samplesDir = System.getProperty("samples.dir");
        String MIPCalculateServiceAAR = samplesDir + File.separator + "MIPCalculate.service/service/target/MIPCalculateService.aar";
        log.info(MIPCalculateServiceAAR);
        FileDataSource fileDataSource = new FileDataSource(MIPCalculateServiceAAR);
        DataHandler dataHandler = new DataHandler(fileDataSource);
        getRuleServiceFileUploadClient().uploadService("MIPCalculateService.aar", dataHandler);

    }

    @Test(groups = {"wso2.brs"}, dependsOnMethods = {"uploadMIPCalculateService"})
    public void testCalculate() throws Exception {

        requestSender.waitForProcessDeployment(getContext().getContextUrls().getServiceUrl() + "/MIPCalculateService");
        serviceClient = new ServiceClient();
        Options options = new Options();
        options.setTo(new EndpointReference(getContext().getContextUrls().getServiceUrl() + "/MIPCalculateService"));
        options.setAction("urn:calculate");
        serviceClient.setOptions(options);

        OMElement result = serviceClient.sendReceive(createPayload());
        assertNotNull(result, "Result cannot be null");
    }

    private OMElement createPayload() throws XMLStreamException {
        String request = "<p:calculateRequest xmlns:p=\"http://brs.carbon.wso2.org\">\n" +
                "   <!--Zero or more repetitions:-->\n" +
                "   <p:Client>\n" +
                "      <!--Zero or 1 repetitions:-->\n" +
                "      <xs:downPayment xmlns:xs=\"http://MIPCalculate.brs/xsd\">90000</xs:downPayment>\n" +
                "      <!--Zero or 1 repetitions:-->\n" +
                "      <xs:loanType xmlns:xs=\"http://MIPCalculate.brs/xsd\">FHA</xs:loanType>\n" +
                "      <!--Zero or 1 repetitions:-->\n" +
                "      <xs:mortgageValue xmlns:xs=\"http://MIPCalculate.brs/xsd\">100000</xs:mortgageValue>\n" +
                "   </p:Client>\n" +
                "</p:calculateRequest>";
        return new StAXOMBuilder(new ByteArrayInputStream(request.getBytes())).getDocumentElement();
    }

    @Test(groups = {"wso2.brs"}, dependsOnMethods = {"uploadMIPCalculateService"})
    public void testMIPCalculate() throws Exception {

        requestSender.waitForProcessDeployment(getContext().getContextUrls().getServiceUrl() + "/MIPCalculateService");

        MIPCalculateServiceStub mipCalculateServiceStub =
                new MIPCalculateServiceStub(getContext().getContextUrls().getServiceUrl() + "/MIPCalculateService");

        PlaceClientDetail placeClientDetail = new PlaceClientDetail();
        Client client = new Client();
        client.setLoanType("FHA");
        client.setDownPayment(8000);
        client.setMortgageValue(90000);
        Client[] clients = new Client[1];
        clients[0] = client;

        MIP[] mips = mipCalculateServiceStub.calculate(clients);
        double result = mips[0].getAnnualMIP();
        assertNotNull(result, "Result cannot be null");
    }
}
