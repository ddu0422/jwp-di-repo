package samples;

import nextstep.annotation.Inject;
import nextstep.mvc.ModelAndView;
import nextstep.stereotype.Controller;
import nextstep.web.annotation.RequestMapping;
import nextstep.web.annotation.RequestMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class MyController {
    private static final Logger logger = LoggerFactory.getLogger(MyController.class);

    private final MyService myService;

    @Inject
    public MyController(MyService myService) {
        this.myService = myService;
    }

    @RequestMapping(value = "/users", method = RequestMethod.GET)
    public ModelAndView findUserId(HttpServletRequest request, HttpServletResponse response) {
        String userId = request.getParameter("userId");
        logger.debug("Find UserId : {}", userId);
        User user = myService.findUserById(userId);
        request.setAttribute("user", user);
        return null;
    }

    @RequestMapping(value = "/users", method = RequestMethod.POST)
    public ModelAndView save(HttpServletRequest request, HttpServletResponse response) {
        User user = new User(
                request.getParameter("userId"),
                request.getParameter("password"),
                request.getParameter("name"),
                request.getParameter("email"));
        logger.debug("User : {}", user);
        myService.addUser(user);
        return null;
    }
}
