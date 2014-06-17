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
package org.wso2.carbon.samples;
import org.apache.axis2.AxisFault;
import org.wso2.carbon.samples.shoppingService.product.AddProduct;
import org.wso2.carbon.samples.shoppingService.product.Product;
import org.wso2.carbon.samples.shoppingService.purchaseOrder.Discount;
import org.wso2.carbon.samples.shoppingService.purchaseOrder.Purchase;
import org.wso2.carbon.samples.shoppingService.purchaseOrder.PurchaseOrder;
import org.wso2.carbon.samples.shoppingService.stub.ShoppingServiceStub;

import java.rmi.RemoteException;

public class ShoppingServiceTestCase {

    public static void main(String[] args) {

        ShoppingServiceStub shoppingServiceStub = null;
        try {
            shoppingServiceStub = new ShoppingServiceStub("http://localhost:9763/services/ShoppingService");
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
            purchase.setCustomer("Ishara");
            purchase.setProduct("toy");

            Purchase[] purchases = new Purchase[1];
            purchases[0] = purchase;


            purchaseOrder.setPurchase(purchases);
            // shoppingServiceStub.


            Discount[] discounts = shoppingServiceStub.purchase(purchases);
            int result = discounts[0].getAmount();
            System.out.println(result);

        } catch (AxisFault axisFault) {
            axisFault.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
