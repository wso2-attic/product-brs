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
import org.wso2.carbon.samples.insuranceService.insurance.ApplyForInsurance;
import org.wso2.carbon.samples.insuranceService.insurance.Car;
import org.wso2.carbon.samples.insuranceService.insurance.Driver;
import org.wso2.carbon.samples.insuranceService.insurance.Policy;
import org.wso2.carbon.samples.insuranceService.stub.InsuranceServiceStub;
//import org.wso2.carbon.integration.framework.TestServerManager;
//import org.wso2.carbon.utils.FileManipulator;
import java.rmi.RemoteException;

public class InsuranceServiceTestCase {

    public static void main(String[] args) {

        try {
            InsuranceServiceStub insuranceServiceStub =
                    new InsuranceServiceStub("http://localhost:9763/services/InsuranceService");

            ApplyForInsurance applyForInsurance = new ApplyForInsurance();

            Car car = new Car();
            car.setColor("red");
            car.setStyle("sport");
            Car[] cars = new Car[1];
            cars[0] = car;

            Driver driver = new Driver();
            driver.setName("Ishara");
            driver.setAge(24);
            driver.setMale(true);
            Driver[] drivers = new Driver[1];
            drivers[0] = driver;

            applyForInsurance.setCarInsurance(cars);
            applyForInsurance.setDriverInsurance(drivers);


            Policy[] policies = insuranceServiceStub.applyForInsurance(cars, drivers);
            double result = policies[0].getPremium();
            System.out.println(result);
        } catch (AxisFault axisFault) {
            axisFault.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }
}
