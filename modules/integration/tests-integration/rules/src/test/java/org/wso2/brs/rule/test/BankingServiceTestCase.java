package org.wso2.brs.rule.test;

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
import org.wso2.brs.integration.common.utils.BRSMasterTest;
import org.wso2.brs.integration.common.utils.RequestSender;
import org.wso2.carbon.samples.test.bankingService.deposit.Deposit;
import org.wso2.carbon.samples.test.bankingService.deposit.DepositAccept;
import org.wso2.carbon.samples.test.bankingService.deposit.DepositE;
import org.wso2.carbon.samples.test.bankingService.stub.BankingServiceStub;
import org.wso2.carbon.samples.test.bankingService.withdraw.*;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.xml.stream.XMLStreamException;
import javax.xml.xpath.XPathExpressionException;
import java.io.ByteArrayInputStream;
import java.io.File;

import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertNotNull;

public class BankingServiceTestCase extends BRSMasterTest {

    private static final Log log = LogFactory.getLog(BankingServiceTestCase.class);
    ServiceClient serviceClient;
    RequestSender requestSender;

    @BeforeClass(groups = {"wso2.brs"})
    public void login() throws Exception {
        init();
        requestSender = new RequestSender();
    }

    @Test(groups = {"wso2.brs"})
    public void uploadBankingService() throws Exception {
        String samplesDir = System.getProperty("samples.dir");
        String BankingServiceAAR = samplesDir + File.separator + "banking.service/service/target/BankingService.aar";
        log.info(BankingServiceAAR);
        FileDataSource fileDataSource = new FileDataSource(BankingServiceAAR);
        DataHandler dataHandler = new DataHandler(fileDataSource);
        getRuleServiceFileUploadClient().uploadService("BankingService.aar", dataHandler);

    }

    @Test(groups = {"wso2.brs"}, dependsOnMethods = {"uploadBankingService"})
    public void testDeposit() throws Exception {

        requestSender.waitForProcessDeployment(brsServer.getContextUrls().getServiceUrl() + "/BankingService");
        serviceClient = new ServiceClient();
        Options options = new Options();
        options.setTo(new EndpointReference(getContext().getContextUrls().getServiceUrl() + "/BankingService"));
        options.setAction("urn:deposit");
        serviceClient.setOptions(options);

        OMElement result = serviceClient.sendReceive(createDepositPayload());
        assertNotNull(result, "Result cannot be null");
    }

    @Test(groups = {"wso2.brs"}, dependsOnMethods = {"testDeposit"})
    protected void testWithdrawal() throws AxisFault, XMLStreamException, XPathExpressionException {

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        serviceClient = new ServiceClient();
        Options options = new Options();
        options.setTo(new EndpointReference(getContext().getContextUrls().getServiceUrl() + "/BankingService"));
        options.setAction("urn:withDraw");
        serviceClient.setOptions(options);

        OMElement result = serviceClient.sendReceive(createWithdrawPayload());
        assertNotNull(result, "Result cannot be null");
    }

    private OMElement createDepositPayload() throws XMLStreamException {
        String request = "<p:depositRequest xmlns:p=\"http://brs.carbon.wso2.org\">\n" +
                "   <!--Zero or more repetitions:-->\n" +
                "   <p:Deposit>\n" +
                "      <!--Zero or 1 repetitions:-->\n" +
                "      <xs:accountNumber xmlns:xs=\"http://banking.brs/xsd\">8425988334v</xs:accountNumber>\n" +
                "      <!--Zero or 1 repetitions:-->\n" +
                "      <xs:amount xmlns:xs=\"http://banking.brs/xsd\">2632</xs:amount>\n" +
                "   </p:Deposit>\n" +
                "</p:depositRequest>";
        return new StAXOMBuilder(new ByteArrayInputStream(request.getBytes())).getDocumentElement();
    }

    private OMElement createWithdrawPayload() throws XMLStreamException {
        String request = "<p:withDrawRequest xmlns:p=\"http://brs.carbon.wso2.org\">\n" +
                "   <!--Zero or more repetitions:-->\n" +
                "   <p:Withdraw>\n" +
                "      <!--Zero or 1 repetitions:-->\n" +
                "      <xs:accountNumber xmlns:xs=\"http://banking.brs/xsd\">8425988334v</xs:accountNumber>\n" +
                "      <!--Zero or 1 repetitions:-->\n" +
                "      <xs:amount xmlns:xs=\"http://banking.brs/xsd\">2000</xs:amount>\n" +
                "   </p:Withdraw>\n" +
                "</p:withDrawRequest>";
        return new StAXOMBuilder(new ByteArrayInputStream(request.getBytes())).getDocumentElement();
    }

    @Test(groups = {"wso2.brs"}, dependsOnMethods = {"uploadBankingService"} )
    public void callBank() throws Exception {

        String result = "";
        requestSender.waitForProcessDeployment(getContext().getContextUrls().getServiceUrl() + "/BankingService");
        BankingServiceStub bankingServiceStub =
                new BankingServiceStub(getContext().getContextUrls().getServiceUrl() + "/BankingService");
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
        result = results[0].getMessage();
        assertNotNull(result, "Result cannot be null");
        assertNotEquals(result, "");

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
        assertNotNull(resultWithDraw, "Result cannot be null");
        assertNotEquals(resultWithDraw, "");
    }

}
