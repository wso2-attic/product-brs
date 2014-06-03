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
package org.wso2.carbon.samples.test;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.axis2.AxisFault;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.xml.stream.XMLStreamException;
import javax.xml.xpath.XPathExpressionException;
import java.io.ByteArrayInputStream;
import java.io.File;

import static org.testng.Assert.assertNotNull;

public class CarRentalServiceTestCase extends BrsMaterTestCase {

    private static final Log log = LogFactory.getLog(CarRentalServiceTestCase.class);


    @BeforeClass(groups = {"wso2.brs"})
    public void login() throws Exception {
        init();

    }

    @Test(groups = {"wso2.brs"})
    public void uploadCarRentalService() throws Exception {
        String samplesDir = System.getProperty("samples.dir");
        String CarRentalServiceAAR = samplesDir + File.separator + "carrental.service/service/target/CarRentalService.aar";
        log.info(CarRentalServiceAAR);
        FileDataSource fileDataSource = new FileDataSource(CarRentalServiceAAR);
        DataHandler dataHandler = new DataHandler(fileDataSource);
        getRuleServiceFileUploadAdminStub().uploadService("CarRentalService.aar", dataHandler);

    }

    @Test(groups = {"wso2.brs"}, dependsOnMethods = {"uploadCarRentalService"})
    public void testCarRental() throws AxisFault, XMLStreamException, XPathExpressionException {

        waitForProcessDeployment(getContext().getContextUrls().getServiceUrl() + "/CarRentalService");
        ServiceClient serviceClient = getClient();
        Options options = new Options();
        options.setTo(new EndpointReference(getContext().getContextUrls().getServiceUrl() + "/CarRentalService"));
        options.setAction("urn:rent");
        serviceClient.setOptions(options);

        OMElement result = serviceClient.sendReceive(createPayload());
        assertNotNull(result, "Result cannot be null");
    }

    private OMElement createPayload() throws XMLStreamException {
        String request = "<p:rentRequest xmlns:p=\"http://brs.carbon.wso2.org\">\n" +
                "   <!--Zero or more repetitions:-->\n" +
                "   <p:Reservation>\n" +
                "      <!--Zero or 1 repetitions:-->\n" +
                "      <xs:MPD xmlns:xs=\"http://carrental.samples/xsd\">18.5</xs:MPD>\n" +
                "      <!--Zero or 1 repetitions:-->\n" +
                "      <xs:type xmlns:xs=\"http://carrental.samples/xsd\">unlimited</xs:type>\n" +
                "   </p:Reservation>\n" +
                "</p:rentRequest>";
        return new StAXOMBuilder(new ByteArrayInputStream(request.getBytes())).getDocumentElement();
    }
}
