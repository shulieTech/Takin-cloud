import io.shulie.takin.cloud.biz.input.scenemanage.SceneManageWrapperInput;
import io.shulie.takin.cloud.open.req.scenemanage.SceneManageWrapperReq;
import io.shulie.takin.utils.json.JsonHelper;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.BeanUtils;

/**
 * @author 无涯
 * @date 2020/10/28 11:50 下午
 */
@Slf4j
public class SceneManageTest {
    @Test
    public void test() {
        String json = "{\"pageSize\":20,\"current\":0,\"license\":null,\"id\":null,\"tenantId\":1,"
                + "\"pressureTestSceneName\":null,\"businessActivityConfig\":null,\"concurrenceNum\":null,\"ipNum\":null,"
                + "\"pressureTestTime\":null,\"pressureMode\":null,\"increasingTime\":null,\"step\":null,"
                + "\"scriptType\":null,\"uploadFile\":null,\"stopCondition\":null,\"warningCondition\":null,"
                + "\"currentPage\":0,\"offset\":0}\n";
        SceneManageWrapperReq wrapperReq1 = new SceneManageWrapperReq();
        wrapperReq1.setTenantId(1L);
        log.info(JsonHelper.bean2Json(wrapperReq1));
        SceneManageWrapperReq wrapperReq = JsonHelper.json2Bean(json, SceneManageWrapperReq.class);
        SceneManageWrapperInput input = new SceneManageWrapperInput();

        BeanUtils.copyProperties(wrapperReq, input);
        log.info(JsonHelper.bean2Json(input));
    }

    @Test
    public void tests() {
        String s = "/Users/chengjiacai/test/copyfile/test.txt";
        String substring = s.substring(0, s.lastIndexOf("/"));
        log.info(substring);
        new Thread(() -> {
            log.info(" thread");

            log.info(" thread");
        }).start();
    }
}
