package com.test.locker.service;

import com.test.locker.helper.Constant;
import com.test.locker.helper.ResponseWrapperDTO;
import com.test.locker.model.dto.CompleteTransaksiDTO;
import com.test.locker.model.dto.LockerDTO;
import com.test.locker.model.dto.LockerPassDTO;
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
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Transactional
@Service
@RequiredArgsConstructor
public class LockerService {
    @Autowired
    private LockerRepository lockerRepository;
    @Autowired
    private TransaksiRepository transaksiRepository;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TransaksiService transaksiService;

    public List<LockerDTO> getLockerListTersedia(){
        List<Locker> lockerList = lockerRepository.findByStatus(Constant.STATUS_TERSEDIA);
        return lockerList.stream().map(LockerService::convertToDTO).collect(Collectors.toList());
    }

    private static LockerDTO convertToDTO(Locker locker) {
        LockerDTO lockerDTO = new LockerDTO();
        lockerDTO.setNoLoker(locker.getNoLoker());
        lockerDTO.setStatus(locker.getStatus());
        return lockerDTO;
    }


    public Object simpanLoker(LockerPassDTO lockerPassDTO) throws Exception {
        //validasi
        if (lockerPassDTO.getNoLoker() == null || lockerPassDTO.getPassword() == null || "".equalsIgnoreCase(lockerPassDTO.getPassword())) {
            throw new Exception("No Loker is Invalid or Password is Invalid");
        }
        Locker locker = lockerRepository.findByNoLokerAndStatus(lockerPassDTO.getNoLoker(), Constant.STATUS_DIPINJAM);
        if (locker==null) {
            throw new Exception("Wrong No Loker");
        }
        if (Constant.YES.equalsIgnoreCase(locker.getIsPasswordLocked())) {
            throw new Exception("Password Loker is Locked");
        }
        Transaksi transaksi = transaksiRepository.findFirstByLockerNoLoker(locker.getNoLoker());
        User user = userRepository.getById(transaksi.getUser().getId());
        if (!lockerPassDTO.getPassword().equals(locker.getPassword())) {
            locker.setWrongCountPassword(locker.getWrongCountPassword()+1);
            if(locker.getWrongCountPassword()==3){
                transaksi.setDenda(BigDecimal.valueOf(25000).add(transaksi.getDenda()).add(user.getDeposit()));
                transaksiRepository.save(transaksi);
                locker.setIsPasswordLocked(Constant.YES);
            }
            lockerRepository.save(locker);
            throw new Exception("Invalid Password");
        }
        if (locker.getUsageCountPassword() >= 2) {
            throw new Exception("Password usage limit exceeded for this locker");
        }

        locker.setUsageCountPassword(locker.getUsageCountPassword()+1);
        locker.setDatePinjam(new Date());
        lockerRepository.save(locker);

        LockerDTO lockerDTO = new LockerDTO();
        lockerDTO.setNoLoker(lockerPassDTO.getNoLoker());
        lockerDTO.setStatus(locker.getStatus());
        return ResponseWrapperDTO.builder()
                .data(lockerDTO)
                .status(Constant.Success)
                .messages("Locker berhasil simpan").build();
    }

    public Object ambilLoker(LockerPassDTO lockerPassDTO) throws Exception {
        //validasi
        if (lockerPassDTO.getNoLoker() == null || lockerPassDTO.getPassword() == null || "".equalsIgnoreCase(lockerPassDTO.getPassword())) {
            throw new Exception("No Loker is Invalid or Password is Invalid");
        }
        Locker locker = lockerRepository.findByNoLokerAndStatus(lockerPassDTO.getNoLoker(), Constant.STATUS_DIPINJAM);
        if (locker==null) {
            throw new Exception("Wrong No Loker");
        }
        if (Constant.YES.equalsIgnoreCase(locker.getIsPasswordLocked())) {
            throw new Exception("Password Loker is Locked");
        }
        Transaksi transaksi = transaksiRepository.findFirstByLockerNoLoker(locker.getNoLoker());
        User user = userRepository.getById(transaksi.getUser().getId());
        if (!lockerPassDTO.getPassword().equals(locker.getPassword())) {
            locker.setWrongCountPassword(locker.getWrongCountPassword()+1);
            if(locker.getWrongCountPassword()==3){
                transaksi.setDenda(BigDecimal.valueOf(25000).add(transaksi.getDenda()).add(user.getDeposit()));
                transaksiRepository.save(transaksi);
                locker.setIsPasswordLocked(Constant.YES);
            }
            lockerRepository.save(locker);
            throw new Exception("Invalid Password");
        }
        if (locker.getUsageCountPassword() >= 2) {
            throw new Exception("Password usage limit exceeded for this locker");
        }

        locker.setDateKembali(new Date());
        locker.setStatus(Constant.STATUS_AMBIL);
        locker.setUsageCountPassword(locker.getUsageCountPassword()+1);
        lockerRepository.save(locker);

        long dendaHarian = hitungDendaHarian(locker);
        if(dendaHarian > 0){
            transaksi.setDenda(BigDecimal.valueOf(dendaHarian).add(transaksi.getDenda()));
            transaksiRepository.save(transaksi);
        }

        LockerDTO lockerDTO = new LockerDTO();
        lockerDTO.setNoLoker(lockerPassDTO.getNoLoker());
        lockerDTO.setStatus(Constant.STATUS_AMBIL);

        List<Object> objects = new ArrayList<>();

        CompleteTransaksiDTO completeTransaksiDTO = transaksiService.checkCompleteTransaksi(transaksi.getId());
        if(completeTransaksiDTO != null){
            objects.add(0,completeTransaksiDTO);
            objects.add(1,lockerDTO);
        }else {
            objects.add(0,lockerDTO);
        }

        return ResponseWrapperDTO.builder()
                .data(objects)
                .status(Constant.Success)
                .messages("Locker berhasil ambil").build();
    }

    public long hitungDendaHarian(Locker locker){
        Date dateKembali = locker.getDateKembali();
        Date datePinjam = locker.getDatePinjam();
        long millisecondsDifference = dateKembali.getTime() - datePinjam.getTime();
        long daysDifference = millisecondsDifference / (1000 * 60 * 60 * 24);
        if (daysDifference > 1) {
            long overdueDays = daysDifference - 1; // Kurangi 1 hari untuk hari pertama yang tidak dihitung
            long denda = overdueDays * 5000; // Hitung denda (Rp.5.000,00 per hari)
            return denda;
        }
        return 0;
    }
}
