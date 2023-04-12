//package com.example.demo.controllers;
//
//import com.example.demo.models.ControllerError;
//import jakarta.servlet.RequestDispatcher;
//import jakarta.servlet.http.HttpServletRequest;
//import org.springframework.boot.web.servlet.error.ErrorController;
//import org.springframework.http.HttpStatus;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//public class CustomErrorController implements ErrorController {
//
////    @RequestMapping("/error")
////    public String handleError(HttpServletRequest request) {
////        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
////
////        if (status != null) {
////            Integer statusCode = Integer.valueOf(status.toString());
////
////            if(statusCode == HttpStatus.NOT_FOUND.value()) {
////                return "error-404";
////            }
////            else if(statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
////                return "error-500";
////            }
////        }
////        return "error";
////    }
//}
