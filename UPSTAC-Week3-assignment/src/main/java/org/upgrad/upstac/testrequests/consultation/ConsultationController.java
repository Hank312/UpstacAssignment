package org.upgrad.upstac.testrequests.consultation;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.upgrad.upstac.config.security.UserLoggedInService;
import org.upgrad.upstac.exception.AppException;
import org.upgrad.upstac.testrequests.RequestStatus;
import org.upgrad.upstac.testrequests.TestRequest;
import org.upgrad.upstac.testrequests.TestRequestQueryService;
import org.upgrad.upstac.testrequests.TestRequestUpdateService;
import org.upgrad.upstac.testrequests.flow.TestRequestFlowService;
import org.upgrad.upstac.users.User;

import javax.validation.ConstraintViolationException;
import java.util.List;

import static org.upgrad.upstac.exception.UpgradResponseStatusException.asBadRequest;
import static org.upgrad.upstac.exception.UpgradResponseStatusException.asConstraintViolation;


@RestController
@RequestMapping("/api/consultations")
public class ConsultationController {

    Logger log = LoggerFactory.getLogger(ConsultationController.class);

    @Autowired
    private TestRequestUpdateService testRequestUpdateService;

    @Autowired
    private TestRequestQueryService testRequestQueryService;

    @Autowired
    TestRequestFlowService  testRequestFlowService;

    @Autowired
    private UserLoggedInService userLoggedInService;



    @GetMapping("/in-queue")
    @PreAuthorize("hasAnyRole('DOCTOR')")
    public List<TestRequest> getForConsultations()  {

        //this gets the list of test requests having status as 'LAB_TEST_COMPLETED'


        return testRequestQueryService.findBy(RequestStatus.LAB_TEST_COMPLETED);

    }


    @GetMapping
    @PreAuthorize("hasAnyRole('DOCTOR')")
    public List<TestRequest> getForDoctor()  {

        //the current logged in doctor is stored as an object
        //and the list of consultation requests assigned to him are returned

        User currentDoctor = userLoggedInService.getLoggedInUser();
        return testRequestQueryService.findByDoctor(currentDoctor);

    }


    @PreAuthorize("hasAnyRole('DOCTOR')")
    @PutMapping("/assign/{id}")
    public TestRequest assignForConsultation(@PathVariable Long id) {

        try {

            // the current logged in doctor is stored as an object
            // and method to assign a consultation from list of consultations to the logged in doctor is implemented


            User currentDoctor = userLoggedInService.getLoggedInUser();
            return testRequestUpdateService.assignForConsultation(id,currentDoctor);


        }

        catch (AppException e) {
            throw asBadRequest(e.getMessage());
        }
    }



    @PreAuthorize("hasAnyRole('DOCTOR')")
    @PutMapping("/update/{id}")
    public TestRequest updateConsultation(@PathVariable Long id,@RequestBody CreateConsultationRequest testResult) {

        try {

            //the current logged in doctor is stored as an object
            // and the method to update lab consultation by logged in doctor is implemented

            User currentDoctor = userLoggedInService.getLoggedInUser();
            return testRequestUpdateService.updateConsultation(id,testResult,currentDoctor);

        }

        catch (ConstraintViolationException e) {
            throw asConstraintViolation(e);
        }

        catch (AppException e) {
            throw asBadRequest(e.getMessage());
        }
    }



}
