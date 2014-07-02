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
package org.wso2.brs.rule.test;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.brs.integration.common.utils.BRSMasterTest;
import org.wso2.brs.integration.common.utils.RequestSender;
import org.wso2.carbon.samples.test.orderApprovalService.order.OrderAccept;
import org.wso2.carbon.samples.test.orderApprovalService.order.PlaceOrder;
import org.wso2.carbon.samples.test.orderApprovalService.order.PlaceOrderE;
import org.wso2.carbon.samples.test.orderApprovalService.order.PlaceOrderResponse;
import org.wso2.carbon.samples.test.orderApprovalService.stub.OrderApprovalServiceCallbackHandler;
import org.wso2.carbon.samples.test.orderApprovalService.stub.OrderApprovalServiceStub;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import java.io.File;

import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertNotNull;

public class PlaceOrderTestCase extends BRSMasterTest {

    private static final Log log = LogFactory.getLog(PlaceOrderTestCase.class);
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

        OrderApprovalServiceStub orderApprovalServiceStub =
                new OrderApprovalServiceStub(getContext().getContextUrls().getServiceUrl() + "/OrderApprovalService");

        PlaceOrderE placeOrderRequest = new PlaceOrderE();
        PlaceOrder placeOrder = new PlaceOrder();
        placeOrder.setPrice(2);
        placeOrder.setSymbol("IBM");
        placeOrder.setQuantity(22);
        placeOrderRequest.addOrder(placeOrder);
        PlaceOrder[] placeOrdersArray = new PlaceOrder[1];
        placeOrdersArray[0] = placeOrder;

        orderApprovalServiceStub.placeOrder(placeOrdersArray);

        OrderApprovalServiceCallbackHandler callback = new OrderApprovalServiceCallbackHandler() {

            public void receiveResultplaceOrder(PlaceOrderResponse result) {

                OrderAccept[] orderAcceptList = result.getOrderAccept();
                String acceptMessage = orderAcceptList[0].getMessage();
                assertNotNull(acceptMessage, "Result cannot be null");
                assertNotEquals(acceptMessage, "");

                synchronized (this) {
                    this.notify();
                }
            }

            public void receiveErrorapproveOrder(Exception e) {
                e.printStackTrace();
            }
        };

        orderApprovalServiceStub.startplaceOrder(placeOrdersArray, callback);
        Thread.sleep(10000);

    }
}

