package com.kamioda.id.service;

import java.io.IOException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kamioda.id.repository.PreEntryRepository;
import com.kamioda.id.repository.UserRepository;
import com.kamioda.id.component.File;
import com.kamioda.id.component.MailSender;
import com.kamioda.id.component.MasterIDGenerator;
import com.kamioda.id.component.PreEntryIDGenerator;
import com.kamioda.id.exception.*;
import com.kamioda.id.model.PreEntryRecord;
import com.kamioda.id.model.User;
import com.kamioda.id.model.dto.AccountInformation;
import com.kamioda.id.model.dto.AccountUpdateInformation;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PreEntryRepository preEntryRepository;
    @Autowired
    private PreEntryIDGenerator preEntryIDGenerator;
    @Autowired
    private MailSender mail;
    @Autowired
    private TokenService tokenService;
    @Autowired
    private MasterIDGenerator masterIDGenerator;
    public void preEntry(String Email) throws BadRequestException, IOException {
        if (userRepository.countRecords(Email) > 0 || preEntryRepository.countByEmail(Email) > 0)
            throw new BadRequestException("Email already in use");
        String PreEntryID = preEntryIDGenerator.generate();
        preEntryRepository.insertPreEntryRecord(PreEntryID, Email);
        String Message = File.readAllText("./data/mail/pre_entry.txt");
        Message.replace("{PreEntryID}", PreEntryID);
        mail.send(Email, "【Kamioda Games ID】仮登録のお知らせ", Message);
    }
    public void entry(String preEntryId, String userId, String name, String password) throws BadRequestException, NotFoundException, IOException {
        try {
            Optional<PreEntryRecord> record = preEntryRepository.findById(preEntryId);
            if (record.isEmpty()) throw new NotFoundException("Pre-entry record not found");
            if (record.get().expired()) throw new BadRequestException("Pre-entry record expired");
            if (userRepository.countRecords(userId) > 0) throw new BadRequestException("User ID already in use");
            String masterID = masterIDGenerator.generate(userId);
            User user = new User(masterID, userId, name, record.get().getEmail(), password);
            userRepository.save(user);
            String Message = File.readAllText("./data/mail/entry.txt");
            Message.replace("{UserName}", name);
            Message.replace("{UserID}", userId);
            mail.send(record.get().getEmail(), "【Kamioda Games ID】本登録のお知らせ", Message);
        } finally {
            preEntryRepository.deletePreEntryRecord(preEntryId);
        }
    }
    public AccountInformation getAccountInformation(String accessToken) {
        return tokenService.getUser(accessToken).toAccountInformation();
    }
    public void updateAccountInformation(String accessToken, AccountUpdateInformation newAccountInformation) throws UnauthorizationException, NotFoundException, IOException {
        User user = tokenService.getUser(accessToken);
        userRepository.updateUser(
            user.getId(), 
            newAccountInformation.getUserId(), 
            newAccountInformation.getName(), 
            null, 
            newAccountInformation.getPassword() == null ? null : User.hashPassword(newAccountInformation.getPassword())
        );
        if (newAccountInformation.getEmail() != null) {
            String updateID = preEntryIDGenerator.generate();
            preEntryRepository.insertEmailUpdateRecord(updateID, user.getId(), newAccountInformation.getEmail());
            String Message = File.readAllText("./data/mail/email_update.txt");
            Message.replace("{UserName}", user.getName());
            Message.replace("{UserID}", user.getUserId());
            Message.replace("{UpdateID}", updateID);
            mail.send(newAccountInformation.getEmail(), "【Kamioda Games ID】メールアドレス変更のお知らせ", Message);
        }
    }
    public void completeEmailUpdate(String updateId) throws NotFoundException, IOException {
        Optional<PreEntryRecord> record = preEntryRepository.findById(updateId);
        if (record.isEmpty()) throw new NotFoundException("Pre-entry record not found");
        try {
            if (record.get().expired()) throw new NotFoundException("Pre-entry record expired");
            userRepository.updateUser(record.get().getMasterId(), null, null, record.get().getEmail(), null);
        } finally {
            preEntryRepository.deletePreEntryRecord(updateId);
        }
    }
    public void deleteAccount(String accessToken) throws UnauthorizationException, NotFoundException, IOException {
        User user = tokenService.getUser(accessToken);
        userRepository.deleteUser(user.getId());
        String Message = File.readAllText("./data/mail/delete.txt");
        Message.replace("{UserName}", user.getName());
        Message.replace("{UserID}", user.getUserId());
        mail.send(user.getEmail(), "【Kamioda Games ID】アカウント削除のお知らせ", Message);
    }
}
