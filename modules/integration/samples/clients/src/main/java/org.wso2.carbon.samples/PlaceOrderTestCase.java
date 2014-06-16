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
import org.wso2.carbon.samples.orderApprovalService.order.OrderAccept;
import org.wso2.carbon.samples.orderApprovalService.order.PlaceOrder;
import org.wso2.carbon.samples.orderApprovalService.order.PlaceOrderE;
import org.wso2.carbon.samples.orderApprovalService.order.PlaceOrderRespone;
import org.wso2.carbon.samples.orderApprovalService.stub.OrderApprovalServiceCallbackHandler;
import org.wso2.carbon.samples.orderApprovalService.stub.OrderApprovalServiceStub;
import java.rmi.RemoteException;

public class PlaceOrderTestCase {

    public static void main(String[] args) {

        try {
            OrderApprovalServiceStub orderApprovalServiceStub = new OrderApprovalServiceStub("http://localhost:9763/services/OrderApprovalService");
            PlaceOrderE placeOrderE = new PlaceOrderE();
            PlaceOrder placeOrder = new PlaceOrder();
            placeOrder.setSymbol("IBM");
            placeOrder.setPrice(150);
            placeOrder.setQuantity(128);
            PlaceOrder[] placeOrders = new PlaceOrder[1];
            placeOrders[0] = placeOrder;
            placeOrderE.setOrder(placeOrders);

            PlaceOrderRespone placeOrderRespone = null; //new PlaceOrderRespone();
            try {
                placeOrderRespone = orderApprovalServiceStub.placeOrder(placeOrders);
            } catch (RemoteException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
            OrderAccept[] orderAccepts = placeOrderRespone.getOrderAccept();
            String result = orderAccepts[0].getMessage();
            System.out.println(result);



        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
