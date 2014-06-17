/*
*  Licensed to the Apache Software Foundation (ASF) under one
*  or more contributor license agreements.  See the NOTICE file
*  distributed with this work for additional information
*  regarding copyright ownership.  The ASF licenses this file
*  to you under the Apache License, Version 2.0 (the
*  "License"); you may not use this file except in compliance
*  with the License.  You may obtain a copy of the License at
*
*   http://www.apache.org/licenses/LICENSE-2.0
*
*  Unless required by applicable law or agreed to in writing,
*  software distributed under the License is distributed on an
*   * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
*  KIND, either express or implied.  See the License for the
*  specific language governing permissions and limitations
*  under the License.
*/
package org.wso2.carbon.samples.test;

import org.apache.axis2.AxisFault;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.samples.test.shoppingService.product.AddProduct;
import org.wso2.carbon.samples.test.shoppingService.product.Product;
import org.wso2.carbon.samples.test.shoppingService.purchaseOrder.Discount;
import org.wso2.carbon.samples.test.shoppingService.purchaseOrder.Purchase;
import org.wso2.carbon.samples.test.shoppingService.purchaseOrder.PurchaseOrder;
import org.wso2.carbon.samples.test.shoppingService.stub.ShoppingServiceStub;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.xml.xpath.XPathExpressionException;
import java.io.File;
import java.rmi.RemoteException;

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
    public void testShoppingServicec() throws XPathExpressionException {

        ShoppingServiceStub shoppingServiceStub = null;
        waitForProcessDeployment(getContext().getContextUrls().getServiceUrl() + "/ShoppingService");
        try {
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

        } catch (AxisFault axisFault) {
            axisFault.printStackTrace();
            assertNotNull(null);
        } catch (RemoteException e) {
            e.printStackTrace();
            assertNotNull(null);
        }
    }
}
