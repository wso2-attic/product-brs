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
import org.wso2.carbon.samples.test.shoppingService.product.AddProduct;
import org.wso2.carbon.samples.test.shoppingService.product.Product;
import org.wso2.carbon.samples.test.shoppingService.purchaseOrder.Discount;
import org.wso2.carbon.samples.test.shoppingService.purchaseOrder.Purchase;
import org.wso2.carbon.samples.test.shoppingService.purchaseOrder.PurchaseOrder;
import org.wso2.carbon.samples.test.shoppingService.stub.ShoppingServiceStub;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.xml.stream.XMLStreamException;
import java.io.ByteArrayInputStream;
import java.io.File;

import static org.testng.Assert.assertNotNull;

public class ShoppingServiceTestCase extends BRSMasterTest {

    private static final Log log = LogFactory.getLog(ShoppingServiceTestCase.class);
    ServiceClient serviceClient;
    RequestSender requestSender;

    @BeforeClass(groups = {"wso2.brs"})
    public void login() throws Exception {
        init();
        requestSender = new RequestSender();
    }

    @Test(groups = {"wso2.brs"})
    public void uploadShoppingService() throws Exception {
        String samplesDir = System.getProperty("samples.dir");
        String ShoppingServiceAAR = samplesDir + File.separator + "shopping.service/service/target/ShoppingService.aar";
        log.info(ShoppingServiceAAR);
        FileDataSource fileDataSource = new FileDataSource(ShoppingServiceAAR);
        DataHandler dataHandler = new DataHandler(fileDataSource);
        getRuleServiceFileUploadClient().uploadService("ShoppingService.aar", dataHandler);

    }

    @Test(groups = {"wso2.brs"}, dependsOnMethods = {"uploadShoppingService"})
    public void testAddProduct() throws Exception {

        requestSender.waitForProcessDeployment(getContext().getContextUrls().getServiceUrl() + "/ShoppingService");
        serviceClient = new ServiceClient();
        Options options = new Options();
        options.setTo(new EndpointReference(getContext().getContextUrls().getServiceUrl() + "/ShoppingService"));
        options.setAction("urn:addProduct");
        serviceClient.setOptions(options);

        serviceClient.sendRobust(createAddProductPayload());
    }

    @Test(groups = {"wso2.brs"}, dependsOnMethods = {"testAddProduct"})
    public void testPurchase() throws Exception {

        requestSender.waitForProcessDeployment(getContext().getContextUrls().getServiceUrl() + "/ShoppingService");
        serviceClient = new ServiceClient();
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
                "      <xs:name xmlns:xs=\"http://shopping.brs/xsd\">Pencil Box</xs:name>\n" +
                "      <!--Zero or 1 repetitions:-->\n" +
                "      <xs:price xmlns:xs=\"http://shopping.brs/xsd\">156.55</xs:price>\n" +
                "   </p:Product>\n" +
                "</p:addProductRequest>";
        return new StAXOMBuilder(new ByteArrayInputStream(request.getBytes())).getDocumentElement();
    }

    private OMElement createPurchasePayload() throws XMLStreamException {
        String request = "<p:purchaseRequest xmlns:p=\"http://brs.carbon.wso2.org\">\n" +
                "   <!--Zero or more repetitions:-->\n" +
                "   <p:Purchase>\n" +
                "      <!--Zero or 1 repetitions:-->\n" +
                "      <xs:customer xmlns:xs=\"http://shopping.brs/xsd\">shammi</xs:customer>\n" +
                "      <!--Zero or 1 repetitions:-->\n" +
                "      <xs:product xmlns:xs=\"http://shopping.brs/xsd\">Pencil Box</xs:product>\n" +
                "   </p:Purchase>\n" +
                "</p:purchaseRequest>";
        return new StAXOMBuilder(new ByteArrayInputStream(request.getBytes())).getDocumentElement();
    }

    @Test(groups = {"wso2.brs"}, dependsOnMethods = {"uploadShoppingService"})
    public void testShoppingServicec() throws Exception {

        ShoppingServiceStub shoppingServiceStub = null;
        requestSender.waitForProcessDeployment(getContext().getContextUrls().getServiceUrl() + "/ShoppingService");
        shoppingServiceStub = new ShoppingServiceStub(getContext().getContextUrls().getServiceUrl() + "/ShoppingService");
        shoppingServiceStub._getServiceClient().getOptions().setManageSession(true);

        AddProduct addProduct = new AddProduct();
        Product product = new Product();
        product.setName("toy");
        product.setPrice(200);
        Product[] products = new Product[1];
        products[0] = product;

        // addProduct.setAddProduct(products);
        shoppingServiceStub.addProduct(products);

        PurchaseOrder purchaseOrder = new PurchaseOrder();
        Purchase purchase = new Purchase();
        purchase.setCustomer("Waruna");
        purchase.setProduct("toy");

        Purchase[] purchases = new Purchase[1];
        purchases[0] = purchase;


        purchaseOrder.setPurchase(purchases);
        // shoppingServiceStub.


        Discount[] discounts = shoppingServiceStub.purchase(purchases);
        int result = discounts[0].getAmount();
        assertNotNull(result, "Result cannot be null");

    }

}
