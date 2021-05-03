package service;

import exceptions.UserNotFoundException;
import model.platform.Platform;
import model.user.CommonUser;
import model.user.Type_User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import service.userService.UserService;
import service.userService.UserServiceImpl;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserServiceTest {

    @Mock
    private UserService userService;

    @BeforeEach
    public void setUp(){
        CommonUser jose = new CommonUser(4,
                Platform.DISNEY_PLUS,
                Type_User.COMMON,
                "Jose1990",
                "Argentina");
    userService = new UserServiceImpl();
    userService.addUser(jose);
    }

    @Test
    public void getUsersTest(){
        Assertions.assertEquals(1, userService.getUsers().size());
    }

    @Test
    public void getUserTest(){
        Assertions.assertEquals("Jose1990", ((CommonUser) userService.getUser(4)).getNick());
    }

    @Test
    public void addUserTest(){
        CommonUser alejadro = new CommonUser(1,
                Platform.AMAZON_PREMIUM,
                Type_User.COMMON,
                "ale123","Argentina");

        userService.addUser(alejadro);
        Assertions.assertEquals("ale123", ((CommonUser) userService.getUser(1)).getNick());
    }

    @Test
    public void  userNotFoundTest(){
        assertThrows(UserNotFoundException.class, ()-> this.userService.getUser(567));
    }
}
