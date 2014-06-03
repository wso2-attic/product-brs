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
import org.wso2.carbon.samples.mipCalculateService.clientDetail.Client;
import org.wso2.carbon.samples.mipCalculateService.clientDetail.MIP;
import org.wso2.carbon.samples.mipCalculateService.clientDetail.PlaceClientDetail;
import org.wso2.carbon.samples.mipCalculateService.stub.MIPCalculateServiceStub;

import java.rmi.RemoteException;

public class MIPCalculateServiceTestCase {

    public static void main(String[] args) {

        try {
            MIPCalculateServiceStub mipCalculateServiceStub =
                    new MIPCalculateServiceStub("http://localhost:9763/services/MIPCalculateService");

            PlaceClientDetail placeClientDetail = new PlaceClientDetail();
            Client client = new Client();
            client.setLoanType("FHA");
            client.setDownPayment(8000);
            client.setMortgageValue(90000);
            Client[] clients = new Client[1];
            clients[0] = client;

            MIP[] mips = mipCalculateServiceStub.calculate(clients);
            double result = mips[0].getAnnualMIP();
            System.out.println(result);

        } catch (AxisFault axisFault) {
            axisFault.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
