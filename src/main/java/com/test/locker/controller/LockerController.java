package com.test.locker.controller;

import com.test.locker.helper.Constant;
import com.test.locker.helper.ResponseWrapperDTO;
import com.test.locker.model.dto.*;
import com.test.locker.service.LockerService;
import com.test.locker.service.TransaksiService;
import com.test.locker.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.test.locker.helper.Constant.API_URL;

@RestController
@RequiredArgsConstructor
public class LockerController {

    @Autowired
    private UserService userService;
    @Autowired
    private LockerService lockerService;
    @Autowired
    private TransaksiService transaksiService;

    @PostMapping(value = API_URL+"/user/register", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> registerUser(@RequestBody UserDTO userDTO) {
        try {
            userDTO = userService.registerUser(userDTO);
            return new ResponseEntity<>(ResponseWrapperDTO.builder()
                                        .data(userDTO)
                                        .status(Constant.Success)
                                        .messages("User registered successfully").build(), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(ResponseWrapperDTO.builder()
                                        .status(Constant.Failed)
                                        .messages(e.getMessage()).build(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = API_URL+"/locker/list", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getLockerList()
    {
        try {
            return new ResponseEntity<>(lockerService.getLockerListTersedia(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping(value = API_URL+"/booking", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> booking(@RequestBody TransaksiDTO transaksiDTO) {
        try {
            transaksiDTO = transaksiService.booking(transaksiDTO);
            return new ResponseEntity<>(ResponseWrapperDTO.builder()
                    .data(transaksiDTO)
                    .status(Constant.Success)
                    .messages("Locker booked successfully").build(), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(ResponseWrapperDTO.builder()
                    .status(Constant.Failed)
                    .messages(e.getMessage()).build(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping(value = API_URL+"/simpan", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> simpan(@RequestBody LockerPassDTO lockerPassDTO) {
        try {
            Object obj = lockerService.simpanLoker(lockerPassDTO);
            return new ResponseEntity<>(obj, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(ResponseWrapperDTO.builder()
                    .status(Constant.Failed)
                    .messages(e.getMessage()).build(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping(value = API_URL+"/ambil", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> ambil(@RequestBody LockerPassDTO lockerPassDTO) {
        try {
            Object obj = lockerService.ambilLoker(lockerPassDTO);
            return new ResponseEntity<>(obj, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(ResponseWrapperDTO.builder()
                    .status(Constant.Failed)
                    .messages(e.getMessage()).build(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping(value = API_URL+"/complete", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> doComplete(@RequestParam(name = "transaksiId") String transaksiId) {
        try {
            CompleteTransaksiDTO completeTransaksiDTO = transaksiService.checkCompleteTransaksi(Integer.valueOf(transaksiId));
            return new ResponseEntity<>(ResponseWrapperDTO.builder()
                    .data(completeTransaksiDTO)
                    .status(Constant.Success)
                    .messages("Transaction complete successfully").build(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(ResponseWrapperDTO.builder()
                    .status(Constant.Failed)
                    .messages(e.getMessage()).build(), HttpStatus.BAD_REQUEST);
        }
    }
}