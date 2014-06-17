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
import org.wso2.carbon.samples.test.carRentalService.reservation.CarReservation;
import org.wso2.carbon.samples.test.carRentalService.reservation.Charge;
import org.wso2.carbon.samples.test.carRentalService.reservation.Reservation;
import org.wso2.carbon.samples.test.carRentalService.stub.CarRentalServiceStub;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.xml.xpath.XPathExpressionException;
import java.io.File;
import java.rmi.RemoteException;

import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertNotNull;

public class CarRentalTestCase extends BrsMaterTestCase {

    private static final Log log = LogFactory.getLog(CarRentalTestCase.class);


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
    public void testCarRental() throws XPathExpressionException {

        waitForProcessDeployment(getContext().getContextUrls().getServiceUrl() + "/CarRentalService");

        try {
            CarRentalServiceStub carRentalServiceStub =
                    new CarRentalServiceStub(getContext().getContextUrls().getServiceUrl() + "/CarRentalService");
            CarReservation carReservation = new CarReservation();
            Reservation reservation = new Reservation();
            reservation.setMPD(18.5);
            reservation.setType("weekly");
            Reservation[] reservations = new Reservation[1];
            reservations[0] = reservation;
            carReservation.setReserve(reservations);

            Charge[] charges = carRentalServiceStub.rent(reservations);
            String result = charges[0].getMessage();
            assertNotNull(result, "Result cannot be null");
            assertNotEquals(result, "");

        } catch (AxisFault axisFault) {
            axisFault.printStackTrace();
            assertNotNull(null);
        } catch (RemoteException e) {
            e.printStackTrace();
            assertNotNull(null);
        }
    }
}
