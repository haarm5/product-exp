package com.tmb.oneapp.productsexpservice.feignclients.loansubmission;

import com.tmb.common.model.legacy.rsl.ws.instant.calculate.uw.request.Body;
import com.tmb.common.model.legacy.rsl.ws.instant.calculate.uw.request.Header;
import com.tmb.common.model.legacy.rsl.ws.instant.calculate.uw.request.RequestInstantLoanCalUW;
import com.tmb.common.model.legacy.rsl.ws.instant.calculate.uw.response.ResponseInstantLoanCalUW;
import com.tmb.common.model.legacy.rsl.ws.loan.submission.LoanSubmissionInstantLoanCalUWServiceLocator;
import com.tmb.common.model.legacy.rsl.ws.loan.submission.LoanSubmissionInstantLoanCalUWSoapBindingStub;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.xml.rpc.ServiceException;
import java.rmi.RemoteException;
import java.util.UUID;

@Service
public class LoanSubmissionInstantLoanCalUWClient {
    @Value("${loan-submission-instant-loan-cal-uw.url}")
    private String loanCalUWUrl;

    LoanSubmissionInstantLoanCalUWServiceLocator locator = new LoanSubmissionInstantLoanCalUWServiceLocator();

    private static final String CHANNEL = "MIB";
    private static final String MODULE = "3";

    public ResponseInstantLoanCalUW getCalculateUnderwriting(RequestInstantLoanCalUW req) throws RemoteException, ServiceException {
        locator.setLoanSubmissionInstantLoanCalUWEndpointAddress(loanCalUWUrl);

        LoanSubmissionInstantLoanCalUWSoapBindingStub stub = (LoanSubmissionInstantLoanCalUWSoapBindingStub) locator.getLoanSubmissionInstantLoanCalUW();

        Header header = new Header();
        header.setChannel(CHANNEL);
        header.setModule(MODULE);
        header.setRequestID(UUID.randomUUID().toString());
        req.setHeader(header);

        Body body = new Body();
        body.setTriggerFlag(req.getBody().getTriggerFlag());
        body.setCaId(req.getBody().getCaId());
        req.setBody(body);


        return stub.calculateUnderwriting(req);
    }
}
