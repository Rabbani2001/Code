package com.schooltech.security.jwt.service;


import com.schooltech.sms.dao.client.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
//@Transactional("masterEntityManagerFactory")
public class CustomUserDetailsService implements UserDetailsService {
    /*
    CustomUserDetailsService class is created to get the details for spring security for the user trying to login
    */
    @Autowired
    private UserRepository userRepository;

    /*
    Checks the passed user is present or not. if present this will be used for password
    authentication otherwise simply exception thrown.
    */
    @Override
    public UserDetails loadUserByUsername(String emailOrUsername) throws UsernameNotFoundException {
        return userRepository.findByUsernameOrEmailOrPhoneNo(emailOrUsername).orElseThrow(() -> new RuntimeException("User Not Found"));

    }

//    private final EntityManager masterEntityManager;
//
//    public CustomUserDetailsService(@Qualifier("masterEntityManagerFactory") EntityManager masterEntityManager) {
//        this.masterEntityManager = masterEntityManager;
//    }

//    @Override
//    public UserDetails loadUserByUsername(String emailOrUsername) throws UsernameNotFoundException {
////        String jpql = "SELECT u FROM User u where u.username = :username";
////        TypedQuery<User> query = masterEntityManager.createQuery(jpql, User.class);
////        query.setParameter("username", username);
////        //List<User> userList = masterEntityManager.createQuery(query).getResultList();
////        System.out.println("================"+query.getSingleResult());
//        return userRepository.findByUsernameOrEmail(emailOrUsername).orElseThrow(() -> new RuntimeException("User Not Found"));
//
//    }


//    private List<User> usersList = new ArrayList<>();
//
////    public UserService() {
////       usersList.add(new User(UUID.randomUUID().toString(),"skn11","skn11"));
////       usersList.add(new User(UUID.randomUUID().toString(),"abc11","abc111"));
////    }
//
//
//    public List<User> getUsersList() {
//        return this.usersList;
//    }
}
