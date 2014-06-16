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
import org.wso2.carbon.samples.bankingService.deposit.Deposit;
import org.wso2.carbon.samples.bankingService.deposit.DepositAccept;
import org.wso2.carbon.samples.bankingService.deposit.DepositE;
import org.wso2.carbon.samples.bankingService.stub.BankingServiceStub;
import org.wso2.carbon.samples.bankingService.withdraw.*;

import java.rmi.RemoteException;
public class BankingServiceTestCase {

    public static void main(String[] args) {

        try {
            BankingServiceStub bankingServiceStub =
                    new BankingServiceStub("http://localhost:9763/services/BankingService");

            bankingServiceStub._getServiceClient().getOptions().setManageSession(true);

            DepositE depositRequest = new DepositE();
            Deposit deposit = new Deposit();
            deposit.setAccountNumber("070229x");
            deposit.setAmount(1000);
            depositRequest.addDeposit(deposit);

            Deposit[] deposits = new Deposit[1];
            deposits[0] = deposit;
            depositRequest.setDeposit(deposits);


            DepositAccept[] results = bankingServiceStub.deposit(deposits);
            String result = results[0].getMessage();
            System.out.println(result);

            WithDrawE withDrawRequest = new WithDrawE();
            Withdraw withdraw = new Withdraw();
            withdraw.setAccountNumber("070229x");
            withdraw.setAmount(500);
            Withdraw[] withdraws = new Withdraw[1];
            withdraws[0] = withdraw;
            withDrawRequest.setWithDraw(withdraws);
            WithDrawRespone withDrawRespone = bankingServiceStub.withDraw(withdraws);
            WithdrawAccept[] withdrawAccepts = withDrawRespone.getWithdrawAccept();
            WithdrawReject[] withdrawRejects = withDrawRespone.getWithdrawReject();
            String resultWithDraw = withdrawAccepts[0].getMessage();
            System.out.println(resultWithDraw);

        } catch (AxisFault axisFault) {
                axisFault.printStackTrace();

        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }
}
