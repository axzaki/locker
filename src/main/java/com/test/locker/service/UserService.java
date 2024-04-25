package com.test.locker.service;

import com.test.locker.helper.Constant;
import com.test.locker.model.dto.UserDTO;
import com.test.locker.model.entity.User;
import com.test.locker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
@RequiredArgsConstructor
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public UserDTO registerUser(UserDTO userDTO) throws Exception {
        //validasi
        if (userDTO.getPhoneNumber() == null || userDTO.getIdCard() == null || userDTO.getEmail() == null ||
                "".equalsIgnoreCase(userDTO.getPhoneNumber())  || "".equalsIgnoreCase(userDTO.getIdCard()) || "".equalsIgnoreCase(userDTO.getEmail()) ) {
            throw new Exception("Phone number, ID card, and email are required");
        }

        // save user
        User user = new User();
        user.setPhoneNumber(userDTO.getPhoneNumber());
        user.setIdCard(userDTO.getIdCard());
        user.setEmail(userDTO.getEmail());
        user.setIsDeleted(Constant.NO);
        userRepository.save(user);

        //return
        userDTO.setId(user.getId());
        return userDTO;
    }
}
