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

public class ShoppingServiceTestCase extends BrsMaterTestCase {

    private static final Log log = LogFactory.getLog(ShoppingServiceTestCase.class);


    @BeforeClass(groups = {"wso2.brs"})
    public void login() throws Exception {
        init();

    }

    @Test(groups = {"wso2.brs"})
    public void uploadShoppingService() throws Exception {
        String samplesDir = System.getProperty("samples.dir");
        String ShoppingServiceAAR = samplesDir + File.separator + "shopping.service/service/target/ShoppingService.aar";
        log.info(ShoppingServiceAAR);
        FileDataSource fileDataSource = new FileDataSource(ShoppingServiceAAR);
        DataHandler dataHandler = new DataHandler(fileDataSource);
        getRuleServiceFileUploadAdminStub().uploadService("ShoppingService.aar", dataHandler);

    }

    @Test(groups = {"wso2.brs"}, dependsOnMethods = {"uploadShoppingService"})
    public void testAddProduct() throws XMLStreamException, AxisFault, XPathExpressionException {
        
        waitForProcessDeployment(getContext().getContextUrls().getServiceUrl() + "/ShoppingService");
        ServiceClient serviceClient = getClient();
        Options options = new Options();
        options.setTo(new EndpointReference(getContext().getContextUrls().getServiceUrl() +"/ShoppingService"));
        options.setAction("urn:addProduct");
        serviceClient.setOptions(options);

        serviceClient.sendRobust(createAddProductPayload());
    }

    @Test(groups = {"wso2.brs"}, dependsOnMethods = {"testAddProduct"})
    public void testPurchase() throws XMLStreamException, AxisFault, XPathExpressionException {

        waitForProcessDeployment(getContext().getContextUrls().getServiceUrl() + "/ShoppingService");
        ServiceClient serviceClient = getClient();
        Options options = new Options();
        options.setTo(new EndpointReference("http://localhost:9763/services/ShoppingService"));
        options.setAction("urn:purchase");
        serviceClient.setOptions(options);

        OMElement result = serviceClient.sendReceive(createPurchasePayload());
        assertNotNull(result, "Result cannot be null");
    }

    private OMElement createAddProductPayload() throws XMLStreamException {
        String request = "<p:addProductRequest xmlns:p=\"http://brs.carbon.wso2.org\">\n" +
                "   <!--Zero or more repetitions:-->\n" +
                "   <p:Product>\n" +
                "      <!--Zero or 1 repetitions:-->\n" +
                "      <xs:name xmlns:xs=\"http://shopping.samples/xsd\">Pencil Box</xs:name>\n" +
                "      <!--Zero or 1 repetitions:-->\n" +
                "      <xs:price xmlns:xs=\"http://shopping.samples/xsd\">156.55</xs:price>\n" +
                "   </p:Product>\n" +
                "</p:addProductRequest>";
        return new StAXOMBuilder(new ByteArrayInputStream(request.getBytes())).getDocumentElement();
    }

    private OMElement createPurchasePayload() throws XMLStreamException {
        String request = "<p:purchaseRequest xmlns:p=\"http://brs.carbon.wso2.org\">\n" +
                "   <!--Zero or more repetitions:-->\n" +
                "   <p:Purchase>\n" +
                "      <!--Zero or 1 repetitions:-->\n" +
                "      <xs:customer xmlns:xs=\"http://shopping.samples/xsd\">shammi</xs:customer>\n" +
                "      <!--Zero or 1 repetitions:-->\n" +
                "      <xs:product xmlns:xs=\"http://shopping.samples/xsd\">Pencil Box</xs:product>\n" +
                "   </p:Purchase>\n" +
                "</p:purchaseRequest>";
        return new StAXOMBuilder(new ByteArrayInputStream(request.getBytes())).getDocumentElement();
    }

}
