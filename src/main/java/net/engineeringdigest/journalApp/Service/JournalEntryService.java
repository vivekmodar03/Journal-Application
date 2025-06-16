package net.engineeringdigest.journalApp.Service;


import ch.qos.logback.core.encoder.EchoEncoder;
import net.engineeringdigest.journalApp.entity.JournalEntry;
import net.engineeringdigest.journalApp.entity.User;
import net.engineeringdigest.journalApp.repository.JournalEntryRepo;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
public class JournalEntryService {

    @Autowired
    private JournalEntryRepo journalEntryRepo;

    @Autowired
    private UserService userService;


//    When i post a new entry in journal_entries db that entry also should be added to the users db associated to any one user.
//    Like we can see here are two tasks one is save entry in journal_entries and second is associate the new entry with user in the users database.
//    So to perform this @Transactional annotation is used.
//    BUT
//    When two users post entries at a time then it will be called as replication.
//    And replication is not possible with @Transactional annotation so for that Mongodb atlas is used.

    @Transactional
    public void saveEntry(JournalEntry journalEntry, String userName){
            try {
                User user= userService.findByUserName(userName);
                journalEntry.setDate(LocalDateTime.now());
                JournalEntry saved = journalEntryRepo.save(journalEntry);
                user.getJournalEntries().add(saved);
                userService.saveEntry(user);
            }catch (Exception e){
                System.out.println(e);
                throw new RuntimeException("An error occured while saving the entry. ",e);
            }

    }

    public void saveEntry(JournalEntry journalEntry){
        journalEntryRepo.save(journalEntry);
    }

    public List<JournalEntry> getAll() {
        return journalEntryRepo.findAll();
    }

    public Optional<JournalEntry> findEntryById(ObjectId id) {
        return journalEntryRepo.findById(id);
    }

    public void deleteEntryById(ObjectId id, String userName){
        User user= userService.findByUserName(userName);
        user.getJournalEntries().removeIf(x -> x.getId().equals(id));
        userService.saveEntry(user);
        journalEntryRepo.deleteById(id);

    }
}









// Before user created



//package net.engineeringdigest.journalApp.Service;
//
//
//import net.engineeringdigest.journalApp.entity.JournalEntry;
//import net.engineeringdigest.journalApp.repository.JournalEntryRepo;
//import org.bson.types.ObjectId;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//import java.sql.SQLOutput;
//import java.util.List;
//import java.util.Optional;
//
//@Component
//public class JournalEntryService {
//
//    @Autowired
//    private JournalEntryRepo journalEntryRepo;
//
//    public void saveEntry(JournalEntry journalEntry){
//        try{
//            journalEntryRepo.save(journalEntry);
//        }catch (Exception e){
//            System.out.println(e);
//        }
//    }
//
//    public List<JournalEntry> getAll() {
//        return journalEntryRepo.findAll();
//    }
//
//    public Optional<JournalEntry> findEntryById(ObjectId id) {
//        return journalEntryRepo.findById(id);
//    }
//
//    public void deleteEntryById(ObjectId id){
//        journalEntryRepo.deleteById(id);
//    }
//}


