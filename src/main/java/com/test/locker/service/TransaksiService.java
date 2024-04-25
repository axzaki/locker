package com.test.locker.service;

import com.test.locker.helper.Constant;
import com.test.locker.helper.EmailHelper;
import com.test.locker.helper.PasswordGenerator;
import com.test.locker.model.dto.CompleteTransaksiDTO;
import com.test.locker.model.dto.LockerDTO;
import com.test.locker.model.dto.TransaksiDTO;
import com.test.locker.model.entity.Locker;
import com.test.locker.model.entity.Transaksi;
import com.test.locker.model.entity.User;
import com.test.locker.repository.LockerRepository;
import com.test.locker.repository.TransaksiRepository;
import com.test.locker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Transactional
@Service
@RequiredArgsConstructor
public class TransaksiService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private LockerRepository lockerRepository;
    @Autowired
    private TransaksiRepository transaksiRepository;
    public TransaksiDTO booking(TransaksiDTO transaksiDTO) throws Exception {
        //validasi
        if (transaksiDTO.getUserId() == null || transaksiDTO.getLockerNo() == null) {
            throw new Exception("User is Invalid or Locker is not Available");
        }
        User user = userRepository.getById(transaksiDTO.getUserId());
        if (user == null) {
            throw new Exception("User is Invalid");
        }
        List<Locker> lockerList = lockerRepository.findByNoLokerInAndStatus(transaksiDTO.getLockerNo(), Constant.STATUS_TERSEDIA);
        if (lockerList == null) {
            throw new Exception("Locker is not Available");
        }
        //calculate deposit amount
        transaksiDTO.setDeposit(BigDecimal.valueOf(transaksiDTO.getLockerNo().size()*10000));
        //initiate trx
        Transaksi transaksi = new Transaksi();
        transaksi.setUser(user);
        transaksi.setStatus(Constant.STATUS_TRX_BOOKED);

        List<Locker> lockerBookedList = lockerList.stream().map(locker -> {
            locker.setStatus(Constant.STATUS_DIPINJAM);
            locker.setIsPasswordLocked(Constant.NO);
            locker.setPassword(PasswordGenerator.generatePassword(8));
            locker.setUsageCountPassword(0);
            locker.setWrongCountPassword(0);
            locker.setIsDeleted(Constant.NO);
            lockerRepository.save(locker);

            return locker;
        }).collect(Collectors.toList());
        transaksi.setLockerList(lockerBookedList);
        transaksi.setIsDeleted(Constant.NO);
        transaksiRepository.save(transaksi);

        //send mail notification
        sendNotificationEmail(user.getEmail(),lockerBookedList);
        return transaksiDTO;
    }

    public void sendNotificationEmail(String to, List<Locker> lockerList){
        String subject = "Locker Management System - Booking";
        StringBuilder body = new StringBuilder();
        body.append("Hi ").append(to).append(" Locker telah dibooked dengan detail berikut:").append("\n");
        for (Locker locker : lockerList) {
            body.append("No Locker: ").append(locker.getNoLoker()).append("\n");
            body.append("Password: ").append(locker.getPassword()).append("\n\n");
        }
        body.append("Terima Kasih! ");

        //send mail async
        EmailHelper.sendEmailAsync(to,subject,body.toString());
    }

    public CompleteTransaksiDTO checkCompleteTransaksi(Integer transaksiId) throws Exception {
        Transaksi transaksi = transaksiRepository.findById(transaksiId);
        User user = userRepository.getById(transaksi.getUser().getId());
        List<Locker> lockerList = transaksi.getLockerList();
        List<Locker> lockerListAmbil = lockerRepository.findByTransaksiIdAndStatus(transaksiId,Constant.STATUS_AMBIL);
        List<LockerDTO> lockerDTOList = new ArrayList<>();
        if(lockerList != null && lockerListAmbil !=null){
            if(lockerList.size() == lockerListAmbil.size()){
                for(Locker locker :lockerListAmbil){
                    locker.setStatus(Constant.STATUS_TERSEDIA);
                    locker.setUsageCountPassword(0);
                    locker.setWrongCountPassword(0);
                    locker.setPassword(null);
                    locker.setIsPasswordLocked(Constant.NO);
                    locker.setDateKembali(null);
                    locker.setDatePinjam(null);
                    lockerRepository.save(locker);

                    LockerDTO lockerDTO =new LockerDTO();
                    lockerDTO.setNoLoker(locker.getNoLoker());
                    lockerDTO.setStatus(Constant.STATUS_TRX_SELESAI);
                    lockerDTOList.add(lockerDTO);
                }

                transaksi.setStatus(Constant.STATUS_TRX_SELESAI);
                transaksi.setIsDeleted(Constant.YES);
                transaksi.setKurangBayar(user.getDeposit().subtract(transaksi.getDenda()));
                transaksiRepository.save(transaksi);

                CompleteTransaksiDTO completeTransaksiDTO = new CompleteTransaksiDTO();
                completeTransaksiDTO.setUserId(user.getId());
                completeTransaksiDTO.setStatus(transaksi.getStatus());
                completeTransaksiDTO.setLockerDTOList(lockerDTOList);
                completeTransaksiDTO.setDeposit(user.getDeposit());
                completeTransaksiDTO.setDenda(transaksi.getDenda());
                completeTransaksiDTO.setKurangBayar(transaksi.getKurangBayar());
                return completeTransaksiDTO;
            }
        }

        return null;
    }
}
