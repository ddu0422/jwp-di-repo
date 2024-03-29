package nextstep.di.factory;

import com.google.common.collect.Sets;
import nextstep.di.factory.example.JdbcUserRepository;
import nextstep.di.factory.example.MyQnaService;
import nextstep.di.factory.example.QnaController;
import nextstep.di.factory.example.UserRepository;
import nextstep.stereotype.Controller;
import nextstep.stereotype.Repository;
import nextstep.stereotype.Service;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class BeanFactoryTest {
    private static final Logger log = LoggerFactory.getLogger( BeanFactoryTest.class );

    private Reflections reflections;
    private BeanFactory beanFactory;

    @BeforeEach
    @SuppressWarnings("unchecked")
    public void setup() {
        reflections = new Reflections("nextstep.di.factory.example");
        Set<Class<?>> preInstantiateClazz = getTypesAnnotatedWith(Controller.class, Service.class, Repository.class);
        beanFactory = new BeanFactory(preInstantiateClazz);
        beanFactory.initialize();
    }

    @Test
    public void di() {
        QnaController qnaController = beanFactory.getBean(QnaController.class);

        assertNotNull(qnaController);
        assertNotNull(qnaController.getQnaService());

        MyQnaService qnaService = qnaController.getQnaService();
        assertNotNull(qnaService.getUserRepository());
        assertNotNull(qnaService.getQuestionRepository());
    }

    @Test
    void 생성자_매개변수가_인터페이스인_경우_싱글_인스턴스_확인() {
        MyQnaService myQnaService = beanFactory.getBean(MyQnaService.class);

        UserRepository actual = myQnaService.getUserRepository();
        UserRepository expected = beanFactory.getBean(JdbcUserRepository.class);

        assertEquals(expected, actual);
    }

    @Test
    void 생성자_매개변수가_클래스인_경우_싱글_인스턴스_확인() {
        QnaController qnaController = beanFactory.getBean(QnaController.class);

        MyQnaService actual = qnaController.getQnaService();
        MyQnaService expected = beanFactory.getBean(MyQnaService.class);

        assertEquals(expected, actual);
    }

    @SuppressWarnings("unchecked")
    private Set<Class<?>> getTypesAnnotatedWith(Class<? extends Annotation>... annotations) {
        Set<Class<?>> beans = Sets.newHashSet();
        for (Class<? extends Annotation> annotation : annotations) {
            beans.addAll(reflections.getTypesAnnotatedWith(annotation));
        }
        log.debug("Scan Beans Type : {}", beans);
        return beans;
    }

    @Test
    void bean_에서_ControllerAnnotation_이_붙은_클래스_찾기() {
        Set<Class<?>> controller = beanFactory.getController();

        assertTrue(controller.contains(QnaController.class));
    }
}
